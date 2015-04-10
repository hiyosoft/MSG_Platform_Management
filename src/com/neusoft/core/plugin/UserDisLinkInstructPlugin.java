package com.neusoft.core.plugin;

import java.util.Date;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.api.APIContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.util.queue.AgentStatus;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.util.queue.ServiceQueue;
import com.neusoft.web.model.Instruction;

public class UserDisLinkInstructPlugin extends Plugin{
	/**
	 * 测试用，消息 拼出来的
	 * @param user
	 * @param orgi
	 * @return
	 */
	public String getMessage(Instruction instruct ,AgentUser user , String orgi , Channel channel){
		if(user!=null){
			AgentUser agentUser = user ;
			agentUser.setDisconnect(true);
			/**
			 * 结束服务，从服务队列中移除 
			 */
			ServiceQueue.removeUser(user.getUserid() , orgi) ;
			ServiceQueue.removeAgentQueueZUserList(orgi, user.getUserid(), user.getAgentno());
			AgentStatus agentstatus = ServiceQueue.getAgent(user.getAgentno(), orgi) ;
			if(agentstatus!=null){
				for(AgentUser au : agentstatus.getUserList()){
					if(au.getUserid().equals(user.getUserid())){
						agentstatus.getUserList().remove(au) ;
						/**
						 * 同时发送消息到 坐席端
						 */
						break ;
					}
				}
			}
			user.setEndtime(new Date()) ;
			user.setSessiontimes(user.getEndtime().getTime() - user.getLogindate().getTime()) ;
			EapDataContext.getService().updateIObject(agentUser) ;
			APIContext.userDisLink(agentUser, orgi, channel) ;
			return super.getChannelMessage(instruct, instruct.getMemo()==null?"您好，本次服务已结束，感谢您的来访，再见！":instruct.getMemo(), user, orgi, channel) ;
		}
		return super.getChannelMessage(instruct, user!=null ? EapSmcDataContext.createPluginMessage(user , instruct, orgi) : "对不起，您不在人工服务状态" , user, orgi, channel);
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initVirInstruct(String orgi , Instruction instruct){
		// TODO Auto-generated method stub
		
	}
}
