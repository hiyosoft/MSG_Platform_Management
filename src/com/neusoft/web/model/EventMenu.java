package com.neusoft.web.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
@Entity
@Table(name = "rivu_event_menu")
@org.hibernate.annotations.Proxy(lazy = false)
public class EventMenu {
	private static final long serialVersionUID = 2921254000129416976L;
	private String id ;
	private String orgi ;
	private String name ;
	private String code ;
	private String plugin;
	private String type ;		//类型：menu,message,robot
	private String scope ;		//菜单作用域 
	private String parent ;
	private String memo ;
	private Date createtime ;
	private String userid ;
	private String username ;
	private String matcherule ;		//匹配规则， 1：完全匹配，0：模糊匹配
	private boolean tipdefault =true;	//默认提示
	private String status;
	private String responsetype; //响应的类型： message ,material,template
	private String responseid; //
	private String params; 	//类型为模板的时候，可以配置动态参数
	@Transient
	private EventMenu parentmenu ;	//parent menu
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
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getPlugin() {
		return plugin;
	}
	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getMatcherule() {
		return matcherule;
	}
	public void setMatcherule(String matcherule) {
		this.matcherule = matcherule;
	}
	public boolean isTipdefault() {
		return tipdefault;
	}
	public void setTipdefault(boolean tipdefault) {
		this.tipdefault = tipdefault;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResponsetype() {
		return responsetype;
	}
	public void setResponsetype(String responsetype) {
		this.responsetype = responsetype;
	}
	public String getResponseid() {
		return responseid;
	}
	public void setResponseid(String responseid) {
		this.responseid = responseid;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	@Transient
	public EventMenu getParentmenu() {
		return parentmenu;
	}
	public void setParentmenu(EventMenu parentmenu) {
		this.parentmenu = parentmenu;
	}
}
