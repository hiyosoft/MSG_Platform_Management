package com.neusoft.core.plugin;


import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.core.channel.SNSUser;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.web.model.AgentSkill;
import com.neusoft.web.model.Instruction;

public class TransferAgentInstructPlugin extends Plugin{
	/**
	 * 测试用，消息 拼出来的
	 * @param user
	 * @param orgi
	 * @return
	 */
	public String getMessage(Instruction instruct ,AgentUser user , String orgi , Channel channel){
		SNSUser snsuser = channel.getSnsuser() ;
//		if(channel.getSnsuser().getUserid()==null){
//			snsuser = PersistenceFactory.getInstance().getSnsUserInfo(channel.getSnsuser().getApiusername(), channel.getSnsuser().getChannel(), channel.getSnsuser().getOrgi()) ;
//			user.setSnsuser(snsuser) ;
//			channel.setSnsuser(snsuser) ;
//		}
//		
		StringBuffer strb = new StringBuffer() ;
		if(snsuser!=null){
			if(EapSmcDataContext.getSearchSetting(orgi).isSkill()){
				List<AgentSkill> skills = EapDataContext.getService().findAllByCriteria(DetachedCriteria.forClass(AgentSkill.class).add(Restrictions.eq("orgi", orgi)));
				strb.append("请选择接入的技能组：\r\n") ;
				for (int i = 0; i < skills.size(); i++) {
					AgentSkill as=skills.get(i);
					strb.append("[").append(as.getCode()).append("] ").append(as.getName()).append("\r\n");
				}
			}
		}
		return super.getChannelMessage(instruct, strb.length()!=0 ? strb.toString() : null, user, orgi, channel) ;
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
