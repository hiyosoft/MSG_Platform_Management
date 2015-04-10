package com.neusoft.core.plugin;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.web.model.Instruction;

public class TipNewsMessagePlugin  implements InstructPluginInterface{

	@Override
	public String getMessage(Instruction instruct, AgentUser user, String orgi , Channel channel) {
		// TODO Auto-generated method stub
		return instruct!= null? EapDataContext.MessageType.NEWS.toString()+":"+instruct.getMemo(): null ;
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return "TIP_NEWS_INSTRUCT";
	}

	@Override
	public void initVirInstruct(String orgi , Instruction instruct){
		// TODO Auto-generated method stub
		
	}

}
