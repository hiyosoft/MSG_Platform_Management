package com.neusoft.core.plugin;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.web.model.AgentSkill;
import com.neusoft.web.model.Instruction;

public class TipChooseAgentSkillPlugin  extends Plugin{

	@Override
	public String getMessage(Instruction instruct, AgentUser user, String orgi , Channel channel) {
		List<AgentSkill> skills = EapDataContext.getService().findAllByCriteria(DetachedCriteria.forClass(AgentSkill.class).add(Restrictions.eq("orgi", orgi)));
		StringBuffer strb = new StringBuffer("请选择接入的分组：\r\n") ;
		for (int i = 0; i < skills.size(); i++) {
			AgentSkill as=skills.get(i);
			strb.append("[").append(i+1).append("] ").append(as.getName()).append("\r\n");
		}
		return super.getChannelMessage(instruct, strb.toString(), user, orgi, channel) ;
	}

	public String getCode() {
		// TODO Auto-generated method stub
		return "TipChooseAgentSkillPlugin";
	}

	@Override
	public void initVirInstruct(String orgi , Instruction instruct){
		// TODO Auto-generated method stub
		
	}

}
