package com.neusoft.util.process;


import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import net.sf.json.xml.XMLSerializer;

import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;
import org.codehaus.xfire.client.Client;

import com.alibaba.fastjson.JSON;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.channel.SNSUser;
import com.neusoft.util.store.EapTools;
import com.neusoft.web.model.IfaceInfo;
import com.neusoft.web.model.UserTemplet;

import freemarker.template.TemplateException;


public class ReqeustProcessUtil {
	private static final Logger log = Logger.getLogger(ReqeustProcessUtil.class);
	
	public static ProcessResult getResponseBody(IfaceInfo info , SNSUser snsUser , String orgi , Map<String , Object> paraMap) throws Exception, TemplateException{
		String responseBody = null ;
		ProcessResult result =null;
		
		Map<String , Object> valueMap = new HashMap<String,Object>() ;
		valueMap.putAll(paraMap);
		valueMap.put("user", snsUser) ;
		//填充URL里的参数
		if(info.getRequesturl()!=null && info.getRequesturl().indexOf("?")>0){
			String temparam=info.getRequesturl().substring(info.getRequesturl().indexOf("?")+1);
			String tprams []=temparam.split("&");
			for (String str : tprams) {
				String tvals []=str.split("=");
				if(tvals!=null && tvals.length==2){
					valueMap.put(tvals[0], tvals[1]);
				}
			}
		}
		if(info.getReqtemplate()!=null && info.getReqtemplate().length()>0){
			UserTemplet userTemplet = EapSmcDataContext.getUserTemplet(info.getReqtemplate(), orgi) ;
			String reqRequest = EapTools.getTemplet(userTemplet, valueMap) ;
			valueMap.put("innerinputparam", reqRequest);
		}
		log.info(valueMap);
		if("http".equals(info.getRpctype())){
			responseBody =  getHTTPResponseBody(info, snsUser , orgi , valueMap);
		}else if("soap".equals(info.getRpctype())){
			responseBody =  getSoapResponseBody(info, snsUser , orgi , valueMap);
		}else if("webservice".equals(info.getRpctype())){
			responseBody =  getWebServiceResponseBody(info, snsUser , orgi , valueMap);
		}else if("innerclass".equals(info.getRpctype())){
			InnerProcess inner = (InnerProcess) Class.forName(info.getClazz()).newInstance() ;
			result = inner.getRequest(info,snsUser, orgi, valueMap) ;
			//responseBody =  result!=null ? result.getBody(): "" ;
		}else if("esb".equals(info.getRpctype())){
			responseBody =   getESBPResponseBody(info, snsUser , orgi , valueMap);;
		}else if("axis2".equals(info.getRpctype())){
			responseBody =   getAxis2ResponseBody(info, snsUser , orgi , valueMap);;
		}
		log.info("原始返回报文：\n"+responseBody);
		if(responseBody!=null){
			//返回的xml数据，节点有属性，无法直接转换JSON，需要去除属性 class="insuredinfo"
			responseBody=responseBody.replace(" type=\"RESPONSE\" version=\"1.0\"", "");
			responseBody=responseBody.replace(" type='RESPONSE' version='1.0'", "");
			responseBody=responseBody.replace(" class=\"insuredinfo\"", "");
			String temstr=null;
			if(responseBody!=null && info.isTrantjson()){
				temstr=new XMLSerializer().read(responseBody).toString();
				temstr=temstr.replaceAll("\\[\\]", "null");
			}
			valueMap.put(info.getCode(), info.isTrantjson()?JSON.parse(temstr):responseBody) ;
			//仅仅为显示，可删除
			log.info("转换后JSON报文：\n"+valueMap.get(info.getCode()));
		}
		
		if(info.getRestemplate()!=null && info.getRestemplate().length()>0){
			UserTemplet userTemplet = EapSmcDataContext.getUserTemplet(info.getRestemplate(), orgi) ;
			responseBody = EapTools.getTemplet(userTemplet, valueMap) ;
		}  
		if(result==null){
			result=new ProcessResult(0,valueMap,responseBody);
		}
		return result ;
	}
	
