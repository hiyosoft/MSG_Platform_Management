package com.neusoft.core.plugin;

import com.neusoft.core.channel.Channel;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.web.model.Instruction;

public class TipCurrentInstructPlugin  extends Plugin{

	@Override
	public String getMessage(Instruction instruct, AgentUser user, String orgi , Channel channel) {
		// TODO Auto-generated method stub
		return super.getChannelMessage(instruct, instruct!= null? instruct.getMemo(): null , user, orgi, channel);
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return "TIP_CURRENT_INSTRUCT";
	}

	@Override
	public void initVirInstruct(String orgi , Instruction instruct){
		// TODO Auto-generated method stub
		
	}

}
