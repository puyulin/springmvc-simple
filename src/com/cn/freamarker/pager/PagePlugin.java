package com.cn.freamarker.pager;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.PropertyException;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.ExecutorException;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * 
* 类名称：mybatis插件扩展（分页）
* 类描述： 
*   
*  
* 分页拦截器，用于拦截需要进行分页查询的操作，然后对其进行分页处理。  
* 利用拦截器实现Mybatis分页的原理：  
* 要利用JDBC对数据库进行操作就必须要有一个对应的Statement对象，Mybatis在执行Sql语句前就会产生一个包含Sql语句的Statement对象，而且对应的Sql语句  是在Statement之前产生的，
* 所以我们就可以在它生成Statement之前对用来生成Statement的Sql语句下手。
* 在Mybatis中Statement语句是通过RoutingStatementHandler对象的prepare方法生成的。
* 所以利用拦截器实现Mybatis分页的一个思路就是拦截StatementHandler接口的prepare方法，然后在拦截器方法中把Sql语句改成对应的分页查询Sql语句，之后再调用  
* StatementHandler对象的prepare方法，即调用invocation.proceed()。  
* 对于分页而言，在拦截器里面我们还需要做的一个操作就是统计满足当前条件的记录一共有多少，这是通过获取到了原始的Sql语句后，把它改为对应的统计语句再利用Mybatis封装好的参数和设  
* 置参数的功能把Sql语句中的参数进行替换，之后再执行查询记录数的Sql语句进行总记录数的统计。  
* 
*  在该注解中包含三个参数，分别是type，method，args。Type指定要拦截的类对象，method是指明要拦截该类的哪个方法，第三个是指明要拦截的方法参数集合
*
 */
@Intercepts({@Signature(type=StatementHandler.class,method="prepare",args={Connection.class})})
public class PagePlugin implements Interceptor {
	
	private static Logger log = LoggerFactory.getLogger(PagePlugin.class);
	private static String DIALECT = "";	//数据库方言
	private static String PAGE_SQL_ID = ""; //mapper.xml中需要拦截的ID(正则匹配)
	
