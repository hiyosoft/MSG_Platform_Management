package com.neusoft.web.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

/**
 * 坐席对象
 * @author 
 *
 */
@Entity
@Table(name = "rivu_agent_info")
@org.hibernate.annotations.Proxy(lazy = false)
public class AgentInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String orgi; //租户标识
	private Date createtime = new Date();
	private String defaultinfo;
	private String code; //坐席号
	private String userid;  //用户id
	private String username;  //用户name
	private String createuser;//创建人
	private String skillid;//技能组id
	private boolean status;
	private Date edittime = new Date();
	private String editid;
	private String platformid;
	private String recordplatformid;
	private String userd;
	private String agentpassword;
	
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

	public String getSkillid() {
		return skillid;
	}
	public void setSkillid(String skillid) {
		this.skillid = skillid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getDefaultinfo() {
		return defaultinfo;
	}
	public void setDefaultinfo(String defaultinfo) {
		this.defaultinfo = defaultinfo;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getCreateuser() {
		return createuser;
	}
	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}
	public Date getEdittime() {
		return edittime;
	}
	public void setEdittime(Date edittime) {
		this.edittime = edittime;
	}
	public String getEditid() {
		return editid;
	}
	public void setEditid(String editid) {
		this.editid = editid;
	}
	public String getPlatformid() {
		return platformid;
	}
	public void setPlatformid(String platformid) {
		this.platformid = platformid;
	}
	public String getRecordplatformid() {
		return recordplatformid;
	}
	public void setRecordplatformid(String recordplatformid) {
		this.recordplatformid = recordplatformid;
	}
	public String getUserd() {
		return userd;
	}
	public void setUserd(String userd) {
		this.userd = userd;
	}
	public String getAgentpassword() {
		return agentpassword;
	}
	public void setAgentpassword(String agentpassword) {
		this.agentpassword = agentpassword;
	}
}
