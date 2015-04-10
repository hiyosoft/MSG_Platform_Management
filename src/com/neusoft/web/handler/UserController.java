package com.neusoft.web.handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.util.queue.AgentStatus;
import com.neusoft.util.queue.ServiceQueue;
import com.neusoft.util.store.EapTools;
import com.neusoft.web.model.AgentInfo;
import com.neusoft.web.model.AgentSkill;
import com.neusoft.web.model.User;

@Controller
@SessionAttributes
public class UserController extends Handler{
	/**
	 * 
	 * @param request
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/{orgi}/login")
    public ModelAndView login(HttpServletRequest request ,HttpServletResponse response ,@PathVariable String orgi, @ModelAttribute("user") User data) {
		ModelAndView view = data != null && (!StringUtils.isEmpty(data.getEmail()) || !StringUtils.isEmpty(data.getUsername())) && !StringUtils.isEmpty(data.getPassword())  ? logindo(request, orgi, data) : request(super.getUser(request)==null ? "/login" : "redirect:/{orgi}/index.html") ;
		view.addObject("setting" , EapSmcDataContext.getSearchSetting(orgi)) ;
		return  view;
	}
	/**
	 * 
	 * @param request
	 * @param data
	 * @return
	 */
    public ModelAndView logindo(HttpServletRequest request ,String orgi, User data) {
    	AgentSkill agentSkill = null ;
    	if(EapSmcDataContext.getSearchSetting(orgi).isSkill() && (data.getAgentno()==null || data.getAgentno().length()==0)){
    		ResponseData res =	new ResponseData("login");
			res.setMessage("请输入分机号！");
			return request(res, orgi , null);
    	}else{
    		List<AgentInfo> agentSkillList = super.getService().findAllByCriteria(DetachedCriteria.forClass(AgentInfo.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) , Restrictions.eq("code", data.getAgentno()))).add(Restrictions.eq("username", data.getUsername())))  ;
    		if(agentSkillList.size()>0){
    			AgentInfo agent = agentSkillList.get(0) ;
    			agentSkill = (AgentSkill) super.getService().getIObjectByPK(AgentSkill.class, agent.getSkillid()) ;
    		}
    	}
    	if(data!=null && !(data.getUsername()==null || data.getUsername().length()==0 || data.getPassword()==null || data.getPassword().length()==0)){
    		String agentno = data.getAgentno() ;
			if(request.getSession().getAttribute(EapDataContext.USER_SESSION_NAME)!=null){
				request.getSession().removeAttribute(EapDataContext.USER_SESSION_NAME) ;
			}
			List<User> userList = new ArrayList<User>();
			if(data.getUsername().matches("[\\S\\s]*@[\\S\\s]*")){
				userList = super.getService().findAllByCriteria(DetachedCriteria.forClass(User.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) , Restrictions.eq("email", data.getUsername()))))  ;
			}else{
				userList = super.getService().findAllByCriteria(DetachedCriteria.forClass(User.class).add(Restrictions.and(Restrictions.eq("username", data.getUsername()),Restrictions.eq("orgi", orgi))))  ;
			}
			User user = null ;
			if(userList.size()>0){
				user = userList.get(0) ;
				if(!user.getPassword().equals(EapTools.md5(data.getPassword()))){
					ResponseData res =	new ResponseData("login");
					res.setMessage("密码错误，请重新输入！");
					return request(res, orgi , null);
				}else{
					data = user;
				}
			}else{
				ResponseData res =	new ResponseData("login");
				res.setMessage("用户名错误，请确认！");
				return request(res, orgi , null);
			}
			if(EapSmcDataContext.getSearchSetting(orgi).isSkill() && agentSkill==null && !"0".equals(user.getUsertype())){
				ResponseData res =	new ResponseData("login");
				res.setMessage("坐席号输入有误，请联系管理员分配坐席号");
				return request(res, orgi , null);
			}
			/**
	    	 * 
	    	 */
	    	data.setAgentno(agentno) ;
	    	data.setAgentSkill(agentSkill) ;
    	}
    	else{
    		ResponseData res =	new ResponseData("login");
    		res.setMessage("请输入用户名和密码！");
			return request(res, orgi , null);
    	}
    	try {
    		ServiceQueue.login(data.getOrgi(), data) ;
    	    ServiceQueue.statusChange(orgi, data.getAgentno() ,  AgentStatus.AgentStatusEnum.LEAVE.toString() , data) ;
		} catch (Exception e) {
			e.printStackTrace();
			ResponseData res =	new ResponseData("login");
			res.setMessage("该分机已经登陆，请使用其他分机！");
			return request(res, orgi , null);
		}
    	if(request.getSession().getAttribute(EapDataContext.USER_SESSION_NAME)==null){
    		request.getSession().removeAttribute(EapDataContext.USER_SESSION_NAME) ;
    	}
    	
    	request.getSession().setAttribute(EapDataContext.USER_SESSION_NAME, data) ;
    	//System.out.println("****************************************"+data.getUsername()+",分机号："+data.getAgentno()+"*****************登陆了");
    	return request(new ResponseData("redirect:/"+orgi+"/index.html"), orgi , null) ;
    }
    /**
	 * 
	 * @param request
	 * @param data
	 * @return
	 */
	@RequestMapping(value = "/{orgi}/logout")
    public ModelAndView logout(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("user") User data) {
		data = (User) request.getSession().getAttribute(EapDataContext.USER_SESSION_NAME) ;
		if(data!=null && data.getAgentstatus()!=null && data.getAgentstatus()!=null){
			try {
				ServiceQueue.logout(data.getOrgi(), data.getAgentstatus().getAgentno()) ;
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
		request.getSession().removeAttribute(EapDataContext.USER_SESSION_NAME) ;
		return request(new ResponseData("redirect:/"+orgi+"/login.html"), orgi , null) ;
	}
	
	@RequestMapping(value = "/{orgi}/userlist")
    public ModelAndView userlist(HttpServletRequest request ,   @PathVariable String orgi, @ModelAttribute("user") RequestData data) {
		String getKey=request.getParameter("key");
		String key = "%" + getKey + "%";
		List<User> dataList=null;
		if(getKey==null||getKey==""){
			dataList = super.getService().findPageByCriteria(DetachedCriteria.forClass(User.class).add(Restrictions.eq("orgi", orgi)) , super.PAGE_SIZE_TW , data.getP()) ;
		}else{
			dataList = super.getService().findPageByCriteria(DetachedCriteria.forClass(User.class).add(Restrictions.and(Restrictions.eq("orgi", orgi),Restrictions.like("username", key))) , super.PAGE_SIZE_TW , data.getP()) ;
			
		}
		ResponseData rsd=new ResponseData("/pages/manage/user" , dataList);
		if(getKey!=null && getKey!=""){
			rsd.setResult(getKey);
		}
		return request(rsd, orgi , data) ;
    }
	
	@RequestMapping(value = "/{orgi}/user/add")
    public ModelAndView add(HttpServletRequest request ,   @PathVariable String orgi, @ModelAttribute("user") RequestData data) {
		return request(new ResponseData("/pages/manage/system/useradd" ,  "/pages/include/iframeindex"), orgi , data) ;
    }
	
	@RequestMapping(value = "/{orgi}/user/edit/{id}")
    public ModelAndView edit(HttpServletRequest request ,   @PathVariable String orgi,  @PathVariable String id, @ModelAttribute("data") RequestData data) {
		ResponseData rspData = new ResponseData("/pages/manage/system/useredit" , "/pages/include/iframeindex" , null) ;
		rspData.setData( super.getService().getIObjectByPK(User.class, id)) ;
 		return request(rspData , orgi, data) ;
    }
	
	@RequestMapping(value = "/{orgi}/user/info/{id}")
    public ModelAndView info(HttpServletRequest request ,   @PathVariable String orgi,  @PathVariable String id,  @ModelAttribute("user") User data) {
		return request(new ResponseData("/pages/manage/system/userinfo"  ,  super.getService().getIObjectByPK(User.class, id)), orgi , null) ;
    }
	
	@RequestMapping(value = "/{orgi}/user/userinfoedit/{id}")
    public ModelAndView infoedit(HttpServletRequest request ,   @PathVariable String orgi,  @PathVariable String id,  @ModelAttribute("user") User data) {
		return request(new ResponseData("/pages/manage/system/userinfoedit"  , super.getService().getIObjectByPK(User.class, id)), orgi , null) ;
    }
	
	@RequestMapping(value = "/{orgi}/user/userinfoeditdo")
    public ModelAndView userinfoeditdo(HttpServletRequest request ,   @PathVariable String orgi, @ModelAttribute("user") User data) {
		System.out.println(data.getPassword());
		super.getService().updateIObject(data) ;
		return request(new ResponseData(new StringBuffer("redirect:/{orgi}/user/info/").append(data.getId()).append(".html").toString()), orgi , null) ;
    }
	
	@RequestMapping(value = "/{orgi}/user/changepasswd/{id}")
    public ModelAndView changepasswd(HttpServletRequest request ,   @PathVariable String orgi,  @PathVariable String id, @ModelAttribute("user") User data) {
		return request(new ResponseData("/pages/manage/system/changepasswd"  ,null, super.getService().getIObjectByPK(User.class, id)), orgi , null) ;
    }
	
	@RequestMapping(value = "/{orgi}/user/editdo")
    public ModelAndView editdo(HttpServletRequest request ,   @PathVariable String orgi, @ModelAttribute("user") User data) {
//		MultipartFile image=getFile(request);
//		if(image!=null&&image.getSize()>0){
//			String imgname=new Date().getTime()+"_"+image.getOriginalFilename();
//			String path=request.getSession().getServletContext().getRealPath(File.separator)+File.separator+"img"+File.separator+"userImg"+File.separator+imgname;
//	        	try {
//					if(image!=null&&image.getSize()>0){
//						deletepic(request.getSession().getServletContext().getRealPath(File.separator)+data.getUrlimg());
//			        	FileCopyUtils.copy(image.getBytes(),new File(path));
//			        	String picurl=File.separator+"img"+File.separator+"userImg"+File.separator+imgname;
//			 	        data.setUrlimg(picurl);
//		        	}
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} 
//		}else{
//			if(data.getUrlimg()!=null){
//		 	        data.setUrlimg(data.getUrlimg());
//	        	}
//			
//        }
		List<User> dataList = super.getService().findAllByCriteria(DetachedCriteria.forClass(User.class).add(Restrictions.and(Restrictions.or(Restrictions.eq("username", data.getUsername()), Restrictions.eq("email", data.getEmail())), Restrictions.eq("orgi", orgi)))) ;
		List<User> emailList = super.getService().findAllByCriteria(DetachedCriteria.forClass(User.class).add(Restrictions.eq("email", data.getEmail())).add(Restrictions.eq("orgi", orgi)).add(Restrictions.ne("id", data.getId()))) ;
		List<User> userList = super.getService().findAllByCriteria(DetachedCriteria.forClass(User.class).add(Restrictions.eq("username", data.getUsername())).add(Restrictions.eq("orgi", orgi)).add(Restrictions.ne("id", data.getId()))) ;
		if(emailList!=null && emailList.size()>0){
			return request(new ResponseData("redirect:/{orgi}/userlist.html" , "邮件地址已存在" , true , null), orgi, null) ;
		}else if(userList !=null && userList.size()>0){
			return request(new ResponseData("redirect:/{orgi}/userlist.html" , "用户名已存在" , true , null), orgi, null) ;
		}else{
			User temp = (User) super.getService().getIObjectByPK(User.class, data.getId()) ;
			data.setOrgi(orgi);
			if(data.getPassword().trim().equals(temp.getPassword())){
				data.setPassword(temp.getPassword());
			}else{
				data.setPassword(EapTools.md5(data.getPassword()));
			}
				super.getService().updateIObject(data) ;
		}
		return request(new ResponseData("redirect:/{orgi}/userlist.html"), orgi , null) ;
    }
	
	@RequestMapping(value = "/{orgi}/user/changepasswddo")
    public ModelAndView changepasswddo(HttpServletRequest request ,   @PathVariable String orgi, @ModelAttribute("user") User data) {
		User user = (User) request.getSession().getAttribute(EapDataContext.USER_SESSION_NAME);
		data.setOrgi(orgi);
		user.setPassword(EapTools.md5(data.getNewpwd()));
		super.getService().updateIObject(user) ;
		return request(new ResponseData("/pages/public/success" , "修改成功！",true,null), orgi , null) ;
    }
	
	@RequestMapping(value = "/{orgi}/user/checkPassword/{passwd}")
    public ModelAndView checkPassword(HttpServletRequest request ,   @PathVariable String orgi, @PathVariable String passwd, @ModelAttribute("user") User data) {
		ResponseData res = new ResponseData("/pages/public/message");
		User user =(User)request.getSession().getAttribute(EapDataContext.USER_SESSION_NAME);
		if(!user.getPassword().equals(EapTools.md5(passwd))){
			res.setMessage("密码错误，请重新输入！");
		}
		return request(res, orgi , null);
    }

	@RequestMapping(value = "/{orgi}/user/adddo")
    public ModelAndView adddo(HttpServletRequest request ,   @PathVariable String orgi, @ModelAttribute("user") User data) {
//		MultipartFile image=getFile(request);
//		if(image!=null&&image.getSize()>0){
//			System.out.println("********jinru image********");
//			String imgname=new Date().getTime()+"_"+image.getOriginalFilename();
//			String path=request.getSession().getServletContext().getRealPath(File.separator)+File.separator+"img"+File.separator+"userImg"+File.separator+imgname;
//	        try {
//				FileCopyUtils.copy(image.getBytes(),new File(path));
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
//	        String picurl=File.separator+"img"+File.separator+"userImg"+File.separator+imgname;
//	        data.setUrlimg(picurl);
//		}
		
		List<User> dataList = super.getService().findAllByCriteria(DetachedCriteria.forClass(User.class).add(Restrictions.and(Restrictions.or(Restrictions.eq("username", data.getUsername()), Restrictions.eq("email", data.getEmail())), Restrictions.eq("orgi", orgi)))) ;
		List<User> emailList = super.getService().findAllByCriteria(DetachedCriteria.forClass(User.class).add(Restrictions.eq("email", data.getEmail())).add(Restrictions.eq("orgi", orgi))) ;
		List<User> userList = super.getService().findAllByCriteria(DetachedCriteria.forClass(User.class).add(Restrictions.eq("username", data.getUsername())).add(Restrictions.eq("orgi", orgi))) ;
		if(emailList!=null && emailList.size()>0){
			return request(new ResponseData("redirect:/{orgi}/userlist.html" , "邮件地址已存在" , true , null), orgi, null) ;
		}
		if(userList !=null && userList.size()>0){
			return request(new ResponseData("redirect:/{orgi}/userlist.html" , "用户名已存在" , true , null), orgi, null) ;
		}
		if(dataList.size()==0){
			data.setOrgi(orgi) ;
			data.setPassword(EapTools.md5(data.getPassword()));
			super.getService().saveIObject(data);
			return request(new ResponseData("redirect:/{orgi}/userlist.html"), orgi , null) ;
		}
		return null;
    }
	
	@RequestMapping(value = "/{orgi}/user/rm/{id}")
    public ModelAndView rm(HttpServletRequest request ,   @PathVariable String orgi, @PathVariable String id, @ModelAttribute("user") User data) {
		String userid = id ;
		if(id.equalsIgnoreCase("402880e52835b654012835b6ab720001")){
			return request(new ResponseData("redirect:/{orgi}/userlist.html","不支持此操作：系统账户，禁止删除",true,null) , orgi , null) ;
		}else{
			 if(ServiceQueue.getAgentQueue().size()>0){
				 Iterator iter = ServiceQueue.getAgentQueue().keySet().iterator();   
				//获得map的Iterator
				while(iter.hasNext()) {
					 String key = iter.next().toString();   
					          if(ServiceQueue.getAgentQueue().get(key).getUser().getId().equals(id)){
					        	 userid= "" ;  
					          }   
					}
			 }
			 if(userid!=null && !userid.equals("")){
				data.setId(id) ;
				super.getService().deleteIObject(data) ;
			 }else{ 
	        	 return request(new ResponseData("redirect:/{orgi}/userlist.html","用户正在使用中,禁止删除",true,null) , orgi , null) ;
			 }
		}
		return request(new ResponseData("redirect:/{orgi}/userlist.html"), orgi , null) ;
    }
	@RequestMapping(value = "/{orgi}/user/search")
	public ModelAndView search(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") User data) {
		String key = "%" + data.getUsername() + "%";
		ResponseData responseData = new ResponseData("/pages/manage/user");
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(User.class).add(Restrictions.and(Restrictions.eq("orgi", orgi),Restrictions.like("username", key)))));
		responseData.setResult(data.getUsername());
		return request(responseData, orgi, null);
	}
	
	
	//****用户头像
	@Autowired
    CommonsMultipartResolver multipartResolver;
	private MultipartFile getFile(HttpServletRequest request){
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;   
		MultipartFile mfile=null;
		if (multipartResolver.isMultipart(multipartRequest)){  //判断 request 是否有文件上传,即多部分请求...  
             // srcfname 是指 文件上传标签的 name=值  
             MultiValueMap<String, MultipartFile> multfiles = multipartRequest.getMultiFileMap();
             for(String srcfname:multfiles.keySet()){
            	 mfile=  multfiles.getFirst(srcfname);
             }
		}
		return mfile;
	}
	
	/** 
	 * 删除单个文件 
	 * @param   sPath    被删除文件的文件名 
	 * @return 单个文件删除成功返回true，否则返回false 
	 */  
	private void deletepic(String sPath) {  
	    File file = new File(sPath);
	    // 路径为文件且不为空则进行删除  
	    if (file.exists()) {  
	        file.delete();  
	    }  
	} 
	//****************
	
}
