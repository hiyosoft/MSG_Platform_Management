package com.neusoft.web.handler.manage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.neusoft.core.channel.WeiXin;
import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.RequestData;
import com.neusoft.web.handler.ResponseData;
import com.neusoft.web.model.FAQModel;
import com.sinosig.pay.platform.kuaiqian.model.MPurchase;

@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}/orders")
@SuppressWarnings("unchecked")
public class OrdersHandler extends Handler{
	
	
	
	//查看支付详情
	@RequestMapping(value = "/edit/{externalRefNumber}")
	public ModelAndView edit(HttpServletRequest request, @PathVariable String orgi,@PathVariable String externalRefNumber,@ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/orderssearch/ordersedit", "/pages/include/iframeindex");
		responseData.setData(super.getService().getIObjectByPK(MPurchase.class, externalRefNumber));
		responseData.setDataList(super.getService().findAllByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.eq("externalRefNumber", ""))));
		return request(responseData, orgi, data);
	}
	
	//显示列表
	@RequestMapping(value = "/ordersList")
	public ModelAndView list(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/orderssearch/ordersList");
		responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)),data.getPs(),data.getP()));
		return request(responseData, orgi, data);
	}
	
	@RequestMapping(value = "/pageList")
	public ModelAndView pageList(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("/pages/manage/orderssearch/ordersList");
		String begintime= request.getParameter("begintime");
		String endtime= request.getParameter("endtime");
		String risks = request.getParameter("risks");
		String risk = request.getParameter("risk");
		SimpleDateFormat toformat=new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat fromformat=new SimpleDateFormat("yyyy-MM-dd");
		String beginDate="" ;
		String endDate="";
		if(!"".equals(begintime) && !"".equals(endtime)){
			try {
				beginDate = toformat.format(fromformat.parse(begintime));
				endDate = toformat.format(fromformat.parse(endtime));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String emsg = request.getParameter("errormsg");
		String ecode = "";
		if(emsg.equals("00") && !"".equals(emsg)){
			ecode ="00";
			if((begintime == null || "".equals(begintime)) && (endtime==null || "".equals(endtime))){
				if((risk == null || "".equals(risk)) && (risks == null || "".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))),data.getPs(),data.getP()));
				}else if((risk == null || "".equals(risk)) && (risks != null && !"".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))).add(Restrictions.and(Restrictions.isNotNull("bizTotalPremium"), Restrictions.isNull("forcePremium"))),data.getPs(),data.getP()));
				}else if((risk != null && !"".equals(risk)) && (risks == null || "".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNull("bizTotalPremium"))),data.getPs(),data.getP()));
				}else if((risk != null && !"".equals(risk)) && (risks != null && !"".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNotNull("bizTotalPremium"))),data.getPs(),data.getP()));
				}
				
			}else if((begintime != null && !"".equals(begintime)) && (endtime!=null && !"".equals(endtime))){
				if((risk == null || "".equals(risk)) && (risks == null || "".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))).add(Restrictions.between("entryTime", beginDate, endDate)),data.getPs(),data.getP()));
				}else if((risk == null || "".equals(risk)) && (risks != null && !"".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("bizTotalPremium"), Restrictions.isNull("forcePremium"))),data.getPs(),data.getP()));
				}else if((risk != null && !"".equals(risk)) && (risks == null || "".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNull("bizTotalPremium"))),data.getPs(),data.getP()));
				}else if((risk != null && !"".equals(risk)) && (risks != null && !"".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNotNull("bizTotalPremium"))),data.getPs(),data.getP()));
				}
				
			}
			
		}else if(emsg.equals("01") && !"".equals(emsg)){
			if((begintime == null || "".equals(begintime)) && (endtime==null || "".equals(endtime))){
				if((risk == null || "".equals(risk)) && (risks == null || "".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))),data.getPs(),data.getP()));
				}else if((risk == null || "".equals(risk)) && (risks != null && !"".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))).add(Restrictions.and(Restrictions.isNotNull("bizTotalPremium"), Restrictions.isNull("forcePremium"))),data.getPs(),data.getP()));
				}else if((risk != null && !"".equals(risk)) && (risks == null || "".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNull("bizTotalPremium"))),data.getPs(),data.getP()));
				}else if((risk != null && !"".equals(risk)) && (risks != null && !"".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNotNull("bizTotalPremium"))),data.getPs(),data.getP()));
				}
				
				
			}else if((begintime != null && !"".equals(begintime)) && (endtime!=null && !"".equals(endtime))){
				if((risk == null || "".equals(risk)) && (risks == null || "".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))).add(Restrictions.between("entryTime", beginDate, endDate)),data.getPs(),data.getP()));
				}else if((risk == null || "".equals(risk)) && (risks != null && !"".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("bizTotalPremium"), Restrictions.isNull("forcePremium"))),data.getPs(),data.getP()));
				}else if((risk != null && !"".equals(risk)) && (risks == null || "".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNull("bizTotalPremium"))),data.getPs(),data.getP()));
				}else if((risk != null && !"".equals(risk)) && (risks != null && !"".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNotNull("bizTotalPremium"))),data.getPs(),data.getP()));
				}
				
			}
			
		}
		
		if("".equals(emsg) || emsg ==null){
			if((begintime == null || "".equals(begintime)) && (endtime==null || "".equals(endtime))){
				if((risk == null || "".equals(risk)) && (risks == null || "".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)),data.getPs(),data.getP()));
				}else if((risk == null || "".equals(risk)) && (risks != null && !"".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.and(Restrictions.isNotNull("bizTotalPremium"), Restrictions.isNull("forcePremium"))),data.getPs(),data.getP()));
				}else if((risk != null && !"".equals(risk)) && (risks == null || "".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNull("bizTotalPremium"))),data.getPs(),data.getP()));
				}else if((risk != null && !"".equals(risk)) && (risks != null && !"".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNotNull("bizTotalPremium"))),data.getPs(),data.getP()));
				}
				
				
			}else if((begintime != null && !"".equals(begintime)) && (endtime!=null && !"".equals(endtime))){
				if((risk == null || "".equals(risk)) && (risks == null || "".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.between("entryTime", beginDate, endDate)),data.getPs(),data.getP()));
				}else if((risk == null || "".equals(risk)) && (risks != null && !"".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("bizTotalPremium"), Restrictions.isNull("forcePremium"))),data.getPs(),data.getP()));
				}else if((risk != null && !"".equals(risk)) && (risks == null || "".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNull("bizTotalPremium"))),data.getPs(),data.getP()));
				}else if((risk != null && !"".equals(risk)) && (risks != null && !"".equals(risks))){
					responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNotNull("bizTotalPremium"))),data.getPs(),data.getP()));
				}
				
			}
		}
		
		return request(responseData, orgi, data);
	}
	//查询
		@RequestMapping(value = "/tableList")
		public ModelAndView search(HttpServletRequest request, @PathVariable String orgi, @ModelAttribute("data") RequestData data) {
			ResponseData responseData = new ResponseData("/pages/manage/orderssearch/ordersList");
			String begintime= request.getParameter("begintime");
			String endtime= request.getParameter("endtime");
			String risks = request.getParameter("risks");
			String risk = request.getParameter("risk");
			SimpleDateFormat toformat=new SimpleDateFormat("yyyyMMddHHmmss");
			SimpleDateFormat fromformat=new SimpleDateFormat("yyyy-MM-dd");
			String beginDate="" ;
			String endDate="";
			if(!"".equals(begintime) && !"".equals(endtime)){
				try {
					beginDate = toformat.format(fromformat.parse(begintime));
					endDate = toformat.format(fromformat.parse(endtime));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			String emsg = request.getParameter("errormsg");
			String ecode = "";
			if(emsg.equals("00") && !"".equals(emsg)){
				ecode ="00";
				if((begintime == null || "".equals(begintime)) && (endtime==null || "".equals(endtime))){
					if((risk == null || "".equals(risk)) && (risks == null || "".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))),data.getPs(),data.getP()));
					}else if((risk == null || "".equals(risk)) && (risks != null && !"".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))).add(Restrictions.and(Restrictions.isNotNull("bizTotalPremium"), Restrictions.isNull("forcePremium"))),data.getPs(),data.getP()));
					}else if((risk != null && !"".equals(risk)) && (risks == null || "".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNull("bizTotalPremium"))),data.getPs(),data.getP()));
					}else if((risk != null && !"".equals(risk)) && (risks != null && !"".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNotNull("bizTotalPremium"))),data.getPs(),data.getP()));
					}
					
				}else if((begintime != null && !"".equals(begintime)) && (endtime!=null && !"".equals(endtime))){
					if((risk == null || "".equals(risk)) && (risks == null || "".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))).add(Restrictions.between("entryTime", beginDate, endDate)),data.getPs(),data.getP()));
					}else if((risk == null || "".equals(risk)) && (risks != null && !"".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("bizTotalPremium"), Restrictions.isNull("forcePremium"))),data.getPs(),data.getP()));
					}else if((risk != null && !"".equals(risk)) && (risks == null || "".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNull("bizTotalPremium"))),data.getPs(),data.getP()));
					}else if((risk != null && !"".equals(risk)) && (risks != null && !"".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("responseCode2", ecode))).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNotNull("bizTotalPremium"))),data.getPs(),data.getP()));
					}
					
				}
				
			}else if(emsg.equals("01") && !"".equals(emsg)){
				if((begintime == null || "".equals(begintime)) && (endtime==null || "".equals(endtime))){
					if((risk == null || "".equals(risk)) && (risks == null || "".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))),data.getPs(),data.getP()));
					}else if((risk == null || "".equals(risk)) && (risks != null && !"".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))).add(Restrictions.and(Restrictions.isNotNull("bizTotalPremium"), Restrictions.isNull("forcePremium"))),data.getPs(),data.getP()));
					}else if((risk != null && !"".equals(risk)) && (risks == null || "".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNull("bizTotalPremium"))),data.getPs(),data.getP()));
					}else if((risk != null && !"".equals(risk)) && (risks != null && !"".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNotNull("bizTotalPremium"))),data.getPs(),data.getP()));
					}
					
					
				}else if((begintime != null && !"".equals(begintime)) && (endtime!=null && !"".equals(endtime))){
					if((risk == null || "".equals(risk)) && (risks == null || "".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))).add(Restrictions.between("entryTime", beginDate, endDate)),data.getPs(),data.getP()));
					}else if((risk == null || "".equals(risk)) && (risks != null && !"".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("bizTotalPremium"), Restrictions.isNull("forcePremium"))),data.getPs(),data.getP()));
					}else if((risk != null && !"".equals(risk)) && (risks == null || "".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNull("bizTotalPremium"))),data.getPs(),data.getP()));
					}else if((risk != null && !"".equals(risk)) && (risks != null && !"".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.or(Restrictions.isNull("responseCode2"), Restrictions.not(Restrictions.eq("responseCode2", "00"))))).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNotNull("bizTotalPremium"))),data.getPs(),data.getP()));
					}
					
				}
				
			}
			
			if("".equals(emsg) || emsg ==null){
				if((begintime == null || "".equals(begintime)) && (endtime==null || "".equals(endtime))){
					if((risk == null || "".equals(risk)) && (risks == null || "".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)),data.getPs(),data.getP()));
					}else if((risk == null || "".equals(risk)) && (risks != null && !"".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.and(Restrictions.isNotNull("bizTotalPremium"), Restrictions.isNull("forcePremium"))),data.getPs(),data.getP()));
					}else if((risk != null && !"".equals(risk)) && (risks == null || "".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNull("bizTotalPremium"))),data.getPs(),data.getP()));
					}else if((risk != null && !"".equals(risk)) && (risks != null && !"".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNotNull("bizTotalPremium"))),data.getPs(),data.getP()));
					}
					
					
				}else if((begintime != null && !"".equals(begintime)) && (endtime!=null && !"".equals(endtime))){
					if((risk == null || "".equals(risk)) && (risks == null || "".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.between("entryTime", beginDate, endDate)),data.getPs(),data.getP()));
					}else if((risk == null || "".equals(risk)) && (risks != null && !"".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("bizTotalPremium"), Restrictions.isNull("forcePremium"))),data.getPs(),data.getP()));
					}else if((risk != null && !"".equals(risk)) && (risks == null || "".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNull("bizTotalPremium"))),data.getPs(),data.getP()));
					}else if((risk != null && !"".equals(risk)) && (risks != null && !"".equals(risks))){
						responseData.setDataList(super.getService().findPageByCriteria(DetachedCriteria.forClass(MPurchase.class).add(Restrictions.eq("orgi", orgi)).add(Restrictions.between("entryTime", beginDate, endDate)).add(Restrictions.and(Restrictions.isNotNull("forcePremium"), Restrictions.isNotNull("bizTotalPremium"))),data.getPs(),data.getP()));
					}
					
				}
			}
			
			return request(responseData, orgi, data);
		}
		
	
}
