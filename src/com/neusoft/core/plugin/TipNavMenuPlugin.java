package com.neusoft.core.plugin;

import java.util.List;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.web.model.Instruction;

public class TipNavMenuPlugin extends Plugin{
	/**
	 * 测试用，消息 拼出来的
	 * @param user
	 * @param orgi
	 * @return
	 */
	public String getMessage(Instruction instruct , AgentUser user , String orgi , Channel channel){
		List<Instruction> insList = EapDataContext.getInstructList(orgi) ;
		StringBuffer strb = new StringBuffer() , systrb = new StringBuffer();
		strb.append(instruct.getMemo());
		for(Instruction ins : insList){
			if("0".equals(ins.getParent()) && ins.isTipdefault()){
				if(EapDataContext.InstructionType.SYSTEM.toString().equals(ins.getType())){
					if(ins.isTipdefault()){
						if(systrb.length()>0){
							systrb.append(";\r\n") ;
						}
						systrb.append("-回复 [").append(ins.getCode()).append("]").append(ins.getName());
					}
				}else if(EapDataContext.InstructionType.BUSINESS.toString().equals(ins.getType())){
					if(strb.length()>0){
						strb.append("\r\n") ;
					}
					strb.append("[").append(ins.getCode()).append("] ").append(ins.getName());
				}
			}
		}
		return super.getChannelMessage(instruct, strb.append("\r\n\r\n").append(systrb.toString()).toString(), user, orgi, channel) ;
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
