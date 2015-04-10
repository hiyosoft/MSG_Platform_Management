package com.sinosig.staff.model;

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
@Table(name = "SINOSIG_STAFF_WELFARE")
@org.hibernate.annotations.Proxy(lazy = false)
public class StaffWelfare implements Serializable{

	private static final long serialVersionUID = 1L;
	private	String	id	;//	主键
	private	String	apiusername	;//	微信标识
	private	String	nickname	;//	微信昵称
	private	String	staffName	;//	员工姓名
	private	String	staffIdentityId	;//	员工身份证号码
	private	String	staffId	;//	员工工号
	private	Date	createTime	;//	创建时间，导出时仅导出204年2月7日早8（含）点到11点（含）之间的数据
	private	String	remark1	;//	备用字段
	private	String	remark2	;//	备用字段
	private	String	remark3	;//	备用字段
	private	String	remark4	;//	备用字段
	private	String	remark5	;//	备用字段
	private	String	remark6	;//	备用字段
	/**创建时间起*/
	private Date welfareBegin;
	/**创建时间止*/
	private Date welfareEnd;
	
	@Transient
	public Date getWelfareBegin() {
		return welfareBegin;
	}
	public void setWelfareBegin(Date welfareBegin) {
		this.welfareBegin = welfareBegin;
	}
	
	@Transient
	public Date getWelfareEnd() {
		return welfareEnd;
	}
	public void setWelfareEnd(Date welfareEnd) {
		this.welfareEnd = welfareEnd;
	}
	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	public String getId() {
		return id;
	}
	public String getApiusername() {
		return apiusername;
	}
	public String getNickname() {
		return nickname;
	}
	@Column(name="STAFF_NAME")
	public String getStaffName() {
		return staffName;
	}
	@Column(name="STAFF_IDENTITY_ID")
	public String getStaffIdentityId() {
		return staffIdentityId;
	}
	
	@Column(name="STAFF_ID")
	public String getStaffId() {
		return staffId;
	}
	
	@Column(name="CREATE_TIME")
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	public void setApiusername(String apiusername) {
		this.apiusername = apiusername;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}
	public void setStaffIdentityId(String staffIdentityId) {
		this.staffIdentityId = staffIdentityId;
	}
	public void setStaffId(String staffId) {
		this.staffId = staffId;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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
	public String getRemark5() {
		return remark5;
	}
	public void setRemark5(String remark5) {
		this.remark5 = remark5;
	}
	public String getRemark6() {
		return remark6;
	}
	public void setRemark6(String remark6) {
		this.remark6 = remark6;
	}
	
}
