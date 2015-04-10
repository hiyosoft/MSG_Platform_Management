/*
 * Comet4J Copyright(c) 2011, http://code.google.com/p/comet4j/ This code is
 * licensed under BSD license. Use it as you wish, but keep this copyright
 * intact.
 */
package com.neusoft.util.comet.demo.talker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.neusoft.core.EapDataContext;
import com.neusoft.core.channel.Channel;
import com.neusoft.core.channel.DataMessage;
import com.neusoft.core.channel.WebIM;
import com.neusoft.core.channel.WebIMUser;
import com.neusoft.util.comet.core.CometContext;
import com.neusoft.util.comet.core.CometEngine;
import com.neusoft.util.comet.core.util.JSONUtil;
import com.neusoft.util.comet.demo.talker.dto.RenameDTO;
import com.neusoft.util.comet.demo.talker.dto.TalkDTO;
import com.neusoft.util.comet.demo.talker.dto.UserDTO;
import com.neusoft.util.rpc.message.Message;

/**
 * web交互
 * @author 
 * @date 2011-3-3
 */

public class WebServlet extends HttpServlet {

	/** serialVersionUID */
	private static final long serialVersionUID = -1311176251844328163L;
	private static final String CMD_FLAG = "cmd";
	private static final String TALK_CMD = "t";
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		doPost(request , response);
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		String cmd = request.getParameter(CMD_FLAG);
		// 发送信息
		if (TALK_CMD.equals(cmd) && request.getParameter("orgi")!=null) {
			String id = request.getSession().getId();
			String orgi = request.getParameter("orgi") ;
			String text = request.getParameter("text");
//			RivuDataContext.getRpcServer().sendMessageToServer(null) ;
			return ;
		}
	}
}
