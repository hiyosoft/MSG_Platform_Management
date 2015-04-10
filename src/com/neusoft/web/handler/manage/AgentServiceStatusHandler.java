package com.neusoft.web.handler.manage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.neusoft.core.EapDataContext;
import com.neusoft.util.persistence.DBPersistence;
import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.RequestData;
import com.neusoft.web.handler.ResponseData;
import com.neusoft.web.model.AgentServiceStatus;
import com.neusoft.web.model.FilterHistoryModel;
@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}/agentservice")
@SuppressWarnings("unchecked")
public class AgentServiceStatusHandler extends Handler {
	@RequestMapping(value = "/list")
	public ModelAndView list(HttpServletRequest request, @PathVariable String orgi,@ModelAttribute("data") RequestData data) throws ParseException {
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
		
		if(request.getParameter("operatetype")!=null && !"".equals(request.getParameter("operatetype").trim())){
			tmp.setUserid(request.getParameter("operatetype"));
		}
		
		tmp.setOrgi(orgi);
		ResponseData responseData;
		responseData = new ResponseData("/pages/manage/agentservice/servicehistorylist");
		tmp.setUser(super.getUser(request));
		responseData.setDataList(listByQuery(tmp, data.getP(), data.getPs()));
		ModelAndView view = request(responseData, orgi , data);
		view.addObject("condtion", tmp);
		view.addObject("begintime",request.getParameter("begintime"));
		view.addObject("endtime",request.getParameter("endtime"));
		return view ;
	}
	public List<?> listByQuery(FilterHistoryModel filter, int p, int ps) {
		DetachedCriteria dr=DetachedCriteria.forClass(AgentServiceStatus.class);
		Criterion temp=Restrictions.eq("orgi",filter.getOrgi());
		SimpleExpression agenno=Restrictions.like("agentno", "%"+filter.getAgentno()+"%");
		SimpleExpression begintime=Restrictions.ge("operatetime", filter.getBegintime());
		SimpleExpression endtime=Restrictions.le("operatetime", filter.getEndtime());
		Criterion bothtime=Restrictions.between("operatetime", filter.getBegintime(), filter.getEndtime());
		SimpleExpression userid=Restrictions.eq("operatetype", filter.getUserid());
		SimpleExpression exctype=Restrictions.ne("operatetype", "3");
		Order order=Order.desc("createtime");
		if("agent".equals(filter.getQuerytype())&&filter.getAgentno()!=null&&!"".equals(filter.getAgentno())){
			temp=Restrictions.and(agenno, temp);
		}else if(filter.getUserid()!=null&&!"".equals(filter.getUserid())){
			temp=Restrictions.and(temp, userid);
		}
		if(filter.getBegintime()!=null&&filter.getEndtime()==null){
			temp=Restrictions.and(temp, begintime);
		}else if(filter.getBegintime()==null&&filter.getEndtime()!=null){
			temp=Restrictions.and(temp, endtime);
		}else if(filter.getBegintime()!=null&&filter.getEndtime()!=null){
			temp=Restrictions.and(temp, bothtime);
		}
		temp=Restrictions.and(exctype, temp);
		dr.add(temp);
		dr.addOrder(order);
		return EapDataContext.getService().findPageByCriteria(dr,ps,p);
	}
}
