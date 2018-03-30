package com.cn.login;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cn.freamarker.session.RedisManager;
import com.cn.freamarker.util.SerializeUtils;
import com.cn.freamarker.web.controller.BaseController;

@RequestMapping("/login")
@Controller
public class LoginController extends BaseController{
	
	/** 
	 * 实际的登录代码 
	 * 如果登录成功，跳转至首页；登录失败，则将失败信息反馈对用户 
	 *  
	 * @param request 
	 * @param model 
	 * @return 
	 */  
	@RequestMapping(value = "/dologin")  
	public String doLogin(HttpServletRequest request, Model model) {  
	    String userName = request.getParameter("userName");  
	    String password = request.getParameter("password");  
	    System.out.println(userName);  
	    System.out.println(password);  
    	HttpSession session = request.getSession();
    	System.out.println(session.getId());
    	if("admin".equals(userName)){
    		Map<String,String> userBean = new HashMap<String,String>();
    		userBean.put("user", userName);
    		userBean.put("password", password);
    		session.setAttribute("userBean",userBean);
    		updateSession(session);
    		return "redirect:index.do";  
        } else {  
            return "redirect:/";  
        }  
	}  
	
	@RequestMapping("/index")
	public String index(){
//		System.out.println("request session id is "+getRequest().getSession().getId());
//		System.out.println("user is "+getRequest().getSession().getAttribute("userBean"));
		return "index";
	}
	
	private void updateSession(HttpSession session){
		RedisManager redisManager = new RedisManager();
		String keyPrefix = "shiro_redis_session:";
		String preKey = keyPrefix + session.getId();
		byte[] key = preKey.getBytes();
		byte[] value = SerializeUtils.serialize(session);
		redisManager.set(key, value);
		
	}
	
}
