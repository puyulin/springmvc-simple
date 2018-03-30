package com.cn.freamarker.session;

import java.io.Serializable;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.shiro.session.Session;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;

@SuppressWarnings("deprecation")
public class CustomSession implements HttpSession,Serializable {

	private static final long serialVersionUID = 8516965043489129247L;

	public static final String DEFAULT_SESSION_ID_NAME = "JSESSIONID";
	
	private Session session;
	
	
	public CustomSession(Session session,HttpServletRequest currentRequest) {
		this.session = session;
	}
	
	@Override
	public long getCreationTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getId() {
		return session.getId().toString();
	}

	@Override
	public long getLastAccessedTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ServletContext getServletContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getMaxInactiveInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return session.getAttribute(name);
	}

	@Override
	public Object getValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getValueNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(String name, Object value) {
		session.setAttribute(name, value);
	}

	@Override
	public void putValue(String name, Object value) {
		session.setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		session.removeAttribute(name);
	}

	@Override
	public void removeValue(String name) {
		session.removeAttribute(name);
	}

	@Override
	public void invalidate() {
		
	}

	@Override
	public boolean isNew() {
		/*Boolean value = (Boolean) currentRequest.getAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_IS_NEW);
        return value != null && value.equals(Boolean.TRUE);*/
		return false;
	}

}
