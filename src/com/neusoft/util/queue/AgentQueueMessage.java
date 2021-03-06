package com.neusoft.util.queue;

import com.neusoft.web.model.Instruction;

public class AgentQueueMessage implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -518436498330498648L;
	private AgentUser agentUser ;
	private AgentStatus status ;
	private String message ;
	private String type ;
	private Instruction instruct ;
	
	public AgentQueueMessage(){}
	public AgentQueueMessage(AgentUser agentUser , AgentStatus status , String type , String message){
		this.agentUser = agentUser ;
		this.message = message ;
		this.status = status;
		this.type = type ;
	}
	public AgentUser getAgentUser() {
		return agentUser;
	}
	public void setAgentUser(AgentUser agentUser) {
		this.agentUser = agentUser;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public AgentStatus getStatus() {
		return status;
	}
	public void setStatus(AgentStatus status) {
		this.status = status;
	}
	public Instruction getInstruct() {
		return instruct;
	}
	public void setInstruct(Instruction instruct) {
		this.instruct = instruct;
	}
}
