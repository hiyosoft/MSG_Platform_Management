package com.neusoft.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.neusoft.core.EapDataContext;

public class UserInterceptorHandler extends HandlerInterceptorAdapter {
	private String exclude  ;
	@Override  
    public boolean preHandle(HttpServletRequest request,  
            HttpServletResponse response, Object handler) throws Exception {  
        String uri = request.getRequestURI();  
        boolean filter = false; 
        for (String s : exclude.split(",")) {  
            if (uri.matches(s)) {  
            	filter = true;  
                break;  
            }  
        }  
        if(!filter){
        	filter = request.getSession().getAttribute(EapDataContext.USER_SESSION_NAME)!=null ;
        }else{}
        if(!filter && uri.indexOf("/",1)>=0){
        	response.sendRedirect("/");
        }
        return filter ;  
    }
	public String getExclude() {
		return exclude;
	}
	public void setExclude(String exclude) {
		this.exclude = exclude;
	}  
}
