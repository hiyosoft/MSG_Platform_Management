package com.neusoft.web.handler.manage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.neusoft.core.EapDataContext;
import com.neusoft.util.queue.AgentStatus;
import com.neusoft.util.queue.ServiceQueue;
import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.RequestData;
import com.neusoft.web.handler.ResponseData;
import com.neusoft.web.model.RuntimeData;

@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}/monitor")
public class MonitorHandler  extends Handler{

	@RequestMapping(value = "/index")
    public ModelAndView index(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/monitor/monitor") ; 
		ModelAndView view = request(responseData, orgi, data) ;
		view.addObject("runtime", EapDataContext.getRuntimeData()) ;	
		return view;
    }
	
	@RequestMapping(value = "/system")
    public ModelAndView system(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/monitor/system") ; 
		ModelAndView view = request(responseData, orgi, data) ;
		view.addObject("runtime", EapDataContext.getRuntimeData()) ; ;
		return view;
    }
	
	@RequestMapping(value = "/system/reset")
    public ModelAndView reset(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/monitor/system") ; 
		ModelAndView view = request(responseData, orgi, data) ;
		view.addObject("runtime", EapDataContext.setRuntimeData()) ;
		return view;
    }
	/**
	 * 获取用户列表
	 * @param request
	 * @param orgi
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/users")
    public ModelAndView users(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/monitor/agentList") ; 
		ModelAndView view = request(responseData, orgi, data) ;
		List<AgentStatus> list = new ArrayList<AgentStatus>();
		 if(ServiceQueue.getAgentQueue().size()>0){
			 Iterator iter = ServiceQueue.getAgentQueue().keySet().iterator();   
			//获得map的Iterator
			while(iter.hasNext()) {
				 String key = iter.next().toString();   
				            list.add(ServiceQueue.getAgentQueue().get(key));   
				}
		 }
		view.addObject("userlist", list) ;
		return view;
    }
	
}
