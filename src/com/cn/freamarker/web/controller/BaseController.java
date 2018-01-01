package com.cn.freamarker.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class BaseController {


	private HttpServletRequest request;
	@SuppressWarnings("unused")
	private HttpServletResponse response;
	
	public String RESULT_KEY = "resultType";
	public String IS_SUCCESS = "isSuccess";
	
	/**
	 * 得到request对象
	 */
	public HttpServletRequest getRequest() {
		return  request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
	}
	
	/**
	 * 得到response对象
	 */
	public HttpServletResponse getResponse() {
		return  response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
	}
	
	/***
	 * 方法名：getParameter
	 * 描述  ：
	 *	获取jsp传递过来的值
	 * @param key
	 * @return
	 */
	public String getParameter(String key){
		if(StringUtils.isEmpty(key)) return null;
		return getRequest().getParameter(key);
	}
	
	/***
	 * 方法名：getParameter
	 * 描述  ：
	 *	设置request的attribuate
	 * @param key
	 * @return
	 */
	public void setAttribuate(String key,Object o){
		getRequest().setAttribute(key, o);
	}
	
	
	/**
     * 兼容$.ajaxFileUpload 的回调函数,调用此方法的控制器不用设置返回值
     * 
     * @param response
     *            HttpServletResponse 对象
     * @param responseObject
     *            推荐使用HashMap<String,String>
     * @throws BusinessException
     */
    public void responseWithJsonString(final HttpServletResponse response, final Object responseObject)
            throws RuntimeException {
        /*final JSONObject responseJSONObject = JSONObject.fromObject(responseObject);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.append(responseJSONObject.toString());
        } catch (final IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if (out != null) {
                out.close();
            }
        }*/
    }
	
	
	@ExceptionHandler
	public String ExceptionHandler(HttpServletRequest request,Exception ex){
		ex.printStackTrace();
		return  "error/exception";
	}
	
	/**
	 * PrintWriter 代理，用于错误日志的输出
	 * @author zhuhme
	 *
	 */
	public class StringPrintWriter extends PrintWriter{
		public StringPrintWriter(){  
	        super(new StringWriter());  
	    }  
	     
	    public StringPrintWriter(int initialSize) {  
	          super(new StringWriter(initialSize));  
	    }  
	     
	    public String getString() {  
	          flush();  
	          return ((StringWriter) this.out).toString();  
	    }  
	     
	    @Override  
	    public String toString() {  
	        return getString();  
	    }  
	}
}
