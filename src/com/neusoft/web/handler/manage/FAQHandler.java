package com.neusoft.web.handler.manage;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.neusoft.util.tools.IndexTools;
import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.RequestData;
import com.neusoft.web.handler.ResponseData;
import com.neusoft.web.model.AgentInfo;
import com.neusoft.web.model.AgentSkill;
import com.neusoft.web.model.FAQModel;
import com.neusoft.web.model.User;

@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}/faq")
@SuppressWarnings("unchecked")
public class FAQHandler extends Handler{
	@RequestMapping(value = "/add/{skillid}")
	public ModelAndView add(HttpServletRequest request, @PathVariable String orgi,@PathVariable String skillid) {
		ResponseData responseData = new ResponseData("/pages/manage/faq/faqadd", "/pages/include/iframeindex");
		responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(AgentSkill.class)));
		ModelAndView view = request(responseData, orgi, null);
		view.addObject("skillid",skillid);
		return view;
	}
	@RequestMapping(value = "/adddo")
	public ModelAndView agentadddo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") FAQModel data) {
		data.setCreateuser(super.getUser(request).getUsername());
		data.setOrgi(orgi);
		super.getService().saveIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success");
		try {
			IndexTools.getInstance().faq(data) ;
			IndexTools.getInstance().commit() ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/tablelist/{skillid}")
	public ModelAndView list(HttpServletRequest request, @PathVariable String orgi,@PathVariable String skillid, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/faq/faqlist");
		responseData.setResult(skillid);
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(FAQModel.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("skillid", skillid))),data.getPs(),data.getP()));
		return request(responseData, orgi, data);
	}
	@RequestMapping(value = "/rm/{skillid}/{faqid}")
	public ModelAndView agentinform(HttpServletRequest request, @PathVariable String orgi,@PathVariable String skillid,@PathVariable String faqid, @ModelAttribute("data") FAQModel data) {
		data.setId(faqid);
		ResponseData responseData = new ResponseData("/pages/manage/faq/faqlist");
		super.getService().deleteIObject(data);
		try {
			IndexTools.getInstance().delete(data.getId()) ;
			IndexTools.getInstance().commit() ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		responseData.setResult(skillid);
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(FAQModel.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("skillid", skillid)))));
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/edit/{faqid}")
	public ModelAndView edit(HttpServletRequest request, @PathVariable String orgi,@PathVariable String faqid,@ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/faq/faqedit", "/pages/include/iframeindex");
		responseData.setData(super.getService().getIObjectByPK(FAQModel.class, faqid));
		//技能组下拉列表
		responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(AgentSkill.class).add(Restrictions.eq("orgi", orgi))));
		return request(responseData, orgi, data);
	}
	@RequestMapping(value = "/editdo")
	public ModelAndView editdo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") FAQModel data) {
		super.getService().updateIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success");
		try {
			IndexTools.getInstance().faq(data) ;
			IndexTools.getInstance().commit() ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/search")
	public ModelAndView search(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") FAQModel data) {
		String key = "%" + data.getTitle() + "%";
		ResponseData responseData = new ResponseData("/pages/manage/faq/faqlist");
		responseData.setResult(data.getSkillid());
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(FAQModel.class).add(Restrictions.and(Restrictions.eq("skillid", data.getSkillid()) ,Restrictions.like("title", key)))));
		return request(responseData, orgi, null);
	}
}
