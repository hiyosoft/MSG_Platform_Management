package com.neusoft.web.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "rivu_iface")
@org.hibernate.annotations.Proxy(lazy = false)
public class IfaceInfo implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String orgi;
	private String code;
	private String cateid;
	private String requesturl;		//请求URL
	private String methodname;		//方法名
	private String reqparams;		//输入参数
	private String reqtemplate;		//输入模板
	private String resresult;		//输出结果
	private String restemplate;		//输出模板
	private String description;
	private boolean ischange;		//判断是否为静态接口，例如银行信息等
	private String rpctype;
	private String requestuser;
	private String requestpwd;
	private String ssltype;
	private boolean needvalid;
	private String clazz;
	private boolean trantjson;
	private String privatekey;
	private String publickey;
	
	
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
	
	public String getPrivatekey() {
		return privatekey;
	}
	public void setPrivatekey(String privatekey) {
		this.privatekey = privatekey;
	}
	public String getPublickey() {
		return publickey;
	}
	public void setPublickey(String publickey) {
		this.publickey = publickey;
	}
	public boolean isTrantjson() {
		return trantjson;
	}
	public void setTrantjson(boolean trantjson) {
		this.trantjson = trantjson;
	}
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public String getRequestuser() {
		return requestuser;
	}
	public void setRequestuser(String requestuser) {
		this.requestuser = requestuser;
	}
	public String getRequestpwd() {
		return requestpwd;
	}
	public void setRequestpwd(String requestpwd) {
		this.requestpwd = requestpwd;
	}
	public String getSsltype() {
		return ssltype;
	}
	public void setSsltype(String ssltype) {
		this.ssltype = ssltype;
	}
	public boolean isNeedvalid() {
		return needvalid;
	}
	public void setNeedvalid(boolean needvalid) {
		this.needvalid = needvalid;
	}
	public String getReqtemplate() {
		return reqtemplate;
	}
	public void setReqtemplate(String reqtemplate) {
		this.reqtemplate = reqtemplate;
	}
	public String getRestemplate() {
		return restemplate;
	}
	public void setRestemplate(String restemplate) {
		this.restemplate = restemplate;
	}
	public String getRpctype() {
		return rpctype;
	}
	public void setRpctype(String rpctype) {
		this.rpctype = rpctype;
	}
	public boolean isIschange() {
		return ischange;
	}
	public void setIschange(boolean ischange) {
		this.ischange = ischange;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrgi() {
		return orgi;
	}
	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getCateid() {
		return cateid;
	}
	public void setCateid(String cateid) {
		this.cateid = cateid;
	}
	public String getRequesturl() {
		return requesturl;
	}
	public void setRequesturl(String requesturl) {
		this.requesturl = requesturl;
	}
	public String getMethodname() {
		return methodname;
	}
	public void setMethodname(String methodname) {
		this.methodname = methodname;
	}
	public String getReqparams() {
		return reqparams;
	}
	public void setReqparams(String reqparams) {
		this.reqparams = reqparams;
	}
	public String getResresult() {
		return resresult;
	}
	public void setResresult(String resresult) {
		this.resresult = resresult;
	}
	public String getDescription() {
		return description!=null && description.length()>0?description:"UTF-8";
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
