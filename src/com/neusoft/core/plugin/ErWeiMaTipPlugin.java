package com.neusoft.core.plugin;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.api.APIContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.core.channel.DataMessage;
import com.neusoft.core.channel.WeiXin;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.web.model.Instruction;

public class ErWeiMaTipPlugin extends Plugin{
	/**
	 * 扫描带参数的二维码，除了返回指令本身，还返回二维码参数对应的IMR,即可返回多个IRM
	 * @param user
	 * @param orgi
	 * @return
	 */
	
	
	public String getMessage(Instruction instruct ,AgentUser user , final String orgi , Channel channelx){
		String eventkey=((WeiXin)channelx).getBcardUserName();
		if(eventkey!=null && eventkey.indexOf("qrscene_")>=0){
			eventkey=eventkey.replaceAll("qrscene_", "");
		}
		if(eventkey!=null && EapDataContext.getInstruct(orgi)!=null && EapDataContext.getInstruct(orgi).containsKey(eventkey)){
			Instruction evins = (EapDataContext.getInstruct(orgi).get(eventkey)).get(0);
			final String resulMsg=super.getChannelMessage(evins , evins.getMemo(), user, orgi, channelx);
			final String userid=channelx.getUserid();
			Thread td=new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					if(resulMsg!=null){
						Channel wx=new WeiXin();
						wx.setTouser(userid) ;
						wx.setUserid(userid) ;
						wx.setText(resulMsg);
						wx.setReplytype(EapDataContext.ReplyType.MANUALLY.toString()) ;
						wx.setMessagetype(EapDataContext.MessageType.TEXT.toString()) ;
						wx.setOrgi(orgi) ;
						DataMessage dataMessage = new DataMessage(EapDataContext.ChannelTypeEnum.WEIXIN.toString() , wx , orgi , wx.getUserid()) ;
						try {
							Thread.currentThread().sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						APIContext.sendMessageToUser(dataMessage) ;
						APIContext.saveMessage(dataMessage) ;
					}
				}
			});
			try {
				td.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			td.start();
		}
		return super.getChannelMessage(instruct , instruct.getMemo(), user, orgi, channelx);
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
}
