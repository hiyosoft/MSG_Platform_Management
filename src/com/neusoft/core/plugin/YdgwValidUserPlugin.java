package com.neusoft.core.plugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.neusoft.core.channel.Channel;
import com.neusoft.core.channel.WeiXin;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.util.store.EapTools;
import com.neusoft.web.model.Instruction;

public class YdgwValidUserPlugin extends Plugin{
	/**
	 * 测试用，消息 拼出来的
	 * @param user
	 * @param orgi
	 * @return
	 */
	public String getMessage(Instruction instruct ,AgentUser user , String orgi , Channel channel){
		Instruction temins=new Instruction();
		temins.setMemo(instruct.getMemo());
		temins.setPlugin(instruct.getPlugin());
		if(channel.getSnsuser()!=null && instruct.getMemo()!=null){
			//instruct.getMemo里有三个参数，消息处理为无处理，一个是请求移动官网的url，另外一个是素材的id，另外一个是未购买的提示消息
			String [] params=temins.getMemo().split("[;；]");
			//TODO：请求移动官网，判断是否购买过====接口未提供稍后
			String result=null;
			if(params!=null && params.length==3){
				String requrl=params[0];
				requrl=requrl.replaceAll("sinosigapiusername", ((WeiXin)channel).getFromUserName());
				try {
					result = EapTools.postString(requrl, "", "utf-8");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//如果有购买过，将会回复素材；否则回复绿色文本消息{"jsonWrapper":{"isBuyByWxId":"false"}}
				if(result!=null && result.indexOf("\"isBuyByWxId\":\"true\"")>0){
					temins.setMemo(params[1]);
				}else{
					temins.setMemo(params[2]);
					temins.setPlugin("");
				}
			}			
		}
		return super.getChannelMessage(temins , temins.getMemo(), user, orgi, channel);
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initVirInstruct(String orgi , Instruction instruct){
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args) {
//		System.out.println(EapTools.postString("http://m.sinosig.com/mobile/wxhelp/wx_help!isBuyByWxId.action?wxId=wxid123456", "", "utf-8"));
//		System.out.println(EapTools.postString("http://10.63.206.6:8088/mobile/wx_help!isBuyByWxId.action?apiusername=test&source=wx", "", "utf-8"));
		System.out.println(EapTools.postString("http://10.63.206.6:8088/mobile/wxhelp/wx_help!isBuyByWxId.action?wxId=445&source=wx", "", "utf-8"));
	}
	public static String httpPostBodyRequestProcesser(
			String requestDateMessage, String requestURLAddress,
			String requestCharset, String responseCharset,String method) throws IOException {

		URL url = null;

		HttpURLConnection httpURLConnection = null;

		String responseDateMessage = null;
		BufferedWriter out =null;
		BufferedReader in =null;

		try {
			url = new URL(requestURLAddress);
			httpURLConnection = (HttpURLConnection) url.openConnection();

			httpURLConnection.setDoOutput(true);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod(method);
			httpURLConnection.setRequestProperty("Content-Type","text/json;charset=" + requestCharset);
//			httpURLConnection.setRequestProperty("SOAPAction","http://WebXml.com.cn/getWeatherbyCityName");
//			httpURLConnection.setRequestProperty("User-Agent","Jakarta Commons-HttpClient/3.1");
			httpURLConnection.setConnectTimeout(50000);
			httpURLConnection.setReadTimeout(50000);

			httpURLConnection.connect();

			out = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), requestCharset));

			out.write(requestDateMessage);
			out.flush();
			out.close();
			
			in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), responseCharset));
			String line = null;
			StringBuilder sb = new StringBuilder();

			while ((line = in.readLine()) != null) 
			{
				sb.append(line);
			}

			responseDateMessage = sb.toString();

		} 
		catch (IOException e) 
		{
			throw new IOException();
		} 
		finally 
		{
			if(httpURLConnection!=null){
				httpURLConnection.disconnect();
			}
			if(in!=null){
				in.close();
			}
		}
		
		return responseDateMessage;
	}
}
