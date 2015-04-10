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

import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.RequestData;
import com.neusoft.web.handler.ResponseData;
import com.neusoft.web.model.AgentInfo;
import com.neusoft.web.model.AgentSkill;
import com.neusoft.web.model.Content;

@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}/content")
public class ContentHandler  extends Handler{

	@RequestMapping(value = "/add/{skillid}")
    public ModelAndView snsaccount(HttpServletRequest request ,@PathVariable String orgi,@PathVariable String skillid,@ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/content/add","/pages/include/iframeindex") ; 
		responseData.setMessage(skillid);
		return request(responseData, orgi, data) ;
    }
	
	@RequestMapping(value = "/adddo")
    public ModelAndView adddo(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") Content data) {
		String[] txt = data.getText().split(";|；") ;
		String skillid=request.getParameter("skillid");
		if(txt.length>0){
			for(String s:txt){
				if(s.trim().length()>0){
					data.setText(s);
					data.setSkillid(skillid);
					data.setCreatetime(new Date());
					data.setUsername(super.getUser(request).getUsername());
					data.setOrgi(orgi);
					
					super.getService().saveIObject(data);
				}
			}
		}else{
			data.setCreatetime(new Date());
			data.setSkillid(skillid);
			data.setUsername(super.getUser(request).getUsername());
			data.setOrgi(orgi);
			super.getService().saveIObject(data);
		}

		ResponseData responseData = new ResponseData("/pages/public/success"  ) ;

		return request(responseData, orgi, null) ; 
		 
    }

	/**
	 * 林招远
	 * 根据skillid分类
	 * 
	 */
	@RequestMapping(value = "/tablelist/{skillid}")
	public ModelAndView agentinfolist(HttpServletRequest request, @PathVariable String orgi,@PathVariable String skillid, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/content/tablelist");
		responseData.setResult(skillid);
		String search=request.getParameter("text");
		search=search==null?"":search;
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(Content.class).add(Restrictions.and(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("skillid", skillid)) ,Restrictions.like("text", "%"+search+"%"))), data.getPs(),data.getP()));
		return request(responseData, orgi, data);
	}
	
	@RequestMapping(value = "/tablelist")
	public ModelAndView tablelist(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") Content data,@ModelAttribute("rqdata") RequestData rqdata) {	
		ResponseData responseData = new ResponseData("/pages/manage/content/tablelist",super.getService().findPageByCriteria(DetachedCriteria.forClass(Content.class).add(Restrictions.eq("orgi", orgi)), rqdata.getPs(),rqdata.getP()),rqdata);
		return request(responseData, orgi, rqdata) ; 
	}
	
	@RequestMapping(value = "/edit")
    public ModelAndView edit(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") Content data,@ModelAttribute("rqdata") RequestData rqdata) {
		ResponseData responseData = new ResponseData("/pages/manage/content/edit","/pages/include/iframeindex");
		String contentid = request.getParameter("id");
		Content con = (Content) super.getService().getIObjectByPK(Content.class, contentid);
		if(con !=null && con.getSkillid()!=null){
			AgentSkill skills=(AgentSkill) super.getService().getIObjectByPK(AgentSkill.class, con.getSkillid());
			if(skills!=null){
				con.setName(skills.getName());
			}
			
		}
		responseData.setData(con);
		return request(responseData, orgi, rqdata) ; 
    }
	
	@RequestMapping(value = "/editdo")
    public ModelAndView editdo(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") Content data) {
		Content hadsave = (Content)super.getService().getIObjectByPK(Content.class,  data.getId());
		hadsave.setText(data.getText());
		super.getService().updateIObject(hadsave);
		ResponseData responseData = new ResponseData("/pages/public/success") ;
		return request(responseData, orgi, null) ; 
    }
	
	@RequestMapping(value = "/rm/{contentid}")
    public ModelAndView contentrm(HttpServletRequest request ,@PathVariable String orgi, @PathVariable String contentid, @ModelAttribute("data") Content data) {
		data.setId(contentid);
		super.getService().deleteIObject(data) ;
		ResponseData responseData = new ResponseData("/pages/manage/content/tablelist");
		responseData.setResult(data.getSkillid());	
		responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(Content.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("skillid", data.getSkillid())))));
		return request(responseData, orgi, null) ; 
    }
	
	@RequestMapping(value = "/search")
	public ModelAndView search(HttpServletRequest request, @ModelAttribute("data") Content datas) {
		String key = "%" + datas.getText() + "%";
		String orgi=request.getParameter("orgi");
		ResponseData data = new ResponseData("/pages/manage/content/tablelist");
		data.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(Content.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.like("text", key))).add(Restrictions.eq("skillid", datas.getSkillid()))));
		data.setResult(datas.getSkillid());	
		return request(data, orgi, null);
	}
	
	
	
}
