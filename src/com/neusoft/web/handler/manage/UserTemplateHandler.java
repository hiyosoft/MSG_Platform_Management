package com.neusoft.web.handler.manage;

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
import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.RequestData;
import com.neusoft.web.handler.ResponseData;
import com.neusoft.web.model.ExtensionPoints;
import com.neusoft.web.model.UserTemplet;

import edu.emory.mathcs.backport.java.util.Arrays;

@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}/usertemplet")
@SuppressWarnings("unchecked")
public class UserTemplateHandler extends Handler {

	@RequestMapping(value = "/tablelist/{channel}")
	public ModelAndView tablelist(HttpServletRequest request, @PathVariable String orgi, @PathVariable String channel,
			@ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData(null,"/pages/manage/usertemplet/tablelist",super.getService().findPageByCriteria(DetachedCriteria.forClass(UserTemplet.class).add(Restrictions.and(Restrictions.eq("orgi", orgi),Restrictions.eq("channel", channel))),  data.getPs(),data.getP()));
		responseData.setResult(channel);
		return request(responseData, orgi, data);
	}

	@RequestMapping(value = "/changetype/{etpid}")
	public ModelAndView changetype(HttpServletRequest request, @PathVariable String orgi, @PathVariable String etpid,
			@ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/extensionpoint/tablelist", super.getService().findPageByCriteria(
				DetachedCriteria.forClass(ExtensionPoints.class).add(Restrictions.and(Restrictions.eq("orgi", orgi),
						Restrictions.eq("extensiontype", EapDataContext.PluginType.INSTRUCTION.toString()))), data.getPs(), data.getP()), data);
		responseData.setResult(etpid);
		return request(responseData, orgi, data);
	}

	@RequestMapping(value = "/add/{templatetype}/{channel}")
	public ModelAndView add(HttpServletRequest request, @PathVariable String orgi,@PathVariable String templatetype, @PathVariable String channel,
			@ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/usertemplet/add", "/pages/include/iframeindex");
		responseData.setDataList(Arrays.asList(EapDataContext.ChannelTypeEnum.class.getEnumConstants()));
		ModelAndView view = request(responseData, orgi, data);
		view.addObject("templatetype", templatetype);
		view.addObject("channel", channel);
		return view;
	}

	@RequestMapping(value = "/adddo")
	public ModelAndView adddo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") UserTemplet data) {
		List list=super.getService().findAllByCriteria(DetachedCriteria.forClass(UserTemplet.class).add(Restrictions.and(Restrictions.eq("orgi", orgi),Restrictions.eq("code", data.getCode()))));
		if(list!=null&&list.size()<1){
			data.setOrgi(orgi);
			super.getService().saveIObject(data);
		}else{
			return request(new ResponseData("redirect://tablelist/"+data.getChannel()+".html" , "代码 "+data.getCode()+" 已存在，请重新输入" , true , null), orgi, null) ;
		}
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}

	@RequestMapping(value = "/rm/{templateid}")
	public ModelAndView rm(HttpServletRequest request, @PathVariable String orgi, @PathVariable String templateid,
			@ModelAttribute("data") UserTemplet data) {
		List<ExtensionPoints> plugins = super.getService().findAllByCriteria(DetachedCriteria.forClass(ExtensionPoints.class).add(Restrictions.eq("iconimagepath", data.getCode()))) ;
		if(plugins!=null&&plugins.size()>0){
			ResponseData responseData=new ResponseData("redirect:/{orgi}/usertemplet/changetype/"+data.getChannel());
			responseData.setError("在插件中有引用，请先删除插件中对该模板的引用") ;
			return request(responseData, orgi, null);
		}else{
			data.setId(templateid);
			// 执行数据库删除操作
			super.getService().deleteIObject(data);
		}
		return request(new ResponseData("redirect:/{orgi}/usertemplet/tablelist/"+data.getChannel()+".html"), orgi, null);
	}

	@RequestMapping(value = "/edit/{templateid}")
	public ModelAndView eidt(HttpServletRequest request, @PathVariable String orgi, @PathVariable String templateid,
			@ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/usertemplet/edit", "/pages/include/iframeindex");
		responseData.setData(super.getService().getIObjectByPK(UserTemplet.class, templateid));
		return request(responseData, orgi, data);
	}
	
	@RequestMapping(value = "/codeedit/{templateid}")
	public ModelAndView codeedit(HttpServletRequest request, @PathVariable String orgi, @PathVariable String templateid,
			@ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/usertemplet/codeedit", "/pages/include/iframeindex");
		responseData.setData(super.getService().getIObjectByPK(UserTemplet.class, templateid));
		return request(responseData, orgi, data);
	}
	
	@RequestMapping(value = "/codeeditdo")
	public ModelAndView codeeditdo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") UserTemplet data) {
		UserTemplet userTempletResult = (UserTemplet) super.getService().getIObjectByPK(UserTemplet.class, data.getId()) ;
		userTempletResult.setTemplettext(data.getTemplettext()) ;
		data = userTempletResult ;
		super.getService().updateIObject(userTempletResult);
		// 更新到缓存的模板中去
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	
	@RequestMapping(value = "/editdo")
	public ModelAndView editdo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") UserTemplet data) {
		List<UserTemplet> list=super.getService().findAllByCriteria(DetachedCriteria.forClass(UserTemplet.class).add(Restrictions.and(Restrictions.eq("orgi", orgi),Restrictions.eq("code", data.getCode()))));
		if((list.size()==0)||(list!=null&&list.size()>0&&list.get(0).getId().equals(data.getId()))){
			super.getService().updateIObject(data);
			// 更新到缓存的模板中去
		}else{
			return request(new ResponseData("redirect://tablelist/"+data.getChannel()+".html" , "代码 "+data.getCode()+" 已存在，请重新输入" , true , null), orgi, null) ;
		}
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	
	@RequestMapping(value = "/search")
	public ModelAndView search(HttpServletRequest request, @PathVariable String orgi,@ModelAttribute("data") UserTemplet data) {
		String key = "%" + data.getName() + "%";
		ResponseData responseData = new ResponseData("/pages/manage/usertemplet/tablelist");
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(UserTemplet.class).add(Restrictions.and(Restrictions.and(Restrictions.eq("orgi", orgi),Restrictions.eq("channel", data.getChannel())) ,Restrictions.like("name", key)))));
		return request(responseData, orgi, null);
	}
	
}
