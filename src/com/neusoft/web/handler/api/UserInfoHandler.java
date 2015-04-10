package com.neusoft.web.handler.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.RequestData;
import com.neusoft.web.handler.ResponseData;
import com.neusoft.web.model.AgentInfo;
import com.neusoft.web.model.AgentSkill;
import com.neusoft.web.model.FAQModel;
import com.neusoft.web.model.SearchResultTemplet;
import com.neusoft.web.model.SinosigUser;
import com.neusoft.web.model.User;

@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}/api/user")
@SuppressWarnings("unchecked")
public class UserInfoHandler extends Handler {
	
	@RequestMapping(value = "/info/{apiusername}")
	public ModelAndView add(HttpServletRequest request, @PathVariable String orgi , @PathVariable String apiusername ) {
		ResponseData responseData = null ;
		if("xml".equals(request.getParameter("wt"))){
			responseData = new ResponseData("/pages/manage/sinosig/api/user/info_xml");
		}else{
			responseData = new ResponseData("/pages/manage/sinosig/api/user/info_json");
		}
		ModelAndView view = request(responseData, orgi, null);
		view.addObject("data", super.getService().getIObjectByPK(SinosigUser.class, apiusername) ) ;
		return view;
	}
	
}
