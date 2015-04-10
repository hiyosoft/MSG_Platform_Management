package com.neusoft.core.plugin;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.alibaba.fastjson.JSON;
import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.util.process.ReqeustProcessUtil;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.util.store.EapTools;
import com.neusoft.web.model.IfaceInfo;
import com.neusoft.web.model.Instruction;
import com.neusoft.web.model.SinosigUser;
import com.neusoft.web.model.UserTemplet;

public class UserUnsubscribePlugin extends Plugin{
	private static final Logger log = Logger.getLogger(UserUnsubscribePlugin.class);

	

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initVirInstruct(String orgi , Instruction instruct){
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getMessage(Instruction instruct, AgentUser user, String orgi, Channel channel) {
		// TODO Auto-generated method stub
		System.out.println("====用户取消关注======");
		if(channel.getSnsuser()!=null){
			List<SinosigUser> sinousers=EapDataContext.getService().findAllByCriteria(DetachedCriteria.forClass(SinosigUser.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.and(Restrictions.eq("apiusername", channel.getSnsuser().getApiusername().replaceAll("-", "_")), Restrictions.eq("userstatus", "1")))));
			System.out.println("========用户信息==========="+JSON.toJSON(sinousers));
			if(sinousers!=null && sinousers.size()>0){
				SinosigUser sinosigUser=sinousers.get(0);
				Map<String,Object> params=new HashMap<String,Object>();
		    	params.put("idType", sinosigUser.getCardtype());
		    	params.put("idNo", sinosigUser.getCardno());
		    	params.put("cnName", sinosigUser.getUsername());
		    	params.put("sex", sinosigUser.getSex());
		    	params.put("birthDate", sinosigUser.getBirthday()!=null?new SimpleDateFormat("yyyy-MM-dd").format(sinosigUser.getBirthday()):null);
		    	List<IfaceInfo> querys=EapDataContext.getService().findAllByCriteria(DetachedCriteria.forClass(IfaceInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi),Restrictions.eq("code", "cifquery"))));
		    	if(querys!=null && querys.size()>0){
		    		IfaceInfo query=querys.get(0);
		    		try {
		    			//根据五要素获取客户ID
		    			UserTemplet queryTemplet = EapSmcDataContext.getUserTemplet(query.getReqtemplate(), orgi) ;
		    			params.put("innerinputparam", EapTools.getTemplet(queryTemplet, params));
		    			log.info("CIF获取客户ID==请求报文：\n"+params.get("innerinputparam"));
						String queryVal=ReqeustProcessUtil.getWebServiceResponseBody(query, null, orgi, params);
						log.info("CIF获取客户ID**返回报文：\n"+queryVal);
						if(queryVal.indexOf("</PARTYNO>")>0){//客户号存在
							String partyNo=getxmlVal(queryVal,"PARTYNO");
							List<IfaceInfo> updates=EapDataContext.getService().findAllByCriteria(DetachedCriteria.forClass(IfaceInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi),Restrictions.eq("code", "cifupdate"))));
							if(updates!=null && updates.size()>0){
								IfaceInfo update=updates.get(0);
								params.clear();
								params.put("partyNo", partyNo);
								params.put("opType", "0");//是否更新，1为关注；0为取消关注
								params.put("wechatNo", sinosigUser.getApiusername());
								//更新微信ID
								UserTemplet updateTemplet = EapSmcDataContext.getUserTemplet(update.getReqtemplate(), orgi) ;
				    			params.put("innerinputparam", EapTools.getTemplet(updateTemplet, params));
				    			log.info("CIF更新微信ID==请求报文：\n"+params.get("innerinputparam"));
								String updateVal=ReqeustProcessUtil.getWebServiceResponseBody(update, null, orgi, params);
								log.info("CIF更新微信ID**返回报文：\n"+updateVal);
								//修改用户userstatu为0
								sinosigUser.setUserstatus("0");
								EapDataContext.getService().updateIObject(sinosigUser);
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    	}
			}
		}
		return "" ;
	}
	private static String getxmlVal(String xml,String key){
    	return xml.indexOf("<"+key+">")>0? xml.substring(xml.indexOf("<"+key+">")+(key.length()+2),xml.indexOf("</"+key+">")):"";
    }
}
