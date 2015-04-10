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
@Table(name = "rivu_sinosig_zlbc_res")
@org.hibernate.annotations.Proxy(lazy = false)
public class SinosigZLBCRes implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String orgi;	 //租户标识
	private Date createtime = new Date();
	private String caseid; 	//事故号
	private String zlbcid; 	//理赔资料补充ID
	private Integer restype;  //1：图片；2：xml文件
	private byte[] image ;
	private String rescontent; //资源内容（仅保存xml文件内容）
	
	
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
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getCaseid() {
		return caseid;
	}
	public void setCaseid(String caseid) {
		this.caseid = caseid;
	}
	public String getZlbcid() {
		return zlbcid;
	}
	public void setZlbcid(String zlbcid) {
		this.zlbcid = zlbcid;
	}
	public Integer getRestype() {
		return restype;
	}
	public void setRestype(Integer restype) {
		this.restype = restype;
	}
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
	public String getRescontent() {
		return rescontent;
	}
	public void setRescontent(String rescontent) {
		this.rescontent = rescontent;
	}
	
	
}
