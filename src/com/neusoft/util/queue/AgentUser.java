package com.neusoft.util.queue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.neusoft.core.channel.DataMessage;
import com.neusoft.core.channel.SNSUser;

@Entity
@Table(name = "rivu_agentuser")
@org.hibernate.annotations.Proxy(lazy = false)
public class AgentUser implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8657469468192323550L;
	private String id ;
	private String username;
	private String agentno ;
	private String userid ;
	private String channel ;
	private Date logindate ;
	private String source ;
	private Date endtime ;
	private long sessiontimes=0 ;
	private String sessiontype ;
	private String contextid = String.valueOf(System.currentTimeMillis());		//会话ID ， 用户会话ID，在用户初次进入是分配，对话结束后失效
	private String agentserviceid;	//坐席服务ID，用于KPI
	private String orgi ;
	@Transient
	private SNSUser snsuser ;		//用户
	@Transient
	private List<DataMessage> unreplaymessage = new ArrayList<DataMessage>() ;		//未回复消息
	@Transient
	private Date lastmessage = new Date();
	
	
	@Transient
	private Date lastgetmessage = new Date();
	@Transient
	private String lastmsg ;
	@Transient
	private DataMessage lastDataMessage ;
	@Transient
	private boolean tip = false ;
	@Transient
	private boolean agent = false ;
	@Transient
	private boolean fromhis = false ;
	@Transient
	private boolean online = false ;
	@Transient
	private boolean disconnect = false ;
	@Transient
	private AgentUser agentUser ;
	private String agentskill ;
	public AgentUser(){}
	public AgentUser(String agentno , String userid , String channel , SNSUser snsuser , String source){
		this.agentno = agentno; 
		this.userid = userid ;
		this.channel = channel ;
		this.snsuser = snsuser ;
		this.source = source ;
	}
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username!=null ? username : snsuser!=null? snsuser.getUsername(): null ;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public Date getLogindate() {
		return logindate;
	}
	public void setLogindate(Date logindate) {
		this.logindate = logindate;
	}
	public String getContextid() {
		return contextid;
	}
	public void setContextid(String contextid) {
		this.contextid = contextid;
	}
	public String getAgentno() {
		return agentno;
	}
	public void setAgentno(String agentno) {
		this.agentno = agentno;
	}
	public String getAgentserviceid() {
		return agentserviceid;
	}
	public void setAgentserviceid(String agentserviceid) {
		this.agentserviceid = agentserviceid;
	}
	@Transient
	public SNSUser getSnsuser() {
		return snsuser;
	}

	public void setSnsuser(SNSUser snsuser) {
		this.snsuser = snsuser;
	}

	public String getOrgi() {
		return orgi;
	}

	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	@Transient
	public List<DataMessage> getUnreplaymessage() {
		return unreplaymessage;
	}
	public void setUnreplaymessage(List<DataMessage> unreplaymessage) {
		this.unreplaymessage = unreplaymessage;
	}
	@Transient
	public Date getLastmessage() {
		return lastmessage;
	}
	public void setLastmessage(Date lastmessage) {
		this.lastmessage = lastmessage;
	}
	@Transient
	public boolean isTip() {
		return tip;
	}
	public void setTip(boolean tip) {
		this.tip = tip;
	}
	@Transient
	public boolean isDisconnect() {
		return disconnect;
	}
	public void setDisconnect(boolean disconnect) {
		this.disconnect = disconnect;
	}
	public Date getEndtime() {
		return endtime;
	}
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	public long getSessiontimes() {
		return sessiontimes;
	}
	public void setSessiontimes(long sessiontimes) {
		this.sessiontimes = sessiontimes;
	}
	@Transient
	public String getSessiontype() {
		return sessiontype;
	}
	public void setSessiontype(String sessiontype) {
		this.sessiontype = sessiontype;
	}
	@Transient
	public AgentUser getAgentUser() {
		return agentUser;
	}
	public void setAgentUser(AgentUser agentUser) {
		this.agentUser = agentUser;
	}
	public String getAgentskill() {
		return agentskill;
	}
	public void setAgentskill(String agentskill) {
		this.agentskill = agentskill;
	}
	@Transient
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	@Transient
	public boolean isFromhis() {
		return fromhis;
	}
	public void setFromhis(boolean fromhis) {
		this.fromhis = fromhis;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	@Transient
	public Date getLastgetmessage() {
		return lastgetmessage;
	}
	public void setLastgetmessage(Date lastgetmessage) {
		this.lastgetmessage = lastgetmessage;
	}
	@Transient
	public String getLastmsg() {
		return lastmsg;
	}
	public void setLastmsg(String lastmsg) {
		this.lastmsg = lastmsg;
	}
	@Transient
	public DataMessage getLastDataMessage() {
		return lastDataMessage;
	}
	public void setLastDataMessage(DataMessage lastDataMessage) {
		this.lastDataMessage = lastDataMessage;
	}
	@Transient
	public boolean isAgent() {
		return agent;
	}
	public void setAgent(boolean agent) {
		this.agent = agent;
	}
}
