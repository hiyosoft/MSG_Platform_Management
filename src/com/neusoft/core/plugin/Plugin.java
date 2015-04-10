package com.neusoft.core.plugin;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.core.channel.WeiXin;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.util.store.EapTools;
import com.neusoft.util.tools.WeiXinTools;
import com.neusoft.web.model.ExtensionPoints;
import com.neusoft.web.model.Instruction;
import com.neusoft.web.model.Material;
import com.neusoft.web.model.SNSAccount;
import com.neusoft.web.model.SearchResultTemplet;

public abstract class Plugin implements InstructPluginInterface{

	@SuppressWarnings("unchecked")
	public String getChannelMessage(Instruction instruct, String text, AgentUser user, String orgi , Channel channel) {
		String message = null ;
		if(text!=null && instruct!=null){
			SearchResultTemplet templet = null ;
			String messageType = "text" ;	//默认 text
			if(instruct!=null && instruct.getPlugin()!=null && instruct.getPlugin().length()>0){
				ExtensionPoints plugin = EapDataContext.getPlugin(instruct.getPlugin()) ;
				if(plugin!=null && plugin.getDscription()!=null && EapSmcDataContext.getSearchResultTempletList(channel.getChannel())!=null){
					messageType = "1".equals(plugin.getDscription())? "news" : "text" ;
				}
			}
			if(EapDataContext.ChannelTypeEnum.WEIXIN.toString().equals(channel.getChannel()) && text.indexOf("sinosigapiusername")>0){
				text=text.replaceAll("sinosigapiusername", ((WeiXin)channel).getFromUserName());
			}
			Map<String,Object> valueMap = new HashMap<String, Object>();
			SNSAccount account=WeiXinTools.getSnsAccount(orgi);
	        boolean hightAPI=account!=null?"1".equals(account.getApipoint()):false;
	        if(hightAPI){
	        	valueMap.put("hightAPI", "1") ;
	        }
			for(int i=0 ; EapSmcDataContext.getSearchResultTempletList(channel.getChannel())!=null && i< EapSmcDataContext.getSearchResultTempletList(channel.getChannel()).size() ; i++){
				SearchResultTemplet srt = EapSmcDataContext.getSearchResultTempletList(channel.getChannel()).get(i) ;
				if(srt.getCode().equals(messageType)){
					templet = srt ;
					break ;
				}
			}
			try {
				valueMap.put("user", channel!=null ? channel.getSnsuser():null) ;
				valueMap.put("msg", channel) ;
				valueMap.put("setting", EapSmcDataContext.getSearchSetting(orgi)) ;
				if(messageType.equals(EapDataContext.MessageType.TEXT.toString())){//RivuDataContext.MessageType.TEXT.toString()
					valueMap.put("text", text) ;
				}else if(messageType.equals(EapDataContext.MessageType.NEWS.toString())){//RivuDataContext.MessageType.NEWS.toString()
					valueMap.put("news", ((Material)(EapDataContext.getService().getIObjectByPK(Material.class, text))).getmItems());
				}else{
					valueMap.put("url", text) ;
				}
				valueMap.put("time", String.valueOf(new Date().getTime())) ;
				valueMap.put("timesecs", String.valueOf((long)(new Date().getTime()/1000))) ;
				message = EapTools.getTemplet(templet, valueMap) ;
			} catch (Exception e) {
				e.printStackTrace();
				message = text ;
			}
		}else{
			message = text ;
		}
		return message ;
	}
	
	public String getChannelMessage(Instruction instruct, AgentUser user, String orgi , Channel channel) {
		return this.getChannelMessage(instruct , instruct.getMemo(), user, orgi, channel) ;
	}
}
