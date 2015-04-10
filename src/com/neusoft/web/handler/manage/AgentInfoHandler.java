package com.neusoft.web.handler.manage;

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
import com.neusoft.web.model.Content;
import com.neusoft.web.model.FAQModel;
import com.neusoft.web.model.SearchResultTemplet;
import com.neusoft.web.model.User;

@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}")
@SuppressWarnings("unchecked")
public class AgentInfoHandler extends Handler {
	
	@RequestMapping(value = "/agentskill/add")
	public ModelAndView add(HttpServletRequest request, @PathVariable String orgi) {
		ResponseData responseData = new ResponseData("/pages/manage/agentinfo/agentskilladd", "/pages/include/iframeindex");
		ModelAndView view = request(responseData, orgi, null);
		return view;
	}
	@RequestMapping(value = "/agentinfo/add/{skillid}")
	public ModelAndView agentadd(HttpServletRequest request, @PathVariable String orgi,@PathVariable String skillid) {
		ResponseData responseData = new ResponseData("/pages/manage/agentinfo/agentinfoadd", "/pages/include/iframeindex");
		responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(User.class).add(Restrictions.eq("orgi", orgi))));
		ModelAndView view = request(responseData, orgi, null);
		view.addObject("skillid",skillid);
		return view;
	}
	@RequestMapping(value = "/agentinfo/adddo")
	public ModelAndView agentadddo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") AgentInfo data) {
		List<AgentInfo> list=super.getService().findPageByCriteria(DetachedCriteria.forClass(AgentInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("code", data.getCode()))));
		if(list!=null&&list.size()>0){
			return request(new ResponseData("redirect://agentskill/tablelist.html" , "坐席号 "+data.getCode()+" 已存在，请重新输入" , true , null), orgi, null) ;
		}
		//data.setCreateuser(super.getUser(request).getUsername());
		data.setOrgi(orgi);
		User useduser=(User) super.getService().getIObjectByPK(User.class, data.getUserid());
		if(useduser!=null){
			data.setUsername(useduser.getUsername());
		}
		super.getService().saveIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/agentinfo/tablelist/{skillid}")
	public ModelAndView agentinfolist(HttpServletRequest request, @PathVariable String orgi,@PathVariable String skillid, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/agentinfo/agentinfolist");
		responseData.setResult(skillid);
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(AgentInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("skillid", skillid))),data.getPs(),data.getP()));
		return request(responseData, orgi, data);
	}
	@RequestMapping(value = "/agentinfo/rm/{skillid}/{agenginfoid}")
	public ModelAndView agentinform(HttpServletRequest request, @PathVariable String orgi,@PathVariable String skillid,@PathVariable String agenginfoid, @ModelAttribute("data") AgentInfo data) {
		data.setId(agenginfoid);
		ResponseData responseData = new ResponseData("/pages/manage/agentinfo/agentinfolist");
		super.getService().deleteIObject(data);
		responseData.setResult(skillid);
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(AgentInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("skillid", skillid)))));
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/agentinfo/edit/{agenginfoid}")
	public ModelAndView agentinfoedit(HttpServletRequest request, @PathVariable String orgi,@PathVariable String agenginfoid,@ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/agentinfo/agentinfoedit", "/pages/include/iframeindex");
		responseData.setData(super.getService().getIObjectByPK(AgentInfo.class, agenginfoid));
		//用户下拉列表
		responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(User.class).add(Restrictions.eq("orgi", orgi))));
		//技能组下拉列表
		responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(AgentSkill.class).add(Restrictions.eq("orgi", orgi))));
		return request(responseData, orgi, data);
	}
	@RequestMapping(value = "/agentinfo/editdo")
	public ModelAndView agentinfoeditdo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") AgentInfo data) {
		List<AgentInfo> list=super.getService().findPageByCriteria(DetachedCriteria.forClass(AgentInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("code", data.getCode()))));
		if(list!=null&&list.size()>0&&!list.get(0).getId().equals(data.getId())){
			return request(new ResponseData("redirect://agentskill/tablelist.html" , "代码 "+data.getCode()+" 已存在，请重新输入" , true , null), orgi, null) ;
		}
		User useduser=(User) super.getService().getIObjectByPK(User.class, data.getUserid());
		if(useduser!=null){
			data.setUsername(useduser.getUsername());
		}
		data.setOrgi(orgi);
		super.getService().updateIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/agentskill/adddo")
	public ModelAndView adddo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") AgentSkill data) {
		List<AgentSkill> list=super.getService().findPageByCriteria(DetachedCriteria.forClass(AgentSkill.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("code", data.getCode()))));
		if(list!=null&&list.size()>0){
			return request(new ResponseData("redirect://agentskill/tablelist.html" , "代码 "+data.getCode()+" 已存在，请重新输入" , true , null), orgi, null) ;
		}
		data.setCreateuser(super.getUser(request).getUsername());
		data.setOrgi(orgi);
		super.getService().saveIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	
	@RequestMapping(value = "/agentskill/tablelist")
	public ModelAndView skilltablelist(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/agentinfo/agentskilllist");
		responseData.setValueList(super.getService().findPageByCriteria(DetachedCriteria.forClass(AgentSkill.class).add(Restrictions.eq("orgi", orgi))));
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/agentskill/rm/{skillid}")
	public ModelAndView skillrm(HttpServletRequest request, @PathVariable String orgi,@PathVariable String skillid, @ModelAttribute("data") AgentSkill data) {
		data.setId(skillid);
		//判断要删除分类下是否有坐席数据
		List<AgentInfo> list=super.getService().findAllByCriteria(DetachedCriteria.forClass(AgentInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("skillid", skillid))));
		List<FAQModel> listfaq=super.getService().findAllByCriteria(DetachedCriteria.forClass(FAQModel.class).add(Restrictions.and(Restrictions.eq("orgi", orgi),Restrictions.eq("skillid", skillid))));
		List<Content> listcontent=super.getService().findAllByCriteria(DetachedCriteria.forClass(Content.class).add(Restrictions.and(Restrictions.eq("orgi", orgi),Restrictions.eq("skillid", skillid))));
		ResponseData responseData = new ResponseData("/pages/manage/agentinfo/agentskilllist");
		if((list!=null&&list.size()>0)||(listfaq!=null&&listfaq.size()>0) || (listcontent!=null&&listcontent.size()>0)){
			responseData.setMessage("请先删除该技能组下坐席或FAQ和内容再删除分类");
		}else{
			responseData.setMessage("删除成功");
			super.getService().deleteIObject(data);
		}
		responseData.setValueList(super.getService().findPageByCriteria(DetachedCriteria.forClass(AgentSkill.class).add(Restrictions.eq("orgi", orgi))));
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/agentskill/edit/{skillid}")
	public ModelAndView edit(HttpServletRequest request, @PathVariable String orgi,@PathVariable String skillid) {
		ResponseData responseData = new ResponseData("/pages/manage/agentinfo/agentskilledit", "/pages/include/iframeindex");
		responseData.setData(super.getService().getIObjectByPK(AgentSkill.class, skillid));
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/agentskill/editdo")
	public ModelAndView editdo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") AgentSkill data) {
		List<AgentSkill> list=super.getService().findPageByCriteria(DetachedCriteria.forClass(AgentSkill.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("code", data.getCode()))));
		if(list!=null&&list.size()>0&&!list.get(0).getId().equals(data.getId())){
			return request(new ResponseData("redirect://agentskill/tablelist.html" , "代码 "+data.getCode()+" 已存在，请重新输入" , true , null), orgi, null) ;
		}
		super.getService().updateIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/agentinfo/search")
	public ModelAndView search(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") AgentInfo data) {
		String key = "%" + data.getCode() + "%";
		ResponseData responseData = new ResponseData("/pages/manage/agentinfo/agentinfolist");
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(AgentInfo.class).add(Restrictions.and(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("skillid", data.getSkillid())) ,Restrictions.like("code", key)))));
		responseData.setResult(data.getSkillid());
		return request(responseData, orgi, null);
	}
}
