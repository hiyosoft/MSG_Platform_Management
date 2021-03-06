package com.neusoft.web.handler.manage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.lionsoul.jcseg.core.ILexicon;
import org.lionsoul.jcseg.core.IWord;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.neusoft.util.tools.IndexTools;
import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.RequestData;
import com.neusoft.web.handler.ResponseData;
import com.neusoft.web.model.Keyword;
import com.neusoft.web.model.KeywordCategory;

@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}")
public class KeywordHandler  extends Handler{

	@RequestMapping(value = "/keyword/add/{cateid}")
    public ModelAndView keywordadd(HttpServletRequest request ,@PathVariable String orgi,@PathVariable String cateid, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/keyword/add" ,"/pages/include/iframeindex" ) ; 
		ModelAndView view = request(responseData, orgi , data);
		view.addObject("cateid", cateid);
		return  view;
    }
	@RequestMapping(value = "/keywordcategory/add")
    public ModelAndView keywordcategoryadd(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/keyword/addcategory" ,"/pages/include/iframeindex") ; 
		return request(responseData, orgi , data) ;
    }
	@RequestMapping(value = "/keyword/catelist")
    public ModelAndView catelist(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/keyword/categorylist"  ) ;
		responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(KeywordCategory.class).add(Restrictions.eq("orgi", orgi))));
		return request(responseData, orgi , data) ; 
    }
	@RequestMapping(value = "/keyword/changetype/{cateid}")
    public ModelAndView changetype(HttpServletRequest request ,@PathVariable String orgi,@PathVariable String cateid, @ModelAttribute("data") KeywordCategory data ,@ModelAttribute("rqdata")RequestData rqdata) {
		ResponseData responseData = new ResponseData(cateid,"/pages/manage/keyword/tablelist"  ) ;
		responseData.setResult(cateid);
		if(rqdata.getPs()==20){
			rqdata.setPs(100);
		}
		responseData.setRqdata(rqdata);
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(Keyword.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.eq("category", cateid))),rqdata.getPs(),rqdata.getP()));
//		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(Keyword.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.eq("category", cateid))), (( rqdata.getP()-1)*rqdata.getPs()), rqdata.getPs()));
		return request(responseData, orgi , rqdata) ; 
    }
	
	@RequestMapping(value = "/keywordcategory/adddo")
    public ModelAndView keywordcategoryadddo(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") KeywordCategory data) {
		data.setCreatetime(new Date());
		data.setOrgi(orgi);
		super.getService().saveIObject(data);
		ResponseData responseData = new ResponseData("/pages/manage/keyword/tablelist"  ) ;
		responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(KeywordCategory.class).add(Restrictions.eq("orgi", orgi))));
		return request(responseData, orgi , null) ; 
		 
    }
	
	@RequestMapping(value = "/keywordcategory/edit/{keywordcateid}")
    public ModelAndView keywordcategoryedit(HttpServletRequest request ,@PathVariable String orgi, @PathVariable String keywordcateid, @ModelAttribute("data") RequestData data) {
		data.setId(keywordcateid);
		ResponseData responseData = new ResponseData("/pages/manage/keyword/editcategory","/pages/include/iframeindex");
		responseData.setData(super.getService().getIObjectByPK(KeywordCategory.class, keywordcateid));
		return request(responseData, orgi , data) ; 
    }
	@RequestMapping(value = "/keywordcategory/edit/editdo")
    public ModelAndView keywordcategoryeditdo(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") KeywordCategory data) {
		super.getService().updateIObject(data);
		ResponseData responseData = new ResponseData("/pages/manage/keyword/tablelist"  ) ;
		responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(KeywordCategory.class).add(Restrictions.eq("orgi", orgi))));
		return request(responseData, orgi , null) ; 
    }
	
	@RequestMapping(value = "/keywordcate/rm/{keywordcateid}")
    public ModelAndView keywordcateidrm(HttpServletRequest request ,@PathVariable String orgi, @PathVariable String keywordcateid, @ModelAttribute("data") KeywordCategory data,@ModelAttribute("commdata") RequestData commdata) {
		data.setId(keywordcateid);
		ResponseData responseData = new ResponseData("/pages/manage/keyword/categorylist");
		if(super.getService().findAllByCriteria(DetachedCriteria.forClass(Keyword.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.eq("category", keywordcateid)))).size()>0){
			responseData.setMessage("请先删除关键字后再删除分类！");
			responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(KeywordCategory.class).add(Restrictions.eq("orgi", orgi))));
		}else{
			super.getService().deleteIObject(data) ;
			responseData.setMessage("删除成功");
			responseData.setResult(keywordcateid);
			responseData.setValueList(super.getService().findAllByCriteria(DetachedCriteria.forClass(KeywordCategory.class).add(Restrictions.eq("orgi", orgi))));
		}
		return request(responseData, orgi , commdata) ; 
    }
	
	@RequestMapping(value = "/keyword/adddo")
    public ModelAndView keywordadddo(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") Keyword data) {
		ResponseData responseData = new ResponseData("/pages/public/success"  ) ;
		String [] keys = data.getKeyword().split(";|；");
	//	int successcount=0,errcount =0;
		StringBuffer strMessage = new StringBuffer();
		List<String> list = new ArrayList<String>();
		if(keys.length>0){
			for(String key :keys){
				if(super.getService().getCountByCriteria(DetachedCriteria.forClass(Keyword.class).add(Restrictions.eq("keyword", key)))>0){
					strMessage.append(" ").append(key+",");
//					return request(new ResponseData("redirect:/{orgi}/keyword/tablelist/"+data.getCategory()+".html","关键字"+key,true,null) , orgi , null) ; 
				}else{
					list.add(key);			
				}
			}
			if(strMessage.length()>0){
				return request(new ResponseData("redirect:/{orgi}/keyword/tablelist/"+data.getCategory()+".html","关键字"+strMessage.substring(0, strMessage.length()-1)+" 重复,添加失败",true,null) , orgi , null) ; 
			}else{
				if(list.size()>0){
					for(int i=0;i<list.size();i++){
						if(list.get(i).trim().length()>0){
							data.setKeyword(list.get(i));
							data.setCreatetime(new Date());
							data.setOrgi(orgi);
							data.setCategory(data.getCategory());
							super.getService().saveIObject(data);
							IndexTools.getInstance().getDic().add(ILexicon.CJK_WORD, data.getKeyword(), IWord.T_CJK_WORD) ;
						}
					}
				}
			}
		}
		responseData.setResult(data.getCategory());
	//	responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(Keyword.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.eq("category", data.getCategory())))));
		return request(responseData, orgi ,null) ;
	
    }
	@RequestMapping(value = "/keyword/eidt/{keywordid}")
    public ModelAndView keywordeidt(HttpServletRequest request ,@PathVariable String orgi, @PathVariable String keywordid, @ModelAttribute("data") RequestData data) {
		data.setId(keywordid);
		ResponseData responseData = new ResponseData("/pages/manage/keyword/eidt");
		responseData.setData(super.getService().findAllByCriteria(DetachedCriteria.forClass(Keyword.class).add(Restrictions.eq("id", keywordid))));
		return request(responseData, orgi , data) ; 
    }
	@RequestMapping(value = "/keyword/editdo")
    public ModelAndView editdo(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") Keyword data) {
		super.getService().updateIObject(data);
		ResponseData responseData = new ResponseData("/pages/public/success"  ) ;
		responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(Keyword.class).add(Restrictions.eq("orgi", orgi))));
		return request(responseData, orgi , null) ; 
    }
	
	@RequestMapping(value = "/keyword/tablelist/{cateid}")
    public ModelAndView tablelist(HttpServletRequest request ,@PathVariable String orgi,@PathVariable String cateid, @ModelAttribute("rqdata") Keyword data,@ModelAttribute("data") RequestData rqdata) {
		ResponseData responseData = new ResponseData("/pages/manage/keyword/tablelist"  ) ;
		rqdata.setPs(PAGE_SIZE_HA);
		responseData.setRqdata(rqdata);
		responseData.setResult(cateid);
		String search=request.getParameter("keyword");
		search=search==null?"":search;
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(Keyword.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.and(Restrictions.eq("category", cateid), Restrictions.like("keyword", "%"+search+"%")))),rqdata.getPs(),rqdata.getP()));
		return request(responseData, orgi , rqdata) ; 
    }
	
	@RequestMapping(value = "/keyword/rm/{cateid}/{keywordid}")
    public ModelAndView keywordrm(HttpServletRequest request ,@PathVariable String orgi, @PathVariable String cateid, @PathVariable String keywordid, @ModelAttribute("data") Keyword data,@ModelAttribute("rqdata") RequestData rqdata) {
		data.setId(keywordid);
		super.getService().deleteIObject(data) ;
		IndexTools.getInstance().getDic().remove(IWord.T_CJK_WORD, data.getKeyword()) ;
		ResponseData responseData = new ResponseData("/pages/manage/keyword/tablelist");
		responseData.setResult(cateid);
		rqdata.setPs(PAGE_SIZE_HA);
		responseData.setRqdata(rqdata);
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(Keyword.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.eq("category", cateid)),rqdata.getPs(),rqdata.getP()));
		return request(responseData, orgi , rqdata) ; 
    }
	
	@RequestMapping(value = "/keyword/search")
    public ModelAndView search(HttpServletRequest request ,@PathVariable String orgi, @ModelAttribute("data") Keyword data,@ModelAttribute("rqdata") RequestData rqdata) {
		String shpath=System.getProperty("user.dir")+"/bin/ffmpeg/ffmpeg -i ";  
        System.out.println("=============shpath==============="+shpath);
		String key="%"+data.getKeyword()+"%";
		ResponseData responseData = new ResponseData("/pages/manage/keyword/tablelist");
		rqdata.setPs(PAGE_SIZE_HA);
		String cataid = request.getParameter("cataid");
		responseData.setResult(cataid);
		if(cataid!=null && !"".equals(cataid)){
			responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(Keyword.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.like("keyword", key))).add(Restrictions.eq("category", cataid)),rqdata.getPs(),rqdata.getP()));
		}else{
			responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(Keyword.class).add(Restrictions.and(Restrictions.eq("orgi", orgi) ,Restrictions.like("keyword", key))),rqdata.getPs(),rqdata.getP()));
		}
		return request(responseData, orgi , rqdata) ; 
    }

}
