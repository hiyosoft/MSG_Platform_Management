package com.sinosig.pay.platform.kuaiqian.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;



@Entity
@Table(name = "rivu_sinosig_orders")
@org.hibernate.annotations.Proxy(lazy = false)
public class MPurchase implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String title;
	private String version;
	private String txnType;
	private String interactiveStatus;
	private String cardNo;
	private String expiredDate;
	private String cvv2;
	private String amount;
	private String merchantId;
	private String settleMerchantId;
	private String terminalId;
	private String cardHolderName;
	private String cardHolderId;
	private String idType;
	private String entryTime;
	private String externalRefNumber;
	private String customerId;
	private String pin;
	private String issuerCountry;
	private String siteType;
	private String siteID;
	private String dynPin;
	private String serialNo;
	private String spFlag;
	private String ext;
	private String ext1;
	private String tr3Url;
	private String errorCode;
	private String errorMessage;
	private String refNumber2;
	private String responseCode2;
	private String responseTextMessage2;
	private String cardOrg2;
	private String issuer2;
	private String key;
	private String value;
	private String orgi;			//租户标识
	private String apiusername;		//微信用户标识
	private String bizTotalPremium; //商业总保费
	private String vehicleTaxPremium;//车船税费
	private String forcePremium;	//交强险费
	private String bizno;			//商业保单号
	private String forceno;			//交强保单号
	private Date payTime;			//支付时间
	private Date tradeTime;			//交易时间
	//商业险订单号
	private String bizOrderNo;
	//交强险订单号
	private String forceOrderNo;
	
	private String playment;
	
	private Date updateTime;
	
	private String mobile;

	@Id
	@Column(length = 64)
	public String getExternalRefNumber() {
		return externalRefNumber;
	}
	
	public String getVersion() {
		return version;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = "消费交易";
	}
	

	public void setVersion(String version) {
		this.version = version;
	}


	public String getTxnType() {
		return txnType;
	}


	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}


	public String getInteractiveStatus() {
		return interactiveStatus;
	}


	public void setInteractiveStatus(String interactiveStatus) {
		this.interactiveStatus = interactiveStatus;
	}


	public String getCardNo() {
		return cardNo;
	}


	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}


	public String getExpiredDate() {
		return expiredDate;
	}


	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
	}


	public String getCvv2() {
		return cvv2;
	}


	public void setCvv2(String cvv2) {
		this.cvv2 = cvv2;
	}


	public String getAmount() {
		return amount;
	}


	public void setAmount(String amount) {
		this.amount = amount;
	}


	public String getMerchantId() {
		return merchantId;
	}


	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}


	public String getSettleMerchantId() {
		return settleMerchantId;
	}


	public void setSettleMerchantId(String settleMerchantId) {
		this.settleMerchantId = settleMerchantId;
	}


	public String getTerminalId() {
		return terminalId;
	}


	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}


	public String getCardHolderName() {
		return cardHolderName;
	}


	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}


	public String getCardHolderId() {
		return cardHolderId;
	}


	public void setCardHolderId(String cardHolderId) {
		this.cardHolderId = cardHolderId;
	}


	public String getIdType() {
		return idType;
	}


	public void setIdType(String idType) {
		this.idType = idType;
	}


	public String getEntryTime() {
		return entryTime;
	}


	public void setEntryTime(String entryTime) {
		this.entryTime = entryTime;
	}


	public String getCustomerId() {
		return customerId;
	}


	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}


	public String getPin() {
		return pin;
	}


	public void setPin(String pin) {
		this.pin = pin;
	}


	public String getIssuerCountry() {
		return issuerCountry;
	}


	public void setIssuerCountry(String issuerCountry) {
		this.issuerCountry = issuerCountry;
	}


	public String getSiteType() {
		return siteType;
	}


	public void setSiteType(String siteType) {
		this.siteType = siteType;
	}


	public String getSiteID() {
		return siteID;
	}


	public void setSiteID(String siteID) {
		this.siteID = siteID;
	}


	public String getDynPin() {
		return dynPin;
	}


	public void setDynPin(String dynPin) {
		this.dynPin = dynPin;
	}


	public String getSerialNo() {
		return serialNo;
	}


	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}


	public String getSpFlag() {
		return spFlag;
	}


	public void setSpFlag(String spFlag) {
		this.spFlag = spFlag;
	}


	public String getExt() {
		return ext;
	}


	public void setExt(String ext) {
		this.ext = ext;
	}


	public String getExt1() {
		return ext1;
	}


	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}


	public String getTr3Url() {
		return tr3Url;
	}


	public void setTr3Url(String tr3Url) {
		this.tr3Url = tr3Url;
	}


	public String getErrorCode() {
		return errorCode;
	}


	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}


	public String getErrorMessage() {
		return errorMessage;
	}


	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}


	public String getRefNumber2() {
		return refNumber2;
	}


	public void setRefNumber2(String refNumber2) {
		this.refNumber2 = refNumber2;
	}


	public String getResponseCode2() {
		return responseCode2;
	}


	public void setResponseCode2(String responseCode2) {
		this.responseCode2 = responseCode2;
	}


	public String getResponseTextMessage2() {
		return responseTextMessage2;
	}


	public void setResponseTextMessage2(String responseTextMessage2) {
		this.responseTextMessage2 = responseTextMessage2;
	}


	public String getCardOrg2() {
		return cardOrg2;
	}


	public void setCardOrg2(String cardOrg2) {
		this.cardOrg2 = cardOrg2;
	}


	public String getIssuer2() {
		return issuer2;
	}


	public void setIssuer2(String issuer2) {
		this.issuer2 = issuer2;
	}


	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}


	public String getOrgi() {
		return orgi;
	}


	public void setOrgi(String orgi) {
		this.orgi = orgi;
	}


	public String getApiusername() {
		return apiusername;
	}


	public void setApiusername(String apiusername) {
		this.apiusername = apiusername;
	}


	public String getBizTotalPremium() {
		return bizTotalPremium;
	}


	public void setBizTotalPremium(String bizTotalPremium) {
		this.bizTotalPremium = bizTotalPremium;
	}


	public String getVehicleTaxPremium() {
		return vehicleTaxPremium;
	}


	public void setVehicleTaxPremium(String vehicleTaxPremium) {
		this.vehicleTaxPremium = vehicleTaxPremium;
	}


	public String getForcePremium() {
		return forcePremium;
	}


	public void setForcePremium(String forcePremium) {
		this.forcePremium = forcePremium;
	}


	public String getBizno() {
		return bizno;
	}


	public void setBizno(String bizno) {
		this.bizno = bizno;
	}


	public String getForceno() {
		return forceno;
	}


	public void setForceno(String forceno) {
		this.forceno = forceno;
	}


	public String getBizOrderNo() {
		return bizOrderNo;
	}


	public void setBizOrderNo(String bizOrderNo) {
		this.bizOrderNo = bizOrderNo;
	}


	public String getForceOrderNo() {
		return forceOrderNo;
	}


	public void setForceOrderNo(String forceOrderNo) {
		this.forceOrderNo = forceOrderNo;
	}


	public void setExternalRefNumber(String externalRefNumber) {
		this.externalRefNumber = externalRefNumber;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		
		this.updateTime = new Date();
	}

	public String getPlayment() {
		return playment;
	}

	public void setPlayment(String playment) {
		this.playment = "99bill";
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
