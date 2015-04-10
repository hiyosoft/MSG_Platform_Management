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
@Table(name = "rivu_sinosig_car_report")
@org.hibernate.annotations.Proxy(lazy = false)
public class SinosigCarReport implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String  wcid;
	private String  wechatno;
	private	String	policyno;
	private	Date	damagedate;
	private	Date	damagedateBegin;
	private	Date	damagedateEnd;
	private Date    notifydate;
	private Date    notifydateBegin;
	private Date    notifydateEnd;
	private	String	damageplace	;
	private String  damagearea;
	private String  ntfmidentity;
	private String  notifyman;
	private String  mobile;
	private String  driver;
	private String  drivermobile;
	private String  losstype;
	private String  casekind;
	private String  isguess;
	private String  damagecase;
	
	private	String	gps_lon;
	private	String	gps_lat;
	private	String	lon;
	private	String	lat;
	
	private	String	damageprovince;
	private	String	damagecity;
	private	String	damagetown;
	private	String	remark1;
	private	String	remark2;
	private	String	remark3;
	private	String	remark4;
	private	String	remark5;
	private	String	remark6;
	private	String	remark7;
	private	String	remark8;
	private String  orgi ;

	@Id
	@Column(length = 32)
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")	
	public String getWcid() {
		return wcid;
	}
	public void setWcid(String wcid) {
		this.wcid = wcid;
	}
	public String getWechatno() {
		return wechatno;
	}
	public void setWechatno(String wechatno) {
		this.wechatno = wechatno;
	}
	public String getPolicyno() {
		return policyno;
	}
	public void setPolicyno(String policyno) {
		this.policyno = policyno;
	}
	public Date getDamagedate() {
		return damagedate;
	}
	public void setDamagedate(Date damagedate) {
		this.damagedate = damagedate;
	}
	@Transient
	public Date getDamagedateBegin() {
		return damagedateBegin;
	}
	public void setDamagedateBegin(Date damagedateBegin) {
		this.damagedateBegin = damagedateBegin;
	}
	@Transient
	public Date getDamagedateEnd() {
		return damagedateEnd;
	}
	public void setDamagedateEnd(Date damagedateEnd) {
		this.damagedateEnd = damagedateEnd;
	}
	public Date getNotifydate() {
		return notifydate;
	}
	public void setNotifydate(Date notifydate) {
		this.notifydate = notifydate;
	}
	@Transient
	public Date getNotifydateBegin() {
		return notifydateBegin;
	}
	public void setNotifydateBegin(Date notifydateBegin) {
		this.notifydateBegin = notifydateBegin;
	}
	@Transient
	public Date getNotifydateEnd() {
		return notifydateEnd;
	}
	public void setNotifydateEnd(Date notifydateEnd) {
		this.notifydateEnd = notifydateEnd;
	}
	public String getDamageplace() {
		return damageplace;
	}
	public void setDamageplace(String damageplace) {
		this.damageplace = damageplace;
	}
	public String getDamagearea() {
		return damagearea;
	}
	public void setDamagearea(String damagearea) {
		this.damagearea = damagearea;
	}
	public String getNtfmidentity() {
		return ntfmidentity;
	}
	public void setNtfmidentity(String ntfmidentity) {
		this.ntfmidentity = ntfmidentity;
	}
	public String getNotifyman() {
		return notifyman;
	}
	public void setNotifyman(String notifyman) {
		this.notifyman = notifyman;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getDrivermobile() {
		return drivermobile;
	}
	public void setDrivermobile(String drivermobile) {
		this.drivermobile = drivermobile;
	}
	public String getLosstype() {
		return losstype;
	}
	public void setLosstype(String losstype) {
		this.losstype = losstype;
	}
	public String getCasekind() {
		return casekind;
	}
	public void setCasekind(String casekind) {
		this.casekind = casekind;
	}
	public String getIsguess() {
		return isguess;
	}
	public void setIsguess(String isguess) {
		this.isguess = isguess;
	}
	public String getDamagecase() {
		return damagecase;
	}
	public void setDamagecase(String damagecase) {
		this.damagecase = damagecase;
	}
	public String getGps_lon() {
		return gps_lon;
	}
	public void setGps_lon(String gpsLon) {
		gps_lon = gpsLon;
	}
	public String getGps_lat() {
		return gps_lat;
	}
	public void setGps_lat(String gpsLat) {
		gps_lat = gpsLat;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getDamageprovince() {
		return damageprovince;
	}
	public void setDamageprovince(String damageprovince) {
		this.damageprovince = damageprovince;
	}
	public String getDamagecity() {
		return damagecity;
	}
	public void setDamagecity(String damagecity) {
		this.damagecity = damagecity;
	}
	public String getDamagetown() {
		return damagetown;
	}
	public void setDamagetown(String damagetown) {
		this.damagetown = damagetown;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
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
	public String getRemark7() {
		return remark7;
	}
	public void setRemark7(String remark7) {
		this.remark7 = remark7;
	}
	public String getRemark8() {
		return remark8;
	}
	public void setRemark8(String remark8) {
		this.remark8 = remark8;
	}
	
	
}
