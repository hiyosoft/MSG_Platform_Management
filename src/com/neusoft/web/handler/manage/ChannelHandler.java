package com.neusoft.web.handler.manage;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.RequestData;
import com.neusoft.web.handler.ResponseData;
import com.neusoft.web.model.SNSAccount;

@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}/channel")
public class ChannelHandler  extends Handler{

	@RequestMapping(value = "/changetype/{type}")
    public ModelAndView changetype(HttpServletRequest request ,@PathVariable String orgi,@PathVariable String type, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/channel/"+type+"tablelist");
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(SNSAccount.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.eq("snstype", type))),data.getPs(),data.getP()));
		return request(responseData, orgi , data) ; 
    }
	
	@RequestMapping(value = "/{type}/add")
    public ModelAndView weixinadd(HttpServletRequest request ,@PathVariable String orgi,@PathVariable String type, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/channel/"+type+"add" ,"/pages/include/iframeindex") ; 
		return request(responseData, orgi , data) ;
    }
	
	@RequestMapping(value = "/{type}/adddo",method = RequestMethod.POST)
    public ModelAndView snsaccountaddo(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") SNSAccount data,@ModelAttribute("rqdata") RequestData rqdata) {
		data.setCreatetime(new Date());
		data.setOrgi(orgi);
		super.getService().saveIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success") ;
		return request(responseData, orgi , rqdata) ; 
    }
	
	@RequestMapping(value = "/tablelist/{type}")
    public ModelAndView snsweixinlist(HttpServletRequest request ,@PathVariable String orgi,@PathVariable String type, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/channel/"+type+"tablelist");
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(SNSAccount.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.eq("snstype", type))),data.getPs(),data.getP()));
		return request(responseData, orgi , data) ; 
    }
	
	@RequestMapping(value = "/{type}/edit/{userid}")
    public ModelAndView snsaccountedit(HttpServletRequest request ,@PathVariable String orgi,@PathVariable String type,@PathVariable String userid, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/channel/"+type+"edit","/pages/include/iframeindex");
		responseData.setData(super.getService().getIObjectByPK(SNSAccount.class, userid));
		return request(responseData, orgi , data) ; 
    }
	
	@RequestMapping(value = "/{type}/edit/editdo/editdo")
    public ModelAndView snsaccounteditdo(HttpServletRequest request ,@PathVariable String orgi,@PathVariable String type, @ModelAttribute("data") SNSAccount data,@ModelAttribute("rqdata") RequestData rqdata) {
		SNSAccount hadsaveObject =(SNSAccount)super.getService().getIObjectByPK(SNSAccount.class, data.getId());
		data.setCreatetime(hadsaveObject.getCreatetime());
		data.setOrgi(orgi);
		data.setSnstype(type);
		super.getService().updateIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success") ;
		return request(responseData, orgi , rqdata) ; 
    }
	
	@RequestMapping(value = "/rm/{userid}")
    public ModelAndView snsaccountrm(HttpServletRequest request ,@PathVariable String orgi, @PathVariable String userid, @ModelAttribute("data") SNSAccount data) {
		data.setId(userid);
		super.getService().deleteIObject(data) ;
		ResponseData responseData = new ResponseData("/pages/manage/channel/"+request.getParameter("snstype")+"tablelist");
		responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(SNSAccount.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.eq("snstype", data.getSnstype())))));
		return request(responseData, orgi , null) ; 
    }

}
