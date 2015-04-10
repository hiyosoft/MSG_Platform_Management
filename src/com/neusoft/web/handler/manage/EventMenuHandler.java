package com.neusoft.web.handler.manage;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.RequestData;
import com.neusoft.web.handler.ResponseData;
import com.neusoft.web.model.AgentInfo;
import com.neusoft.web.model.AgentSkill;
import com.neusoft.web.model.EventMenu;
import com.neusoft.web.model.Instruction;
import com.neusoft.web.model.Material;
import com.neusoft.web.model.User;

@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}")
@SuppressWarnings("unchecked")
public class EventMenuHandler extends Handler {
	@RequestMapping(value = "/emenu/add/{parentid}/{emenutype}")
	public ModelAndView add(HttpServletRequest request, @PathVariable String orgi, @PathVariable String parentid,@PathVariable String emenutype) {
		ResponseData responseData = new ResponseData("/pages/manage/eventmenu/emenuadd", "/pages/include/iframeindex");
		ModelAndView view = request(responseData, orgi, null);
		view.addObject("type",emenutype);  
		return view;
	}
	@RequestMapping(value = "/emenu/adddo")
	public ModelAndView adddo(HttpServletRequest request, @PathVariable String orgi,@ModelAttribute EventMenu data) {
		data.setUserid(super.getUser(request).getId());
		data.setUsername(super.getUser(request).getUsername());
		data.setOrgi(orgi);
		super.getService().saveIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/emenulist/add/{parentid}/{emenutype}")
	public ModelAndView emanuadd(HttpServletRequest request, @PathVariable String orgi, @PathVariable String parentid, @PathVariable String emenutype) {
		ResponseData responseData = new ResponseData("/pages/manage/eventmenu/emenuchildadd", "/pages/include/iframeindex");
		responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(Material.class).add(Restrictions.eq("orgi", orgi))));
		responseData.setValueList(EapDataContext.getPluginList(EapDataContext.PluginType.INSTRUCTION.toString()));
		ModelAndView view = request(responseData, orgi, null);
		view.addObject("parent",parentid); 
		view.addObject("type",emenutype); 
		return view;
	}
	@RequestMapping(value = "/emenulist/adddo")
	public ModelAndView emanuadddo(HttpServletRequest request, @PathVariable String orgi,@ModelAttribute EventMenu data) {
		data.setUserid(super.getUser(request).getId());
		data.setUsername(super.getUser(request).getUsername());
		data.setOrgi(orgi);
		super.getService().saveIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/emenulist/edit/{emenuid}")
	public ModelAndView emenulistedit(HttpServletRequest request, @PathVariable String orgi,@PathVariable String emenuid,@ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/eventmenu/emenuchildedit" , "/pages/include/iframeindex" , data.getP() , EapDataContext.getPluginList(EapDataContext.PluginType.INSTRUCTION.toString()));
		responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(Material.class).add(Restrictions.eq("orgi", orgi))));
		responseData.setData(super.getService().getIObjectByPK(EventMenu.class, emenuid));
		ModelAndView view = request(responseData, orgi ,data) ; 
		view.addObject("parent", request.getParameter("parent")) ;
		view.addObject("type", request.getParameter("type")) ;
		return request(responseData, orgi, data);
	}
	@RequestMapping(value = "/emenulist/editdo")
    public ModelAndView emenulisteditdo(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") EventMenu data) {
		data.setCreatetime(new Date());
		data.setUsername(super.getUser(request).getUsername());
		data.setUserid(super.getUser(request).getId()) ;
		data.setStatus("1") ;
		data.setParent(data.getParent()==null ? "" : data.getParent()) ;
		super.getService().updateIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success"  ) ;
		return request(responseData, orgi , null) ; 
    }
	@RequestMapping(value = "/emenulist/rm/{emenutype}/{emenuid}")
	public ModelAndView emenulistrm(HttpServletRequest request, @PathVariable String orgi,@PathVariable String emenutype,@PathVariable String emenuid, @ModelAttribute("data") EventMenu data) {
		data.setId(emenuid);
		super.getService().deleteIObject(data);
		ResponseData responseData = new ResponseData("/pages/manage/eventmenu/eventmenulist");
		String parent = request.getParameter("parent");
		responseData.setResult(emenuid+"/"+emenutype);
		responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(EventMenu.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("parent", parent)))));
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/emenulist/tablelist/{emenuid}/{emenutype}")
	public ModelAndView emnutablelist(HttpServletRequest request, @PathVariable String orgi, @PathVariable String emenuid, @PathVariable String emenutype, @ModelAttribute("data") RequestData data) {
 		ResponseData responseData = new ResponseData("/pages/manage/eventmenu/eventmenulist");
 		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(EventMenu.class).add(Restrictions.and(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("type", emenutype)), Restrictions.eq("parent", emenuid))),data.getPs(),data.getP()));
		return request(responseData, orgi, data);
	}
	@RequestMapping(value = "/emenu/rm/{emenutype}/{emenuid}")
	public ModelAndView rm(HttpServletRequest request, @PathVariable String orgi,@PathVariable String emenutype,@PathVariable String emenuid, @ModelAttribute("data") EventMenu data) {
		data.setId(emenuid);
		super.getService().deleteIObject(data);
		ResponseData responseData = new ResponseData("/pages/manage/eventmenu");
		responseData.setResult(emenuid+"/"+emenutype);
		responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(EventMenu.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("parent", "0")))));
		return request(responseData, orgi, null);
	}
	
	@RequestMapping(value = "/emenu/tablelist/{emenutype}")
	public ModelAndView tablelist(HttpServletRequest request, @PathVariable String orgi, @PathVariable String emenutype, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/eventmenu/leftmenulist");
	    responseData.setResult(emenutype);
		responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(EventMenu.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("parent", "0")))));
		return request(responseData, orgi, data);
	}
	@RequestMapping(value = "/emenu/edit/{emenuid}")
	public ModelAndView edit(HttpServletRequest request, @PathVariable String orgi,@PathVariable String emenuid) {
		ResponseData responseData = new ResponseData("/pages/manage/eventmenu/emenuedit", "/pages/include/iframeindex");
		responseData.setData(super.getService().getIObjectByPK(EventMenu.class, emenuid));
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/emenu/editdo")
	public ModelAndView editdo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") EventMenu data) {
		super.getService().updateIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/emenuitem/add/{emenutype}")
	public ModelAndView emenuitemadd(HttpServletRequest request, @PathVariable String orgi, @PathVariable String emenutype) {
		ResponseData responseData = new ResponseData("/pages/manage/eventmenu/emenuadd", "/pages/include/iframeindex");
		responseData.setValueList(EapDataContext.getPluginList(EapDataContext.PluginType.INSTRUCTION.toString()));
		ModelAndView view = request(responseData, orgi, null);
		view.addObject("type",emenutype);  
		return view;
	}
	@RequestMapping(value = "/emenulist/search")
	public ModelAndView search(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("rqdata") EventMenu rqdata, @ModelAttribute("data") RequestData data) {
		String key = "%" + rqdata.getName() + "%";
		String parent = request.getParameter("parentid").split("/")[0];
		String emenutype = request.getParameter("parentid").split("/")[1];
		ResponseData responseData = new ResponseData("/pages/manage/eventmenu/eventmenulist");
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(EventMenu.class).add(Restrictions.and(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.like("name", key)), Restrictions.eq("parent", parent))),data.getPs(),data.getP()));
		responseData.setResult(parent+"/"+emenutype);
		return request(responseData, orgi, null);
	}
}
