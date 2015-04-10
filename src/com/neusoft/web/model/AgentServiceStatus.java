package com.neusoft.web.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Proxy;

@Entity
@Table(name = "rivu_agent_service_status")
@Proxy(lazy = false)
public class AgentServiceStatus implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;                            // 主键id
	private String agentno;                       // 座席号
	private String agentname;                     // 座席名字
	private String skill;                       // 技能组
	private Date operatetime ;                 //操作时间
	private String operatetype;               // 操作类型：1：上线      0：暂停服务
	private Date createtime;                  // 上次充值时间
	private String orgi;                       // 租户

	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	public String getId() {
		return this.id;
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

	public String getAgentname() {
		return agentname;
	}

	public void setAgentname(String agentname) {
		this.agentname = agentname;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public Date getOperatetime() {
		return operatetime;
	}

	public void setOperatetime(Date operatetime) {
		this.operatetime = operatetime;
	}

	public String getOperatetype() {
		return operatetype;
	}

	public void setOperatetype(String operatetype) {
		this.operatetype = operatetype;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getOrgi() {
		return orgi;
	}

	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
}