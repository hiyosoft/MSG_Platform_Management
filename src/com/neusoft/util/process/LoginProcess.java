package com.neusoft.util.process;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.alibaba.fastjson.JSON;
import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.channel.SNSUser;
import com.neusoft.core.channel.WeiXinUser;
import com.neusoft.util.store.EapTools;
import com.neusoft.web.model.IfaceInfo;
import com.neusoft.web.model.SinosigUser;
import com.neusoft.web.model.UserTemplet;
import com.sinosig.ec.common.util.security.Base64;
import com.sinosig.ec.common.util.security.RSA;
/**
 * 
 * 登陆的处理流程：1.如果是第一次登陆(检查数据库是否有该记录)，则先调用官网的登陆接口--返回五要素信息和是否完成身份认证，对于已经完成身份认证的
 * 接着调用cif的五要素获取客户信息，如果存在该客户，则调用保存微信号的接口；未完成身份认证的，则带着custid直接调用单点登录接口。
 * 2.不是第一次登录（数据库是有该记录）。首选判断五要素是否可以变（VERIFYSTAT字段）的，如果是不可以变的，则直接调单点登录；如果是可变的，调用官网根据custid查询5要素和认证状态；
 * 然后的处理跟第一次登录调用官网接口完成身份认证后的处理流程一样。
 * @author Kerwin
 *
 */

public class LoginProcess extends InnerProcess{
	private static final Logger log = Logger.getLogger(InnerProcess.class);
	private static  Map<String,String> cifcardtype =new HashMap<String,String>();
	static{
		 //官网1：身份证3：护照4：军官证5：港澳台同胞证10：其他11：户口本
		 //CIF:10 居民身份证51 护照13 军官证17 港澳台同胞证99 其他证件11 居民户口薄
		cifcardtype.put("1", "10");
		cifcardtype.put("3", "51");
		cifcardtype.put("4", "13");
		cifcardtype.put("5", "17");
		cifcardtype.put("10", "99");
		cifcardtype.put("11", "11");
	}
	@Override
	public ProcessResult getRequest(SNSUser snsUser, String orgi, Map<String, Object> paraMap) {
		return null;
	}

