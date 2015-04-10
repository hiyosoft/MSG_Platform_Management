package com.neusoft.core.api;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.core.channel.DataMessage;
import com.neusoft.core.channel.SNSUser;
import com.neusoft.core.channel.WeiXin;
import com.neusoft.util.comet.demo.talker.Constant;
import com.neusoft.util.persistence.PersistenceFactory;
import com.neusoft.util.queue.AgentQueueMessage;
import com.neusoft.util.queue.AgentStatus;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.util.queue.ServiceQueue;
import com.neusoft.util.rpc.client.Client;
import com.neusoft.util.rpc.client.MessageSender;
import com.neusoft.util.rpc.message.Message;
import com.neusoft.util.rpc.message.SystemMessage;
import com.neusoft.util.store.EapTools;
import com.neusoft.web.model.SNSAccount;
import com.neusoft.web.model.SearchSetting;

public class APIContext {
	
	public static void saveMessage(DataMessage message , AgentQueueMessage agentQueueMessage){
		if(agentQueueMessage!=null && agentQueueMessage.getAgentUser()!=null){
			message.getChannel().setContextid(agentQueueMessage.getAgentUser().getContextid()) ;
			message.getChannel().setUserid(agentQueueMessage.getAgentUser().getSnsuser().getUserid()) ;
			message.getChannel().setUsername(agentQueueMessage.getAgentUser().getSnsuser().getUsername()) ;
			saveMessage(message) ;
		}else{
			WeiXin wx=null;
			if(EapDataContext.ChannelTypeEnum.WEIXIN.toString().equals(message.getChannel().getChannel()) && !EapDataContext.MessageType.TEXT.toString().equals(message.getChannel().getMessagetype())){
				wx=(WeiXin) message.getChannel();
				if(wx!=null && wx.getContent()!=null && wx.getContent().length()<100){
					wx.setContentstr(wx.getContent());
					message.setChannel(wx);
				}
			}
			saveMessage(message) ;
		}
	}
	public static void saveUser(SNSUser snsuser) {
		final SNSUser saveUsr=snsuser;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				PersistenceFactory.getInstance().saveUser(saveUsr);
			}
		}).start();
		
	}
	public static List getMessageListBySubType(String channel , String subtype , String orgi , int p, int ps){
		return PersistenceFactory.getInstance().getMessagetListBySubType(channel, orgi, subtype, p, ps) ;
	}
	public static void saveMessage(DataMessage message){
		PersistenceFactory.getInstance().saveMessage(message);
	}
	
	/**
	 * 
	 * @param userid
	 * @return
	 */
	public static SNSUser getUserInfo(DataMessage dataMessage ,String channel ,  String userid){
		return PersistenceFactory.getInstance().getSnsUserInfo(userid, dataMessage);
	}
	/**
	 * 消息处理
	 * @param dataMessage
	 */
	static long times=0;
	public static void processMessage(DataMessage dataMessage) {
		times = System.currentTimeMillis();
		System.out.println("=====SMC接收到Gateway消息==="+dataMessage.getUserid());
		dataMessage.setType(Constant.TALK) ;
		/**
		 * 排队，获取坐席
		 */
		AgentQueueMessage agentQueueMessage = null ;
		try{
			long times2=System.currentTimeMillis();
			agentQueueMessage = ServiceQueue.userServiceRequest(dataMessage.getOrgi(), dataMessage.getChannel().getSnsuser(), false , dataMessage , ServiceQueue.maxUserNum , 0) ;
//			agentQueueMessage = new AgentQueueMessage(null , null ,RivuDataContext.AgentQueueMessageType.AUTOMATICREPLY.toString() , "测试====");
			System.out.println("====agentQueueMessage处理消息用时：====="+(System.currentTimeMillis()-times2)+"\t"+((agentQueueMessage!=null)?agentQueueMessage.getMessage():""));
			/** 
			 * 保存消息
			 */
			if(agentQueueMessage!=null && (agentQueueMessage.getType().equals(EapDataContext.AgentQueueMessageType.NOAGENT.toString()) || agentQueueMessage.getType().equals(EapDataContext.AgentQueueMessageType.INSERVICE.toString()) || agentQueueMessage.getType().equals(EapDataContext.AgentQueueMessageType.LINEUP.toString()) || agentQueueMessage.getType().equals(EapDataContext.AgentQueueMessageType.ALLOTAGENT.toString()) || agentQueueMessage.getType().equals(EapDataContext.AgentQueueMessageType.AUTOMATICREPLY.toString()))){
				/**
				 * 发送消息回渠道来源 ，提示正在进行排队，提示当前排队数量 
				 */
				/*if(!agentQueueMessage.getType().equals(RivuDataContext.AgentQueueMessageType.AUTOMATICREPLY.toString()) && agentQueueMessage.getMessage().length()>0){
					List<SearchResultTemplet> srTempletList = SmcRivuDataContext.getSearchResultTempletList(dataMessage.getChannel().getChannel()) ;
					if(srTempletList!=null){
						for(SearchResultTemplet srt : srTempletList){
							if(RivuDataContext.MessageType.TEXT.toString().equals(srt.getCode())){
								HashMap<String,Object> values = new HashMap<String,Object>();
								try {
									values.put("user", dataMessage.getChannel().getSnsuser()) ;
									values.put("msg", dataMessage.getChannel()) ;
									values.put("setting", SmcRivuDataContext.getSearchSetting(dataMessage.getOrgi())) ;
									values.put("text", agentQueueMessage.getMessage()) ;
									
									values.put("time", String.valueOf(new Date().getTime())) ;
									agentQueueMessage.setMessage(EapTools.getTemplet(srt, values)) ;
								} catch (IOException e) {
									e.printStackTrace();
								} catch (TemplateException e) {
									e.printStackTrace();
								}
								break ;
							}
						}
					}
				}*/
				retMessageToUser(agentQueueMessage , dataMessage) ; 
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	 * 弹出消息数据给坐席端
	 * @param agentQueueMessage
	 * @param dataMessage
	 */
	public static void sendMessageToAgent(AgentQueueMessage agentQueueMessage , DataMessage dataMessage){
		/**
		 * 如果坐席不为空，将消息转发给坐席 ， 否则向用户返回一条消息，提示进入排队
		 */
		if(agentQueueMessage!=null && agentQueueMessage.getAgentUser()!=null){
			/**
			 * 清除掉 二进制消息
			 */
			dataMessage.getChannel().setBytedata(null) ;
			String temptext = dataMessage.getChannel().getText() ;
			/**
			 * 避免发送过大的内容到前台导致链接丢失
			 */
			if(dataMessage.getChannel().getText().length()>20){
				dataMessage.getChannel().setText("") ;
			}
			APIContext.sendToAgent(Constant.APP_CHANNEL, agentQueueMessage.getAgentUser().getAgentno() , dataMessage , dataMessage.getOrgi()) ;
			dataMessage.getChannel().setText(temptext) ;
			
			if(agentQueueMessage.getType().equals(EapDataContext.AgentQueueMessageType.ALLOTAGENT.toString())){
				APIContext.sendToAgent(Constant.APP_CHANNEL, agentQueueMessage.getAgentUser().getAgentno() , new DataMessage(agentQueueMessage.getAgentUser().getId(),Constant.NEWUSER, dataMessage.getChannel(), dataMessage.getOrgi(), agentQueueMessage.getAgentUser().getUserid()) , dataMessage.getOrgi()) ;
			}
		}
	}
	/**
	 * 发送消息给客户
	 * @param agentQueueMessage
	 * @param dataMessage
	 */
	public static void retMessageToUser(AgentQueueMessage agentQueueMessage , DataMessage dataMessage){
		String userMessage = dataMessage.getChannel().getText() ;
		dataMessage.getChannel().setText(agentQueueMessage.getMessage()) ;
		dataMessage.getChannel().setTouser(dataMessage.getUserid()) ;		//设置touser
		byte[] data = dataMessage.getChannel().getBytedata() ;
		dataMessage.getChannel().setBytedata(null) ;
		sendMessageToUser(dataMessage , true) ;	//所有通过此方法调用的消息都是　自动回复的消息
//		APIContext.getRpcServer().sendMessageToServer(new Message(RivuDataContext.HANDLER,JSON.toJSONString(new SystemMessage(RivuDataContext.SystemRPComman.MESSAGE.toString() , dataMessage) , SerializerFeature.WriteClassName))) ;
		dataMessage.getChannel().setText(userMessage) ;
		dataMessage.getChannel().setBytedata(data) ;
	}
	
	/**
	 * 加上了 统计自动回复消息信息 ， 自动回复的消息有两种， 一种是将 AgentQueueMessage的 回复类型设置为 AUTOMATICREPLY ， 即用户发送消息的 自动回复消息  ， 另外一类是由系统主动发起的 回复消息 
	 * @param dataMessage
	 * @param auto
	 */
	private static void sendMessageToUser(DataMessage dataMessage , boolean autoreplay){
		/**
		 * 统计发送的消息量
		 */
		EapDataContext.staticSendRuntimeData(dataMessage.getLength()) ;
		if(EapDataContext.ReplyType.AUTOMATIC.toString().equals(dataMessage.getChannel().getReplytype()) || autoreplay){
			EapDataContext.staticAutoMessageRuntimeData(dataMessage.getLength()) ;
		}
		APIContext.getRpcServer().send(new Message(EapDataContext.HANDLER,JSON.toJSONString(new SystemMessage(EapDataContext.SystemRPComman.MESSAGE.toString() , dataMessage) , SerializerFeature.WriteClassName))) ;
	}
	/**
	 * 坐席回复客户消息发送渠道(只走http方式)
	 * @param msg
	 * @param orgi
	 */
	public static void sendMsg(DataMessage msg,String orgi){
		SearchSetting setting = null ;
		Message message = new Message(EapDataContext.HANDLER,JSON.toJSONString(new SystemMessage(EapDataContext.SystemRPComman.MESSAGE.toString() , msg) , SerializerFeature.WriteClassName)) ;
        SystemMessage sysMessage = JSON.parseObject(message.getMessage(), SystemMessage.class) ;
        if(sysMessage!=null && orgi!=null){
       	  setting = EapSmcDataContext.getSearchSetting(orgi) ;
         }
         if(setting!=null && setting.getGwhost()!=null && setting.getGwhost().length()>0){
        	 EapTools.postString(setting.getGwhost() + "/"+setting.getOrgi() + "/api/message.html", message.getMessage(), "UTF-8") ; 
		  }
		
	}
	/**
	 * 给GW返回接口数据消息 
	 * @param dataMessage
	 * @param auto
	 */
	public static void responseMessageToGW(SystemMessage systeMessage ){
		if(APIContext.getRpcServers().size()>0){
			APIContext.getRpcServer().send(new Message(EapDataContext.HANDLER,JSON.toJSONString(systeMessage , SerializerFeature.WriteClassName))) ;
		}
	}
	/**
	 * 发送消息给客户
	 * @param agentQueueMessage
	 * @param dataMessage
	 */
	public static void sendMessageToUser(DataMessage dataMessage){
		sendMessageToUser(dataMessage , false);
	}
	
	private static List<Client> rpcServers = new ArrayList<Client>();	//live server
	private static List<Client> waitConnectionServers = new ArrayList<Client>();	//
	
	public static List<Client> getWaitConnectionServers(){
		return waitConnectionServers ;
	}
    /**
     * 
     * @return
     */
    public static List getRpcServers(){
    	List<String> list = new ArrayList();
    	list.add("server") ;
		return list ;
    }
    /**
     * RPC 调用的 客户端 ， Gateway 作为 服务器
     * @param client
     */
    /**
     * 
     * @return
     */
    public static MessageSender getRpcServer(){
		return new MessageSender(null, null);
    }
    /**
     * 
     * @param user
     * @param orgi
     * @param channel
     */
    public static void userDisLink(AgentUser user , String orgi , Channel channel){
    	channel=new WeiXin();
    	DataMessage dataMessage = new DataMessage(user.getUserid(),Constant.DOWN, channel , user.getOrgi(), user.getUserid()) ;
		channel.setText("--------------客户已断开链接----------------");
		channel.setReplytype(EapDataContext.ReplyType.AUTOMATIC.toString());
		//在ServiceQueue保存超时断开的消息，看情况是否需考虑用户主动断开的消息保存
		//PersistenceFactory.getInstance().saveMessage(dataMessage);
		sendToAgent(Constant.APP_CHANNEL, user.getAgentno() , dataMessage , orgi);
    }
    
    public static void createMenu(Message message,String orgi){
    	  SearchSetting setting = null ;
          SystemMessage sysMessage = JSON.parseObject(message.getMessage(), SystemMessage.class) ;
    	   if(sysMessage!=null && orgi!=null){
         	  setting = EapSmcDataContext.getSearchSetting(orgi) ;
           }
    	 EapTools.postString(setting.getGwhost() + "/"+setting.getOrgi() + "/api/message.html", message.getMessage(), "UTF-8") ;
    }
    /**
     * 发送SNS账号到 GW
     * @param accountList
     */
    public static void sendSNSAccountToGW(List<SNSAccount> accountList){
    	if(APIContext.getRpcServers().size()>0){
    		APIContext.getRpcServer().send(
				new Message(EapDataContext.HANDLER, JSON.toJSONString(new SystemMessage(EapDataContext.SystemRPComman.SNSACCOUNT.toString(), accountList), SerializerFeature.WriteClassName)));
    	}
    }
    /**
     * 发送消息
     * @param channel
     * @param agent
     * @param dataMessage
     * @param orgi
     */
	public static void sendToAgent(String channel , String agent , DataMessage dataMessage , String orgi){
		{
			AgentStatus agentStatus = ServiceQueue.getAgentQueue().get(agent);
			if(agentStatus!=null && dataMessage.getUserid()!=null){
				if(agentStatus.getLastMessage().get(dataMessage.getUserid())==null){
					agentStatus.getLastMessage().put(dataMessage.getUserid(), new ArrayList<DataMessage>()) ;
				}else{
					if(!Constant.DOWN.equalsIgnoreCase(dataMessage.getType())){
						agentStatus.getLastMessage().get(dataMessage.getUserid()).clear();
					}
				}
				agentStatus.getLastMessage().get(dataMessage.getUserid()).add(dataMessage) ;
				ServiceQueue.getAgentQueue().put(agent, agentStatus);
			}
		}
//		CometContext.getInstance().getEngine().sendTo(Constant.APP_CHANNEL, agent , dataMessage , orgi) ;
	}
}
