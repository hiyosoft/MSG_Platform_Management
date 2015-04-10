package com.neusoft.util.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.xml.namespace.QName;

import net.sf.json.xml.XMLSerializer;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

import com.alibaba.fastjson.JSON;
import com.neusoft.core.channel.SNSUser;
import com.neusoft.web.model.IfaceInfo;

public class CarReportProcess extends InnerProcess{
	private static final Logger log = Logger.getLogger(CarReportProcess.class);
	
	public CarReportProcess() 
	{
		
	}
	
	public ProcessResult getRequest(IfaceInfo info, SNSUser snsUser, String orgi, Map<String, Object> paraMap) 
	{
			
			//请求报文
			String reqVal=(String)paraMap.get("innerinputparam");
			
			String username=(String)paraMap.get("username");
			
			String password=(String)paraMap.get("password");
			
			String wcid=(String)paraMap.get("wcid");
			
			log.info("原请求用户名 ：\n\r "+username);
			
			log.info("原请求密码 ：\n\r "+password);
			
			log.info("原请求wcid ：\n\r "+wcid);
			
			log.info("原请求报文 ：\n\r "+reqVal);
			
			//String responseData=postString(info.getRequesturl(),reqVal,info.getDescription());
			
			String responseData=postString(info.getRequesturl(),reqVal,username,password,info.getDescription());
			
			log.info("原返回报文: \n"+responseData);
			
			responseData=responseData.trim();
			
			String temstr=new XMLSerializer().read(responseData).toString();
			
			//log.info("解释后json报文: \n"+JSON.parse(info.isTrantjson()?temstr:responseData).toString());
			
			paraMap.put("wcid", wcid);
			
			paraMap.put(info.getCode(),JSON.parse(info.isTrantjson()?temstr:responseData)) ;
			
			return new ProcessResult(0,paraMap,null);
	}
	
	public static String postString(String url,String xmlvalue,String username,String password,String enc) {
		String[] returnBytes = null;
		String returnString ="";
		// 定义客户端对象
		RPCServiceClient serviceClient = null;
		try {		 
			//String username="WeChat95510";
			//String password="WeChat95510";
			
			serviceClient = new RPCServiceClient();
			Options options = serviceClient.getOptions();
			EndpointReference targetEPR = new EndpointReference(url);
			options.setTo(targetEPR);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
			options.setTimeOutInMilliSeconds(30000);// 等待时间
			Object[] opAddEntryArgs = new Object[] {username,password,xmlvalue};// 调用方法入参
			Class[] classes = new Class[] { String[].class };// 调用方法返回结果
			QName qname = new QName("http://server.wechat.flexcc.com","baoan_wechat");
			Object[] rtnObjs = serviceClient.invokeBlocking(qname,opAddEntryArgs,classes);
		    returnString=((String[]) rtnObjs[0])[0];
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (serviceClient != null) {
				try {
					serviceClient.cleanupTransport();
				    serviceClient.cleanup();
				} catch (AxisFault e) {
					e.printStackTrace();
				}
			}
		}
	
		return returnString;
	}
	
	
	public static void main(String[] args)  {
		
		String[] returnBytes = null;
		String returnString ="";
		// 定义客户端对象
		RPCServiceClient serviceClient = null;
		try {		 
			String username="WeChat95510";
			String password="WeChat95510";
			String xmlvalue="<?xml version='1.0' encoding='UTF-8'?><root><policyno>1021105072013000004</policyno><damagedate>2014-02-21 09:25:30</damagedate><notifydate>2014-02-21 10:25:30</notifydate><damageplace>北京市北京市海淀区花园北路14号</damageplace><damagearea>01</damagearea><ntfmidentity>1</ntfmidentity><notifyman>高晓红</notifyman><mobile>13333332323</mobile><driver>张三</driver><drivermobile>13555555552</drivermobile><losstype>833</losstype><casekind>0</casekind><isguess>0</isguess><damagecase>出险经过</damagecase><gps_lon>116.38382380689</gps_lon><gps_lat>39.985162453176</gps_lat><damageprovince>北京市</damageprovince><damagecity>北京市</damagecity><damagetown>海淀区</damagetown><wcid>77DA8D819DE34F388E847F618E6F</wcid><wechatno>oLc37jj-XXua-shPf-bYSm60A_Ds</wechatno><isalipay></isalipay><alipayaccounts></alipayaccounts></root>";
			serviceClient = new RPCServiceClient();
			
			Options options = serviceClient.getOptions();
			EndpointReference targetEPR = new EndpointReference("http://10.10.164.100:7006/webservice/services/FlexccWeChatServer");
			options.setTo(targetEPR);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
			options.setTimeOutInMilliSeconds(30000);// 等待时间
			Object[] opAddEntryArgs = new Object[] {username,password,xmlvalue};// 调用方法入参
			Class[] classes = new Class[] { String[].class };// 调用方法返回结果
			QName qname = new QName("http://server.wechat.flexcc.com","baoan_wechat");//cancelTBPolicy handleTBPolicy transPolicy 调用方法名称救援单证新接口请求方法transDate
			Object[] rtnObjs = serviceClient.invokeBlocking(qname,opAddEntryArgs,classes);
			System.out.println("====="+rtnObjs);
			System.out.println("返回报文:"+((String[]) rtnObjs[0])[0]);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (serviceClient != null) {
				try {
					serviceClient.cleanupTransport();
				    serviceClient.cleanup();
				} catch (AxisFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


	}
	
	@Override
	public ProcessResult getRequest(SNSUser snsUser, String orgi, Map<String, Object> paraMap) {
		// TODO Auto-generated method stub
		return null;
	}
}
