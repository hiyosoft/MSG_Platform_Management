package com.neusoft.util.persistence;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.core.channel.DataMessage;
import com.neusoft.core.channel.SNSUser;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.web.model.FilterHistoryModel;
import com.neusoft.web.model.User;

public interface Persistence {
	/**
	 * 获取消息接口，消息存储位置有两种，
	 * 						1：数据库中
	 * 						2：R3 rivuES中
	 * @param contextid
	 * @param agentserviceid
	 * @return
	 */
	public List<?> getMessagetList(AgentUser agentuser , int p, int ps) ;
	
	/**
	 * 获取消息接口，消息存储位置有两种，
	 * 						1：数据库中
	 * 						2：R3 rivuES中
	 * @param contextid
	 * @param agentserviceid
	 * @return
	 */
	public List<?> getMessagetListBySubType(String channel,String orgi , String subtype, int p, int ps) ;
	
	public List<?> getsumByAgentOrUser(FilterHistoryModel filter, int p, int ps);
	
	public SNSUser getSnsUserInfo(String userid , DataMessage dataMessage);
	
	public Channel getMessage(String id , String channel , String orgi) ;

	public SNSUser getSnsUserInfo(String userid, String channel , String orgi);
	
	public SNSUser getSnsUserInfoByUsername(String uername, String channel , String orgi);
	
	public String getLastInstruct(String userid, DataMessage dataMessage) ;
	
	public void saveUser(SNSUser user) ;
	
	public void updateUser(SNSUser user) ;
	
	public void saveMessage(DataMessage message) ;
	
	public List<?> getMsgMonitor(FilterHistoryModel filter,int ps ,int p);
	
	public void updateMessage(Channel channel) ;
	
	public void rmMessage(Channel channel) ;
}
