package com.neusoft.web.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
@Entity
@Table(name = "rivu_system_config")
@org.hibernate.annotations.Proxy(lazy = false)
public class SystemConfig {
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String orgi; //租户标识
	private Date edittime = new Date();
	private String defaultinfo;
	private String code; //配置的标识
	private int timevalue=1;  //时间参数,给个默认值
	private String conftype; //配置类型，为二级菜单
	private String description;
	private boolean iseffect; //是否提示
	
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
	
	public boolean isIseffect() {
		return iseffect;
	}
	public void setIseffect(boolean iseffect) {
		this.iseffect = iseffect;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public Date getEdittime() {
		return edittime;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setEdittime(Date edittime) {
		this.edittime = edittime;
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
	public int getTimevalue() {
		return timevalue;
	}
	public void setTimevalue(int timevalue) {
		this.timevalue = timevalue;
	}
	public String getConftype() {
		return conftype;
	}
	public void setConftype(String conftype) {
		this.conftype = conftype;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
