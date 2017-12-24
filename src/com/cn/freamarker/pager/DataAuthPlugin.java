package com.cn.freamarker.pager;

import java.sql.Connection;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.statement.BaseStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

/**
 * 数据权限拦截
 * @author zengcx
 *
 */
@Intercepts({@Signature(type=StatementHandler.class,method="prepare",args={Connection.class})})
public class DataAuthPlugin implements Interceptor{
	private static Log log = LogFactory.getLog(DataAuthPlugin.class);
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		//if(invocation.getTarget() instanceof RoutingStatementHandler){
		if(invocation.getTarget() instanceof StatementHandler){
			StatementHandler statementHandler = (StatementHandler)invocation.getTarget();
			Plugin plu = (Plugin) ReflectHelper.getValueByFieldName(statementHandler, "h");
			RoutingStatementHandler  routingStatementHandler = (RoutingStatementHandler) ReflectHelper.getValueByFieldName(plu, "target");
	        //通过反射获取到当前RoutingStatementHandler对象的delegate属性  
	        BaseStatementHandler delegate = (BaseStatementHandler) ReflectHelper.getValueByFieldName(routingStatementHandler, "delegate");
	        MappedStatement mappedStatement = (MappedStatement) ReflectHelper.getValueByFieldName(delegate, "mappedStatement");//XML
	        String sqlType = mappedStatement.getSqlCommandType()+"";
	        if(sqlType.toUpperCase().equals("SELECT")){//数据查询时候进行权限过滤
	        	BoundSql boundSql = delegate.getBoundSql();//获取即将执行的SQL
	        	String sql = boundSql.getSql();
	        	Object parameterObject = boundSql.getParameterObject();
	        	if(parameterObject instanceof Map){
	        	}
				sql = sql.toString().replaceAll("\\s+", " ");
				
				ReflectHelper.setValueByFieldName(boundSql, "sql", sql); //将分页sql语句反射回BoundSql.
	        }
	        if(true){
	        	BoundSql boundSqldd = delegate.getBoundSql();
	        	String sqldd = boundSqldd.getSql(); 
	        	log.info(sqldd+">>>>>>"+boundSqldd.getParameterObject());
	        }
			}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object obj) {
		return Plugin.wrap(obj, this);
	}

	@Override
	public void setProperties(Properties pro) {
		// TODO Auto-generated method stub
		
	}

}
