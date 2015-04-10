package com.neusoft.util.queue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import com.neusoft.core.channel.DataMessage;
import com.neusoft.web.model.AgentSkill;
import com.neusoft.web.model.User;

public class AgentStatus implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5883426846142132613L;
	public enum AgentStatusEnum{
		READY,
		BUSY,
		IDLE,
		LEAVE,
		SERVICES;
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	
	private String id ;				//坐席ID
	private String agentno ;			//坐席号码
	private Date logindate ;		//登陆时间
	private String status = "0" ;			//坐席状态			0:刚登陆未就绪   1:就绪    2:示忙    3:离开    4:协助 	5: 6: 7
	private String orgi ;			//租户ID	
	private String agentserviceid ;		//会话ID
	private int serusernum 	= 10 ;		//最大服务用户数量
	private List<AgentUser> userList = new ArrayList<AgentUser>() ;						//服务中 客户
	private List<AgentUser> queueList = new ArrayList<AgentUser>() ;					//排队中 客户 ， 暂时不用
	private User user ;
	private AgentSkill agentSkill  ;
	private Map<String , List<DataMessage>> lastMessage = new HashMap<String , List<DataMessage>>();						//保存用户最后一次发送的消息

	public AgentStatus(String id , String agentno , AgentSkill agentSkill , Date logindate , String status , String orgi){
		this.id = id ;
		this.agentno = agentno ;
		this.logindate = logindate ;
		this.status = status ;
		this.orgi = orgi ;
		this.agentSkill = agentSkill ;
	}
	public AgentStatus() {
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAgentno() {
		return agentno;
	}
	public void setAgentno(String agentno) {
		this.agentno = agentno;
	}
	public Date getLogindate() {
		return logindate;
	}
	public void setLogindate(Date logindate) {
		this.logindate = logindate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getAgentserviceid() {
		return agentserviceid;
	}
	public void setAgentserviceid(String agentserviceid) {
		this.agentserviceid = agentserviceid;
	}
	public List<AgentUser> getUserList() {
		List<AgentUser> agentUserList = new ArrayList<AgentUser>();
		/**
		 * 
		 */
		for(AgentUser agentUser : ServiceQueue.getUserQueue().values()){
			if(agentUser.getOrgi()!=null && agentUser.getOrgi().equals(this.orgi) && this.agentno.equals(agentUser.getAgentno())){
				agentUserList.add(agentUser) ;
			}
		}
		return agentUserList;
	}
	public void setUserList(List<AgentUser> userList) {
		this.userList = userList;
	}
	public List<AgentUser> getQueueList() {
		return queueList;
	}
	public void setQueueList(List<AgentUser> queueList) {
		this.queueList = queueList;
	}
	public int getSerusernum() {
		return serusernum;
	}
	public void setSerusernum(int serusernum) {
		this.serusernum = serusernum;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public AgentSkill getAgentSkill() {
		return agentSkill;
	}
	public void setAgentSkill(AgentSkill agentSkill) {
		this.agentSkill = agentSkill;
	}
	@Transient
	public Map<String, List<DataMessage>> getLastMessage() {
		return lastMessage;
	}
	public void setLastMessage(Map<String, List<DataMessage>> lastMessage) {
		this.lastMessage = lastMessage;
	}
}
