package com.neusoft.web.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.solr.client.solrj.beans.Field;
import org.hibernate.annotations.GenericGenerator;

import com.alibaba.fastjson.annotation.JSONField;
import com.neusoft.core.EapDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.core.channel.SNSUser;

@Entity
@Table(name = "rivu_favmsg")
@org.hibernate.annotations.Proxy(lazy = false)
public class FavMessage implements Channel{
	private String channel = EapDataContext.ChannelTypeEnum.WEIXIN.toString();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7530432292285212159L;
	private String weixinid ;
	@Field
	private String id ; 
	@Field("str_messageid")
	private String messageid ; 
	@Field("str_type")
	private String type ;
	@Field("str_fileId")
	private String fileId ;
	@Field("str_hasReply")
	private String hasReply ;
	@Field("str_fakeId")
	private String fakeId ;
	@Field("str_usermessage")
	private boolean usermessage;
	
	@Field("str_nickName")
	private String nickName ;
	@Field("str_remarkName")
	private String remarkName ;
	@Field("str_dateTime")
	private String dateTime ;
	@Field("str_icon")
	private String icon ;
	@Field("str_content")
	private String content ;
	@Field("str_playLength")
	private String playLength ;
	@Field("str_length")
	private String length ;	
	@Field("str_source")
	private String source ;
	@Field("str_starred")
	private String starred ;		//微博发布的时候用该字段存储 转发次数
	@Field("str_status")
	private String status ;
	@Field("str_subtype")
	private String subtype ;
	@Field("str_showType")
	private String showType ;
	@Field("str_desc")
	private String desc ;
	@Field("str_title")
	private String title ;
	@Field("str_appName")
	private String appName ; 
	@Field("str_contentUrl")
	private String contentUrl ;
	@Field("str_bcardNickName")
	private String bcardNickName ;
	@Field("str_bcardUserName")
	private String bcardUserName ;
	@Field("str_bcardFakeId")
	private String bcardFakeId ;
	@Field("str_msgid")
	private String msgid ;
	@Field("str_fromUserName")
	private String fromUserName ;
	@Field("str_toUserName")
	private String toUserName ;
	@Field("str_messagetype")
	private String messagetype ;
	@Field("str_xmlMessage")
	private String xmlMessage ;
	@Field("createtime")
	private Date createtime = new Date();			//存储微博的时候，使用这个字段存储  微博创建时间，如果不许要审核，则该字段和 发布时间一致，如果需要审核，则使用 createdate 存储审核后的发布时间
	@Field("newmessage")
	private boolean newmessage = true;
	@Field("orgi")
	private String orgi ;
	@Field("createdate")
	private String createdate ;
	@Transient
	private String url ;
	@Transient
	private String divid ;
	@Transient
	private String action ;				//微博发布的时候使用该字段存储 评论次数
	@Transient
	private SNSUser snsuser ;
	private String username ;
	private String userid ;
	private String contextid ;			
	private boolean topmessage ;		
	private String lon ;		//经度
	private String lat ;		//维度
	private String favuser ; 	//收藏用户
	private String touser ;
	private String replytype ;
	private String pipeline ;
	private String instruct ;
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public String getHasReply() {
		return hasReply;
	}
	public void setHasReply(String hasReply) {
		this.hasReply = hasReply;
	}
	public String getFakeId() {
		return fakeId;
	}
	public void setFakeId(String fakeId) {
		this.fakeId = fakeId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getRemarkName() {
		return remarkName;
	}
	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}
	@JSONField(name="date_time")
	public String getDateTime() {
		return dateTime;
	}
	@JSONField(name="date_time")
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPlayLength() {
		return playLength;
	}
	public void setPlayLength(String playLength) {
		this.playLength = playLength;
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getStarred() {
		return starred;
	}
	public void setStarred(String starred) {
		this.starred = starred;
	}
	@Column(name = "msgstatus")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSubtype() {
		return subtype;
	}
	public void setSubtype(String subtype) {
		this.subtype = subtype;
	}
	public String getShowType() {
		return showType;
	}
	public void setShowType(String showType) {
		this.showType = showType;
	}
	@Column(name = "msgdesc")
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getContentUrl() {
		return contentUrl;
	}
	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}
	public String getBcardNickName() {
		return bcardNickName;
	}
	public void setBcardNickName(String bcardNickName) {
		this.bcardNickName = bcardNickName;
	}
	public String getBcardUserName() {
		return bcardUserName;
	}
	public void setBcardUserName(String bcardUserName) {
		this.bcardUserName = bcardUserName;
	}
	public String getBcardFakeId() {
		return bcardFakeId;
	}
	public void setBcardFakeId(String bcardFakeId) {
		this.bcardFakeId = bcardFakeId;
	}
	public String getMsgid() {
		return msgid;
	}
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	public String getXmlMessage() {
		return xmlMessage;
	}
	public void setXmlMessage(String xmlMessage) {
		this.xmlMessage = xmlMessage;
	}
	public String getMessageid() {
		return messageid;
	}
	public void setMessageid(String messageid) {
		this.messageid = messageid;
	}
	public boolean isNewmessage() {
		return newmessage;
	}
	public void setNewmessage(boolean newmessage) {
		this.newmessage = newmessage;
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
	public String getCreatedate() {
		return createdate;
	}
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}
	public boolean isUsermessage() {
		return usermessage;
	}
	public void setUsermessage(boolean usermessage) {
		this.usermessage = usermessage;
	}
	@Transient
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Transient
	public String getDivid() {
		return divid;
	}
	public void setDivid(String divid) {
		this.divid = divid;
	}
	@Transient
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	@Override
	public String getChannel() {
		return channel;
	}
	@Transient
	@Override
	public String getText() {
		return content;
	}
	@Transient
	@Override
	public void setText(String text) {
		this.content = text;
	}
	@Transient
	public Date getDate() {
		return dateTime!=null ? new Date(Long.parseLong(dateTime)) : new Date();
	}
	@Transient
	@Override
	public String getUser() {
		return this.fakeId;
	}
	@Transient
	public SNSUser getSnsuser() {
		return snsuser;
	}
	public void setSnsuser(SNSUser snsuser) {
		this.snsuser = snsuser;
	}
	public String getContextid() {
		return contextid;
	}
	public void setContextid(String contextid) {
		this.contextid = contextid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getMessagetype() {
		return messagetype;
	}
	public void setMessagetype(String messagetype) {
		this.messagetype = messagetype;
	}
	public boolean isTopmessage() {
		return topmessage;
	}
	public void setTopmessage(boolean topmessage) {
		this.topmessage = topmessage;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public String getFavuser() {
		return favuser;
	}
	public void setFavuser(String favuser) {
		this.favuser = favuser;
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
	public String getTouser() {
		return touser;
	}
	public void setTouser(String touser) {
		this.touser = touser;
	}
	public String getReplytype() {
		return replytype;
	}
	public void setReplytype(String replytype) {
		this.replytype = replytype;
	}
	public String getInstruct() {
		return instruct;
	}
	public void setInstruct(String instruct) {
		this.instruct = instruct;
	}
	@Transient
	public String getPipeline() {
		return pipeline;
	}
	public void setPipeline(String pipeline) {
		this.pipeline = pipeline;
	}
	@Override
	public void setBytedata(byte[] data) {
		// TODO Auto-generated method stub
		
	}
	@Transient
	@Override
	public byte[] getBytedata() {
		// TODO Auto-generated method stub
		return null;
	}
	public String getWeixinid() {
		return weixinid;
	}
	public void setWeixinid(String weixinid) {
		this.weixinid = weixinid;
	}
}
