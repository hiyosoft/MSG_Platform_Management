package com.neusoft.core.plugin;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.web.model.Instruction;
import com.neusoft.web.model.SearchResultTemplet;

public class TipSubInstructPlugin extends Plugin{
	/**
	 * 测试用，消息 拼出来的
	 * @param user
	 * @param orgi
	 * @return
	 */
	public String getMessage(Instruction instruct ,AgentUser user , String orgi , Channel channel){
		
		List<Instruction> insList = EapDataContext.getInstructList(orgi , instruct.getId()) ;
		StringBuffer strb = new StringBuffer() ;
		strb.append(instruct.getMemo());
		int i=0 ;
		
		for(Instruction ins : insList){
			if(i%2==0){
				strb.append("\r\n");
			}else{
				strb.append("   ");
			}
			i++ ;
			strb.append("[").append(ins.getCode()).append("] ").append(ins.getName());
		}
		return super.getChannelMessage(instruct , strb.toString(), user, orgi, channel);
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
