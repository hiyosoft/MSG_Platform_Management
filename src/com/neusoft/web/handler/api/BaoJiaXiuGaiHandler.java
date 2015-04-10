package com.neusoft.web.handler.api;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.neusoft.core.EapSmcDataContext;
import com.neusoft.util.process.CheXianProcess;
import com.neusoft.util.process.ProcessResult;
import com.neusoft.util.store.EapTools;
import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.ResponseData;
import com.neusoft.web.model.IfaceInfo;
import com.neusoft.web.model.SinosigUser;
import com.neusoft.web.model.UserTemplet;

@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}/api")
@SuppressWarnings("unchecked")
public class BaoJiaXiuGaiHandler extends Handler {
	
	@RequestMapping(value = "/info/{apiusername}")
	public ModelAndView add(HttpServletRequest request, @PathVariable String orgi , @PathVariable String apiusername ) {
		ResponseData responseData = null ;
		
		if("xml".equals(request.getParameter("wt"))){
			responseData = new ResponseData("/pages/manage/sinosig/api/user/info_xml");
		}else{
			responseData = new ResponseData("/pages/manage/sinosig/api/user/info_json");
		}
		ModelAndView view = request(responseData, orgi, null);
		view.addObject("data", super.getService().getIObjectByPK(SinosigUser.class, apiusername) ) ;
		return view;
	}
	@RequestMapping(value = "/changebaojia")
	public ModelAndView add(HttpServletRequest request,HttpServletResponse response, @PathVariable String orgi ) {
//		ResponseData responseData =responseData = new ResponseData("/pages/manage/sinosig/api/chexian/xiugaibaojia");
		System.out.println("*********************");
		IfaceInfo info=new IfaceInfo();
		info.setDescription("GBK");
		info.setRequesturl("http://10.10.163.33:7002/CooperateServlet");
		info.setTrantjson(true);
		CheXianProcess cp=new CheXianProcess();
		Map<String, Object> paraMap=new HashMap<String, Object>();
		UserTemplet userTemplet = EapSmcDataContext.getUserTemplet("8abf8cc141ba51590141bac943de0002", orgi) ;
		PrintWriter pw=null;
		try {
			String reqRequest = EapTools.getTemplet(userTemplet, paraMap) ;
			paraMap.put("innerinputparam", reqRequest);
			ProcessResult result =cp.getRequest(info, null, orgi, paraMap);
			System.out.println(result.getResultVal().get(info.getCode()));
			String tem=result.getResultVal().get(info.getCode()).toString();
			//tem=tem.substring(tem.indexOf("\"Header\"")+9,tem.indexOf(",\"Sign\""));
			
			response.setContentType("text/html; charset=utf-8"); 
			pw=response.getWriter();
			pw.write(tem);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			pw.close();
		}
//		responseData.setData(resultStr);
//		response.setContentType("application/json; charset=UTF-8"); 
		return null;
	}
	
}
