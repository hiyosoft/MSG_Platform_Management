package com.sinosig.task.interfaces;

import java.io.File;

public interface IZLBCVariables {
	
	final static String FOLDER_BASE = System.getProperties().getProperty("user.home").toString()+File.separator;
	final static String FOLDER_IMAGE = FOLDER_BASE+"weixin_images";
	
	final static String FOLDER_ZIP =  FOLDER_BASE+"weixin_zips";
	
	//	测试环境
//	final static String URL_UPLOAD_IMAGES_ADDRESS = "http://10.10.163.98:9002/SunECM/servlet/UploadImage";
	//	测试环境
//	final static String URL_GETCLAIMMSG_ADDRESS= "http://10.10.163.174:7001/autoclaim_new/sunshineAccept/getclaimmsg";
	
	
	//	正式环境
	final static String URL_UPLOAD_IMAGES_ADDRESS = "http://10.10.0.69/SunECM/servlet/UploadImage";
	//	正式环境
	final static String URL_GETCLAIMMSG_ADDRESS= "http://10.10.0.24/autoclaim_new/sunshineAccept/getclaimmsg";
	
//	final static String URL_GETCLAIMMSG_ADDRESS = "http://10.10.163.196:7001/autoclaim_new/sunshineAccept/getclaimdata";
	
	
	

}
