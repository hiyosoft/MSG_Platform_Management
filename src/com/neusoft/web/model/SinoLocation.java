package com.neusoft.web.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "rivu_location")
@org.hibernate.annotations.Proxy(lazy = false)
public class SinoLocation implements Serializable {
	private static final long serialVersionUID = 1L;
	private String id;
	private String province;
	private String city;
	private String area;
	private String address;
	private String dept;
	private String tel;
	private String lat;
	private String lon;
	private String orgi;
	private String status;		//0-未输入；1-已输入；2-审核
	private Date updatedate;
	private String contract;
	private String mobile;
	private String confidence;
	private String type;
	private String hisaddress;
	private String hisdept;
	private String histel;		//电话
	private String hislat;
	private String hislon;
	private String hiscontract;
	private String fackid;
	private String hisfackid;
	
	@Id
	@Column(length = 32)
	//@GeneratedValue(generator = "system-uuid")
	//@GenericGenerator(name = "system-uuid", strategy = "uuid")
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getFackid() {
		return fackid;
	}
	public void setFackid(String fackid) {
		this.fackid = fackid;
	}
	public String getHisfackid() {
		return hisfackid;
	}
	public void setHisfackid(String hisfackid) {
		this.hisfackid = hisfackid;
	}
	public String getHisaddress() {
		return hisaddress;
	}
	public String getHiscontract() {
		return hiscontract;
	}
	public void setHiscontract(String hiscontract) {
		this.hiscontract = hiscontract;
	}
	public void setHisaddress(String hisaddress) {
		this.hisaddress = hisaddress;
	}
	public String getHisdept() {
		return hisdept;
	}
	public void setHisdept(String hisdept) {
		this.hisdept = hisdept;
	}
	public String getHistel() {
		return histel;
	}
	public void setHistel(String histel) {
		this.histel = histel;
	}
	public String getHislat() {
		return hislat;
	}
	public void setHislat(String hislat) {
		this.hislat = hislat;
	}
	public String getHislon() {
		return hislon;
	}
	public void setHislon(String hislon) {
		this.hislon = hislon;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getUpdatedate() {
		return updatedate;
	}
	public void setUpdatedate(Date updatedate) {
		this.updatedate = updatedate;
	}
	public String getContract() {
		return contract;
	}
	public void setContract(String contract) {
		this.contract = contract;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getConfidence() {
		return confidence;
	}
	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
}
