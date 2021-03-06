package com.neusoft.web.handler.manage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.neusoft.util.persistence.DBPersistence;
import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.RequestData;
import com.neusoft.web.handler.ResponseData;
import com.neusoft.web.model.FilterHistoryModel;

@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}/mshistory")
public class MSHistoryHandler  extends Handler{

	@RequestMapping(value = "/tablelist")
	public ModelAndView tablelist(HttpServletRequest request ,@PathVariable String orgi,@ModelAttribute("data") RequestData data) throws ParseException {
		FilterHistoryModel tmp = new FilterHistoryModel();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//初始化日期为null
		Date begindate,endDate=null;
		if(request.getParameter("begintime")!=null && !"".equals(request.getParameter("begintime"))){
		     begindate = format.parse(request.getParameter("begintime") + " 00:00:00 ");
		     tmp.setBegintime(begindate);
		}
		if(request.getParameter("endtime")!=null && !"".equals(request.getParameter("endtime"))){
			 endDate = format.parse(request.getParameter("endtime") + " 23:59:59 ");
			 tmp.setEndtime(endDate);
		}
		tmp.setQuerytype(request.getParameter("querytype"));
		
		if(request.getParameter("agentno")!=null && !"".equals(request.getParameter("agentno").trim())){
			tmp.setAgentno(request.getParameter("agentno"));
		}
		
		if(request.getParameter("userid")!=null && !"".equals(request.getParameter("userid").trim())){
			tmp.setUserid(request.getParameter("userid"));
		}
		
		tmp.setOrgi(orgi);
		ResponseData responseData;
		if("agent".equals(request.getParameter("querytype"))||tmp.getQuerytype()==null||tmp.getQuerytype().equals("")){
			responseData = new ResponseData("/pages/manage/msghistory/msghistorylist");
		}else{
			responseData = new ResponseData("/pages/manage/msghistory/msghistorylistforUser");
		}
		tmp.setUser(super.getUser(request));
		responseData.setDataList(new DBPersistence().getsumByAgentOrUser(tmp, data.getP(), data.getPs()));
		ModelAndView view = request(responseData, orgi , data);
		view.addObject("condtion", tmp);
		view.addObject("begintime",request.getParameter("begintime"));
		view.addObject("endtime",request.getParameter("endtime"));
		return view ; 
		
	}
	@RequestMapping(value = "/msgmonitor")
	public ModelAndView msgmonitor(HttpServletRequest request ,@PathVariable String orgi,@ModelAttribute("data") RequestData data) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FilterHistoryModel tmp = new FilterHistoryModel();
		//初始化日期为null
		Date begindate,endDate=null;
		if(request.getParameter("begintime")!=null && !"".equals(request.getParameter("begintime"))){
		     begindate = format.parse(request.getParameter("begintime") + " 00:00:00 ");
		     tmp.setBegintime(begindate);
		}
		if(request.getParameter("endtime")!=null && !"".equals(request.getParameter("endtime"))){
			 endDate = format.parse(request.getParameter("endtime") + " 23:59:59 ");
			 tmp.setEndtime(endDate);
		}
		tmp.setOrgi(orgi);
		tmp.setChannel(request.getParameter("channel"));
		ResponseData responseData = new ResponseData("/pages/manage/msghistory/msgmonitorlist");
		responseData.setDataList(new DBPersistence().getsumByAgentOrUser(tmp,  data.getP(),data.getPs()));
		responseData.setResult("page");
		ModelAndView view = request(responseData, orgi , data);
		view.addObject("begintime",request.getParameter("begintime"));
		view.addObject("endtime",request.getParameter("endtime"));
		return view ; 
		
	}
	@RequestMapping(value = "/msgrealtime")
	public ModelAndView msgmonitorrealtime(HttpServletRequest request ,@PathVariable String orgi,@ModelAttribute("data") RequestData data) throws ParseException {
		ResponseData responseData = new ResponseData("/pages/manage/msghistory/msgmonitorlist"  ) ;
//		responseData.setDataList(new DBPersistence().getMsgMonitor(tmp,1,1));
		responseData.setDataList(new DBPersistence().getMsgMonitor(orgi,request.getParameter("channel")));
//		List<Object> userlist=Arrays.asList(ServiceQueue.getUserQueue().get(orgi).values().toArray());
		return request(responseData , orgi , data) ;
	}

}
