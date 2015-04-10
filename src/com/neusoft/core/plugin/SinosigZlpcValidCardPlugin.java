package com.neusoft.core.plugin;

import java.io.IOException;
import java.util.Date;

import org.jfree.util.Log;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.web.model.Instruction;
import com.neusoft.web.model.SinosigZLBC;
import com.sinosig.task.interfaces.IZLBCVariables;

import common.Logger;

public class SinosigZlpcValidCardPlugin  extends Plugin  implements IZLBCVariables{

	
	private Logger log = Logger.getLogger(SinosigZlpcValidCardPlugin.class);
	
	StringBuffer XML_MESSAGES_BUFFER=new StringBuffer("<?xml version='1.0' encoding='GBK'?><PACKET type='REQUEST' version='1.0'><Request><LicenseNo>${LicenseNo}</LicenseNo><DamageDate>${DamageDate}</DamageDate></Request></PACKET>");
	
	@Override
	public String getMessage(Instruction instruct, AgentUser user, String orgi , Channel channel) {
		// TODO 插件功能：根据车牌号，调用核心的系统，返回相关的数据并保存
		//channel.getText()获取用户发送的消息,instruct.memo为请求接口的url
		System.out.println("====调用资料补充接口===");
		if(channel.getText()!=null){
			String responsexml=null;
			try {
				//根据车牌号去核心判断是否有案件号
//				if()
				String msg=channel.getText();
				
				String[] msgArr = null;

				if(msg!=null&&!msg.trim().equals(""))
				{
					if(msg.indexOf(",")>0)
					{
						msgArr = msg.split(",");
					}
					else if( msg.indexOf("，")>0)
					{
						msgArr = msg.split("，");
					}
					else
					{
						instruct.setMemo("尊敬的客户，您好！当前您没有需要补充资料的案件，请复核您的录入信息或与您的理赔服务人员联系，谢谢。");
					}
				}
				
				if(msgArr!=null&&msgArr.length==2)
				{
					
					
					XML_MESSAGES_BUFFER.replace(XML_MESSAGES_BUFFER.lastIndexOf("${LicenseNo}"), XML_MESSAGES_BUFFER.lastIndexOf("${LicenseNo}")+"${LicenseNo}".length(), msgArr[0].toUpperCase());
					XML_MESSAGES_BUFFER.replace(XML_MESSAGES_BUFFER.lastIndexOf("${DamageDate}"), XML_MESSAGES_BUFFER.lastIndexOf("${DamageDate}")+"${DamageDate}".length(), msgArr[1]);
					
					log.info(XML_MESSAGES_BUFFER.toString());
					
					
					log.info("----------------------------->  "+URL_GETCLAIMMSG_ADDRESS);
					responsexml=EapSmcDataContext.httpPostBodyRequestProcesser(XML_MESSAGES_BUFFER.toString(),URL_GETCLAIMMSG_ADDRESS,"GBK", "GBK","POST");
					
					
				}
				
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			EapDataContext.getLogger(SinosigZlpcValidCardPlugin.class).info("车牌号查询是否有需要补充资料的案件返回报文："+responsexml);
			
			if(responsexml!=null && responsexml.indexOf("CASE_NO") > 0)
			{
				instruct.setMemo("尊敬的客户，您的爱车发生以下情况："+EapSmcDataContext.findDateFromXml(responsexml, "DAMAGECASE")+"，\r\n需要您补充以下资料："+EapSmcDataContext.findDateFromXml(responsexml, "CLAIM_MSG")+" \r\n请在所有资料照片上传结束后，发送“#”字符表示上传结束。");
				final Channel temChannel=channel;
				final String finalxml=responsexml;
				//把事件信息保存到数据库，对于model为SinosigZLBC
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						SinosigZLBC zlbc=new SinosigZLBC();
						zlbc.setApiusername(temChannel.getSnsuser().getApiusername());
						String caseid=EapSmcDataContext.findDateFromXml(finalxml, "CASE_NO");
						zlbc.setCaseid(caseid);
						zlbc.setOpid(EapSmcDataContext.findDateFromXml(finalxml, "OP_ID"));
						zlbc.setOpuser(EapSmcDataContext.findDateFromXml(finalxml, "OP_USER"));
						zlbc.setComcode(EapSmcDataContext.findDateFromXml(finalxml, "COM_CODE"));
						zlbc.setOrgnum(EapSmcDataContext.findDateFromXml(finalxml, "ORG_NUM"));
						zlbc.setDamagecase(EapSmcDataContext.findDateFromXml(finalxml, "DAMAGECASE"));
						zlbc.setClaimmsg(EapSmcDataContext.findDateFromXml(finalxml, "CLAIM_MSG"));
						zlbc.setOrgi(temChannel.getOrgi());
						zlbc.setStatus(0); 
						zlbc.setZipfile(caseid+"_"+new Date().getTime());
						EapDataContext.getService().saveIObject(zlbc);
						//把事件缓存
						EapSmcDataContext.getZlbcMap().put(temChannel.getSnsuser().getApiusername(),zlbc);
					}
				}).start();
			}else{
				instruct.setMemo("尊敬的客户，您好！当前您没有需要补充资料的案件，请复核您的录入信息或与您的理赔服务人员联系，谢谢。");
			}
		}else{
			instruct.setMemo("尊敬的客户，您好！当前您没有需要补充资料的案件，请复核您的录入信息或与您的理赔服务人员联系，谢谢。");
		}
		return super.getChannelMessage(instruct, instruct!= null? instruct.getMemo(): null , user, orgi, channel);
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void initVirInstruct(String orgi , Instruction instruct){
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		
		StringBuffer XML_MESSAGES_BUFFER=new StringBuffer("<?xml version='1.0' encoding='GBK'?><PACKET type='REQUEST' version='1.0'><Request><LicenseNo>${LicenseNo}</LicenseNo><DamageDate>${DamageDate}</DamageDate></Request></PACKET>");
		
		XML_MESSAGES_BUFFER.replace(XML_MESSAGES_BUFFER.lastIndexOf("${LicenseNo}"), XML_MESSAGES_BUFFER.lastIndexOf("${LicenseNo}")+"${LicenseNo}".length(), "浙A8HX82");
		XML_MESSAGES_BUFFER.replace(XML_MESSAGES_BUFFER.lastIndexOf("${DamageDate}"), XML_MESSAGES_BUFFER.lastIndexOf("${DamageDate}")+"${DamageDate}".length(),"20131016");

		try
		{
			String responsexml=EapSmcDataContext.httpPostBodyRequestProcesser(XML_MESSAGES_BUFFER.toString(),URL_GETCLAIMMSG_ADDRESS,"GBK", "GBK","POST");
			
			System.out.println(responsexml);
			
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
