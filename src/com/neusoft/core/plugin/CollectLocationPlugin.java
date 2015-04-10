package com.neusoft.core.plugin;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.neusoft.core.EapDataContext;
import com.neusoft.core.api.APIContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.core.channel.WeiXin;
import com.neusoft.util.queue.AgentUser;
import com.neusoft.util.rpc.message.Message;
import com.neusoft.util.rpc.message.SystemMessage;
import com.neusoft.util.store.EapTools;
import com.neusoft.web.model.Instruction;
import com.neusoft.web.model.SinoLocation;

public class CollectLocationPlugin extends Plugin{
	private static String SINOSIG_LOCATION="SINOSIG_LOCATION";
	

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return "TIP_NEWS_INSTRUCT";
	}

	@Override
	public void initVirInstruct(String orgi , Instruction instruct){
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getMessage(Instruction instruct, AgentUser user, String orgi, Channel channel) {
		// TODO Auto-generated method stub
		StringBuffer strb = new StringBuffer() ;
		WeiXin wx=((WeiXin)channel);
		@SuppressWarnings("unchecked")
		List<SinoLocation> dllocals=EapDataContext.getService().findAllByCriteria(DetachedCriteria.forClass(SinoLocation.class).add(Restrictions.and(Restrictions.eq("orgi", orgi), Restrictions.eq("fackid",wx.getFromUserName()))));
		if(dllocals!=null && dllocals.size()> 0 ){
			SinoLocation dllocal=dllocals.get(0);
			if("3".equals(dllocal.getStatus())){
				strb.append("输入的服务网点信息正在审核，不允许修改");
			}else{
					//保存地理位置信息
//					dllocal.setHislat(dllocal.getLat());
//					dllocal.setHislon(dllocal.getLon());
					dllocal.setLat(wx.getLon());
					dllocal.setLon(wx.getLat());
					dllocal.setStatus("0");
					/*new Thread(new Runnable() {
						
						@Override
						public void run() {
							APIContext.getRpcServer().sendMessageToServer(
									new Message(RivuDataContext.HANDLER, JSON.toJSONString(new SystemMessage(RivuDataContext.SystemRPComman.SITEPUBLISH.toString(), list), SerializerFeature.WriteClassName)));
						}
					}).start();*/
					//发消息给GW，在GW更新百度信息
					APIContext.getRpcServer().sendMessageToServer(
							new Message(EapDataContext.HANDLER, JSON.toJSONString(new SystemMessage(SINOSIG_LOCATION, dllocal), SerializerFeature.WriteClassName)));
					EapDataContext.getService().updateIObject(dllocal);
					strb.append("信息输入完成，非常感谢您的配合与支持");
				}
			}
		return  super.getChannelMessage(instruct, strb.toString(), user, orgi, channel) ;
	}
	public static void main(String[] args) {
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        //必须字段
		nvps.add(new BasicNameValuePair("geotable_id", "35515"));
        nvps.add(new BasicNameValuePair("ak", "12d30b681c78b111c5088519989af2a1"));
        nvps.add(new BasicNameValuePair("title", "阳光保险有限公司北京财险分公司"));
        nvps.add(new BasicNameValuePair("address", "北京联合大厦"));
        nvps.add(new BasicNameValuePair("latitude", "39.323231212"));
        nvps.add(new BasicNameValuePair("longitude", "119.3232"));
        nvps.add(new BasicNameValuePair("coord_type", "3"));
        nvps.add(new BasicNameValuePair("contract", "岳晶晶"));
        nvps.add(new BasicNameValuePair("id", "36901514"));
        //自定义字段
        nvps.add(new BasicNameValuePair("deptid", "8abf490d42226fc00142227169d30002"));
		System.out.println(EapTools.postData("http://api.map.baidu.com/geodata/v2/poi/update", nvps));
		
	}
}
