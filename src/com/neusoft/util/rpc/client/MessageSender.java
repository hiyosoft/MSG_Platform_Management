package com.neusoft.util.rpc.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.thrift.protocol.TProtocol;

import com.alibaba.fastjson.JSON;
import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.util.rpc.message.Message;
import com.neusoft.util.rpc.message.SystemMessage;
import com.neusoft.util.store.EapTools;
import com.neusoft.web.model.SearchSetting;

/**
 * The class responsible for sending messages to the server.
 * 
 * @author Joel Meyer
 */
public class MessageSender{
  private final BlockingQueue<Message> msgSendQueue;
  private boolean running = true;
  
  public MessageSender(
      TProtocol protocol,
      ConnectionStatusMonitor connectionMonitor) {
    this.msgSendQueue = new LinkedBlockingQueue<Message>();
  }
  public void sendMessageToServer(Message msg){
	  send(msg);
  }
  public void send(Message msg) {
	  //如果是推送的文本消息，直接发送，不走队列
	  SystemMessage sysMessage = JSON.parseObject(msg.getMessage(), SystemMessage.class) ;
	  if(EapDataContext.SystemRPComman.MESSAGE.toString().equals(sysMessage.getType()) && msg.getMessage().length()>0){
		  String orgi = getOrgi(msg.getMessage());
          if(sysMessage!=null && orgi!=null){
        	  SearchSetting setting = EapSmcDataContext.getSearchSetting(orgi) ;
        	  EapTools.postString(setting.getGwhost()+"/"+orgi+"/api/message.html", msg.getMessage(), "UTF-8") ;
          }
	  } else {
		  try {
		        /**
		         * 最终通信消息都会从这个位置发送出去 ， 两种途径 都发送， 先检查 GW的 链接是否配置，如果配置，则优先使用GW的 HTTP服务
		         * 发送消息，如果未配置，则启用原来的 RPC方式通信。
		         */
		          SearchSetting setting = null ;
		          String orgi = getOrgi(msg.getMessage());
		          if(sysMessage!=null && orgi!=null){
		        	  setting = EapSmcDataContext.getSearchSetting(orgi) ;
		          }
		          if(setting!=null && setting.getGwhost()!=null && setting.getGwhost().length()>0 && setting.getAuthurl()!=null && setting.getAuthurl().length()>0){
		        	//对于发布的消息，集群时统一通过HTTP发送:并分别推送到两个gateway
		              if(setting!=null && setting.getLoginurl()!=null && setting.getLoginurl().length()>0){
		            	  //如果类型是素材发布、网站内容发布、公众账号推送、（考虑初始化会加载SearchSetting，为避免初始化异常，暂不同步）
		            	  if(EapDataContext.SystemRPComman.MATERIALPUBLISH.toString().equals(sysMessage.getType()) || EapDataContext.SystemRPComman.SITEPUBLISH.toString().equals(sysMessage.getType())  || EapDataContext.SystemRPComman.SNSACCOUNT.toString().equals(sysMessage.getType()) ){
		            		  if(setting.getLoginurl()!=null && setting.getLoginurl().length()>0){
		            			  System.out.println("===gateway1发布返回结果=="+EapTools.postString(setting.getLoginurl() + "/"+setting.getOrgi() + "/api/message.html", msg.getMessage(), "UTF-8")) ;
		            		  }
		            		  if(setting.getRegurl()!=null && setting.getRegurl().length()>0){
		            			  System.out.println("===gateway2发布返回结果=="+EapTools.postString(setting.getRegurl() + "/"+setting.getOrgi() + "/api/message.html", msg.getMessage(), "UTF-8")) ;
		            		  }
		            	  }
		              }else{
		            	  EapTools.postString(setting.getGwhost() + "/"+setting.getOrgi() + "/api/message.html", msg.getMessage(), "UTF-8") ; 
		              }
		    	  }
		        } catch (Exception e) {
		          // The message isn't lost, but it could end up being sent out of
		          // order - not ideal.
		          //msgSendQueue.add(msg);
		        }
	  }
  }
  
  	private String getOrgi(String msg){
  		String orgi = null ;
  		java.util.regex.Pattern pattern = Pattern.compile("\"orgi\":\"([\\S\\s]*?)\"") ;
  		Matcher matcher = pattern.matcher(msg) ;
  		if(matcher.find() && matcher.groupCount()>=1){
  			orgi = matcher.group(1) ;
  		}
  		return orgi ;
  	}
	public boolean isRunning() {
		return running;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
}
