package com.neusoft.core.plugin;


import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.web.model.AgentInfo;
import com.neusoft.web.model.AgentSkill;
import com.neusoft.web.model.Instruction;

public class TransferAgentSkillInstructPlugin implements InstructPluginInterface{
	/**
	 * 测试用，消息 拼出来的
	 * @param user
	 * @param orgi
	 * @return
	 */
	public String getMessage(Instruction instruct ,AgentUser user , String orgi , Channel channel){
		
		return null ;
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return "TipChooseAgentSkill";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initVirInstruct(String orgi , Instruction instruct) {
		if(orgi!=null){
			List<AgentSkill> agentSkillList = EapDataContext.getService().findAllByCriteria(DetachedCriteria.forClass(AgentSkill.class).add(Restrictions.eq("orgi", orgi)))  ;
			if(agentSkillList!=null){
				for(AgentSkill skill : agentSkillList){
					Instruction ins = new Instruction() ;
					try {
						if(instruct.getCreatetime()==null){
							instruct.setCreatetime(new Date());
						}
						BeanUtils.copyProperties(ins, instruct) ;
						ins.setId(skill.getId());
						ins.setVir(true);
						ins.setParent(ins.getId());
						ins.setParentins(instruct);
						ins.setPlugin(null);
						ins.setMemo(null);
						ins.setCode(skill.getCode());
						ins.setName(skill.getName());
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					EapDataContext.initVirInstruct(orgi, ins) ;
				}
			}
		}
	}
}
