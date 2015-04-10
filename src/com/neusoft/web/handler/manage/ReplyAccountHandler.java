package com.neusoft.web.handler.manage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.neusoft.util.persistence.DBPersistence;
import com.neusoft.web.handler.Handler;
import com.neusoft.web.handler.RequestData;
import com.neusoft.web.handler.ResponseData;

@Controller
@SessionAttributes
@RequestMapping(value = "/{orgi}/replyAccount")
public class ReplyAccountHandler  extends Handler{

	/**
	 * 林招远
	 */
	@RequestMapping(value = "/tablelist/{channel}")
	public ModelAndView weixinmessage(HttpServletRequest request, @PathVariable String orgi,@PathVariable String channel, @ModelAttribute("data") RequestData data) {
		ResponseData responseData = new ResponseData("pages/manage/replyAccount/tablelist");
		ReplyAccountHandler rah=new ReplyAccountHandler();
		String result=rah.getreplyAccount(channel);
		responseData.setResult(result);
		return request(responseData, orgi, data);
	}
	
	/**
	 * JDBC
	 * 
	 */
	Connection connection = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	String [] colors={"AFD8F8","F6BD0F","FF8E46","8BBA00","008E8E","D64646","8E468E","588526","B3AA00","008ED6","9D080D","A186BE"};

	public String getreplyAccount(String channel) {
		StringBuffer sb = new StringBuffer();
		sb.append("<graph caption='回话统计' xAxisName='回复类型' yAxisName='总量' showNames='1' decimalPrecision='0' formatNumberScale='0'>"); 
		String replyType="";
		try {
			connection=DBPersistence.getconnection();
			if (connection != null) {
				try {
					String sql = "select id,replytype,count(*) from rivu_weixinmessage where replytype='manually' and channel= ?  group by replytype union all select id,replytype,count(*) from rivu_weixinmessage where replytype='automatic' and channel= ? group by replytype";
					ps = connection.prepareStatement(sql);
					ps.setString(1, channel);
					ps.setString(2, channel);
					rs = ps.executeQuery();
					
					while(rs.next()){
						if(rs.getString("replytype").equals("manually")){
							replyType="人工回复";
						}else if(rs.getString("replytype").equals("automatic")){
							replyType="系统回复";
						}
						sb.append("<set name='").append(replyType).append("' value='").append(rs.getString("count(*)")).append("' color='").append(colors[new Random().nextInt(12)]).append("' />");
					
					}
					sb.append("</graph>");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						rs.close();
						ps.close();
						connection.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		} catch (Exception e) {
		}
		return sb.toString();
	}
	
	
}
