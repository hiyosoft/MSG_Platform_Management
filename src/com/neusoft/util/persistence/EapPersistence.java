package com.neusoft.util.persistence;

import java.util.List;

import com.neusoft.core.channel.Channel;
import com.neusoft.core.channel.DataMessage;
import com.neusoft.core.channel.SNSUser;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.web.model.FilterHistoryModel;

public class EapPersistence implements Persistence{

	@Override
	public List<?> getMessagetList(AgentUser agentuser, int p , int ps) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SNSUser getSnsUserInfo(String userid, DataMessage dataMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Channel getMessage(String id, String channel , String orgi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SNSUser getSnsUserInfo(String userid, String channel , String orgi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLastInstruct(String userid, DataMessage dataMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<?> getsumByAgentOrUser(FilterHistoryModel filter, int p, int ps) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveUser(SNSUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveMessage(DataMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<?> getMsgMonitor(FilterHistoryModel filter, int ps, int p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SNSUser getSnsUserInfoByUsername(String uername, String channel,
			String orgi) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateUser(SNSUser user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<?> getMessagetListBySubType(String channel, String orgi,
			String subtype, int p, int ps) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateMessage(Channel channel) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rmMessage(Channel channel) {
		// TODO Auto-generated method stub
		
	}
}
