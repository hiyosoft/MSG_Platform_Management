package com.neusoft.web.handler.manage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.api.APIContext;
import com.neusoft.util.rpc.client.MessageSender;
import com.neusoft.util.rpc.message.Message;
import com.neusoft.util.rpc.message.SystemMessage;
import com.neusoft.util.store.EapTools;
import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.RequestData;
import com.neusoft.web.handler.ResponseData;
import com.neusoft.web.model.Instruction;
import com.neusoft.web.model.Material;
import com.neusoft.web.model.MaterialItem;
import com.neusoft.web.model.SearchSetting;
import com.neusoft.web.model.UserTemplet;

@Controller
@SessionAttributes
@RequestMapping(value ="/{orgi}")
@SuppressWarnings("unchecked")
public class MaterialHandler extends Handler {
	
	@RequestMapping(value = "/materialitem/add/{materialid}")
	public ModelAndView add(HttpServletRequest request, @PathVariable String orgi,@PathVariable String materialid) {
		ResponseData responseData = new ResponseData("/pages/manage/material/materialitemadd");
		ModelAndView view = request(responseData, orgi, null);
		view.addObject("materialid",materialid);
		return view;
	}
	@RequestMapping(value = "/material/add")
	public ModelAndView materialadd(HttpServletRequest request, @PathVariable String orgi) {
		ResponseData responseData = new ResponseData("/pages/manage/material/materialadd", "/pages/include/iframeindex");
		ModelAndView view = request(responseData, orgi, null);
		return view;
	}
	@RequestMapping(value = "/api/saveimg")
	public void saveimg(HttpServletRequest request ,HttpServletResponse response , @PathVariable String orgi) throws IOException {
		InputStream in = request.getInputStream();
		PrintWriter out=response.getWriter();
		StringBuffer strb = new StringBuffer() ;
		//读取图片信息
		BufferedReader input = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		String tempString ="";
		 // 一次读入一行，直到读入null为文件结束
		while ((tempString = input.readLine()) != null) 
		{
			strb.append(tempString).append("\n");
		}
		
		Message message = new Message(EapDataContext.HANDLER,strb.toString()) ;
		MaterialItem item=JSON.parseObject(message.getMessage(), MaterialItem.class) ;
		if(item.getImage()!=null && item.getImage().length>0){
			FileCopyUtils.copy(item.getImage(),new File(request.getSession().getServletContext().getRealPath(File.separator)+File.separator+"img"+File.separator+"material"+File.separator+item.getPicurl()));
		}
		if(item.getHispicurl()!=null){
			deletepic(item.getHispicurl());
		}
		out.print("==saveimg=");
		//关闭IO
		out.close();
		in.close();
	}
	@RequestMapping(value = "/material/adddo")
	public ModelAndView materialadddo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") Material data) {
		MultipartFile image=getFile(request);
		if(image!=null&&image.getSize()>0){
			String imgname= UUID.randomUUID().toString()+ image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
			String path=request.getSession().getServletContext().getRealPath(File.separator)+File.separator+"img"+File.separator+"material"+File.separator+imgname;
	        try {
				FileCopyUtils.copy(image.getBytes(),new File(path));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}        
	        String picurl=File.separator+"img"+File.separator+"material"+File.separator+imgname;
	        data.setPicurl(picurl);
		}
		super.getService().saveIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/material/edit/{materialid}")
	public ModelAndView materialedit(HttpServletRequest request, @PathVariable String orgi, @PathVariable String materialid) {
		ResponseData responseData = new ResponseData("/pages/manage/material/materialedit", "/pages/include/iframeindex");
		responseData.setData(super.getService().getIObjectByPK(Material.class, materialid));
		ModelAndView view = request(responseData, orgi, null);
		return view;
	}
	@RequestMapping(value = "/material/editdo")
	public ModelAndView materialeditdo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") Material data) {
		MultipartFile image=getFile(request);
		if(image!=null&&image.getSize()>0){
			String imgname = UUID.randomUUID().toString()+ image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
			String path=request.getSession().getServletContext().getRealPath(File.separator)+File.separator+"img"+File.separator+"material"+File.separator+imgname;
	        String imghisname=data.getPicurl().substring(data.getPicurl().lastIndexOf(File.separator)+1);
			try {
				if(image!=null&&image.getSize()>0&&!imghisname.equals(imgname)){
					deletepic(request.getSession().getServletContext().getRealPath(File.separator)+data.getPicurl());
		        	FileCopyUtils.copy(image.getBytes(),new File(path));
		        	String picurl=File.separator+"img"+File.separator+"material"+File.separator+imgname;
		 	        data.setPicurl(picurl);
	        	}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		super.getService().updateIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	@RequestMapping(value = "/material/rm/{materialid}")
	public ModelAndView materialrm(HttpServletRequest request, @PathVariable String orgi, @PathVariable String materialid, @ModelAttribute("data") Material data) {
		Material material=(Material) super.getService().getIObjectByPK(Material.class, materialid);
		ResponseData responseData = new ResponseData("/pages/manage/material");
		List<Instruction> ins=super.getService().findAllByCriteria(DetachedCriteria.forClass(Instruction.class).add(Restrictions.and(Restrictions.eq("orgi", orgi),Restrictions.eq("memo", materialid))));
		boolean ismaterial=false;
		if(material!=null&&  ins!=null && ins.size()>0){
			responseData.setMessage("请先删除IMR中"+ins.get(0).getName()+"的引用");
			responseData.setResult(material.getId());
			List<MaterialItem> items=material.getmItems();
			if(items!=null&&items.size()>0){
				responseData.setData(items.get(0));
				responseData.setValueList(items);
			}
			ismaterial=true;
		}else{
			responseData.setMessage("删除成功");
			data.setId(materialid);
			String sPath=request.getSession().getServletContext().getRealPath(File.separator)+request.getParameter("picurl");
			deletepic(sPath);
			super.getService().deleteIObject(data);
		}
		responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(Material.class).add(Restrictions.eq("orgi", orgi)).addOrder(Order.asc("createtime"))));
		ModelAndView view = request(responseData, orgi, null);
		if(ismaterial){
			view.addObject("mainmaterial", material);
		}
		return view;
	}
	@RequestMapping(value = "/material/tablelist")
	public ModelAndView materialtablelist(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/material");
		List<Material> materials= super.getService().findAllByCriteria(DetachedCriteria.forClass(Material.class).add(Restrictions.eq("orgi", orgi)));
		Material material=null;
		if(materials!=null&&materials.size()>0){
			material=materials.get(0);
			responseData.setResult(material.getId());
			List<MaterialItem> items=material.getmItems();
			if(items!=null&&items.size()>0){
				responseData.setData(items.get(0));
				responseData.setValueList(items);
			}
		}
		responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(Material.class).add(Restrictions.eq("orgi", orgi)).addOrder(Order.asc("createtime"))));
		ModelAndView view = request(responseData, orgi, null);
		view.addObject("mainmaterial", material);
		return view;
	}
	@RequestMapping(value = "/materialitem/edit/{materialid}/{materialitemid}")
	public ModelAndView edit(HttpServletRequest request, @PathVariable String orgi,@PathVariable String materialid,@PathVariable String materialitemid) {
		Material material=(Material) super.getService().getIObjectByPK(Material.class,materialid);
		List<MaterialItem> items=material.getmItems();
		ResponseData responseData = new ResponseData("/pages/manage/material/materialiteminfo");
		MaterialItem mitem=items.get(Integer.parseInt(materialitemid));
		mitem.setId(materialitemid);
		responseData.setData(mitem);
		ModelAndView view = request(responseData, orgi, null);
		view.addObject("materialid",materialid);
		return view;
	}
	@Autowired
    CommonsMultipartResolver multipartResolver;
	@RequestMapping(value="/materialitem/adddo",method=RequestMethod.POST)
	public ModelAndView adddo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") MaterialItem data) {
		MultipartFile image=getFile(request);
		if(image!=null&&image.getSize()>0){
			String imgname= UUID.randomUUID().toString()+ image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
			String path=request.getSession().getServletContext().getRealPath(File.separator)+File.separator+"img"+File.separator+"material"+File.separator+imgname;
	        try {
				FileCopyUtils.copy(image.getBytes(),new File(path));
				//如果设置了集群的SMC地址，则同步发布
				SearchSetting setting = EapSmcDataContext.getSearchSetting(orgi);
		        String posturl=null;
		        String localurl=request.getRequestURL().toString();
		        localurl=localurl.substring(localurl.indexOf("://")+3,localurl.lastIndexOf(":"));
		        if(setting!=null){
		        	//说明编辑的SMC不是该配置的SMC，需同步的地址
		        	if(setting.getBindmsgtype()!=null && setting.getBindmsgtype().indexOf(localurl)<0){
		        		posturl=setting.getBindmsgtype();
		        	}
		        	if(setting.getBindmsg()!=null && setting.getBindmsg().indexOf(localurl)<0){
		        		posturl=setting.getBindmsg();
		        	}
		        	syncImage(orgi,image.getBytes(),imgname,posturl);
		        }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	        String picurl=File.separator+"img"+File.separator+"material"+File.separator+imgname;
	        data.setPicurl(picurl);
	        data.setHispicurl(picurl);
		}
		Material material=(Material) super.getService().getIObjectByPK(Material.class,data.getMaterialid());
		List<MaterialItem> items=material.getmItems();
		data.setId(Integer.toString(items.size()));
		items.add(data);
		material.setDescription(JSONArray.toJSONString(items));
		super.getService().updateIObject(material);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
	}
	public void syncImage(final String orgi,final byte [] bytes,final String imgname,final String posturl){
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
	        	MaterialItem item=new MaterialItem();
	        	item.setPicurl(imgname);
	        	item.setImage(bytes);
	        	Message msg=new Message(EapDataContext.HANDLER,JSON.toJSONString(item , SerializerFeature.WriteClassName));
	        	System.out.println(EapTools.postString(posturl + "/"+orgi + "/api/saveimg.html", msg.getMessage(), "UTF-8"));
			}
		}).start();
	}
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
	@RequestMapping(value = "/materialitem/editdo")
	public ModelAndView editdo(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") MaterialItem data) {
		MultipartFile image=getFile(request);
		Material material=(Material) super.getService().getIObjectByPK(Material.class,data.getMaterialid());
		//移除数据和原来图片
		List<MaterialItem> items=material.getmItems();
//		items.remove(Integer.parseInt(data.getId()));
		//deletepic(path);
		//重新添加picurl和data
		if(image!=null&&image.getSize()>0){
			data.setHispicurl(data.getPicurl());
			String imgname= UUID.randomUUID().toString()+ image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf("."));
			System.out.println(File.separator+"img"+File.separator+"material"+File.separator+imgname);
			data.setPicurl(File.separator+"img"+File.separator+"material"+File.separator+imgname);
			String path=request.getSession().getServletContext().getRealPath(File.separator)+"/img"+File.separator+"material"+File.separator+imgname;
			System.out.println(request.getSession().getServletContext().getRealPath(File.separator));
			System.out.println(path);
			try {
				if(data.getHispicurl()!=null&&data.getPicurl()!=null&&!"".equals(data.getPicurl())&&!data.getPicurl().equals(data.getHispicurl())){
					String sPath=request.getSession().getServletContext().getRealPath(File.separator)+data.getHispicurl();
					deletepic(sPath);
					FileCopyUtils.copy(image.getBytes(),new File(path));
					//如果设置了集群的SMC地址，则同步发布
					SearchSetting setting = EapSmcDataContext.getSearchSetting(orgi);
					String posturl=null;
					String localurl=request.getRequestURL().toString();
			        localurl=localurl.substring(localurl.indexOf("://")+3,localurl.lastIndexOf(":"));
			        if(setting!=null){
			        	//说明编辑的SMC不是该配置的SMC，需同步的地址
			        	if(setting.getBindmsgtype()!=null && setting.getBindmsgtype().indexOf(localurl)<0){
			        		posturl=setting.getBindmsgtype();
			        	}
			        	if(setting.getBindmsg()!=null && setting.getBindmsg().indexOf(localurl)<0){
			        		posturl=setting.getBindmsg();
			        	}
			        	syncImage(orgi,image.getBytes(),imgname,posturl);
			        }
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}else{
			if(data.getPicurl()==null || data.getPicurl().trim().length() ==0){
				data.setPicurl(null) ;
				data.setHispicurl(null) ;
			}
		}
		items.set(Integer.parseInt(data.getId()), data);
		material.setDescription(JSONArray.toJSONString(items));
		super.getService().updateIObject(material);
		ResponseData responseData = new ResponseData("/pages/public/success");
		return request(responseData, orgi, null);
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
	@RequestMapping(value = "/materialitem/rm/{materialid}/{materialitemid}")
	public ModelAndView rm(HttpServletRequest request, @PathVariable String orgi,@PathVariable String materialid,@PathVariable String materialitemid) {
		Material material=(Material) super.getService().getIObjectByPK(Material.class,materialid);
		List<MaterialItem> items=material.getmItems();
		String sPath=request.getSession().getServletContext().getRealPath(File.separator)+items.get(Integer.parseInt(materialitemid)).getPicurl();
		deletepic(sPath);
		items.remove(Integer.parseInt(materialitemid));
		material.setDescription(JSONArray.toJSONString(items));
		super.getService().updateIObject(material);
		ResponseData responseData = new ResponseData("/pages/manage/material/materiallist");
		responseData.setResult(material.getId());
		List<MaterialItem> resitems=material.getmItems();
		if(items!=null&&items.size()>0){
			responseData.setData(resitems.get(0));
			responseData.setValueList(resitems);
		}
		/*//如果设置了集群的SMC地址，则同步发布
		SearchSetting setting = SmcRivuDataContext.getSearchSetting(orgi);
	        if(setting!=null && setting.getRegurl()!=null && setting.getRegurl().trim().length()>0){
        	syncImage(orgi,image.getBytes(),,setting.getRegurl());
        }*/
		ModelAndView view = request(responseData, orgi, null);
		view.addObject("mainmaterial", material);
		return view;
	}
	@RequestMapping(value = "/material/tablelist/{materialid}")
	public ModelAndView list(HttpServletRequest request, @PathVariable String orgi,@PathVariable String materialid, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/material/materiallist");
		responseData.setResult(materialid);
		Material material=(Material) super.getService().getIObjectByPK(Material.class,materialid);
		List<MaterialItem> items=material.getmItems();
		if(items!=null&&items.size()>0){
			responseData.setData(items.get(0));
			responseData.setValueList(items);
		}
		ModelAndView view = request(responseData, orgi, data);
		view.addObject("mainmaterial", material);
		return view;
	}
	
	@RequestMapping(value = "/material/publish/{materialid}")
	public ModelAndView publish(HttpServletRequest request, @PathVariable String orgi,@PathVariable String materialid, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("redirect:/"+orgi+"/material/tablelist/"+materialid+".html");
		Material material = (Material) super.getService().getIObjectByPK(Material.class,materialid);
		UserTemplet templet = EapSmcDataContext.getUserTempletByChannel(EapDataContext.getDefaultSiteTemplet(orgi),orgi,"SITE_NEWS") ;
		material.setTempMaterialItems(material.getmItems()) ;
		for(MaterialItem item: material.getTempMaterialItems()){
			try {
				Map<String , Object> values = new HashMap<String , Object>() ;
				values.put("item", item) ;
				values.put("orgi", material.getOrgi()) ;

				item.setHtml(EapTools.getTemplet(templet, values).getBytes("UTF-8")) ;
				System.out.println("==material public == getImageID:"+item.getImageID());
				if(item.getPicurl()!=null && new File(request.getSession().getServletContext().getRealPath(File.separator)+item.getPicurl()).exists()){
					item.setImage(
							FileUtils.readFileToByteArray(
									new File(request.getSession().getServletContext().getRealPath(File.separator),item.getPicurl()
											)
									)
							);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(APIContext.getRpcServers().size()>0){
			new MessageSender(null, null).send(
				new Message(EapDataContext.HANDLER, JSON.toJSONString(new SystemMessage(EapDataContext.SystemRPComman.MATERIALPUBLISH.toString(), material), SerializerFeature.WriteClassName)));
		
		}
		return request(responseData, orgi, data);
	}
}
