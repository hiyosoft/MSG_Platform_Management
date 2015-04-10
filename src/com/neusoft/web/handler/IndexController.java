package com.neusoft.web.handler; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.channel.WeiXin;
import com.neusoft.core.channel.WeiXinUser;
import com.neusoft.util.persistence.DBPersistence;
import com.neusoft.util.queue.AgentStatus;
import com.neusoft.util.queue.ServiceQueue;
import com.neusoft.web.model.AgentInfo;
import com.neusoft.web.model.AgentServiceStatus;
import com.neusoft.web.model.AgentSkill;
import com.neusoft.web.model.Content;
import com.neusoft.web.model.DataDic;
import com.neusoft.web.model.EventMenu;
import com.neusoft.web.model.ExtensionPoints;
import com.neusoft.web.model.FAQModel;
import com.neusoft.web.model.FilterHistoryModel;
import com.neusoft.web.model.IfaceCategory;
import com.neusoft.web.model.IfaceInfo;
import com.neusoft.web.model.KWCheckCategory;
import com.neusoft.web.model.Keyword;
import com.neusoft.web.model.KeywordCategory;
import com.neusoft.web.model.KeywordCheck;
import com.neusoft.web.model.Material;
import com.neusoft.web.model.MaterialItem;
import com.neusoft.web.model.OptCount;
import com.neusoft.web.model.PageTemplate;
import com.neusoft.web.model.PageType;
import com.neusoft.web.model.SNSAccount;
import com.neusoft.web.model.SearchResultTemplet;
import com.neusoft.web.model.SinoLocation;
import com.neusoft.web.model.User;
import com.neusoft.web.model.UserGroup;
import com.neusoft.web.model.UserTemplet;
import com.sinosig.pay.platform.kuaiqian.model.MPurchase;
import com.sinosig.staff.model.StaffWelfare;

@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}")
@SuppressWarnings("unchecked")
public class IndexController extends Handler{
	@RequestMapping(value = "/index")
    public ModelAndView index(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/index"  ) ;
		responseData.setData(super.getUser(request));
		ModelAndView view = request(responseData , orgi , data) ;
		User user = super.getUser(request) ;
		if(user!=null){
			AgentStatus agentStatus = ServiceQueue.getAgent(user.getAgentno(), orgi);
			view.addObject("agentstatus", agentStatus) ;
			view.addObject("userlist", agentStatus!=null ? agentStatus.getUserList() : null) ;
			view.addObject("inseruser", agentStatus.getUserList().size()) ;
			view.addObject("queueuser", ServiceQueue.getQueueNum(orgi));
		}
		return view ;
    }
	
	@RequestMapping(value = "/index/dashboard")
    public ModelAndView dashboard(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/dashboard"  ) ; 
		responseData.setData(super.getUser(request));
		return request(responseData , orgi , data) ;
    }
	
