package com.neusoft.util.process;

import java.util.Map;

import com.neusoft.core.channel.SNSUser;
import com.neusoft.web.model.IfaceInfo;

import freemarker.template.TemplateException;

public abstract class InnerProcess {
	
	public ProcessResult getResponse(IfaceInfo info , SNSUser snsUser , String orgi , Map<String , Object> paraMap) throws TemplateException, Exception {
		return !info.getRpctype().equals("innerclass")? ReqeustProcessUtil.getResponseBody(info, snsUser, orgi, paraMap) : null ;
	}
	
	public abstract ProcessResult getRequest(SNSUser snsUser , String orgi , Map<String , Object> paraMap) ;

	public abstract ProcessResult getRequest(IfaceInfo info, SNSUser snsUser, String orgi, Map<String, Object> paraMap);
}
