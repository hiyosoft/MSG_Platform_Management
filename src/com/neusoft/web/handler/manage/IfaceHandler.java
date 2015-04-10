package com.neusoft.web.handler.manage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.client.methods.HttpPost;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.neusoft.core.EapSmcDataContext;
import com.neusoft.util.process.ReqeustProcessUtil;
import com.neusoft.util.tools.HttpClientTools;
import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.RequestData;
import com.neusoft.web.handler.ResponseData;
import com.neusoft.web.model.IfaceCategory;
import com.neusoft.web.model.IfaceInfo;
import com.neusoft.web.model.UserTemplet;

import freemarker.template.TemplateException;

@Controller
@SessionAttributes
@RequestMapping(value="/{orgi}")
public class IfaceHandler extends Handler{
	@RequestMapping(value = "/ifacecate/add")
	public ModelAndView add(HttpServletRequest request, @PathVariable String orgi) {
		ResponseData responseData = new ResponseData("/pages/manage/iface/ifacecateadd", "/pages/include/iframeindex");
		ModelAndView view = request(responseData, orgi, null);
		return view;
	}
	@RequestMapping(value = "/ifacecate/adddo")
	public ModelAndView adddo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") IfaceCategory data) {
		super.getService().saveIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/ifacecate/edit/{cateid}")
	public ModelAndView edit(HttpServletRequest request, @PathVariable String orgi,@PathVariable String cateid) {
		ResponseData responseData = new ResponseData("/pages/manage/iface/ifacecateedit", "/pages/include/iframeindex");
		responseData.setData(super.getService().getIObjectByPK(IfaceCategory.class, cateid));
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/ifacecate/editdo")
	public ModelAndView editdo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") IfaceCategory data) {
		super.getService().updateIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/ifacecate/rm/{cateid}")
	public ModelAndView rm(HttpServletRequest request, @PathVariable String orgi,@PathVariable String cateid, @ModelAttribute("data") IfaceCategory data) {
		data.setId(cateid);
		ResponseData responseData = new ResponseData("/pages/manage/iface/ifacecatelist");
		List<IfaceInfo> list = super.getService().findAllByCriteria(DetachedCriteria.forClass(IfaceInfo.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.eq("cateid", cateid)));
		if(list!=null && list.size()>0){
			responseData.setMessage("请先删除接口再删除分类！");
			responseData.setValueList(super.getService().findPageByCriteria(DetachedCriteria.forClass(IfaceCategory.class).add(Restrictions.eq("orgi", orgi))));
		}else{
		super.getService().deleteIObject(data);
		responseData.setMessage("删除成功");
		responseData.setValueList(super.getService().findPageByCriteria(DetachedCriteria.forClass(IfaceCategory.class).add(Restrictions.eq("orgi", orgi))));
		
		}
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/ifacecate/tablelist")
	public ModelAndView list(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/iface/ifacecatelist");
		responseData.setValueList(super.getService().findPageByCriteria(DetachedCriteria.forClass(IfaceCategory.class).add(Restrictions.eq("orgi", orgi)),data.getPs(),data.getP()));
		return request(responseData, orgi, data);
	}
	@RequestMapping(value = "/iface/add/{cateid}")
	public ModelAndView ifaceadd(HttpServletRequest request, @PathVariable String orgi, @PathVariable String cateid) {
		ResponseData responseData = new ResponseData("/pages/manage/iface/ifaceadd", "/pages/include/iframeindex");
		ModelAndView view = request(responseData, orgi, null);
		view.addObject("outemplates",EapSmcDataContext.getUserTempletList("outputadapter", orgi));
		view.addObject("intemplates",EapSmcDataContext.getUserTempletList("inputadapter", orgi));
		view.addObject("cateid",cateid);
		return view;
	}
	@RequestMapping(value = "/iface/adddo")
	public ModelAndView ifaceadddo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") IfaceInfo data) {
		List<IfaceInfo> list=super.getService().findPageByCriteria(DetachedCriteria.forClass(IfaceInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("code", data.getCode()))));
		if(list!=null&&list.size()>0){
			return request(new ResponseData("redirect://iface/ifacelist.html" , "代码 "+data.getCode()+" 已存在，请重新输入" , true , null), orgi, null) ;
		}
		super.getService().saveIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/iface/edit/{ifaceid}")
	public ModelAndView ifaceoedit(HttpServletRequest request, @PathVariable String orgi,@PathVariable String ifaceid,@ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/iface/ifaceedit", "/pages/include/iframeindex");
		ModelAndView view = request(responseData, orgi, null);
		view.addObject("outemplates",EapSmcDataContext.getUserTempletList("outputadapter", orgi));
		view.addObject("intemplates",EapSmcDataContext.getUserTempletList("inputadapter", orgi));
		responseData.setData(super.getService().getIObjectByPK(IfaceInfo.class, ifaceid));
		return view;
	}
	@RequestMapping(value = "/iface/editdo")
	public ModelAndView ifaceeditdo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") IfaceInfo data) {
		List<IfaceInfo> list=super.getService().findPageByCriteria(DetachedCriteria.forClass(IfaceInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("code", data.getCode()))));
		if(list!=null&&list.size()>0 && !data.getId().equals(list.get(0).getId())){
			return request(new ResponseData("redirect://iface/ifacelist.html" , "代码 "+data.getCode()+" 已存在，请重新输入" , true , null), orgi, null) ;
		}
		data.setOrgi(orgi);
		super.getService().updateIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/iface/test/{ifaceid}")
	public ModelAndView ifaceotest(HttpServletRequest request, @PathVariable String orgi,@PathVariable String ifaceid,@ModelAttribute("data") RequestData data) {
		IfaceInfo info=(IfaceInfo) super.getService().getIObjectByPK(IfaceInfo.class, ifaceid);
		Map<String , Object > map=new HashMap<String , Object >();
		String tem = null; 
		try {
			tem=ReqeustProcessUtil.getResponseBody(info,null, orgi,map).getResponseBody();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResponseData responseData = new ResponseData("/pages/manage/iface/ifacetest", "/pages/include/iframeindex");
		ModelAndView view = request(responseData, orgi, null);
		view.addObject("resultVal",tem);
		return view;
	}
	@RequestMapping(value = "/iface/testdo")
	public ModelAndView ifacetestdo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") IfaceInfo data) throws Exception, TemplateException {
		Map<String , Object > map=new HashMap<String , Object >();
		if(data!=null && data.getRequesturl()!=null){
			String tem = ReqeustProcessUtil.getResponseBody(data,null, orgi,map).getResponseBody();
			return request(new ResponseData("redirect://{orgi}/iface/ifacetest.html" , "返回结果："+tem , true , null), orgi, null) ;
		}
		data.setOrgi(orgi);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/iface/rm/{cateid}/{ifaceid}")
	public ModelAndView ifacerm(HttpServletRequest request, @PathVariable String orgi,@PathVariable String cateid,@PathVariable String ifaceid, @ModelAttribute("data") IfaceInfo data) {
		data.setId(ifaceid);
		ResponseData responseData = new ResponseData("/pages/manage/iface/ifacelist");
		super.getService().deleteIObject(data);
		responseData.setResult(cateid);
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(IfaceInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("cateid", cateid)))));
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/iface/tablelist/{cateid}")
	public ModelAndView ifacelist(HttpServletRequest request, @PathVariable String orgi,@PathVariable String cateid, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/iface/ifacelist");
		responseData.setResult(cateid);
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(IfaceInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("cateid", cateid))),data.getPs(),data.getP()));
		return request(responseData, orgi, data);
	}
	@RequestMapping(value = "/iface/search")
	public ModelAndView search(HttpServletRequest request, @PathVariable String orgi,@ModelAttribute("data") IfaceInfo data) {
		String key = "%" + data.getName() + "%";
		ResponseData responseData = new ResponseData("/pages/manage/iface/ifacelist");
		responseData.setResult(data.getCateid());
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(IfaceInfo.class).add(Restrictions.and((Restrictions.eq("orgi", orgi)) ,Restrictions.like("name", key))).add(Restrictions.eq("cateid", data.getCateid()))));
		return request(responseData, orgi, null);
	}
	

}
