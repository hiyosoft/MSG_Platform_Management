package com.neusoft.core.plugin;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.web.model.Instruction;
import com.neusoft.web.model.SearchResultTemplet;

public class FourMinsTipPlugin implements InstructPluginInterface {
	/**
	 * 测试用，消息 拼出来的
	 * 
	 * @param user
	 * @param orgi
	 * @return
	 */
	public String getMessage(Instruction instruct, AgentUser user, String orgi , Channel channel) {
		StringBuffer strb = new StringBuffer();
		SearchResultTemplet srt = (SearchResultTemplet) (EapDataContext.getService().findAllByCriteria(
				DetachedCriteria.forClass(SearchResultTemplet.class).add(Restrictions.eq("code", getCode()))).get(0));
		strb.append(srt.getTemplettext());
		return strb.toString();
	}
	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return EapSmcDataContext.TemplateCodeEnum.FOURMINSTIP.toString();
	}
	@Override
	public void initVirInstruct(String orgi , Instruction instruct){
	}
	
}
