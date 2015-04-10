package com.neusoft.core.plugin;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.web.model.Instruction;
import com.neusoft.web.model.SinosigZLBC;
import com.neusoft.web.model.SinosigZLBCRes;
/**
 * 功能：保存上传的图片
 * @author Kerwin
 *
 */
public class SinosigZlpcSaveImagePlugin  extends Plugin{

	@Override
	public String getMessage(Instruction instruct, AgentUser user, String orgi ,  Channel channel) {
		final byte [] images=channel.getBytedata();
		System.out.println("===========图片大小======"+images.length);
		//把图片保存到数据库
		if(channel.getSnsuser()!=null){
			//案件
			final Channel temChannel=channel;
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					SinosigZLBC zlbc=EapSmcDataContext.getZlbcMap().get(temChannel.getSnsuser().getApiusername());
					if(zlbc!=null && zlbc.getCaseid()!=null && zlbc.getCaseid().length()>1){
						SinosigZLBCRes res=new SinosigZLBCRes();
						res.setCaseid(zlbc.getCaseid());
						res.setOrgi(temChannel.getOrgi());
						res.setRestype(1);
						res.setZlbcid(zlbc.getId());
						res.setImage(images);
						EapDataContext.getService().saveIObject(res);
					}
				}
			}).start();
		}
		EapDataContext.getLogger(SinosigZlpcSaveImagePlugin.class).info("========图片上传成功==========");
		instruct.setMemo("请在所有资料照片上传结束后，发送“#”字符表示上传结束。");
		
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

}
