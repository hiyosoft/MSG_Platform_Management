package com.neusoft.web.handler;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.datasource.handler.DCriteriaPageSupport;
import com.neusoft.core.datasource.handler.GeneraDAO;
import com.neusoft.util.queue.AgentStatus;
import com.neusoft.web.model.Content;
import com.neusoft.web.model.Cube;
import com.neusoft.web.model.FAQModel;
import com.neusoft.web.model.TypeCategory;
import com.neusoft.web.model.User;

@Controller
@SessionAttributes
public class Handler {
	
	public final static int PAGE_SIZE_TW = 20 ;
	public final static int PAGE_SIZE_FV = 50 ;
	public final static int PAGE_SIZE_HA = 100 ;
	public final static int PAGE_SIZE_FIVE = 5;
	public final static int PAGE_SIZE_TEN = 10;
	
	public User getUser(HttpServletRequest request){
		return (User) request.getSession(true).getAttribute(EapDataContext.USER_SESSION_NAME) ;
	}
	
	public GeneraDAO getService(){
		return EapDataContext.getService() ;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public ModelAndView request(String path) {
		return new ModelAndView(path);
    }
	/**
	 * 
	 * @param data
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public ModelAndView request(ResponseData data , String orgi, RequestData reqdata) {
		ModelAndView view = new ModelAndView(data.getPage() , "data", data);
		if(data.getDataList()!=null && data.getDataList() instanceof DCriteriaPageSupport){
			DCriteriaPageSupport pInfo = ((DCriteriaPageSupport)data.getDataList()) ;
			view.addObject("total", pInfo.getTotalCount()) ;
			view.addObject("ps", pInfo.get_page_size()) ;
			view.addObject("pages", pInfo.getIndexes()!=null ? pInfo.getIndexes().length : 0) ;
			view.addObject("p", (pInfo.getStartIndex()/pInfo.get_page_size())+1) ;
			data.setP((pInfo.getStartIndex()/pInfo.get_page_size())+1) ;
		}else if(reqdata!=null){
			view.addObject("p", reqdata.getP()) ;
		}
		
		view.addObject("orgi", orgi) ;
    	return view ;
    }
	
	@SuppressWarnings("unchecked")
	public List<TypeCategory> getTypeCategoryList(String orgi){
		return getService().findAllByCriteria(DetachedCriteria.forClass(TypeCategory.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("catetype", EapDataContext.TypeCategoryEnum.KM.toString())))) ;
	}
	
	@SuppressWarnings("unchecked")
	public List<TypeCategory> getKeywordTypeCategoryList(String orgi){
		return getService().findAllByCriteria(DetachedCriteria.forClass(TypeCategory.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("catetype", EapDataContext.TypeCategoryEnum.WEIBO_KEYWORD.toString())))) ;
	}
	
	@SuppressWarnings("unchecked")
	public List<Cube> getCube(String orgi){
		return getService().findAllByCriteria(DetachedCriteria.forClass(Cube.class)) ;
	}
	

	@SuppressWarnings("unchecked")
	public List<Content> getCommonLanguage(String orgi,AgentStatus agentStatus){
		List<Content> contents=null;
		if(EapSmcDataContext.getSearchSetting(orgi).isSkill()&&agentStatus.getAgentSkill()!=null){
			contents= getService().findAllByCriteria(DetachedCriteria.forClass(Content.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("skillid", agentStatus.getAgentSkill().getId()))));
		}else{
			contents= getService().findAllByCriteria(DetachedCriteria.forClass(Content.class).add(Restrictions.eq("orgi", orgi)));
		}
		return contents;
	}
	/**
	 * 添加获取FAQ的方法
	 * 如果启用技能组，则显示技能组下的FAQ；否则显示所有FAQ;
	 * @param orgi
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<FAQModel> getFaqByUserid(String orgi,AgentStatus agentStatus){
		List<FAQModel> faqs=null;
		if(EapSmcDataContext.getSearchSetting(orgi).isSkill()&&agentStatus.getAgentSkill()!=null){
			faqs= getService().findAllByCriteria(DetachedCriteria.forClass(FAQModel.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("skillid", agentStatus.getAgentSkill().getId()))));
		}else{
			faqs= getService().findAllByCriteria(DetachedCriteria.forClass(FAQModel.class).add(Restrictions.eq("orgi", orgi)));
		}
		return faqs;
	}
	/**
	 * 
	 * @param data
	 * @return
	 */
	public ModelAndView request(ResponseData data , Map<String, Object> dataMap) {
		dataMap.put("data", data) ;
    	return new ModelAndView(data.getPage() , dataMap);
    }
	/**
	 * 
	 * @param data
	 * @return
	 */
	public ModelAndView request(ResponseData data , RequestData rqdata) {
    	return new ModelAndView(rqdata.getQ()!=null ? new StringBuffer().append(data.getPage()).append(data.getPage().indexOf("\\?")<0 ? "?":"").append(rqdata.getQ()).toString() : data.getPage() , "data", data);
    }
	/**
	 * 
	 * @param path
	 * @param error
	 * @return
	 */
	public ModelAndView request(String path , String error , String orgi) {
    	return request(new ResponseData(path , error) , orgi , null) ;
    }
	/**
	 * 重定向
	 * @param redirectView
	 * @return
	 */
	public ModelAndView request(RedirectView redirectView) {
    	return new ModelAndView(redirectView);
    }
}