	@Override
	public ProcessResult getRequest(IfaceInfo info, SNSUser snsUser, String orgi, Map<String, Object> paraMap) {
		// TODO Auto-generated method stub
		try {
			String apiusername=(String) paraMap.get("apiusername");
			log.info("============apiusername============="+apiusername);
			String resultstr=null;
			if(apiusername!=null){
				apiusername=apiusername.replaceAll("-", "_");
				List<SinosigUser> sinousers=EapDataContext.getService().findAllByCriteria(DetachedCriteria.forClass(SinosigUser.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.and(Restrictions.eq("apiusername", apiusername), Restrictions.eq("userstatus", "1")))));
				//数据库存在该有效记录	
				if(sinousers!=null && sinousers.size()>0){
					SinosigUser sinouser=sinousers.get(0);
					if(!"1".equals(sinouser.getIschange())){//五要素可以修改，先去官网获取客户信息
						List<IfaceInfo> gwquerys=EapDataContext.getService().findAllByCriteria(DetachedCriteria.forClass(IfaceInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi),Restrictions.eq("code", "gwquery"))));
						String queryVal=null;
						if(gwquerys!=null && gwquerys.size()>0){
				    		IfaceInfo gwquery=gwquerys.get(0);
				    		//获取到客户信息
				    		UserTemplet updateTemplet = EapSmcDataContext.getUserTemplet(gwquery.getReqtemplate(), orgi) ;
				    		paraMap.put("custid", sinouser.getCustid());
				    		paraMap.put("innerinputparam", EapTools.getTemplet(updateTemplet, paraMap));
				    		log.info("=========非第一次登陆，官网登陆请求报文=========="+paraMap.get("innerinputparam"));
				    		queryVal=ReqeustProcessUtil.getWebServiceResponseBody(gwquery, snsUser, orgi, paraMap);
				    		if(queryVal.indexOf("<VERIFYSTAT>1</VERIFYSTAT>")>0){
				    			sinouser.setIschange("1");
				    			rpcCIFInterface(orgi,queryVal,apiusername);
							}
				    	}
				    	//TODO:还应该考虑更新五要素信息
						sinouser.setCardtype(cifcardtype.get(getxmlVal(queryVal,"IDENTITYTYPE")));
				    	sinouser.setCardno(getxmlVal(queryVal,"IDENTITYNO"));
				    	sinouser.setUsername(getxmlVal(queryVal,"CUSTNAME"));
				    	sinouser.setSex(getxmlVal(queryVal,"CUSTGENDER"));
						String date=getxmlVal(queryVal,"CUSTBIRTH");
						if(date!=null && !"".equals(date)){
							try {
								sinouser.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(date));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						sinouser.setUpdatetime(new Date());
						EapDataContext.getService().updateIObject(sinouser);
					}
					resultstr=encodeT(sinouser.getCustid(),sinouser.getAccountype());
				}else{//数据库没有该客户数据，第一次登录
					UserTemplet loginTemplet = EapSmcDataContext.getUserTemplet(info.getReqtemplate(), orgi) ;
		    		paraMap.put("innerinputparam", EapTools.getTemplet(loginTemplet, paraMap));
		    		log.info("=========第一次登录,官网登陆请求报文=========="+paraMap.get("innerinputparam"));
					String logval=ReqeustProcessUtil.getWebServiceResponseBody(info, snsUser, orgi, paraMap);
					log.info("登陆返回报文：\n"+logval);
					if(logval!=null){
						//用户登录成功
						if(logval.indexOf("<SUCCESSYN>Y</SUCCESSYN>")>0){
							SinosigUser siuser= getUserFromXML(logval);
							//TODO：调用CIF的接口
							if(logval.indexOf("<VERIFYSTAT>1</VERIFYSTAT>")>0){
								siuser.setIschange("1");
								rpcCIFInterface(orgi,logval,apiusername);
							}else{
								siuser.setIschange("0");
							}
							String username=(String)paraMap.get("username");
							String accounttype=username!=null && username.indexOf("@")>0?"01":"02";
							String agentid="";
							if(logval.indexOf("<CUSTID>")>0){
								agentid=logval.substring(logval.indexOf("<CUSTID>")+8,logval.indexOf("</CUSTID>"));
							}
							//保存用户信息
							siuser.setCreatetime(new Date());
							siuser.setUserstatus("1");
							siuser.setOrgi(orgi);
							siuser.setCustid(agentid);
							siuser.setAccountype(accounttype);
							siuser.setApiusername(apiusername);
							siuser.setPhoneno(getxmlVal(logval,"CUSTMOBILE"));
							EapDataContext.getService().saveIObject(siuser);
							//TODO: 更新用户信息，weixin_user 是否认证
							List<WeiXinUser> wusers=EapDataContext.getService().findAllByCriteria(DetachedCriteria.forClass(WeiXinUser.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.and(Restrictions.eq("apiusername", apiusername), Restrictions.eq("channel", EapDataContext.ChannelTypeEnum.WEIXIN.toString())))));
							if(wusers!=null && wusers.size()>0){
								WeiXinUser wuser=wusers.get(0);
								wuser.setUserau(true);
								EapDataContext.getService().updateIObject(wuser);
							}
							resultstr=encodeT(agentid,accounttype);
						}else{//登录失败
							paraMap.put("errormsg", "用户名或者密码不正确");
						}
					}
				}
			}
			paraMap.put(info.getCode(), resultstr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ProcessResult result = new ProcessResult(0,paraMap,null) ;
		
		return result;
	}
	public static void rpcCIFInterface(String orgi,String logval,String apiusername){
		final String t1=orgi;
		final String t2=logval;
		final String t3=apiusername;
		new Thread(new Runnable() {
			@Override
			public void run() {
				rpcCIFtoUpdateWeixin(t1,t2,t3);
			}
		}).start();
	}
	/**
     * 加密测试方法
     */
    private static String encodeT(String agentid,String accounttype) throws Exception{
        String publicStr="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCVAHhAZaTo+76PCO5+cdNCE+XVqDkNBXxWOTBbWGL3FQQ10S6ciMSHptq9hYmtPaFumQc3Su/TtL6817CGxKbWIyH+CghJLbd6O/9/YYN+WxnRIbTqDi/ln4niwUAQCZs98BNukvOIp5oZ2XETZG9dKbKJBGo/tmupdl427OzPDQIDAQAB";
        String origin = "agentId="+agentid+"&accountType="+accounttype+"&time="+System.currentTimeMillis();
        String resultkey=Base64.encode(RSA.encrypt(publicStr,origin.getBytes("utf-8")));
        //System.out.println("加密后字符串:"+resultkey);
        return resultkey;
    }
    private static void rpcCIFtoUpdateWeixin(String orgi,String logval,String apiusername){
    	Map<String,Object> params=new HashMap<String,Object>();
    	params.put("idType", cifcardtype.get(getxmlVal(logval,"IDENTITYTYPE")));
    	params.put("idNo", getxmlVal(logval,"IDENTITYNO"));
    	params.put("cnName", getxmlVal(logval,"CUSTNAME"));
    	params.put("sex", getxmlVal(logval,"CUSTGENDER"));
    	params.put("birthDate", getxmlVal(logval,"CUSTBIRTH"));
    	List<IfaceInfo> querys=EapDataContext.getService().findAllByCriteria(DetachedCriteria.forClass(IfaceInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi),Restrictions.eq("code", "cifquery"))));
    	if(querys!=null && querys.size()>0){
    		IfaceInfo query=querys.get(0);
    		try {
    			//根据五要素获取客户ID
    			UserTemplet queryTemplet = EapSmcDataContext.getUserTemplet(query.getReqtemplate(), orgi) ;
    			params.put("innerinputparam", EapTools.getTemplet(queryTemplet, params));
    			log.info("CIF获取客户ID==请求报文：\n"+params.get("innerinputparam"));
				String queryVal=ReqeustProcessUtil.getWebServiceResponseBody(query, null, orgi, params);
				log.info("CIF获取客户ID**返回报文：\n"+queryVal);
				if(queryVal.indexOf("</PARTYNO>")>0){//客户号存在
					String partyNo=getxmlVal(queryVal,"PARTYNO");
					List<IfaceInfo> updates=EapDataContext.getService().findAllByCriteria(DetachedCriteria.forClass(IfaceInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi),Restrictions.eq("code", "cifupdate"))));
					if(updates!=null && updates.size()>0){
						IfaceInfo update=updates.get(0);
						params.clear();
						params.put("partyNo", partyNo);
						params.put("wechatNo", apiusername);
						params.put("opType", "1");//是否更新，1为关注；0为取消关注
						//更新微信ID
						log.info("CIF更新微信ID==请求参数：\n"+JSON.toJSONString(params));
						UserTemplet updateTemplet = EapSmcDataContext.getUserTemplet(update.getReqtemplate(), orgi) ;
		    			params.put("innerinputparam", EapTools.getTemplet(updateTemplet, params));
		    			log.info("CIF更新微信ID==请求报文：\n"+params.get("innerinputparam"));
						String updateVal=ReqeustProcessUtil.getWebServiceResponseBody(update, null, orgi, params);
						log.info("CIF更新微信ID**返回报文：\n"+updateVal);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    private static String getxmlVal(String xml,String key){
    	return xml.indexOf("<"+key+">")>0? xml.substring(xml.indexOf("<"+key+">")+(key.length()+2),xml.indexOf("</"+key+">")):"";
    }
    private static SinosigUser getUserFromXML(String logval){
    	SinosigUser sinouser=new SinosigUser();
    	sinouser.setCardtype(cifcardtype.get(getxmlVal(logval,"IDENTITYTYPE")));
    	sinouser.setCardno(getxmlVal(logval,"IDENTITYNO"));
    	sinouser.setUsername(getxmlVal(logval,"CUSTNAME"));
    	sinouser.setSex(getxmlVal(logval,"CUSTGENDER"));
		String date=getxmlVal(logval,"CUSTBIRTH");
		if(date!=null && !"".equals(date)){
			try {
				sinouser.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse(date));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sinouser;
    }
    public static void main(String[] args) {
    	try {
			encodeT("108247","02");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
