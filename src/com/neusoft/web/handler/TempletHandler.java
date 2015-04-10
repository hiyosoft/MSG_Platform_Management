package com.neusoft.web.handler;

import java.util.HashMap;
import java.util.List;

import javax.naming.directory.SearchResult;
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
import com.neusoft.web.model.SearchResultTemplet;
import com.neusoft.web.model.TypeCategory;

@Controller
@SessionAttributes
@SuppressWarnings("unchecked")
public class TempletHandler  extends Handler{
	@RequestMapping(value = "/{orgi}/templet")
    public ModelAndView templet(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/platform/system/templet"  ) ; 
		responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(TypeCategory.class).add(Restrictions.eq("catetype", EapDataContext.TypeCategoryEnum.TEMPLET.toString())))) ;
		if(responseData.getValueList()!=null && responseData.getValueList().size()>0){
			TypeCategory typeCategory = (TypeCategory) responseData.getValueList().get(0) ;
			responseData.setData(typeCategory) ;
			List<SearchResultTemplet> dataList = super.getService().findPageByCriteria(DetachedCriteria.forClass(SearchResultTemplet.class).add(Restrictions.eq("templettype", typeCategory.getId())) ,PAGE_SIZE_FV , data.getP()) ;
			responseData.setDataList(dataList) ;
		}
		return request(responseData , orgi , data) ;
    }
	
	@RequestMapping(value = "/{orgi}/pagetemplet/{id}")
    public ModelAndView pagetemplet(HttpServletRequest request ,@PathVariable String orgi,@PathVariable String id,  @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/platform/system/pagetemplet"  ) ; 
		responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(TypeCategory.class).add(Restrictions.eq("catetype", EapDataContext.TypeCategoryEnum.TEMPLET.toString())))) ;
		responseData.setMessage(id);
		if(responseData.getValueList()!=null && responseData.getValueList().size()>0){
			TypeCategory typeCategory = (TypeCategory) super.getService().getIObjectByPK(TypeCategory.class,id)  ;
			responseData.setData(typeCategory) ;
			List<SearchResultTemplet> dataList = super.getService().findPageByCriteria(DetachedCriteria.forClass(SearchResultTemplet.class).add(Restrictions.eq("templettype", typeCategory.getId())) ,PAGE_SIZE_FV , data.getP()) ;
			responseData.setDataList(dataList) ;
		}
		return request(responseData , orgi , data) ;
    }
	
	@RequestMapping(value = "/{orgi}/templet/{subcateid}")
    public ModelAndView templetype(HttpServletRequest request ,@PathVariable String orgi, @PathVariable String subcateid,@ModelAttribute("data") RequestData data) {
		List<TypeCategory> dataList1 = super.getService().findAllByCriteria(DetachedCriteria.forClass(TypeCategory.class) ) ;
		TypeCategory typeCate = (TypeCategory) super.getService().getIObjectByPK(TypeCategory.class,subcateid)  ;
		ResponseData responseData = new ResponseData("/pages/platform/system/templetlist" ,dataList1, typeCate) ; 
		List<SearchResultTemplet> dataList = super.getService().findPageByCriteria(DetachedCriteria.forClass(SearchResultTemplet.class).add(Restrictions.eq("templettype", typeCate.getId())) ,PAGE_SIZE_FV , data.getP()) ;
		responseData.setValueList(dataList) ;
		return request(responseData , orgi , data) ;
    }
	
	
	@RequestMapping(value = "/{orgi}/templet/add/{typeid}")
    public ModelAndView templetadd(HttpServletRequest request ,@PathVariable String orgi, @PathVariable String typeid, @ModelAttribute("data") RequestData data) {
		return request(new ResponseData("/pages/platform/system/templetadd" , super.getService().findAllByCriteria(DetachedCriteria.forClass(TypeCategory.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.ne("ctype", "project"))) , super.getService().getIObjectByPK(TypeCategory.class, typeid)), orgi , data) ;
    }
	
	@RequestMapping(value = "/{orgi}/templet/adddo")
    public ModelAndView templetadddo(HttpServletRequest request ,@PathVariable String orgi,@ModelAttribute("data") SearchResultTemplet data) {
		data.setOrgi(orgi) ;
		super.getService().saveIObject(data) ;
		return request(new ResponseData(new StringBuffer().append("redirect:/{orgi}/templet/").append(data.getTemplatetype()).append(".html").toString()), orgi , null) ;
    }
	
	@RequestMapping(value = "/{orgi}/templet/edit/{id}")
    public ModelAndView templetedit(HttpServletRequest request ,@PathVariable String orgi,@PathVariable String id,@ModelAttribute("data") RequestData data) {
		return request(new ResponseData("/pages/platform/system/templetedit"  , super.getService().findAllByIObjectCType(TypeCategory.class) , super.getService().getIObjectByPK(SearchResultTemplet.class, id)), orgi , data) ;
    }
	
	@RequestMapping(value = "/{orgi}/templet/editdo")
    public ModelAndView templeteditdo(HttpServletRequest request ,@PathVariable String orgi,@ModelAttribute("data") SearchResultTemplet data) {
		SearchResultTemplet templet = (SearchResultTemplet) super.getService().getIObjectByPK(SearchResultTemplet.class, data.getId()) ;
		data.setTemplettext(templet.getTemplettext()) ;
		super.getService().updateIObject(data) ;
		return request(new ResponseData(new StringBuffer().append("redirect:/{orgi}/templet/").append(data.getTemplatetype()).append(".html").toString()), orgi , null) ;
    }
	
	@RequestMapping(value = "/{orgi}/templet/code/{id}")
    public ModelAndView templetcode(HttpServletRequest request ,@PathVariable String orgi,@PathVariable String id,@ModelAttribute("data") RequestData data) {
		return request(new ResponseData("/pages/platform/system/templetcode"  , null , super.getService().getIObjectByPK(SearchResultTemplet.class, id)), orgi , data) ;
    }
	@RequestMapping(value = "/{orgi}/templet/codesave")
    public ModelAndView templetcodesave(HttpServletRequest request ,@PathVariable String orgi,@ModelAttribute("data") SearchResultTemplet data) {
		SearchResultTemplet templet = (SearchResultTemplet) super.getService().getIObjectByPK(SearchResultTemplet.class, data.getId()) ;
		templet.setTemplettext(data.getTemplettext()) ;
		super.getService().updateIObject(templet) ;
		return request(new ResponseData(new StringBuffer().append("redirect:/{orgi}/templet/").append(templet.getTemplatetype()).append(".html").toString()), orgi , null) ;
    }
	
	@RequestMapping(value = "/{orgi}/templet/rm/{id}/{typeid}")
    public ModelAndView templetrm(HttpServletRequest request ,@PathVariable String orgi, @PathVariable String id,@PathVariable String typeid,  @ModelAttribute("data") RequestData data) {
		SearchResultTemplet templet = new SearchResultTemplet();
		templet.setId(id) ;
		super.getService().deleteIObject(templet) ;
		return request(new ResponseData(new StringBuffer().append("redirect:/{orgi}/templet/").append(typeid).append(".html").toString()), orgi , data) ;
    }
}