	@RequestMapping(value = "/index/snsaccount")
    public ModelAndView snsaccount(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data,@ModelAttribute("data") RequestData requestdata) {
		//临时导出数据
				/*try {
					List<WeiXin> weixins=super.getService().findAllByCriteria(DetachedCriteria.forClass(WeiXin.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.isNull("replytype")).add(Restrictions.eq("messagetype", "text")).addOrder(Order.desc("createtime")));
					FileWriter fw = new FileWriter(System.getProperties().getProperty("user.home").toString()+File.separator+"weixinmessage.txt");   
					for (WeiXin wx : weixins) {
						if(!"kf".equals(wx.getContent())){
							fw.write(wx.getContent()+"\t"+wx.getUsername()+"\t"+wx.getCreatedate()+"\r\n");
						}
					}
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
		ResponseData responseData = new ResponseData("/pages/manage/snsaccount"  ) ;
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(SNSAccount.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.eq("snstype", EapDataContext.ChannelTypeEnum.WEIXIN.toString()))),requestdata.getPs(),(requestdata.getP()-1)*requestdata.getPs()));
		responseData.setDocNum(super.getService().getCountByCriteria(DetachedCriteria.forClass(SNSAccount.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.eq("snstype", EapDataContext.ChannelTypeEnum.WEIXIN.toString())))));
		responseData.setValueList(Arrays.asList(EapDataContext.ChannelTypeEnum.class.getEnumConstants()));
		return request(responseData , orgi , data) ;
    }
	
	@RequestMapping(value = "/index/monitor")
    public ModelAndView monitor(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/msgmonitor"  ) ;
//		responseData.setDataList(new DBPersistence().getMsgMonitor(tmp,1,1));
		responseData.setDataList(new DBPersistence().getMsgMonitor(orgi,"weixin"));
		//responseData.setResult(null);
//		responseData.setResult(RivuDataContext.SNSTypeEnum.WEIXIN.toString());
//		List<Object> userlist=Arrays.asList(ServiceQueue.getUserQueue().get(orgi).values().toArray());
		return request(responseData , orgi , data) ;
    }
	
	@RequestMapping(value = "/index/msghistory")
    public ModelAndView msghistory(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/msghistory"  ) ; 
		FilterHistoryModel tmp = new FilterHistoryModel();
		tmp.setUser(super.getUser(request));
		tmp.setOrgi(orgi);
		responseData.setDataList(new DBPersistence().getsumByAgentOrUser(tmp, data.getP(), data.getPs()));
		return request(responseData , orgi , data) ;
    }
	
	@RequestMapping(value = "/index/kwfilter")
    public ModelAndView kwfilter(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/kwfilter") ; 
		data.setPs(PAGE_SIZE_HA);
		responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(KWCheckCategory.class).add(Restrictions.eq("orgi", orgi))));
		if(super.getService().getCountByCriteria(DetachedCriteria.forClass(KWCheckCategory.class).add(Restrictions.eq("orgi", orgi)))>0){ 
			KWCheckCategory key = (KWCheckCategory)super.getService().findAllByCriteria(DetachedCriteria.forClass(KWCheckCategory.class).add(Restrictions.eq("orgi", orgi))).get(0);
			responseData.setResult(key.getId());
			responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(KeywordCheck.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.eq("category", key.getId()))),data.getPs(),data.getP()));
		}
		return request(responseData , orgi , data) ;
    }
	
	@RequestMapping(value = "/index/keyword")
    public ModelAndView keyword(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData rqdata) {
		ResponseData responseData = new ResponseData("/pages/manage/keyword"  ) ; 
		rqdata.setPs(PAGE_SIZE_HA);
		responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(KeywordCategory.class).add(Restrictions.eq("orgi", orgi))));
		if(super.getService().getCountByCriteria(DetachedCriteria.forClass(KeywordCategory.class).add(Restrictions.eq("orgi", orgi)))>0){ 
			KeywordCategory key = (KeywordCategory)super.getService().findAllByCriteria(DetachedCriteria.forClass(KeywordCategory.class).add(Restrictions.eq("orgi", orgi))).get(0);
			responseData.setResult(key.getId());
			responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(Keyword.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.eq("category", key.getId()))),rqdata.getPs(),rqdata.getP()));
		}
		return request(responseData , orgi , rqdata) ;
    }
	
	@RequestMapping(value = "/index/syscommand")
    public ModelAndView syscommand(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/syscommand"  ) ; 
		return request(responseData , orgi , data) ;
    }
	
	/*
	@RequestMapping(value = "/index/snsaccount")
    public ModelAndView snsaccount(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data,@ModelAttribute("data") RequestData requestdata) {
		ResponseData responseData = new ResponseData("/pages/manage/snsaccount"  ) ;
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(SNSAccount.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.eq("snstype", RivuDataContext.SNSTypeEnum.WEIXIN.toString()))),requestdata.getPs(),(requestdata.getP()-1)*requestdata.getPs()));
		responseData.setDocNum(super.getService().getCountByCriteria(DetachedCriteria.forClass(SNSAccount.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.eq("snstype", RivuDataContext.SNSTypeEnum.WEIXIN.toString())))));
		return request(responseData , orgi , data) ;
    }*/
	@RequestMapping(value = "index/extensionpoint")
    public ModelAndView extensionpoint(HttpServletRequest request ,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata) {
		ResponseData responseData = new ResponseData(null,"/pages/manage/extensionpoint",super.getService().findPageByCriteria(DetachedCriteria.forClass(ExtensionPoints.class).add(Restrictions.eq("extensiontype", EapDataContext.PluginType.INSTRUCTION.toString())),  rqdata.getPs(),rqdata.getP()));
//		ResponseData responseData = new ResponseData(null,"/pages/manage/extensionpoint",super.getService().findPageByCriteria(DetachedCriteria.forClass(ExtensionPoints.class).add(Restrictions.eq("orgi", orgi)),  rqdata.getPs(),rqdata.getP()));
		responseData.setRqdata(rqdata);
		responseData.setResult(EapDataContext.PluginType.INSTRUCTION.toString());
		return request(responseData , orgi , rqdata) ;
    }
	@RequestMapping(value = "index/template")
    public ModelAndView template(HttpServletRequest request ,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata) {
		ResponseData responseData = new ResponseData(null,"/pages/manage/template",super.getService().findPageByCriteria(DetachedCriteria.forClass(SearchResultTemplet.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("channel", EapDataContext.ChannelTypeEnum.WEIXIN.toString()))),  rqdata.getPs(),rqdata.getP()));
		responseData.setRqdata(rqdata);
		responseData.setResult( EapDataContext.ChannelTypeEnum.WEIXIN.toString());
		return request(responseData , orgi , rqdata) ;
    }
	@RequestMapping(value = "index/usertemplet")
    public ModelAndView usertemplate(HttpServletRequest request ,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata) {
		ResponseData responseData = new ResponseData(null,"/pages/manage/usertemplet",super.getService().findPageByCriteria(DetachedCriteria.forClass(UserTemplet.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("channel", EapDataContext.getDefaultSiteTemplet(orgi)))),  rqdata.getPs(),rqdata.getP()));
		responseData.setRqdata(rqdata);
		responseData.setResult( EapDataContext.getDefaultSiteTemplet(orgi));
		return request(responseData , orgi , rqdata) ;
    }
	@RequestMapping(value = "index/agentinfo")
	public ModelAndView agentinfo(HttpServletRequest request,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata){
		ResponseData responseData=new ResponseData("pages/manage/agentinfo");
		responseData.setRqdata(rqdata);
		List<AgentSkill> skills=super.getService().findAllByCriteria(DetachedCriteria.forClass(AgentSkill.class).add(Restrictions.eq("orgi", orgi)));
		if(skills!=null&&skills.size()>0){
			AgentSkill ask=skills.get(0);
			responseData.setValueList(skills);
			responseData.setResult(ask.getId());
			responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(AgentInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("skillid", ask.getId())))));
		}
		return request(responseData, orgi, rqdata);
	}
	@RequestMapping(value = "index/faq")
    public ModelAndView faq(HttpServletRequest request ,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata) {
		ResponseData responseData = new ResponseData("/pages/manage/faq");
		responseData.setRqdata(rqdata);
		//左边的二级菜单
		List<AgentSkill> valuelist=super.getService().findAllByCriteria(DetachedCriteria.forClass(AgentSkill.class).add(Restrictions.eq("orgi", orgi)));
		responseData.setValueList(valuelist);
		if(valuelist!=null&&valuelist.size()>0){
			String skillid=valuelist.get(0).getId();
			responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(FAQModel.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("skillid", skillid)))));
			responseData.setResult(skillid);
		}
		return request(responseData , orgi , rqdata) ;
    }
	@RequestMapping(value = "index/systemconfig")
	public ModelAndView systemconfig(HttpServletRequest request,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata){
		ResponseData responseData=new ResponseData("pages/manage/systemconfig");
		ModelAndView view = request(responseData, orgi, rqdata); 
		view.addObject("setting", EapSmcDataContext.getSearchSetting(orgi)) ;
		view.addObject("instruct", EapDataContext.getInstructList(orgi)) ;
		view.addObject("material", super.getService().findAllByCriteria(DetachedCriteria.forClass(Material.class).add(Restrictions.eq("orgi", orgi)))) ;
		view.addObject("pagetemplate", super.getService().findAllByCriteria(DetachedCriteria.forClass(PageTemplate.class).add(Restrictions.eq("orgi", orgi)))) ;
		List l = EapDataContext.getInstructList(orgi);
		return view;
	}
	
	@RequestMapping(value = "index/agentnolist")
	public ModelAndView agentlist(HttpServletRequest request,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata){
		ResponseData responseData=new ResponseData("pages/manage/agentnolist");
		ModelAndView view = request(responseData, orgi, rqdata); 
		return view;
	}
	
	@RequestMapping(value = "index/material")
	public ModelAndView material(HttpServletRequest request,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata){
		ResponseData responseData=new ResponseData("pages/manage/material");
		responseData.setRqdata(rqdata);
		List<Material> materials=super.getService().findAllByCriteria(DetachedCriteria.forClass(Material.class).add(Restrictions.eq("orgi", orgi)));
		Material material=null;
		if(materials!=null&&materials.size()>0){
			material=materials.get(0);
			responseData.setResult(material.getId());
			List<MaterialItem> items=material.getmItems();
			if(items!=null&&items.size()>0){
				responseData.setData(items.get(0));
				responseData.setValueList(items);
			}
		}
		responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(Material.class).add(Restrictions.eq("orgi", orgi)).addOrder(Order.asc("createtime"))));
		ModelAndView view = request(responseData, orgi, null);
		view.addObject("mainmaterial", material);
		return view;
	}
	@RequestMapping(value = "index/eventmenu")
	public ModelAndView eventmenu(HttpServletRequest request,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata){
		ResponseData responseData=new ResponseData("pages/manage/eventmenu");
		responseData.setRqdata(rqdata);
		List<EventMenu> eventMenus=super.getService().findAllByCriteria(DetachedCriteria.forClass(EventMenu.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("parent", "0"))));
		if(eventMenus!=null&&eventMenus.size()>0){
			EventMenu eMenu=eventMenus.get(0);
			if(eMenu!=null){
				responseData.setResult(eMenu.getId()+"/"+eMenu.getType());
				responseData.setValueList(eventMenus);
				responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(EventMenu.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("parent", eMenu.getId())))));
			}
		}
		return request(responseData, orgi, rqdata);
	}
	
	@RequestMapping(value = "index/cms")
	public ModelAndView cms(HttpServletRequest request,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata){
		ResponseData responseData=new ResponseData("pages/manage/cms");
		responseData.setRqdata(rqdata);
		//左边的二级菜单
			List<PageType> valuelist=super.getService().findAllByCriteria(DetachedCriteria.forClass(PageType.class).add(Restrictions.eq("orgi", orgi)).addOrder(Order.desc("name")));
			responseData.setValueList(valuelist);
			String cateid =  "0" ;
			if(valuelist!=null&&valuelist.size()>0){
				cateid = valuelist.get(0).getId();
			}
			responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(PageTemplate.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("parentid", cateid))).addOrder(Order.asc("code"))));
			responseData.setResult(cateid);
		ModelAndView view = request(responseData, orgi, rqdata);
		view.addObject("setting" , EapSmcDataContext.getSearchSetting(orgi)) ;
		return view ;
	}


	/**
	 * 林招远--->内容维护
	 */
	@RequestMapping(value = "index/content")
	public ModelAndView content(HttpServletRequest request,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata){
		ResponseData responseData=new ResponseData("pages/manage/content");
		responseData.setRqdata(rqdata);
		List<AgentSkill> skills=super.getService().findAllByCriteria(DetachedCriteria.forClass(AgentSkill.class).add(Restrictions.eq("orgi", orgi)));
		if(skills!=null&&skills.size()>0){
			AgentSkill ask=skills.get(0);
			responseData.setValueList(skills);
			responseData.setResult(ask.getId());
			responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(Content.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("skillid", ask.getId())))));
		}
		return request(responseData, orgi, rqdata);
	}
	@RequestMapping(value = "index/iface")
    public ModelAndView iface(HttpServletRequest request ,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata) {
		ResponseData responseData = new ResponseData("/pages/manage/iface");
		responseData.setRqdata(rqdata);
		//左边的二级菜单
		List<IfaceCategory> valuelist=super.getService().findAllByCriteria(DetachedCriteria.forClass(IfaceCategory.class).add(Restrictions.eq("orgi", orgi)));
		responseData.setValueList(valuelist);
		if(valuelist!=null&&valuelist.size()>0){
			String cateid=valuelist.get(0).getId();
			responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(IfaceInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("cateid", cateid)))));
			responseData.setResult(cateid);
		}
		return request(responseData , orgi , rqdata) ;
    }
	@RequestMapping(value = "index/location")
	public ModelAndView location(HttpServletRequest request ,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata) {
		ResponseData responseData = new ResponseData("/pages/manage/sinosig/location");
		responseData.setRqdata(rqdata);
		//左边的二级菜单		
		responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(SinoLocation.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("type", "1"))),null,Projections.distinct(Projections.property("province"))));
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(SinoLocation.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.eq("type", "1"))));
		ModelAndView view=request(responseData , orgi , rqdata);
		view.addObject("type", "1");
		view.addObject("sxpros",super.getService().findAllByCriteria(DetachedCriteria.forClass(SinoLocation.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("type", "2"))),null,Projections.distinct(Projections.property("province"))));
		return  view;
	}
	@RequestMapping(value = "index/sinosigstats")
	public ModelAndView sinosigstats(HttpServletRequest request ,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata) {
		 ResponseData responseData = new ResponseData("/pages/manage/sinosigstats");

		    String[] colors = { "AFD8F8", "F6BD0F", "FF8E46", "8BBA00", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE" };
		    String beginTime = "";
		    String endTime = "";

		    String bsql = "";
		    if (!beginTime.equals("")) {
		      bsql = "and msg.title >'" + beginTime + "'";
		    }
		    String esql = "";
		    if (!endTime.equals("")) {
		      esql = "and msg.title <'" + endTime + "'";
		    }
		    StringBuffer sb = new StringBuffer();
		    String sql = "select msg.name,count(*) from DataDic msg where msg.orgi='" + orgi + "'  " + bsql + "  " + esql + " and msg.name is not null  and msg.name!='关注提示' group by msg.name order by count(*) desc";
		    sb.append("<graph caption='菜单点击统计' xAxisName='菜单' yAxisName='总数' showNames='1' decimalPrecision='0' formatNumberScale='0'>");
		    List list = super.getService().hqlList(sql, DataDic.class, 33, 1);
		    for (Iterator localIterator = list.iterator(); localIterator.hasNext(); ) { Object object = localIterator.next();
		      Object[] account = (Object[])object;
		      if ((account != null) && (account.length == 2)) {
		        sb.append("<set name='").append(account[0]).append("' value='").append(account[1]).append("' color='").append(colors[new java.util.Random().nextInt(12)]).append("' />");
		      }
		    }
		    if ((list == null) || ((list != null) && (list.size() == 0))) {
		      sb.append("<set name='无数据' value='0' ").append(" color='").append(colors[new java.util.Random().nextInt(12)]).append("' />");
		    }
		    sb.append("</graph>");
		    responseData.setMessage(sb.toString());
		    return request(responseData, orgi, rqdata);
	}
	@RequestMapping(value = "index/fans")
	public ModelAndView fans(HttpServletRequest request ,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata) {
		ResponseData responseData = new ResponseData("/pages/manage/fans");
		responseData.setRqdata(rqdata);
		//左边的二级菜单		
		List<UserGroup> groups=super.getService().findPageByCriteria(DetachedCriteria.forClass(UserGroup.class).add(Restrictions.eq("orgi", orgi)));
		if(groups!=null && groups.size()>0){
			responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(WeiXinUser.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.and(Restrictions.eq("channel", EapDataContext.ChannelTypeEnum.WEIXIN.toString()), Restrictions.eq("groupID", groups.get(0).getCode())))));
			 responseData.setValueList(groups);
			 responseData.setResult(groups.get(0).getCode());
		}
		return  request(responseData , orgi , rqdata);
	}
	@RequestMapping(value = "/index/agentStatus")
    public ModelAndView agentStatus(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/agentstatus"  ) ; 
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(AgentServiceStatus.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.ne("operatetype", "3")).addOrder(Order.desc("createtime")), data.getPs(), data.getP()));
		return request(responseData , orgi , data) ;
    }
	/**
	 * 林招远--->通信报表
	 */
	@RequestMapping(value = "index/reportstats")
	public ModelAndView reportstats(HttpServletRequest request,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata){
		 ResponseData responseData = new ResponseData("pages/manage/reportstats");
		 	String beginTime= "" ;
		 	String endTime="";
		    String[] colors = { "AFD8F8", "F6BD0F", "FF8E46", "8BBA00", "008E8E", "D64646", "8E468E", "588526", "B3AA00", "008ED6", "9D080D", "A186BE" };
		    List agentlist = super.getService().hqlList("select to_char(agt.logindate,'yyyy-mm-dd'),count(*) from AgentUser agt where orgi='" + orgi + "' " + ((beginTime == null) || ("".equals(beginTime)) ? "" : new StringBuilder("and logindate >= to_date('").append(beginTime).append("','yyyy-MM-dd')").toString()) + ((endTime == null) || ("".equals(endTime)) ? "" : new StringBuilder("and logindate <= to_date('").append(endTime).append("','yyyy-MM-dd')").toString()) + " group by to_char(logindate,'yyyy-mm-dd') order by to_char(logindate,'yyyy-mm-dd') desc", WeiXin.class, 10, 1);
		    StringBuffer sb = new StringBuffer();
		    sb.append("<graph caption='通信次数' xAxisName='日期' yAxisName='总数' showNames='1' decimalPrecision='0' formatNumberScale='0'>");
		    for (Iterator localIterator = agentlist.iterator(); localIterator.hasNext(); ) { Object object = localIterator.next();
		      Object[] agent = (Object[])object;
		      if ((agent != null) && (agent.length == 2)) {
		        sb.append("<set name='").append(agent[0]).append("' value='").append(agent[1]).append("' color='").append(colors[new java.util.Random().nextInt(12)]).append("' />");
		      }
		    }
		    if ((agentlist == null) || ((agentlist != null) && (agentlist.size() == 0))) {
		      sb.append("<set name='无数据' value='0' ").append(" color='").append(colors[new java.util.Random().nextInt(12)]).append("' />");
		    }
		    sb.append("</graph>");
		    responseData.setMessage(sb.toString());
		    return request(responseData, orgi, rqdata);
	}
	 
	/**
	 * 林招远--->回话统计
	 */
	@RequestMapping(value = "index/replyAccount")
	public ModelAndView replyAccount(HttpServletRequest request,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata){
		ResponseData responseData=new ResponseData("pages/manage/replyAccount");
		String result=null;
		IndexController ic=new IndexController();
		result=ic.getreplyAccount("weixin");
		responseData.setResult(result);
		return request(responseData, orgi, rqdata);
	}
	
	/**
	 * 支付结果查询
	 */
	@RequestMapping(value = "index/orderssearch")
	public ModelAndView orderssearch(HttpServletRequest request,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata){
		ResponseData responseData=new ResponseData("pages/manage/orderssearch");
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi))));		
		return request(responseData, orgi, rqdata);
	}
	
	
	/**
	 *操作信息记录查询
	 */
	@RequestMapping(value = "index/optcount")
	public ModelAndView optcount(HttpServletRequest request,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata){
		ResponseData responseData=new ResponseData("pages/manage/optcount");
		String result=null;
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(OptCount.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.eq("isvalid",new Integer(1)))));		
		return request(responseData, orgi, rqdata);
	}
	
	/**
	 *登陆红包查询
	 */
	@RequestMapping(value = "index/welfare")
	public ModelAndView welfare(HttpServletRequest request,@PathVariable String orgi,@ModelAttribute("rqdata") RequestData rqdata){
		ResponseData responseData=new ResponseData("pages/manage/welfare");
		List<StaffWelfare> StaffWelfareList = getService().findPageByCriteria(DetachedCriteria.forClass(StaffWelfare.class).add(Restrictions.eq("remark1", orgi)));
		String  nickName ="";
		responseData.setDataList(StaffWelfareList);		
		if(StaffWelfareList != null && StaffWelfareList.size()>0){
			for(StaffWelfare staff : StaffWelfareList){
				String apiusername = staff.getApiusername();
	    		
	    		List <WeiXinUser> weiXinUserList=super.getService().findAllByCriteria(DetachedCriteria.forClass(WeiXinUser.class).add(Restrictions.and(Restrictions.eq("orgi", orgi),Restrictions.eq("apiusername", apiusername))));
	    		if(weiXinUserList !=null && weiXinUserList.size()>0){
	    			nickName = weiXinUserList.get(0).getNickName();
	    		}
			}
		}
		ModelAndView view = request(responseData, orgi , rqdata);
		view.addObject("nickName", nickName);
		return view;
	}
	
	/**
	 * JDBC
	 */
	Connection connection = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	String [] colors={"AFD8F8","F6BD0F","FF8E46","8BBA00","008E8E","D64646","8E468E","588526","B3AA00","008ED6","9D080D","A186BE"};

	public String getreplyAccount(String channel) {
		StringBuffer sb = new StringBuffer();
		sb.append("<graph caption='回话统计' xAxisName='回复类型' yAxisName='总量' showNames='1' decimalPrecision='0' formatNumberScale='0'>"); 
		String replyType="";
		try {
			connection=DBPersistence.getconnection();
			if (connection != null) {
				try {
					String sql = "select id,replytype,count(*) from rivu_weixinmessage where replytype='manually' and channel= ?  group by replytype union all select id,replytype,count(*) from rivu_weixinmessage where replytype='automatic' and channel= ? group by replytype";
					ps = connection.prepareStatement(sql);
					ps.setString(1, channel);
					ps.setString(2, channel);
					rs = ps.executeQuery();
					
					while(rs.next()){
						if(rs.getString("replytype").equals("manually")){
							replyType="人工回复";
						}else if(rs.getString("replytype").equals("automatic")){
							replyType="系统回复";
						}
						sb.append("<set name='").append(replyType).append("' value='").append(rs.getString("count(*)")).append("' color='").append(colors[new Random().nextInt(12)]).append("' />");
					
					}
					sb.append("</graph>");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						rs.close();
						ps.close();
						connection.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		} catch (Exception e) {
		}
		return sb.toString();
	}
	
	public String getReport(String channel,String type,String beginTime,String endTime){
		StringBuffer sb = new StringBuffer();
		sb.append("<graph caption='通信次数' xAxisName='日期' yAxisName='总数' showNames='1' decimalPrecision='0' formatNumberScale='0'>"); 
		try {
			connection=DBPersistence.getconnection();
			if (connection != null) {
				try {
					String sql;
					if(!type.equals("nullType")){
						 sql = "select "+"left(createtime,4),"+type+"(createtime),count(content) from rivu_weixinmessage where channel=? and (createtime between ? and ?) group by "+type+"(createtime) order by "+type+"(createtime);";	
						 ps = connection.prepareStatement(sql);
						 ps.setString(1, channel);
						 ps.setString(2, beginTime);
						 ps.setString(3, endTime);
						 rs = ps.executeQuery();
						 while (rs.next()) {
							 sb.append("<set name='").append(rs.getString("left(createtime,4)")+"年"+rs.getString(type+"(createtime)")+"月").append("' value='").append(rs.getString("count(content)")).append("' color='").append(colors[new Random().nextInt(12)]).append("' />");
						
						 }
						 sb.append("</graph>");
					}else{
						 sql="select rivu_weixinuser.province,count(rivu_weixinmessage.contextid) from rivu_weixinmessage left join rivu_weixinuser on rivu_weixinmessage.userid=rivu_weixinuser.userid  where  rivu_weixinmessage.channel=? and rivu_weixinuser.province !='' group by rivu_weixinuser.province order by count(rivu_weixinmessage.contextid )  limit 10;";
						 ps = connection.prepareStatement(sql);
						 ps.setString(1, channel);
						 rs = ps.executeQuery();
						 while (rs.next()) {
							 sb.append("<set name='").append(rs.getString("rivu_weixinuser.province")).append("' value='").append(rs.getString("count(rivu_weixinmessage.contextid)")).append("' color='").append(colors[new Random().nextInt(12)]).append("' />");
						}
						 sb.append("</graph>");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						rs.close();
						ps.close();
						connection.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		} catch (Exception e) {
		}
		return sb.toString();
	}
	
}
