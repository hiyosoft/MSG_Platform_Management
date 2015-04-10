package com.neusoft.web.handler.manage;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.jfree.util.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.channel.WeiXinUser;
import com.neusoft.util.store.EapTools;
import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.RequestData;
import com.neusoft.web.handler.ResponseData;
import com.neusoft.web.model.AgentServiceStatus;
import com.neusoft.web.model.SearchSetting;
import com.neusoft.web.model.User;
import com.neusoft.web.model.UserGroup;

@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}/fans")
@SuppressWarnings("unchecked")
public class FansHandler extends Handler {
	@RequestMapping(value = "/edit/{fansid}")
	public ModelAndView edit(HttpServletRequest request, @PathVariable String orgi,@PathVariable String fansid,@ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/fans/fansedit", "/pages/include/iframeindex");
		responseData.setValueList(super.getService().findPageByCriteria(DetachedCriteria.forClass(UserGroup.class).add(Restrictions.eq("orgi", orgi))));
		responseData.setData(super.getService().getIObjectByPK(WeiXinUser.class, fansid));
		return request(responseData, orgi, data);
	}
	@RequestMapping(value = "/find/{fansid}")
	public ModelAndView find(HttpServletRequest request, @PathVariable String orgi,@PathVariable String fansid,@ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/fans/find", "/pages/include/iframeindex");
		responseData.setData(super.getService().getIObjectByPK(WeiXinUser.class, fansid));
		return request(responseData, orgi, data);
	}
	@RequestMapping(value = "/editdo")
	public ModelAndView editdo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") WeiXinUser data) {
		if(data!=null && data.getId()!=null){
			WeiXinUser user=(WeiXinUser) super.getService().getIObjectByPK(WeiXinUser.class, data.getId());
			String hisgroupcode=request.getParameter("hisgroupcode");
			//移动了分组
			if(data.getGroupID()!=null && !data.getGroupID().equals(hisgroupcode)){
				SearchSetting setting=EapSmcDataContext.getSearchSetting(orgi);
				String result=EapTools.postString(setting.getGwhost() + "/"+orgi +"/api/fans/movegroup.html?groupid="+data.getGroupID()+"&apiusername="+user.getApiusername(),"" , "UTF-8") ;
				if(result!=null && result.indexOf("ok")>0){
					user.setGroupID(data.getGroupID());
				}else{
					System.out.println("==网络问题,用户移动分组失败=");
				}
			}
			user.setCity(data.getCity());
			user.setMemo(data.getMemo());
			user.setUserau(data.isUserau());
			user.setUserbind(data.isUserbind());
			user.setMobile(data.getMobile());
			super.getService().updateIObject(user);
		}
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/tablelist/{skillid}")
	public ModelAndView agentinfolist(HttpServletRequest request, @PathVariable String orgi,@PathVariable String skillid, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/fans/fanslist");
		responseData.setResult(skillid);
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(WeiXinUser.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("groupID", skillid))),data.getPs(),data.getP()));
		return request(responseData, orgi, data);
	}
	@RequestMapping(value = "/synusers")
	public ModelAndView skilltablelist(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		User user = super.getUser(request);
		ResponseData responseData = new ResponseData("/pages/manage/fans");
		if(user!=null){
			responseData.setRqdata(data);
			//左边的二级菜单		
			List<UserGroup> groups=super.getService().findPageByCriteria(DetachedCriteria.forClass(UserGroup.class).add(Restrictions.eq("orgi", orgi)));
			if(groups!=null && groups.size()>0){
				responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(WeiXinUser.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.and(Restrictions.eq("channel", EapDataContext.ChannelTypeEnum.WEIXIN.toString()), Restrictions.eq("groupID", groups.get(0).getCode())))));
				responseData.setValueList(groups);
				responseData.setResult(groups.get(0).getCode());
			}
			try {
				Calendar ca=Calendar.getInstance();
				ca.add(Calendar.DAY_OF_MONTH, -1);
				List<AgentServiceStatus> aList = super.getService().findAllByCriteria(DetachedCriteria.forClass(AgentServiceStatus.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("operatetype", "3"))).add(Restrictions.gt("operatetime", ca.getTime())));
				if(aList.size()==0){
					SearchSetting setting=EapSmcDataContext.getSearchSetting(orgi);
					int oldNum = super.getService().deleteBySql("rivu_weixinuser"," orgi='" + orgi + "'");
					Log.info(user.getUsername()+"发起了同步粉丝的请求,共删除了" + oldNum + "条数据。");
					AgentServiceStatus fansAc = new AgentServiceStatus();
					fansAc.setAgentno(user.getAgentstatus().getAgentno());
					fansAc.setAgentname(user.getAgentstatus().getUser().getUsername());
					fansAc.setCreatetime(new Date());
					fansAc.setOperatetime(new Date());
					fansAc.setOperatetype("3");
					fansAc.setOrgi(orgi);
					super.getService().saveIObject(fansAc);
					EapTools.postString(setting.getGwhost() + "/"+orgi +"/api/fans/syncuser.html","" , "UTF-8") ;
					Thread.sleep(1500);
					responseData.setMessage("粉丝同步正在后台运行中……");
				}else{
					responseData.setMessage("24小时内不能重复同步粉丝。");
				}
			} catch (InterruptedException e) {
				 //TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return request(responseData, orgi, null);
	}
	
	
	@RequestMapping(value = "/search")
	public ModelAndView search(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") WeiXinUser data) {
		String key = "%" + data.getNickName() + "%";
		ResponseData responseData = new ResponseData("/pages/manage/fans/fanslist");
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(WeiXinUser.class).add(Restrictions.and(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("groupID", data.getGroupID())) ,Restrictions.like("nickName", key)))));
		responseData.setResult(data.getGroupID());
		return request(responseData, orgi, null);
	}
	/**
	 * 获取添加页面
	 * @param request
	 * @param orgi
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/group/add")
	public ModelAndView add(HttpServletRequest request, @PathVariable String orgi,@ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/group/groupadd", "/pages/include/iframeindex");
		ModelAndView view = request(responseData, orgi, data);
		return view;
	}
	/**
	 * 获取分组列表
	 * @param request
	 * @param orgi
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/group/tablelist")
	public ModelAndView tablelist(HttpServletRequest request, @PathVariable String orgi,@ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/fans/fansgroupslist");
		responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(UserGroup.class).add(Restrictions.eq("orgi", orgi))));
		ModelAndView view = request(responseData, orgi, data);
		return view;
	}
	/**
	 * 添加分组
	 * @param request
	 * @param orgi
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/group/adddo")
	public ModelAndView adddo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") UserGroup data) {
		List<UserGroup> list =super.getService().findAllByCriteria(DetachedCriteria.forClass(UserGroup.class).add(Restrictions.eq("groupName", data.getGroupName())).add(Restrictions.eq("orgi", orgi)));
		if(list!=null&&list.size()<1){
			data.setOrgi(orgi);
			SearchSetting setting=EapSmcDataContext.getSearchSetting(orgi);
			String result=EapTools.postString(setting.getGwhost() + "/"+orgi +"/api/fans/creatgroup.html?groupname="+data.getGroupName(),"" , "UTF-8") ;
			if(result!=null && result.indexOf("group")>0){
				JSONObject group=JSON.parseObject(result).getJSONObject("group");
				data.setCode(group.getString("id"));
				super.getService().saveIObject(data);
			}
				
		}else{
			return request(new ResponseData("redirect://tablelist/fansgroupslist" , "名字 "+data.getGroupName()+" 已存在，请重新输入" , true , null), orgi, null) ;
		}
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	/**
	 * 删除分组
	 * @param request
	 * @param orgi
	 * @param id
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/rm/{id}")
	public ModelAndView rm(HttpServletRequest request, @PathVariable String orgi,@PathVariable String id, @ModelAttribute("data") UserGroup data) {
		List<WeiXinUser> list = super.getService().findPageByCriteria(DetachedCriteria.forClass(WeiXinUser.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.and(Restrictions.eq("channel", EapDataContext.ChannelTypeEnum.WEIXIN.toString()), Restrictions.eq("groupID",id ))));
		ResponseData responseData = new ResponseData("/pages/manage/fans/fansgroupslist");
		if(list==null || list.size()<0 || list.isEmpty()){
			data.setId(id);
			super.getService().deleteIObject(data);
			responseData.setMessage("删除成功");

		}else{
			responseData.setMessage("分组下有数据不能删除");
		}
		responseData.setValueList(super.getService().findPageByCriteria(DetachedCriteria.forClass(UserGroup.class).add(Restrictions.eq("orgi", orgi))));
		return request(responseData, orgi, null);
	}
	/**
	 * 预编辑分组
	 * @param request
	 * @param orgi
	 * @param id
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/group/edit/{id}")
	public ModelAndView edit(HttpServletRequest request, @PathVariable String orgi,@PathVariable String id, @ModelAttribute("data") UserGroup data) {
		data.setId(id);
		ResponseData responseData = new ResponseData("/pages/manage/group/groupedit","/pages/include/iframeindex");
		responseData.setData(super.getService().getIObjectByPK(UserGroup.class, id));
		return request(responseData, orgi, null);
	}
	/**
	 * 编辑分组
	 * @param request
	 * @param orgi
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/group/editdo")
	public ModelAndView editdo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") UserGroup data) {
		List<UserGroup> list = super.getService().findAllByCriteria(DetachedCriteria.forClass(UserGroup.class).add(Restrictions.eq("groupName", data.getGroupName())).add(Restrictions.eq("orgi", orgi)));
		if(list!=null&&list.size()>0 && !data.getId().equals(list.get(0).getId())){
			return request(new ResponseData("redirect://tablelist/fansgroupslist.html" , "名字 "+data.getGroupName()+" 已存在，请重新输入" , true , null), orgi, null) ;
		}
		data.setOrgi(orgi);
		SearchSetting setting=EapSmcDataContext.getSearchSetting(orgi);
		String result=EapTools.postString(setting.getGwhost() + "/"+orgi +"/api/fans/updategroup.html?groupname="+data.getGroupName()+"&groupid="+data.getCode(),"" , "UTF-8") ;
		if(result!=null && result.indexOf("ok")>0){
			super.getService().updateIObject(data);
		}
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/qunfa")
	public ModelAndView qunfa(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/fans/qunfa");
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(WeiXinUser.class)));
		return request(responseData, orgi, null);
	}
}
