package com.neusoft.web.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "rivu_sinosig_user")
@org.hibernate.annotations.Proxy(lazy = false)
public class SinosigUser implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String username;	//
	private String custid;		//客户唯一的id,也称为agentid
	private String cardtype;	//证件类型
	private String cardno;		//证件号码
	private String sex;			//性别
	private String phoneno;		//手机号
	private Date birthday;		//生日
	private boolean islogin =false;	//是否已经登录
	private boolean isbind =false;		//是否已经绑定
	private String remark1;
	private String remark2;
	private String remark3;
	private String remark4;
	private String userstatus;		//用户状态，1为可用；0为不可用
	private String apiusername;		//用户微信标识
	private String orgi;			//租户标识
	private Date createtime;		//创建时间
	private Date updatetime;		//修改时间
	private String ischange;		//五要素信息是否可以修改,1为不可以修改，0为可以修改
	private String bussno;			//业务流水号
	private String accountype;		//登陆类型；1为邮箱用户、2为手机用户
	
	
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
	
	public String getAccountype() {
		return accountype;
	}
	public void setAccountype(String accountype) {
		this.accountype = accountype;
	}
	public String getApiusername() {
		return apiusername;
	}
	public String getUserstatus() {
		return userstatus;
	}
	public void setUserstatus(String userstatus) {
		this.userstatus = userstatus;
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
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public String getIschange() {
		return ischange;
	}
	public void setIschange(String ischange) {
		this.ischange = ischange;
	}
	public String getBussno() {
		return bussno;
	}
	public void setBussno(String bussno) {
		this.bussno = bussno;
	}
	public void setApiusername(String apiusername) {
		this.apiusername = apiusername;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCustid() {
		return custid;
	}
	public void setCustid(String custid) {
		this.custid = custid;
	}
	public String getCardtype() {
		return cardtype;
	}
	public void setCardtype(String cardtype) {
		this.cardtype = cardtype;
	}
	public String getCardno() {
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getPhoneno() {
		return phoneno;
	}
	public void setPhoneno(String phoneno) {
		this.phoneno = phoneno;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public boolean isIslogin() {
		return islogin;
	}
	public void setIslogin(boolean islogin) {
		this.islogin = islogin;
	}
	public boolean isIsbind() {
		return isbind;
	}
	public void setIsbind(boolean isbind) {
		this.isbind = isbind;
	}
	public String getRemark1() {
		return remark1;
	}
	public void setRemark1(String remark1) {
		this.remark1 = remark1;
	}
	public String getRemark2() {
		return remark2;
	}
	public void setRemark2(String remark2) {
		this.remark2 = remark2;
	}
	public String getRemark3() {
		return remark3;
	}
	public void setRemark3(String remark3) {
		this.remark3 = remark3;
	}
	public String getRemark4() {
		return remark4;
	}
	public void setRemark4(String remark4) {
		this.remark4 = remark4;
	}
}
