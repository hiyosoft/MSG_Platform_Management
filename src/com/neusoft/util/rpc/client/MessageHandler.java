package com.neusoft.util.rpc.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.thrift.TException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.neusoft.core.EapDataContext;
import com.neusoft.core.api.APIContext;
import com.neusoft.core.channel.DataMessage;
import com.neusoft.core.channel.SNSUser;
import com.neusoft.core.channel.WeiXinUser;
import com.neusoft.util.persistence.DBPersistence;
import com.neusoft.util.persistence.PersistenceFactory;
import com.neusoft.util.process.ProcessResult;
import com.neusoft.util.process.ReqeustProcessUtil;
import com.neusoft.util.rpc.message.Message;
import com.neusoft.util.rpc.message.MessageService;
import com.neusoft.util.rpc.message.SystemMessage;
import com.neusoft.util.rpc.message.MessageService.Iface;
import com.neusoft.web.model.IfaceInfo;
import com.neusoft.web.model.PageTemplate;
import com.neusoft.web.model.SNSAccount;
import com.neusoft.web.model.UserGroup;

import freemarker.template.TemplateException;

public class MessageHandler implements MessageService.Iface {
	private Object data ;
	private Iface client ;
	private long lastPingTime ;
	@SuppressWarnings("unchecked")
	@Override
	public void process(Message msg) {
		SystemMessage systemMessage = JSON.parseObject(msg.getMessage(), SystemMessage.class);
		if(EapDataContext.SystemRPComman.PING.toString().equals(systemMessage.getType())){
			((MessageSender)this.getMessageSender()).send(msg) ;
			//this.client.setLastPingTime(System.currentTimeMillis()) ;
			((Client)this.client).setId(msg.getMessage()) ;
			return ;
		}
		if(EapDataContext.SystemRPComman.GWREQUEST.toString().equals(systemMessage.getType())){
			systemMessage.setType(EapDataContext.SystemRPComman.SMCRESPONSE.toString());
			//获取GW传递过来的网页信息、接口列表以及map参数
			PageTemplate message=(PageTemplate) systemMessage.getMessage();
			String ifaces []= message.getIfaces()!=null && message.getIfaces().length() >0? (message.getIfaces() .split(",")) :null;
			Map<String,Object> resultVal=new HashMap<String,Object>();
			int type = 0 ;
			SNSUser snsUser = null ;
			if(ifaces!=null && ifaces.length>0){
				for (String str : ifaces) {
					IfaceInfo ifaceinfo=(IfaceInfo) EapDataContext.getService().getIObjectByPK(IfaceInfo.class, str);
					if(ifaceinfo!=null){
						//key为接口的别名
						try {
							ProcessResult result = ReqeustProcessUtil.getResponseBody(ifaceinfo,  message.getSnsUser(), message.getOrgi() ,message.getParams()) ;
							if(result!=null && result.getResponseBody()!=null){
								resultVal.put(ifaceinfo.getCode(), result.getResponseBody());
							}
							if(result!=null && result.getResultVal()!=null){
								resultVal.putAll(result.getResultVal()) ;
							}
							if(result.getSnsUser()!= null){
								snsUser = result.getSnsUser() ;
							}
							if(type < result.getType()){
								type = result.getType();
							}
						} catch (IOException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			ProcessResult result = new ProcessResult(type, resultVal) ; 
			if(snsUser!=null){
				result.setSnsUser(snsUser) ;
			}
			//给GW返回数据
			systemMessage.setMessage(result);
			APIContext.responseMessageToGW(systemMessage);
			return ;
		}
		if(EapDataContext.SystemRPComman.UPDATESNSACCOUNT.toString().equals(systemMessage.getType())){
			SNSAccount snsAccount = (SNSAccount)systemMessage.getMessage() ;
			EapDataContext.getService().updateIObject(snsAccount) ;
			return ;
		}
		if(EapDataContext.SystemRPComman.GETUSER.toString().equals(systemMessage.getType())){
			SNSUser snsUser = (SNSUser)systemMessage.getMessage() ;
			snsUser = PersistenceFactory.getInstance().getSnsUserInfo(snsUser.getUserid(), snsUser.getChannel(), snsUser.getOrgi()) ;
			systemMessage.setMessage(snsUser);
			APIContext.responseMessageToGW(systemMessage);
			return ;
		}
		if(EapDataContext.SystemRPComman.GWRESPONSEUSERS.toString().equals(systemMessage.getType())){
			List<WeiXinUser> users = (List<WeiXinUser>)systemMessage.getMessage() ;
			//key is the group's code、value is group
			Map<String,UserGroup> groupMap=new HashMap<String, UserGroup>();
			List<UserGroup> groups=null;
			//获取到系统所有分组情况：1.如果有系统不存在的分组，则添加2.如果当前数据与最新不一致，则更新为最新
			String orgi="";
			if(users!=null && users.size()>0){
				orgi=users.get(0).getOrgi();
				groups=EapDataContext.getService().findAllByCriteria(DetachedCriteria.forClass(UserGroup.class).add(Restrictions.eq("orgi",orgi)));
			}
			for (UserGroup userGroup : groups) {
				if(!groupMap.containsKey(userGroup.getCode())){
					groupMap.put(userGroup.getCode(), userGroup);
				}
			}
			for (WeiXinUser user : users) {
				//TODO:如何区分用户,是否要考虑多租户的情况;如果数据库有fakeid,则不处理
				if(groupMap.containsKey(user.getGroupID()) && user.getMemo()!=null && !user.getMemo().equals(groupMap.get(user.getGroupID()).getGroupName())){
					//update group
					UserGroup ug=groupMap.get(user.getGroupID());
					ug.setGroupName(user.getMemo());
					EapDataContext.getService().updateIObject(ug);
				}
				if(!groupMap.containsKey(user.getGroupID())){
					//save group
					UserGroup ug=new UserGroup();
					ug.setGroupName(user.getMemo());
					ug.setCode(user.getGroupID());
					ug.setGroupType("1");
					ug.setOrgi(orgi);
					groupMap.put(user.getGroupID(), ug);
					EapDataContext.getService().saveIObject(ug);
				}
				List<WeiXinUser> wx=EapDataContext.getService().findAllByCriteria(DetachedCriteria.forClass(WeiXinUser.class).add(Restrictions.and(Restrictions.eq("fakeId", user.getFakeId()), Restrictions.eq("orgi",user.getOrgi()))));
				if(wx==null ||  wx.size()==0){
					//保存，考虑id
					user.setId(UUID.randomUUID().toString());
					EapDataContext.getService().saveIObject(user);
				}
			}
			//APIContext.responseMessageToGW(systemMessage);
			return ;
		}
		DataMessage dataMessage = (DataMessage) systemMessage.getMessage() ;
		/**
		 * 统计接收到的消息
		 */
		EapDataContext.staticReciveRuntimeData(dataMessage.getLength()) ;
		/**
		 * 接入排队
		 */
		if (dataMessage.getChannel().getSnsuser() == null) {
			/**
			 * 查询用户ID，如果找到，则转发给排队坐席，如果未找到，则请求用户信息
			 */
			SNSUser user = APIContext.getUserInfo(dataMessage, dataMessage.getChannel().getChannel(), dataMessage.getUserid());
			/**
			 * 如果没有用户信息
			 */
			if (user == null) {
				try {
					user = (SNSUser) EapDataContext.getSNSUserBean(dataMessage.getChannel().getChannel() , EapDataContext.SNSBeanType.USER.toString()).newInstance();
					/**
					 * 获取用户信息
					 */
					this.process(new Message(EapDataContext.SystemRPComman.GETUSER.toString(), JSON.toJSONString(user, SerializerFeature.WriteClassName)));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			dataMessage.getChannel().setSnsuser(user) ;
		}
		/**
		 * 消息处理
		 */
		APIContext.processMessage(dataMessage);
	}

	@Override
	public void setMessageSender(Object sender) {
		this.data = sender ;
	}

	@Override
	public Object getMessageSender() {
		// TODO Auto-generated method stub
		return this.data;
	}

	public Iface getClient() {
		return client;
	}


	public void setClient(Iface client) {
		this.client = client;
	}

	public long getLastPingTime() {
		return lastPingTime;
	}

	public void setLastPingTime(long lastPingTime) {
		this.lastPingTime = lastPingTime;
	}
}
