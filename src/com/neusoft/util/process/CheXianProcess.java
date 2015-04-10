package com.neusoft.util.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;

import net.sf.json.xml.XMLSerializer;

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

public class CheXianProcess extends InnerProcess{

	private static final Logger log = Logger.getLogger(CheXianProcess.class);
	
	public CheXianProcess() {
		Log.info("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP");
		// TODO Auto-generated constructor stub
	}
	
	//@Override
//	public ProcessResult getResponse(IfaceInfo info, SNSUser snsUser, String orgi, Map<String, Object> paraMap) throws TemplateException, Exception {
//		// TODO Auto-generated method stub
//		return super.getResponse(info, snsUser, orgi, paraMap);
//	}


	/**
	 * 
	 */
	//@Override
	public ProcessResult getRequest(IfaceInfo info, SNSUser snsUser, String orgi, Map<String, Object> paraMap) {
		
		//请求报文
		String reqVal=(String)paraMap.get("innerinputparam");
		
		log.info("原请求报文 ：\n\r "+reqVal);
		
		
		/*******************/
		//请求加密的过程1.reqVal取出sign节点的值 2.给填充回去<sign></sign>
		/*******************/
		String privateKey = null;
		if(info.getPrivatekey()!=null)
			privateKey = info.getPrivatekey();
		else
			privateKey="MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJiUrccS6WdEGJFxAZ93rYl46S9wZZ4s2BsH0e8gi3hCfR1TVRF8367NltWwG3jHUlESHuTdqTYqq_Is4sKAfzy9B9ItiLuRa4LA1KNcW9DSxSa-NxP-szuLjrTMDTeB4gl6URPp14HTBxfn9E6vCXLltJVhkaefV8OmJScDO9T9AgMBAAECgYEAhHhZI8RclkZ92hA164CZTwiF06UO5LRkb5trfeRQknmPbJ2D9unmvjpKYX5Da6oJ4s8LuYJlPdzwahLkUSAE1pPdJhLEVpaBqNgu1FFCPRKvssfVRGQg7HOo9fjS5f5Og53eEmn394dRjBIljdGGnJoVz7gxcC-PWWQ0kkBxd9UCQQD4LqJvdqEkZKfVpIqx2cKiFRibPTKslU7N9ogBYBMXbVSjf1jTQAbVv45LwA5HMiXbMiN1x4OmJhTy1flxSDt7AkEAnWMa8pHtrExEaYNtpF026IWFrZCTSzop5B-srrSUi0MDWTUd7Ag1zKSLEuPkOutIBE2krS_hFXSk7PihHAmr5wJBALrnC-dbdJGeyBKJvvWfZAeCUayfUulL7DQKBHTcsKQE7yfOAMvevQb-IqCDe308k-vWf4P0g_19Umtqn1cjqb8CQGgvLax7a0DOL89FADR4vBtKIAaYGNkIhKiNSytPQLG7R6Eq50bS3II-Pg3gK7nQ_BhVKXi3pCEm2PJBV60OvesCQQDzXHlKFIU0JCMHNEjEv2j_JUs3ABX0p8K-eqALmvquVQLNEdExG-XVV9AeS1Y9rrPbOOme2hV5cgIl5xgUlLEZ";
		
		
		try {
			reqVal = sign(reqVal,privateKey,info.getDescription());
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			
			log.info("加密后的请求报文:\r\n"+reqVal);
			
			//加密后，真正请求的报文
			paraMap.put("innerinputparam", reqVal);
			
			log.info("签名后报文：\n\r"+reqVal);
			
			//resVal车险返回的报文
			//String responseData=ReqeustProcessUtil.getHTTPResponseBody(info, snsUser , orgi , paraMap);
			String responseData=postString(info.getRequesturl(),(String)paraMap.get("innerinputparam") , info.getDescription());
			
			log.info("原返回报文: \n"+responseData);
			
			/*******************/
			//请求结果解密的过程1.resVal取出sign节点的值 2.给填充回去
			/*******************/
			
			String publicKey=null;
			
			if(info.getPublickey()!=null){
				publicKey = info.getPublickey();
			}else{
				publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpi714LS6BTL2xVPy7tAGPd2muIebSHrKTLqQ2mmUXjaEfHftIH1-slYQbtfTzimX7LO5VJGZtBohOfhMY-YSGEiMFJAmmHefX0SX6dY80FpC_Wgf-l0FVn4NQ5HcIXYMONAF7HXnhSJfgJ4Rp1x7NP1-0mLnhuHWCv1zYtw9gjwIDAQAB";
			}
			//验证签名
			if(verify(responseData, publicKey, info.getDescription()))
			{
				if(responseData!=null){
					if(responseData.indexOf("<SubOrder type=\"biz\">")>1){
						responseData=responseData.replaceAll("<SubOrder type=\"biz\">", "<SubOrder><type>biz</type>");
					}
					if(responseData.indexOf("<SubOrder type=\"force\">")>0){
						responseData=responseData.replaceAll("<SubOrder type=\"force\">", "<SubOrder><type>force</type>");
					}
					responseData=responseData.replace("<Definition></Definition>", "");
					responseData=responseData.replace(" type=\"vehicleInfo\"", "");
					responseData=responseData.replace(" type=\"renewal\"", "");
					responseData=responseData.replace(" type=\"recommend\"", "");
					responseData=responseData.replace(" type=\"luxury\"", "");
					responseData=responseData.replace(" type=\"economic\"", "");
					responseData=responseData.replace(" type=\"optional\"", "");
					responseData=responseData.replace(" type=\"force\"", "");
					responseData=responseData.replace(" type=\"biz\"", "");
					responseData=responseData.replace(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
					responseData=responseData.replace(" type=\"deadline\"", "");
					while((responseData.indexOf("Definition name=\"premium\"")>0 ||responseData.indexOf("Definition name=\"checkUrl\"")>0) &&responseData.indexOf("TagsList")>0){
						responseData=deallDefinion(responseData,"type");
						responseData=deallDefinion(responseData,"key");
						responseData=deallDefinion(responseData,"label");
						responseData=deallDefinion(responseData,"value");
						responseData=deallDefinion(responseData,"data");
						responseData=deallDefinion(responseData,"premium");
						responseData=deallDefinion(responseData,"dataUrl");
						responseData=deallDefinion(responseData,"checkUrl");
					}
					try {
						log.info("解析后XML报文："+responseData);
						String temstr=new XMLSerializer().read(responseData).toString();
						if(temstr!=null&& true){
							temstr=temstr.replaceAll("\\[\\]", "null");
							if(temstr.indexOf("\"RequestType\":\"100\"")>0 && temstr.indexOf("\"InsureType\":\"100\"")>0 && temstr.indexOf("\"Tag\":")>0){
								temstr=temstr.replaceAll("\\{\"Tag\":", "[");
								temstr=temstr.replaceAll("null}}", "null}]");
							}
							log.info("返回报文JSON："+temstr);
						}
						paraMap.put(info.getCode(),JSON.parse(info.isTrantjson()?temstr:responseData)) ;
						System.out.println();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			else
				paraMap.put(info.getCode(),null) ;
			
			//解密后的报文
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ProcessResult(0,paraMap,null );
	}
	public String deallDefinion(String responseData,String name){
		String tem="Definition name=\""+name+"\"";
		if(responseData.indexOf(tem)>0&&responseData.indexOf(tem)-responseData.indexOf("</Definition>")<0){
			responseData=responseData.replaceFirst(tem, name);
			responseData=responseData.replaceFirst("Definition", name);
		}
		return responseData;
	}
	public static String postString(String url, String text , String enc) {
		HttpPost httpost = new HttpPost(url);
		try {
			StringEntity myEntity = new StringEntity(text, enc);
	        httpost.setEntity(myEntity) ;
	        HttpClient client=new DefaultHttpClient();
//			HttpEntity resEntity = HttpClientTools.getHttpClient().execute(httpost).getEntity();
			HttpEntity resEntity = client.execute(httpost).getEntity();
			BufferedReader reader = null;
			if (resEntity != null) {
				reader = new BufferedReader(new InputStreamReader(resEntity.getContent() , enc!=null && enc.length()>0 ? enc : "UTF-8"));
				StringBuffer sb = new StringBuffer();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append("\r\n");
				}
				return sb.toString();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "";
	}
	
	private String sign(String requestData,String privateKey,String charset) throws UnsupportedEncodingException
	{
		KeyPairer keyPairer = new KeyPairer();
		
		String _requestBody =null;
		
		if(requestData.indexOf("<Request>")!=-1&&requestData.indexOf("</Request>")!=-1)
		{
			
			_requestBody = requestData.substring(requestData.indexOf("<Request>"),requestData.indexOf("</Request>")+"</Request>".length());
			
			log.info(" --------------------------->  _requestBody \n\r"+_requestBody);
			
		}
			
		if(_requestBody!=null)
		{
			
			PrivateKey key = keyPairer.getPrivateKey(privateKey);
			
			DefaultSigner ds = new DefaultSigner();
			
			//_requestBody = new String(_requestBody.getBytes(charset));
			
			log.info("转码后的报文 ： "+_requestBody);
			String _sign=ds.sign(_requestBody.getBytes(charset), key);
			if(requestData.indexOf("<Sign>")!=-1&&requestData.indexOf("</Sign>")!=-1)
			{
				if(requestData.indexOf("</Sign>")-requestData.indexOf("<Sign>")==6)
					requestData=requestData.replaceAll("<Sign></Sign>", "<Sign><![CDATA["+_sign+"]]></Sign>");
				else
				{
					StringBuffer _temp = new StringBuffer(500);
				
					_temp.append(requestData.substring(0,requestData.indexOf("<Sign>")+"<Sign>".length()));
					_temp.append("<![CDATA[");
					_temp.append(_sign);
					_temp.append("]]>");
					_temp.append(requestData.substring(requestData.indexOf("</Sign>"),requestData.length()));
					
					requestData = _temp.toString();
					
				}
			}
		}
		
		return requestData;
	}
	
	private boolean verify(String responseData,String publicKey,String charset) throws UnsupportedEncodingException
	{
		KeyPairer keyPairer = new KeyPairer();
		
		String responseBodyData=null;
		
		if(responseData.indexOf("<Response>")!=-1&&responseData.indexOf("</Response>")!=-1)
			responseBodyData =responseData.substring(responseData.indexOf("<Response>"),responseData.indexOf("</Response>")+"</Response>".length()); 
			
		if(responseData!=null)
		{
			PublicKey key = keyPairer.getPublicKey(publicKey);
			
			DefaultSigner ds = new DefaultSigner();
			
			String sign = null;
			
			if(responseData.indexOf("<Sign>")!=-1&&responseData.indexOf("</Sign>")!=-1)
			{
				sign = responseData.substring(responseData.indexOf("<Sign>")+"<Sign>".length(),responseData.indexOf("</Sign>"));
			}
			if(sign!=null)
			{
				//responseBodyData = new String(responseBodyData.getBytes(charset));
				
				log.info(" 转码后的报文 "+responseBodyData);
				
				return ds.verify(responseBodyData.getBytes(charset), sign, key);
			}
		}
		
		return false;
	}
	public static void main(String[] args) {
		try {
			/*String tem1=("<Tags><Tag><Definition>text</Definition><Definition>ownerIdNo</Definition><Definition>身份证号码</Definition><Definition></Definition><Definition></Definition><Definition></Definition></Tag><Tag><Definition>text</Definition><Definition>ownerIdNo</Definition><Definition>身份证号码</Definition><Definition></Definition><Definition></Definition><Definition></Definition></Tag></Tags>");
			String tem2=new XMLSerializer().read(tem1).toString();
			System.out.println(tem2);*/
			
			String temstr="{\"Package\":{\"Header\":{\"version\":\"2\",\"RequestType\":\"100\",\"InsureType\":\"100\",\"SessionId\":\"SMG2013931115624538192287\",\"SellerId\":\"3597746367\",\"SendTime\":\"2013-10-31 11:56:33\",\"Status\":\"200\",\"ErrorMessage\":null},\"Sign\":\"e04PVE47X0YdBVtUnBM-wL9J0lxJCeT5AVM3dxOinzaSoyic-EuK-iepuEQServ24WlRDVqEXBFwIg50MIkBqMnDk2zeHKCiqQniqFc-ZYgdY3NO06OhYkNLc5bh_KqABLbAWF_JJozWI4fCMe-d070X95PLWbQMgfrhBZZJfXs\",\"Response\":{\"TagsList\":{\"Tags\":{\"Tag\":{\"type\":\"text\",\"key\":\"ownerIdNo\",\"label\":\"身份证号码\",\"value\":null,\"dataUrl\":null,\"checkUrl\":null}}}}}}";
			if(temstr.indexOf("\"RequestType\":\"100\"")>0 && temstr.indexOf("\"InsureType\":\"100\"")>0 && temstr.indexOf("\"Tag\":")>0){
				temstr=temstr.replaceAll("\\{\"Tag\":", "[");
				temstr=temstr.replaceAll("null}}", "null}]");
			}
			System.out.println();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public ProcessResult getRequest(SNSUser snsUser, String orgi, Map<String, Object> paraMap) {
		// TODO Auto-generated method stub
		return null;
	}
}