	private static String getAxis2ResponseBody(IfaceInfo info, SNSUser snsUser,
			String orgi, Map<String, Object> valueMap) throws Exception {
		// axis2解析webservice
		String[] returnBytes = null;
		// 定义客户端对象
		RPCServiceClient serviceClient = null;
		try {		 
			serviceClient = new RPCServiceClient();
			
			//String 	urlsss="http://10.10.236.34:9000/servicebus/services/TransferXMLService";
			//url="http://localhost:7001/servicebus/services/TransferXMLService";
			//System.out.println("text======="+text);
			Options options = serviceClient.getOptions();
			// 生成模板EPR对象 --http://10.63.204.96:7001/servicebus/services/TransPolicyService
			//--http://10.10.228.33:9000/servicebus/services/TransPolicyService
			EndpointReference targetEPR = new EndpointReference(info.getRequesturl());
			options.setTo(targetEPR);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
			options.setTimeOutInMilliSeconds(2 * 60 * 1000);// 等待时间
			String[] opAddEntryArgs = new String[] {valueMap.get("innerinputparam")!=null?(new String(((String)valueMap.get("innerinputparam")).getBytes(info.getDescription()))):""};// 调用方法入参
			Class[] classes = new Class[] { String[].class };// 调用方法返回结果
			QName opAddEntry = new QName(info.getRequesturl(), info.getMethodname());//cancelTBPolicy handleTBPolicy transPolicy 调用方法名称救援单证新接口请求方法transDate
			returnBytes = (String[]) serviceClient.invokeBlocking(opAddEntry,
					opAddEntryArgs, classes)[0];	
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (serviceClient != null) {
				serviceClient.cleanupTransport();
				serviceClient.cleanup();
			}
		}
		return returnBytes!=null && returnBytes.length>0?returnBytes[0]:null;
	}

	public static String getHTTPResponseBody(IfaceInfo info , SNSUser snsUser , String orgi, Map<String , Object> valueMap) throws IOException, TemplateException{
		if(info.getReqtemplate()==null || "".equals(info.getReqtemplate())){
			return EapTools.getURL(EapTools.getTemplet(info.getRequesturl(), valueMap) , info.getDescription()) ;
		}
		return EapTools.postString(info.getRequesturl(),(String)valueMap.get("innerinputparam") , info.getDescription()) ;
	}
	private static String getESBPResponseBody(IfaceInfo info , SNSUser snsUser , String orgi, Map<String , Object> valueMap) throws IOException, TemplateException{
		String[] returnBytes = null;
		// 定义客户端对象
		RPCServiceClient serviceClient = null;
		try {		 
			serviceClient = new RPCServiceClient();
			
			//String 	urlsss="http://10.10.236.34:9000/servicebus/services/TransferXMLService";
			//url="http://localhost:7001/servicebus/services/TransferXMLService";
			//System.out.println("text======="+text);
			String nameSpace = "http://webservice.transferxml.servicebus.sinosig.com";
			Options options = serviceClient.getOptions();
			// 生成模板EPR对象 --http://10.63.204.96:7001/servicebus/services/TransPolicyService
			//--http://10.10.228.33:9000/servicebus/services/TransPolicyService
			EndpointReference targetEPR = new EndpointReference(info.getRequesturl());
			options.setTo(targetEPR);
			options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
			options.setTimeOutInMilliSeconds(2 * 60 * 1000);// 等待时间
			Object[] opAddEntryArgs = new Object[] {((String)valueMap.get("innerinputparam")).getBytes(info.getDescription())};// 调用方法入参
			Class[] classes = new Class[] { String[].class };// 调用方法返回结果
			QName opAddEntry = new QName(nameSpace, info.getMethodname());//cancelTBPolicy handleTBPolicy transPolicy 调用方法名称救援单证新接口请求方法transDate
			returnBytes = (String[]) serviceClient.invokeBlocking(opAddEntry,
					opAddEntryArgs, classes)[0];	
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (serviceClient != null) {
				serviceClient.cleanupTransport();
				serviceClient.cleanup();
			}
		}
		if(returnBytes!=null && returnBytes.length>0 ){
			return new String(Base64.decode(returnBytes[0]),info.getDescription());
		}
		return null;
	}
	
