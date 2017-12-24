package com.cn.freamarker.pager;


import java.io.Serializable;
import java.util.List;

/**
 * @author zhuhme
 * 为了使用方便使用前段的jgGrid，使用Pager作为分页数据的命名
 * @param <T>
 */
public class Pager<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9027461908834397651L;
	
	private int total;

	private int page = 1;

	private int pageSize = 10;

	private int records;

	private List<T> data;

	private String sord = "desc";
	
	private int index=0;//获取记录的开始索引
	
	private Object queryObject;
	

	

	public Object getQueryObject() {
		return queryObject;
	}

	public void setQueryObject(Object queryObject) {
		this.queryObject = queryObject;
	}

	/**
	 * 获取总页数
	 * 
	 * @return
	 */
	public int getTotal() {
		//转换计算结果为double，避免，Math.ceil(1/2)结果为0的情况
		if(pageSize==0){
			this.pageSize=20;
		}
		this.total = (int) Math.ceil((this.records+0.0) / this.pageSize);
		return this.total;
	}

	/**
	 * 获取当前页码
	 * 
	 * @return
	 */
	public int getPage() {
		return this.page;
	}

	/**
	 * 设置当前页码
	 * 
	 * @param paramInt
	 */
	public void setPage(int paramInt) {
		this.page = paramInt;
	}

	/**
	 * 获取记录总条数
	 * 
	 * @return
	 */
	public int getRecords() {
		return this.records;
	}

	/**
	 * 设置总记录数
	 * 
	 * @param paramInt
	 */
	public void setRecords(int paramInt) {
		this.records = paramInt;
	}

	/**
	 * 获取分页数据
	 * 
	 * @return
	 */
	public List<T> getData() {
		return this.data;
	}

	/**
	 * 设置分页数据
	 * 
	 * @param paramList
	 */
	public void setData(List<T> paramList) {
		this.data = paramList;
	}

	/**
	 * 获取每页多少条
	 * 
	 * @return
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	/**
	 * 设置每页多少条
	 * 
	 * @param paramInt
	 */
	public void setPageSize(int paramInt) {
		this.pageSize = paramInt;
	}
	
	
	/**
	 * 设置每页多少条,jqgrid请求的参数为rows
	 * 
	 * @param paramInt
	 */
	public void setRows(int paramInt) {
		this.pageSize = paramInt;
		
		
		
	}
	public int getIndex() {
		this.index = (this.page-1)*this.pageSize;
		if(this.index<0)
			this.index = 0;
		return this.index;
	}



	/**
	 * 获取排序信息
	 * @return
	 */
	public String getSord() {
		return this.sord;
	}

	public void setSord(String paramString) {
		if (("asc".equalsIgnoreCase(paramString))
				|| ("desc".equalsIgnoreCase(paramString))) {
			this.sord = paramString;
			return;
		}
		this.sord = "asc";
	}
}