	public Object intercept(Invocation ivk) throws Throwable {
//		System.out.println("==="+ivk.getTarget().getClass());
//		System.out.println("==="+RoutingStatementHandler.class.isInstance(ivk.getTarget()));
//		System.out.println("==="+RoutingStatementHandler.class.isAssignableFrom(ivk.getTarget().getClass()));
		if(ivk.getTarget() instanceof RoutingStatementHandler){
			RoutingStatementHandler statementHandler = (RoutingStatementHandler)ivk.getTarget();
			BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper.getValueByFieldName(statementHandler, "delegate");
			MappedStatement mappedStatement = (MappedStatement) ReflectHelper.getValueByFieldName(delegate, "mappedStatement");//XML中<select></select>节点
			
			String id=mappedStatement.getId().toLowerCase();
			
			if(id.indexOf(PAGE_SQL_ID.toLowerCase())>=0){ //拦截需要分页的SQL，匹配xm中的id字段是否和拦截器中的字段匹配，进行约定式的拦截
				BoundSql boundSql = delegate.getBoundSql();//获取即将执行的SQL
				Object parameterObject = boundSql.getParameterObject();//分页SQL<select>中parameterType属性对应的实体参数，即Mapper接口中执行分页方法的参数,该参数不得为空
				if(parameterObject==null){
					throw new NullPointerException("parameterObject尚未实例化！");
				}else{
					Connection connection = (Connection) ivk.getArgs()[0];
					String sql = boundSql.getSql();//获取将要执行的sql
					log.debug("[<<venus log>>] use mybatis PagePlugin for page ,this sql is [{}]",sql);
					String countSql = "select count(1)  from (" + sql+ ")  tmp_count"; //总数sql
					PreparedStatement countStmt = connection.prepareStatement(countSql);
					BoundSql countBS = new BoundSql(mappedStatement.getConfiguration(),countSql,boundSql.getParameterMappings(),parameterObject);
					setParameters(countStmt,mappedStatement,countBS,parameterObject);
					ResultSet rs = countStmt.executeQuery();
					int records = 0;
					if (rs.next()) {
						records = rs.getInt(1);
					}
					rs.close();
					countStmt.close();
					log.debug("[<<venus log>>] execute  count sql to get  numbers of the date, the sql is {},  and number is {}",countSql,records);
					
					@SuppressWarnings("rawtypes")
					Pager pager=null;
					if(parameterObject instanceof Pager){//参数就是Page实体
						pager = (Pager) parameterObject;
						pager.setRecords(records);
					}
					String pageSql = generatePageSql(sql,pager);
					ReflectHelper.setValueByFieldName(boundSql, "sql", pageSql); //将分页sql语句反射回BoundSql.
				}
			}
		}
		return ivk.proceed();
	}

	
	/**
	 * 对SQL参数(?)设值,参考org.apache.ibatis.executor.parameter.DefaultParameterHandler
	 * 将全局配置和参数配置重新带入
	 * @param ps
	 * @param mappedStatement
	 * @param boundSql
	 * @param parameterObject
	 * @throws SQLException
	 */
	private void setParameters(PreparedStatement ps,MappedStatement mappedStatement,BoundSql boundSql,Object parameterObject) throws SQLException {
		ErrorContext.instance().activity("setting parameters").object(mappedStatement.getParameterMap().getId());
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (parameterMappings != null) {
			Configuration configuration = mappedStatement.getConfiguration();
			TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
			MetaObject metaObject = parameterObject == null ? null: configuration.newMetaObject(parameterObject);
			for (int i = 0; i < parameterMappings.size(); i++) {
				ParameterMapping parameterMapping = parameterMappings.get(i);
				if (parameterMapping.getMode() != ParameterMode.OUT) {
					Object value;
					String propertyName = parameterMapping.getProperty();
					PropertyTokenizer prop = new PropertyTokenizer(propertyName);
					if (parameterObject == null) {
						value = null;
					} else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
						value = parameterObject;
					} else if (boundSql.hasAdditionalParameter(propertyName)) {
						value = boundSql.getAdditionalParameter(propertyName);
					} else if (propertyName.startsWith(ForEachSqlNode.ITEM_PREFIX)&& boundSql.hasAdditionalParameter(prop.getName())) {
						value = boundSql.getAdditionalParameter(prop.getName());
						if (value != null) {
							value = configuration.newMetaObject(value).getValue(propertyName.substring(prop.getName().length()));
						}
					} else {
						value = metaObject == null ? null : metaObject.getValue(propertyName);
					}
					TypeHandler typeHandler = parameterMapping.getTypeHandler();
					if (typeHandler == null) {
						throw new ExecutorException("There was no TypeHandler found for parameter "+ propertyName + " of statement "+ mappedStatement.getId());
					}
					typeHandler.setParameter(ps, i + 1, value, parameterMapping.getJdbcType());
				}
			}
		}
	}
	
	/**
	 * 根据数据库方言，生成特定的分页sql
	 * @param sql
	 * @param page
	 * @return
	 */
	private String generatePageSql(String sql,Pager p){
		if(p!=null){
			if("postgresql".equals(DIALECT)){
				return getPageSqlForPostgresql(sql,p);
			}else if("oracle".equals(DIALECT)){
				return getPageSqlForOracle(sql,p);
			}else if("mysql".equals(DIALECT)){
				return getPageSqlForMySQL(sql,p);
			}
		}else{
			return sql;
		}
		return sql;
		
	}
	
	
	private static String getPageSqlForOracle(String  sql,Pager p){
		StringBuffer pageSql = new StringBuffer();
		pageSql.append("select * from (select tmp_tb.*,ROWNUM row_id from (");
		pageSql.append(sql);
		pageSql.append(") tmp_tb where ROWNUM<=");
		pageSql.append(p.getPage()*p.getPageSize());
		pageSql.append(") where row_id>");
		pageSql.append(p.getIndex());
		String sqlForPage= pageSql.toString();
		log.debug("[<<venus log>>] the  page  sql  of oracle   is {}",sqlForPage);
		return sqlForPage;
	}
	
	private static String getPageSqlForMySQL(String  sql,Pager p){
		StringBuffer pageSql = new StringBuffer();
		pageSql.append(sql);
		pageSql.append(" limit "+(p.getPage()-1)*p.getPageSize()+","+p.getPage()*p.getPageSize());
		String sqlForPage= pageSql.toString();
		log.debug("[<<venus log>>] the   page  sql  of mysql   is {}",sqlForPage);
		return pageSql.toString();
	}
	
	private static String getPageSqlForPostgresql(String  sql,Pager p){
		StringBuffer pageSql = new StringBuffer();
		pageSql.append("select * from ( ");
		pageSql.append(sql);
		pageSql.append(" limit ");
		pageSql.append(p.getPage()*p.getPageSize());
		pageSql.append(") tmp_tb offset ");
		pageSql.append(p.getIndex());
		String sqlForPage= pageSql.toString();
		log.debug("[<<venus log>>] the  page  sql  of postgresql   is {}",sqlForPage);
		return sqlForPage;
	}
	
	
	public Object plugin(Object arg0) {
		return Plugin.wrap(arg0, this);
	}

	public void setProperties(Properties p) {
		DIALECT = p.getProperty("dialect");
		if(DIALECT==null || DIALECT.equals("")){
			log.error("[<<venus log>>] dont set the dialect of plugins in WEB-INF/config/mybatis/mybatis-config");
			throw new NullPointerException("dialect property is not found!");	
		}
		PAGE_SQL_ID = p.getProperty("pageSqlId");
		if(PAGE_SQL_ID==null || PAGE_SQL_ID.equals("")){
			log.error("[<<venus log>>] dont set the PAGE_SQL_ID of plugins in WEB-INF/config/mybatis/mybatis-config");
			throw new NullPointerException("pageSqlId property is not found!");
			
		}
		
	}

}