	public static String getSoapResponseBody(IfaceInfo info , SNSUser snsUser , String orgi , Map<String , Object> valueMap) throws IOException, TemplateException{
		String responsBody = null ;
		if(info.getReqtemplate()!=null && info.getReqtemplate().length()>0){
			responsBody = EapTools.postString(EapTools.getTemplet(info.getRequesturl(), valueMap), (String)valueMap.get("innerinputparam") , info.getDescription()) ;
		}else{
			responsBody = getHTTPResponseBody(info , snsUser , orgi , valueMap) ;
		}		
		return responsBody ;
	}
	public static String getWebServiceResponseBody(IfaceInfo info , SNSUser snsUser , String orgi , Map<String , Object> valueMap) throws Exception{
		String wsURL = info.getRequesturl();
		try {
			Client client = new Client(new URL(wsURL));
			Object params[]=null;
			Object[] paramArray =null;
			if(info.getReqparams()!=null && info.getReqparams().split(",").length>1){
				params=info.getReqparams().split(",");
				paramArray=new Object[params.length+1];
				for (int i = 0; i <paramArray.length; i++) {
					if(i==params.length && info.getReqtemplate()!=null){
						paramArray[i]=valueMap.get("innerinputparam");
					}else{
						String tempara=(String) params[i];
						if(tempara!=null && tempara.indexOf("=")>0){
							paramArray[i]=tempara.split("=")[1];
						}else{
							paramArray[i]=valueMap.get(tempara);
						}
					}
				}
			}else{
				paramArray=new Object[]{valueMap.get("innerinputparam")};
			}
			Object[] results = client.invoke(info.getMethodname(),
					paramArray);
			
			return results==null || (results!=null&&results.length==0)?null:results[0].toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String replaceTool(String matcherStr,String regex,String replacement)
	{
	        Pattern pattern = Pattern.compile(regex); 
	        Matcher matcher = pattern.matcher(matcherStr); 
	        StringBuffer sb = new StringBuffer(); 
	        while (matcher.find()) { 
	            matcher.appendReplacement(sb, replacement); 
	        } 
	        matcher.appendTail(sb); 
	        return sb.toString();
	}
	public static void main(String[] args) {
		try {  
            /*String endpoint = "http://10.10.164.43:8889/eservice/contractquery";  
            //直接引用远程的wsdl文件  
            //以下都是套路   
            Service service = new Service();  
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(endpoint);  
            call.setOperationName("getContbaseinfoByContno");//WSDL里面描述的接口名称  
            call.addParameter("arg0", org.apache.axis.encoding.XMLType.XSD_DATE,  
                          javax.xml.rpc.ParameterMode.IN);//接口的参数  
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);//设置返回类型    
            String temp = "3232";  
            String result = (String)call.invoke(new Object[]{temp});  
            //给方法传递参数，并且调用方法  
            System.out.println("result is "+result);  */
			
//			JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
//			Client client = dcf.createClient("http://10.10.164.43:8889/slisws/cl/claimquery?wsdl");
////			Endpoint endpoint = client.getEndpoint();  
////			  
////			  
////	        // Make use of CXF service model to introspect the existing WSDL  
////	        ServiceInfo serviceInfo = endpoint.getService().getServiceInfos().get(0);  
//			Object[] objects = client.invoke("getClaims", "<?xml version='1.0' encoding='UTF-8'?><data><head><wsid>SLIS_CL_20130904_01</wsid><bussno>511122198108088823</bussno></head><body><insuredidno>511122198108088823</insuredidno></body></data>");
//			System.out.println(objects);
			
			String wsURL = "http://esb.sinosig.com:8291/ecif/service/commonInterface?wsdl";
			try {
				Client client = new Client(new URL(wsURL));
				Object[] results = client.invoke("merge",
						new String[] { "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ROOT><HEAD><SEQNO>be8ca3ab-687d-43cc-8ecc-5af310fe5730</SEQNO><USERNAME>WEIXIN</USERNAME><PASSWORD>123</PASSWORD><SERVICECODE>6001</SERVICECODE><SENDLOGO>WEIXIN</SENDLOGO><RECEIVELOGO>CIF</RECEIVELOGO><SENDTIME>20131127103654</SENDTIME><EXT></EXT></HEAD><BODY><DATA><partyNo>1002356070</partyNo><wechatNo>ocP_Sjpm_8wXmvkCDeCOqR02ZO9E</wechatNo><opType>1</opType></DATA></BODY></ROOT>"});
				System.out.println("CIF返回==============="+new XMLSerializer().read(results[0].toString()).toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			
			//System.out.println(new XMLSerializer().read("<?xml version='1.0' encoding='utf-8'?><PACKET type='RESPONSE' version='1.0'><HEAD><TRANSTYPE></TRANSTYPE><TRANSCODE></TRANSCODE><RESPONSECODE>000002</RESPONSECODE><ERRORMESSAGE>web service server error!! Build Document Object Error.Error on line 1 of document  : Content is not allowed in prolog. Nested exception: Content is not allowed in prolog.</ERRORMESSAGE><SVCSEQNO></SVCSEQNO></HEAD><BODY></BODY></PACKET>"));
			
			/*String[] returnBytes = null;
			// 定义客户端对象
			RPCServiceClient serviceClient = null;
			try {		 
				serviceClient = new RPCServiceClient();
				
				String path = "D:/benefsinfo.xml";//--TB-WBT-冲突.xml-TB-LY-Handle.xml-淘宝退保查询请求报文-保单查询.xml//tbtest-req.xml
				File file = new File(path);
				FileInputStream fis = new FileInputStream(file);
				byte[] buf = new byte[(int) file.length()];		
				fis.read(buf);
				String text = new String(buf);
				String 	url="http://114.251.230.13:7002/sinosig/PartnerServlet";
//				url="http://localhost:7001/servicebus/services/TransferXMLService";
				System.out.println("text======="+text);
				String nameSpace = "http://webservice.transferxml.servicebus.sinosig.com";
				Options options = serviceClient.getOptions();
				// 生成模板EPR对象 --http://10.63.204.96:7001/servicebus/services/TransPolicyService
				//--http://10.10.228.33:9000/servicebus/services/TransPolicyService
				EndpointReference targetEPR = new EndpointReference(url);
				options.setTo(targetEPR);
				options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
				options.setTimeOutInMilliSeconds(2 * 60 * 1000);// 等待时间
				Object[] opAddEntryArgs = new Object[] { text };// 调用方法入参
				Class[] classes = new Class[] { String[].class };// 调用方法返回结果
				QName opAddEntry = new QName(nameSpace, "getBasePolicyInfo");//cancelTBPolicy handleTBPolicy transPolicy 调用方法名称救援单证新接口请求方法transDate
				returnBytes = (String[]) serviceClient.invokeBlocking(opAddEntry,
						opAddEntryArgs, classes)[0];			
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (serviceClient != null) {
					serviceClient.cleanupTransport();
					serviceClient.cleanup();
				}
			}
			
			System.out.println("returnBytes======="+new String(Base64.decode(returnBytes[0])));
			*/
			
			String reg="<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"yes\"?><ECOMMERCE>" +
        "<ACCOUNTNAME>13631643146</ACCOUNTNAME><ACCOUNTTYPE>02</ACCOUNTTYPE><ACCOUNTPWD>123456</ACCOUNTPWD><CHANNELTYPE>13</CHANNELTYPE><SOURCETYPE>0</SOURCETYPE><InterfaceCode>01</InterfaceCode>" +
        "</ECOMMERCE>";
			String login="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ECOMMERCE><ACCOUNTNAME>13349741212</ACCOUNTNAME><ACCOUNTPWD>123456</ACCOUNTPWD><ACCOUNTTYPE>02</ACCOUNTTYPE></ECOMMERCE>";
			String query="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ECOMMERCE><CUSTID>106391</CUSTID></ECOMMERCE>";
			String wuyao="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ECOMMERCE><CUSTID>4426200</CUSTID></ECOMMERCE>";
			
			Client client = new Client(new URL("http://www.sinosig.com/ws/wxAgentService?wsdl"));
//			Client client = new Client(new URL("http://192.168.1.103:8080/ws/wxAgentService?wsdl"));
			Object[] results = client.invoke("getCustInfoById",
					new String[]{"WEIXIN123123ZPINGTAIKEYONGZHANGH", 
					"JIUXIANYONGZHEGEBA763917628ZAIGA",wuyao});

			System.out.println(results[0].toString());
			
//			String tem=new XMLSerializer().read("<?xml version='1.0' encoding='utf-8'?><PACKET><HEAD><TRANSTYPE>InsuredsInfo</TRANSTYPE><TRANSCODE>20001</TRANSCODE><RESPONSECODE>000004</RESPONSECODE><ERRORMESSAGE>Can't find this policy.</ERRORMESSAGE><SVCSEQNO></SVCSEQNO></HEAD><BODY></BODY></PACKET>").toString().replaceAll("\\[\\]", "null");
//			System.out.println(JSON.parse(tem));
//			System.out.println(JSON.parse(new XMLSerializer().read("<?xml version='1.0' encoding='UTF-8'?><ROOT><HEAD><SEQNO>5ed5c41f-c96d-4ca3-8745-06e13405c25b</SEQNO><USERNAME>GEN</USERNAME><PASSWORD>123</PASSWORD><SERVICECODE>1001</SERVICECODE><SENDLOGO>CIF</SENDLOGO><RECEIVELOGO>WEIXIN</RECEIVELOGO><SENDTIME>20130925141021</SENDTIME><REFSENDLOGO>WEIXIN</REFSENDLOGO><REFRECEIVELOGO>CIF</REFRECEIVELOGO><RETCODE>000000</RETCODE><RETMESSAGE></RETMESSAGE><EXT></EXT></HEAD><BODY><ISSUCCESS>TRUE</ISSUCCESS><ERRORMESSAGE></ERRORMESSAGE><PAGENO>1</PAGENO><PAGESIZE>100</PAGESIZE><TOTALRECORDS>1</TOTALRECORDS><T_S_PERSON><DATA><PARTYNO>1005985884</PARTYNO></DATA></T_S_PERSON></BODY></ROOT>").toString()));
			/*IfaceInfo info=new IfaceInfo();
			info.setRequesturl("http://114.251.230.13:7002/sinosig/PartnerServlet");
			System.out.println(EapTools.getURL(EapTools.getTemplet(info.getRequesturl(), new HashMap()) ));
			*/
			
			/*String path = "D:/benefsinfo.xml";//--TB-WBT-冲突.xml-TB-LY-Handle.xml-淘宝退保查询请求报文-保单查询.xml//tbtest-req.xml
			File file = new File(path);
			FileInputStream fis = new FileInputStream(file);
			byte[] buf = new byte[(int) file.length()];		
			fis.read(buf);
			String text = new String(buf);
			System.out.println( EapTools.postString("http://114.251.230.13:7002/sinosig/PartnerServlet",text));
			*/
			
			 /*JaxWsProxyFactoryBean svr = new JaxWsProxyFactoryBean();
			  svr.setServiceClass(WXAgentWS.class);
			  svr.setAddress("http://192.168.1.103:8080/ws/wxAgentService?wsdl");
			  WxAgentService service= (WxAgentService) svr.create();
			*/
			  /*WxAgentService service = new WxAgentService();  
			  WXAgentWS agent = service.getWXAgentWSPort() ;
			  String tem=agent.accountLogin("WEIXIN123123ZPINGTAIKEYONGZHANGH", "JIUXIANYONGZHEGEBA763917628ZAIGA", "<?xml version='1.0' encoding='GBK' standalone='yes'?><ECOMMERCE><ACCOUNTNAME>帐号名</ ACCOUNTNAME ><ACCOUNTPWD>明文密码</ACCOUNTPWD ></ECOMMERCE>");
			*/
			
			//System.out.println(EapTools.postString("http://10.10.168.190:7001/sinosig/api/user/userlogout.html","<?xml version=\"1.0\" encoding=\"utf-8\"?><ROOT><ResponseType>xml</ResponseType><AgentId>12sdfdsfsfsdfdf3</AgentId><FromSystem>TestSystem</FromSystem></ROOT>", "utf-8"));
			//System.out.println(EapTools.postString("http://10.63.201.141:8080/test/api/sendmsg.html","<?xml version=\"1.0\" encoding=\"utf-8\"?><ROOT><ResponseType>xml</ResponseType><AgentId>106309</AgentId><FromSystem>TestSystem</FromSystem></ROOT>", "utf-8"));
			 /*try { 
		        	WxAgentService service = new WxAgentService(new URL("http://192.168.1.103:8080/ws/wxAgentService?wsdl")) ;
		        	WXAgentWS agent = service.getWXAgentWSPort() ;
		        	agent.accountLogin("WEIXIN123123ZPINGTAIKEYONGZHANGH", "JIUXIANYONGZHEGEBA763917628ZAIGA", "<?xml version=\"1.0\" encoding=\"GBK\" standalone=\"yes\"?><ECOMMERCE><ACCOUNTNAME>帐号名</ ACCOUNTNAME ><ACCOUNTPWD>明文密码</ACCOUNTPWD></ECOMMERCE>");
		        } catch (Exception e) { 
		            e.printStackTrace(); 
		        }*/

     }  
     catch (Exception e) {  
            e.printStackTrace();
            
     }  
//		System.out.println(postSOAP("http://10.10.164.43:8887/slisws/cl/claimquery?wsdl","<?xml version='1.0' encoding='UTF-8'?><data><head><wsid>SLIS_CL_20130904_01</wsid><bussno>511122198108088823</bussno></head><body><insuredidno>511122198108088823</insuredidno></body></data>"));
//		System.out.println(getRPCResponsetest("http://10.10.164.43:8887/slisws/cl/claimquery?wsdl","<?xml version='1.0' encoding='UTF-8'?><data><head><wsid>SLIS_CL_20130904_01</wsid><bussno>511122198108088823</bussno></head><body><insuredidno>511122198108088823</insuredidno></body></data>"));
//		String url = "http://validform.rjboy.cn/demo/ajax_post.php";
//		System.out.println(postSOAP(url, null));
		
		/*String url1 = "http://10.10.164.43:8887/slisws/cl/claimquery?wsdl";
		try {
			System.out.println(apatchSoap(url1, "<?xml version='1.0' encoding='UTF-8'?><data><head><wsid>SLIS_CL_20130904_01</wsid><bussno>511122198108088823</bussno></head><body><insuredidno>511122198108088823</insuredidno></body></data>"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}

}
