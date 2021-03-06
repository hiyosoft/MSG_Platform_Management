package com.neusoft.core.plugin;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.util.tools.IndexTools;
import com.neusoft.web.model.Instruction;

public class TipNotFoundInstructPlugin extends Plugin{
	/**
	 * 测试用，消息 拼出来的
	 * @param user
	 * @param orgi
	 * @return
	 */
	public String getMessage(Instruction instruct ,AgentUser user , String orgi , Channel channel){
		List<Instruction> insList = EapDataContext.getInstructList(orgi) ;
		StringBuffer strb = new StringBuffer();
		try {
			List<Document>  docList = IndexTools.getInstance().search("faq",channel.getText(),orgi,10 , true) ;
			for(int i=0 ;i<docList.size() && i<1 ; i++){
				if(strb.length()>0){
					strb.append("\r\n") ;
				}
				strb.append(docList.get(i).get("text")) ;
			}
			if(strb.length() == 0 ){
				strb.append(EapSmcDataContext.getSearchSetting(orgi).getNotfoundmsg()) ;
				for(Instruction ins : insList){
					if("0".equals(ins.getParent())){
						if(EapDataContext.InstructionType.SYSTEM.toString().equals(ins.getType()) && ins.isTipdefault()){
							if(strb.length()>0){
								strb.append("\r\n") ;
							}
							strb.append("-回复 [").append(ins.getCode()).append("]").append(ins.getName());
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.getChannelMessage(instruct, strb.toString(), user, orgi, channel) ;
	}
	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return EapSmcDataContext.TemplateCodeEnum.TIPNOTFOUNDINSTRUCT.toString();
	}
	@Override
	public void initVirInstruct(String orgi , Instruction instruct){
		// TODO Auto-generated method stub
		
	}
}
