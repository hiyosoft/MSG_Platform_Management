package com.neusoft.web.interceptor;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  
  

import org.springframework.web.servlet.HandlerExceptionResolver;  
import org.springframework.web.servlet.ModelAndView;  

import com.neusoft.web.handler.ResponseData;
  
public class ExceptionHandler implements HandlerExceptionResolver {  
  
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,  
            Exception ex) {  
    	ex.printStackTrace();
    	ResponseData data = new ResponseData("error" , ex!=null && ex.getMessage()!=null ? ex.getMessage() : ex!=null && ex.getCause()!=null ? ex.getCause().getMessage():ex.getClass().toString()) ;
    	ModelAndView mv = new ModelAndView("error","data" , data);  
    	if(data.getError()!=null){
			StringBuffer strb = new StringBuffer() , errormsg = new StringBuffer("执行过程中出现错误，提示信息如下：") ;
			errormsg.append(data.getError()) ;
			data.setError(errormsg.toString()) ;
			for(int i=0 ; i<data.getError().length() ; i++){
				if(data.getError().charAt(i)>=19968 && data.getError().charAt(i)<=40959){
					strb.append("&#").append((int)data.getError().charAt(i)).append(";") ;
				}else{
					strb.append(data.getError().charAt(i)) ;
				}
			}
			response.addHeader("emsg", strb.toString()) ;
			response.setCharacterEncoding("UTF-8") ;
			response.setContentType("text/html,charset=UTF-8") ;
			if(data.isThrowError()){
				try {
					response.sendError(400 , data.getError());
				} catch (IOException e) {
					e.printStackTrace();
				}
				mv.setViewName("/pages/public/error");
			}
		}
        return mv ;
    }  
}  
