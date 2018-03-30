package com.cn.freamarker.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.SerializationUtils;

import com.cn.freamarker.CustomHttpSessionRequest;
import com.cn.freamarker.session.CustomSession;
import com.cn.freamarker.session.RedisManager;

public class CustomRequestFilter implements Filter{
	
	private RedisManager redisManager = new RedisManager();

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		 HttpServletRequest req = (HttpServletRequest) request;
		 HttpServletResponse rep = (HttpServletResponse)response;
		 Cookie[] cookies = req.getCookies();
		 CustomSession session = null;
		 if(cookies!=null){
			 for (Cookie cookie : cookies) {
				 System.out.println(cookie.getName()+"----"+cookie.getValue());
				 if("JSESSIONID".equals(cookie.getName())){
					 session  = getRedisSession(cookie.getValue());
				 }
			 }
		 }
         // 将request对象替换成自定义的wrapper对象
		 
		 
         req = new CustomHttpSessionRequest(req ,session);
         Cookie cookie = new Cookie("JSESSIONID", req.getSession().getId());
         rep.addCookie(cookie);
         chain.doFilter(req, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
	
	
	private CustomSession getRedisSession(String sessionId){
		String keyPrefix = "shiro_redis_session:";
		String preKey = keyPrefix + sessionId;
		byte[] key = preKey.getBytes();
		byte[] value = redisManager.get(key);
		CustomSession session = (CustomSession)SerializationUtils.deserialize(value);
		return session;
	}

}
