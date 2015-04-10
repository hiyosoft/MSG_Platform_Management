package com.neusoft.core.plugin;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.neusoft.core.EapDataContext;
import com.neusoft.core.EapSmcDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.util.store.EapTools;
import com.neusoft.web.model.Instruction;
import com.neusoft.web.model.Material;
import com.neusoft.web.model.SearchResultTemplet;

import freemarker.template.TemplateException;

public class TipUserBindMessagePlugin  implements InstructPluginInterface{

	@Override
	public String getMessage(Instruction instruct, AgentUser user, String orgi , Channel channel) {
		Map<String,Object> valueMap = new HashMap<String, Object>();
		String text = null ;
		SearchResultTemplet templet = null ;
		try {
			valueMap.put("user", user!=null ? user.getSnsuser():null) ;
			valueMap.put("msg", channel) ;
			valueMap.put("setting", EapSmcDataContext.getSearchSetting(orgi)) ;
			
			if("1".equals(EapSmcDataContext.getSearchSetting(orgi).getBindmsgtype())){
				Material material = EapSmcDataContext.getMaterial(orgi, EapSmcDataContext.getSearchSetting(orgi).getBindmaterial()) ;
				valueMap.put("news", material.getmItems());
				for(int i=0 ; i< EapSmcDataContext.getSearchResultTempletList(channel.getChannel()).size() ; i++){
					SearchResultTemplet srt = EapSmcDataContext.getSearchResultTempletList(channel.getChannel()).get(i) ;
					if(srt.getCode().equals(EapDataContext.MessageType.NEWS.toString())){
						templet = srt ;
						break ;
					}
				}
			}else{
				valueMap.put("text", EapSmcDataContext.getSearchSetting(orgi).getBindmsg()) ;
				for(int i=0 ; i< EapSmcDataContext.getSearchResultTempletList(channel.getChannel()).size() ; i++){
					SearchResultTemplet srt = EapSmcDataContext.getSearchResultTempletList(channel.getChannel()).get(i) ;
					if(srt.getCode().equals(EapDataContext.MessageType.TEXT.toString())){
						templet = srt ;
						break ;
					}
				}
			}
			
			valueMap.put("time", String.valueOf(new Date().getTime())) ;
			text = EapTools.getTemplet(templet, valueMap) ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text!=null ? text : "" ;
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return "TIP_USER_BIND_INSTRUCT";
	}

	@Override
	public void initVirInstruct(String orgi , Instruction instruct){
		// TODO Auto-generated method stub
		
	}
	/**
	 * 
	 * @return
	 */
	private String getText(String from , String to){
		StringBuffer strb = new StringBuffer() ;
		strb.append("<xml><ToUserName><![CDATA[").append(to).append("]]></ToUserName><FromUserName><![CDATA[").append(from).append("]]></FromUserName><CreateTime>").append(System.currentTimeMillis()).append("</CreateTime><MsgType><![CDATA[news]]></MsgType><ArticleCount>7</ArticleCount>") ;
		strb.append("<Articles>");
		strb.append("<item><Title><![CDATA[").append("R3 rivuES 大数据解决方案").append("").append("]]></Title> ").append("<Description><![CDATA[").append("").append("").append("]]></Description><PicUrl><![CDATA[").append("http://mmsns.qpic.cn/mmsns/Z6r92z1DjiaIr2Zvf9ic6A4DlOtIHtPSlqche7AwTtmW0t5UQh6pmyIg/0").append("]]></PicUrl> <Url><![CDATA[").append("http://mp.weixin.qq.com/mp/appmsg/show?__biz=MjM5NjE3MjIwMQ==&appmsgid=10000028&itemidx=1&sign=93c415f7451f9155f8b262eac1987d69#wechat_redirect").append("]]></Url></item>");
		strb.append("<item><Title><![CDATA[").append("R3 rivuES 企业级搜索平台").append("").append("]]></Title> ").append("<Description><![CDATA[").append("").append("").append("]]></Description><PicUrl><![CDATA[").append("http://mmsns.qpic.cn/mmsns/Z6r92z1DjiaIr2Zvf9ic6A4DlOtIHtPSlqPRyibRpkRmzb0dasn58A8yw/0").append("]]></PicUrl> <Url><![CDATA[").append("http://mp.weixin.qq.com/mp/appmsg/show?__biz=MjM5NjE3MjIwMQ==&appmsgid=10000028&itemidx=2&sign=fdbc8206ee87ac29104d46d8a6e32003#wechat_redirect").append("]]></Url></item>");
		strb.append("<item><Title><![CDATA[").append("R3 Query 大数据分析展现平台").append("").append("]]></Title> ").append("<Description><![CDATA[").append("").append("").append("]]></Description><PicUrl><![CDATA[").append("http://mmsns.qpic.cn/mmsns/Z6r92z1DjiaIr2Zvf9ic6A4DlOtIHtPSlqv96tPkkhxklRwX5yoY0xcA/0").append("]]></PicUrl> <Url><![CDATA[").append("http://mp.weixin.qq.com/mp/appmsg/show?__biz=MjM5NjE3MjIwMQ==&appmsgid=10000028&itemidx=3&sign=f5f486edd426226926191ad49d918704#wechat_redirect").append("]]></Url></item>");
		strb.append("<item><Title><![CDATA[").append("R3 SMC 社交媒体联络平台").append("").append("]]></Title> ").append("<Description><![CDATA[").append("").append("").append("]]></Description><PicUrl><![CDATA[").append("http://mmsns.qpic.cn/mmsns/Z6r92z1DjiaIr2Zvf9ic6A4DlOtIHtPSlqw2UQ5Z4Y9xW69lSyzyIJQw/0").append("]]></PicUrl> <Url><![CDATA[").append("http://mp.weixin.qq.com/mp/appmsg/show?__biz=MjM5NjE3MjIwMQ==&appmsgid=10000028&itemidx=4&sign=57efc0ffa9743d56ad48b3dd31d54f96#wechat_redirect").append("]]></Url></item>");
		strb.append("<item><Title><![CDATA[").append("祝贺中国保险IT应用高峰论坛2013成功举办，欢迎与会嘉宾注册！").append("").append("]]></Title> ").append("<Description><![CDATA[").append("").append("").append("]]></Description><PicUrl><![CDATA[").append("http://mmsns.qpic.cn/mmsns/Z6r92z1DjiaIr2Zvf9ic6A4DlOtIHtPSlqJu6euyZRastcHX7piaakCWw/0").append("]]></PicUrl> <Url><![CDATA[").append("http://demo.rivues.com/rivues/reg.html?openid=").append(to).append("]]></Url></item>");
		strb.append("<item><Title><![CDATA[").append("请关注中科融研官方微博@中科融研rivuES，让您及时了解产品最新动态！").append("").append("]]></Title> ").append("<Description><![CDATA[").append("").append("").append("]]></Description><PicUrl><![CDATA[").append("http://mmsns.qpic.cn/mmsns/Z6r92z1DjiaIr2Zvf9ic6A4DlOtIHtPSlqX7zg2C8t54DH0MuvZOJib9g/0").append("]]></PicUrl> <Url><![CDATA[").append("http://e.weibo.com/rivues").append("]]></Url></item>");
		strb.append("<item><Title><![CDATA[").append("您可以输入【?】进入交互式导航菜单，输入【kf】转人工联络！").append("").append("]]></Title> ").append("<Description><![CDATA[").append("").append("").append("]]></Description><PicUrl><![CDATA[").append("").append("]]></PicUrl><Url></Url></item>");
		
		strb.append("</Articles><FuncFlag>1</FuncFlag></xml>");
		return strb.toString() ;
	}
}
