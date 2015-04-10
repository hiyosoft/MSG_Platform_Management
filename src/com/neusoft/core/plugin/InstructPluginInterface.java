package com.neusoft.core.plugin;

import com.neusoft.core.channel.Channel;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.web.model.Instruction;

public interface InstructPluginInterface {
	public String getMessage(Instruction instruct , AgentUser user , String orgi , Channel channel) ;
	public String getCode();
	public void initVirInstruct(String orgi , Instruction instruct) ;
}
