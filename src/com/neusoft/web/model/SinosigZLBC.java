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
@Table(name = "rivu_sinosig_zlbc")
@org.hibernate.annotations.Proxy(lazy = false)
public class SinosigZLBC implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String orgi;	 //租户标识
	private Date createtime = new Date();
	private String apiusername;
	private String caseid; 	//事故号，由接口推送获取
	private Integer status;  //状态标识，是否上传成功（0：上传中；1：上传成功2：上传失败；）
	private String zipfile;
	private String orgnum;
	private String comcode;
	private String opid;
	private String opuser;
	
	/**
	 * 出险介绍
	 */
	private String damagecase;
	
	/**
	 * 需要补充的资料
	 */
	private String claimmsg;
	
	
	
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
	
	public String getOrgnum() {
		return orgnum;
	}
	public void setOrgnum(String orgnum) {
		this.orgnum = orgnum;
	}
	public String getComcode() {
		return comcode;
	}
	public void setComcode(String comcode) {
		this.comcode = comcode;
	}
	public String getOpid() {
		return opid;
	}
	public void setOpid(String opid) {
		this.opid = opid;
	}
	public String getOpuser() {
		return opuser;
	}
	public void setOpuser(String opuser) {
		this.opuser = opuser;
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
	public String getApiusername() {
		return apiusername;
	}
	public void setApiusername(String apiusername) {
		this.apiusername = apiusername;
	}
	public String getCaseid() {
		return caseid;
	}
	public void setCaseid(String caseid) {
		this.caseid = caseid;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getZipfile() {
		return zipfile;
	}
	public void setZipfile(String zipfile) {
		this.zipfile = zipfile;
	}
	public String getDamagecase()
	{
		return damagecase;
	}
	public void setDamagecase(String damagecase)
	{
		this.damagecase = damagecase;
	}
	
	public String getClaimmsg()
	{
		return claimmsg;
	}
	public void setClaimmsg(String claimmsg)
	{
		this.claimmsg = claimmsg;
	}
	
}
