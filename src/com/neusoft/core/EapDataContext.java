package com.neusoft.core;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.solr.common.util.Hash;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.web.context.WebApplicationContext;

import com.alibaba.fastjson.JSON;
import com.hazelcast.core.Hazelcast;
import com.neusoft.core.channel.WebIM;
import com.neusoft.core.channel.WeiXin;
import com.neusoft.core.channel.WeiXinUser;
import com.neusoft.core.datasource.handler.GeneraDAO;
import com.neusoft.core.plugin.InstructPluginInterface;
import com.neusoft.web.model.ExtensionPoints;
import com.neusoft.web.model.HistoryJobDetail;
import com.neusoft.web.model.Instruction;
import com.neusoft.web.model.JobDetail;
import com.neusoft.web.model.RuntimeData;
import com.neusoft.web.model.SNSAccount;
import com.neusoft.web.model.TypeCategory;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class EapDataContext {
	private final static String DAO_NAME = "dao" ;
	public final static String USER_SESSION_NAME = "user" ;
	public static final String PLUGIN_TYPE_TASK = "task" ;
	public static final String TABLE_NAME_DEFAULT = "r3_tab" ;
	public static final String DATA_M_DEFAULT = "r3_datam" ;
	public static final String FILE_SPLIT_LINE = " BEGIN----------------------FILE_SPLIT_LINE----------------------------END " ;
	public static final String PLUGIN_TYPE_INDEX_PROCESS = "indexprocess" ;
	public static String SAVE_FILE_DIR = "/WEB-INF/data/file" ;
	public static String SERVER_ADDRESS ="platform.server.address";
	public static String DATA_DIR = "/WEB-INF/data";
	public static final String JDBC_R3 = "jdbc:r3:" ;
	public static final String WEB = "web";
	public static final String MODEL = "model";
	public static final String HANDLER = "handler";
	public static String TPL_DIR = "/WEB-INF/view";
	private static String dateformat = "yyyy-MM-dd HH:mm:ss" ;
	public static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd" ;
	private static ProjectDataContext datacontext;
	private static DecimalFormat decimalf = new DecimalFormat("###.00");
	
	public static Map<String , JobDetail> runningJob = new HashMap<String , JobDetail>();
	public static Map<String , Class> resourceMap = new HashMap<String , Class>() ;
	public static String REAL_PATH;
	
	private static GeneraDAO service ;
	private static Map<String, String> fieldTypeMap = new HashMap<String, String>();
	
	private static WebApplicationContext wac;
	private static String jars ;
	private static Map<String , String> webPackageName = new HashMap<String , String>() ;
	private static Map<String , Map<String , Class>> snsUserBeanMap = new HashMap<String , Map<String , Class>>() ;
	private static List<SNSAccount> accountList = null ;
	private static Map<String,List<ExtensionPoints>> pluginListMap = EapSmcDataContext.getInstance().getMap("pluginListMap");/*new HashMap<String, List<ExtensionPoints>>()*/;
	private static RuntimeData runtimeData = new RuntimeData() /*(RuntimeData) SmcRivuDataContext.getInstance().getMap("runtimeDataMap").get("runtimeData")*/;
	public static Map<String,Map<String,List<Instruction>>> instructMap =EapSmcDataContext.getInstance().getMap("instructMap");/*new HashMap<String,Map<String,List<Instruction>>>();*/
	public static Map<String, List<Instruction>> instructList = EapSmcDataContext.getInstance().getMap("inslist");//new HashMap<String , ArrayList<Instruction>>();
	public static Map<String , Object> threadPoolMap = new HashMap<String , Object>();
	public static Map<String , Map<String , TypeCategory>> customerMenuMap = new HashMap<String,Map<String , TypeCategory>>();
	
	public static DecimalFormat getDecimalFormat(){
		return decimalf ;
	}
	public static String getDefaultSiteTemplet(String orgi){
		return "site" ;
	}
	
	public static Map<String, List<ExtensionPoints>> getPluginList() {
		return pluginListMap;
	}
	public static void initPlugin() {
		//初始化插件列表
		pluginListMap.remove(PluginType.INSTRUCTION.toString());
		pluginListMap.put(PluginType.INSTRUCTION.toString(), getService().findAllByCriteria(DetachedCriteria.forClass(ExtensionPoints.class))) ;
	}
	
	/*static{
		pluginList.put(PluginType.INSTRUCTION.toString(), new ArrayList<ExtensionPoints>()) ;
		ExtensionPoints point = new ExtensionPoints() ;
		point.setClazz("com.rivues.core.plugin.TipNavMenuPlugin") ;
		point.setName("系统菜单提示") ;
		point.setId("165") ;
		pluginList.get(PluginType.INSTRUCTION.toString()).add(point) ;
		ExtensionPoints point2 = new ExtensionPoints() ;
		point2.setClazz("com.rivues.plugin.Plugin2Test") ;
		point2.setName("切换接入") ;
		point2.setId("154") ;
		pluginList.get(PluginType.INSTRUCTION.toString()).add(point2) ;
		
		ExtensionPoints point3 = new ExtensionPoints() ;
		point3.setClazz("com.rivues.core.plugin.TransferAgentInstructPlugin") ;
		point3.setName("转人工坐席") ;
		point3.setId("1254") ;
		pluginList.get(PluginType.INSTRUCTION.toString()).add(point3) ;
		
	}*/
	
	public enum TypeCategoryEnum{
		TEMPLET ,
		KM,
		STATUS,
		FAV,
		SHARE,
		ATTACHMENT,
		COMMENT,
		CUBE , 
		CHART,
		REPORT ,
		WEIBO_KEYWORD ;
		
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	
	public enum InstructionType{
		SYSTEM,
		BUSINESS,
		EVENTMENU,
		YXMENU,
		MESSAGE;
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	
	public enum ReplyType{
		MANUALLY,
		AUTOMATIC;
		
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	
	public enum PluginType{
		INSTRUCTION;
		
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	
	public enum SystemRPComman{
		GETUSER,
		SNSACCOUNT,
		MESSAGE,
		PING,
		EVENTMENU,
		MATERIALPUBLISH,
		SITEPUBLISH,
		GWREQUEST,
		SMCRESPONSE,
		SETTINGPUBLISH,
		UPDATESNSACCOUNT,
		SMCREQUESTUSERS,
		GWRESPONSEUSERS;
		
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	
	
	public enum MessageType{
		TEXT,
		IMAGE,
		VOICE , 
		STREAM,
		VIDEO,
		NEWS,
		EVENT,
		LOCATION,
		WEBPAGE,
		BBS,
		MAIL,
		WEIBO,
		WEIBOPRIMSG;
		
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	/**
	 * 社交媒体Bean 类型
	 * @author admin
	 *
	 */
	public enum SNSBeanType{
		USER,
		MESSAGE;
		
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	public enum SNSTypeEnum{
		WEIXIN,
		YIXIN,
		SINA,
		TENCENT;
		
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	public enum AgentQueueMessageType{
		NOAGENT ,					//无坐席
		LINEUP,						//进入排队
		INSERVICE,					//服务中
		ALLOTAGENT,					//分配坐席
		AUTOMATICREPLY,				//自动应答	
		AGENTMESSAGE;				//坐席消息
		
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	public enum ChannelTypeEnum{
		WEBIM ,
		WEIXIN,
		YIXIN,
		ENTQQ,
		WEIBO,
		WEIBOPRIMSG,
		WEBPAGE,
		BBS,
		EMAIL,
		WEIBOKEYWORD,
		WEIBOTOPIC,
		WEIBOUSER;
		
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	public enum TemplateNameEnum{
		web ,
		applicationContext , 
		springServlet,
		JavaBean ;
		
		public String toString(){
			return super.toString() ;
		}
	}
	
	public enum CubeEnum{
		CUBE ,
		TABLE,
		R3;
		
		public String toString(){
			return super.toString().toLowerCase() ;
		}
	}
	static{
		fieldTypeMap.put("varchar", "String") ;
		fieldTypeMap.put("int", "int") ;
		fieldTypeMap.put("datetime", "java.util.Date") ;
		fieldTypeMap.put("text", "String") ;
		fieldTypeMap.put("String", "String") ;
		/**
		 * 微信
		 */
		snsUserBeanMap.put(EapDataContext.ChannelTypeEnum.WEIXIN.toString(),new HashMap<String, Class>()) ;
		snsUserBeanMap.get(EapDataContext.ChannelTypeEnum.WEIXIN.toString()).put(SNSBeanType.USER.toString(), WeiXinUser.class) ;
		snsUserBeanMap.get(EapDataContext.ChannelTypeEnum.WEIXIN.toString()).put(SNSBeanType.MESSAGE.toString(), WeiXin.class) ;
		
		/**
		 * Web IM
		 */
		snsUserBeanMap.put(EapDataContext.ChannelTypeEnum.WEBIM.toString(),new HashMap<String, Class>()) ;
		snsUserBeanMap.get(EapDataContext.ChannelTypeEnum.WEBIM.toString()).put(SNSBeanType.USER.toString(), WeiXinUser.class) ;
		snsUserBeanMap.get(EapDataContext.ChannelTypeEnum.WEBIM.toString()).put(SNSBeanType.MESSAGE.toString(), WeiXin.class) ;
		
		
		webPackageName.put(EapDataContext.WEB, "web");
		webPackageName.put(EapDataContext.MODEL, "web.model");
		webPackageName.put(EapDataContext.HANDLER, "web.handler");
	}
	/**
	 * 获取Bean类型，channel 是渠道，type 是Bean类型，目前包括：消息 SNSUser ， 消息 Channel
	 * @param channel
	 * @return
	 */
	public static Class getSNSUserBean(String channel , String type){
		return SNSBeanType.USER.toString().equals(type) ? WeiXinUser.class : WeiXin.class;
	}
	public static Class getWebImUserBean(String channel , String type){
		return SNSBeanType.USER.toString().equals(type) ? WeiXinUser.class : WebIM.class;
	}
	/**
	 * 
	 * @param resource
	 * @return
	 */
	public static Class getResource(String resource){
		return resourceMap.get(resource) ;
	}
	public static void setWac(WebApplicationContext wac) {
		EapDataContext.wac = wac;
	}
	public static WebApplicationContext getWac(){
		return wac ;
	}
	public static GeneraDAO getService(){
		return service==null ? (GeneraDAO)wac.getBean(DAO_NAME) : service;
	}
	/**
	 * 
	 * @param clazz
	 * @return
	 */
	public static Logger getLogger(Class clazz){
		return Logger.getLogger(clazz.toString()) ;
	}
	/**
	 * 表单设计里的默认模板
	 * @return
	 */
	public static String getDefaultViewFieldTemplet(){
		return "40289e633c5af7cc013c5b2bf2710018" ;
	}
	public static String getDateformat() {
		// TODO Auto-generated method stub
		return dateformat;
	}
	public static ProjectDataContext getDataContext()
	  {
	    return datacontext;
	  }
	
	
	/**
	 * 获取模型展现方式
	 * @param type
	 * @return
	 */
	public static Object getViewType(String type , String viewtype) {
		return viewtype!=null && viewtype.equals("view") ? type : viewtype ;
	}
	/**
	 * 当前运行任务
	 * @param job
	 */
	public static void jobRunning(JobDetail job) {
		if(runningJob.get(job.getId())==null){
			runningJob.put(job.getId(), job) ;
		}
	}
	/**
	 * 删除运行任务
	 * @param job
	 */
	public static void stopJob(JobDetail job) {
		if(runningJob.get(job.getId())!=null){
			runningJob.remove(job.getId()) ;
		}
	}
	/**
	 * 记录任务执行历史
	 * @param historyJob
	 */
	@SuppressWarnings("unchecked")
	public static void reportHisJob(HistoryJobDetail historyJob) {
		EapDataContext.getService().saveIObject(historyJob) ;
	}
	/**
	 * 活动数据类型
	 * @param datatype
	 * @return
	 */
	public static String getFieldType(String datatype) {
		return fieldTypeMap.get(datatype!=null ? datatype.toLowerCase(): "String");
	}
	public static String getJars(){
		return jars ;
	}
	public static void initJars(String jarFilePath) throws Exception{
		getJarFiles(new File(jarFilePath , "lib")) ;
	}
	/** 
     * 查找该目录下的所有的jar文件  
     * @param sourceFile  
     * @param sourceFileList  
     * @throws Exception  
     */  
    private static  String  getJarFiles(File sourceFile) throws Exception {   
//      String jars="";   
        if (sourceFile.exists()) {// 文件或者目录必须存在   
            if (sourceFile.isDirectory()) {// 若file对象为目录   
                // 得到该目录下以.java结尾的文件或者目录   
                File[] childrenFiles = sourceFile.listFiles(new FileFilter() {   
                    public boolean accept(File pathname) {   
                        if (pathname.isDirectory()) {   
                            return true;   
                        } else {   
                            String name = pathname.getName();   
                            if(name.endsWith(".jar") ? true : false){   
                                jars=jars+pathname.getPath()+";";   
                                return true;   
                            }   
                            return false;   
                        }   
                    }   
                });   
            }     
        }   
        return jars;   
    }  
    public static Map<String, String> getWebPackageName() {
    	return webPackageName ;
    }
    public static List<SNSAccount> getAccountList() {
		return accountList;
	}
	public static void setAccountList(List<SNSAccount> accountList) {
		EapDataContext.accountList = accountList;
	}
	/**
	 * 
	 * @param type
	 * @return
	 */
	public static List<ExtensionPoints> getPluginList(String type){
		return pluginListMap.get(type) ;
	}
	
	public static Map<String,List<Instruction>> getInstruct(String orgi){
		return instructMap.get(orgi) ;
	}
	public static List<Instruction> getInstructList(String orgi){
		return instructList.get(orgi) ;
	}
	/**
	 * 获取下级指令，默认不包含 模糊匹配，非默认提示的 指令
	 * @param orgi
	 * @param parent
	 * @return
	 */
	public static List<Instruction> getInstructList(String orgi ,  String parent){
		List<Instruction> subInstructList = new ArrayList<Instruction>();
		for(Instruction ins : instructList.get(orgi)){
			if(parent.equals(ins.getParent()) && ins.isTipdefault()){
				subInstructList.add(ins) ;
			}
		}
		return subInstructList ;
	}
	
	/**
	 * 获取下级指令，默认不包含 模糊匹配，非默认提示的 指令
	 * @param orgi
	 * @param parent
	 * @return
	 */
	public static Instruction getRootInstruct(String orgi ,  String id){
		Instruction instruct = null ;
		for(Instruction ins : instructList.get(orgi)){
			if(id.equals(ins.getId())){
				instruct = ins ;
				break ;
			}
		}
		return instruct!=null && "0".equals(instruct.getParent()) ? instruct : instruct!=null ? getRootInstruct(orgi , instruct.getParent()): null ;
	}
	
	/**
	 * 获取上级指令中最近的一个包含插件的 指令
	 * @param orgi
	 * @param parent
	 * @return
	 */
	public static Instruction getInstructPlugin(String orgi ,  String id){
		Instruction instruct = null ;
		for(Instruction ins : instructList.get(orgi)){
			if(id.equals(ins.getId())){
				instruct = ins ;
				break ;
			}
		}
		return instruct!=null && instruct.getPlugin()!=null && instruct.getPlugin().length()>0 ? instruct : instruct!=null ? getRootInstruct(orgi , instruct.getParent()): null ;
	}
	
	
	public static ExtensionPoints getPlugin(String id){
		ExtensionPoints retPlugin = null;
		for(ExtensionPoints plugin:pluginListMap.get(PluginType.INSTRUCTION.toString())){
			if(plugin.getId().equals(id)){
				retPlugin = plugin ; 
				break ;
			}
		}
		
		return retPlugin ;
	}
	/**
	 * 
	 */
	public static void initInstruct(){
		List<Instruction> temptructList = EapDataContext.getService().findAllByIObjectCType(Instruction.class) ;
		//初始化之前进行清除
		instructMap.clear();
		instructList.clear();
		for(Instruction ins : temptructList){
			if(ins.getCode()==null || ins.getName()==null){
				continue ;
			}
			Map<String,List<Instruction>> hzinsMap=instructMap.get(ins.getOrgi());
			List<Instruction> hzinsl=instructList.get(ins.getOrgi());
			if(hzinsMap==null){
				hzinsMap= new HashMap<String, List<Instruction>>() ;
			}
			
			if(hzinsMap!=null && hzinsMap.get(ins.getCode().toLowerCase())==null){
				hzinsMap.put(ins.getCode().toLowerCase() , new ArrayList<Instruction>()) ;
			}
			instructMap.put(ins.getOrgi(), hzinsMap);
			if(hzinsMap!=null && hzinsMap.get(ins.getCode().toLowerCase())!=null){
				hzinsMap.get(ins.getCode().toLowerCase()).add(ins) ;
				EapDataContext.instructMap.put(ins.getOrgi(), hzinsMap);
			}
			if(ins.getMessagetype()!=null){
				if(hzinsMap!=null && hzinsMap.get(ins.getMessagetype().toLowerCase())==null){
					hzinsMap.put(ins.getMessagetype().toLowerCase() , new ArrayList<Instruction>()) ;
				}
				if(hzinsMap!=null && hzinsMap.get(ins.getMessagetype().toLowerCase())!=null){
					hzinsMap.get(ins.getMessagetype().toLowerCase()).add(ins) ;
					EapDataContext.instructMap.put(ins.getOrgi(), hzinsMap);
				}
			}
			if(hzinsl==null){
				hzinsl=new ArrayList<Instruction>() ;
			}
			instructList.put(ins.getOrgi(), hzinsl);
			if(hzinsl!=null){
				hzinsl.add(ins);
				instructList.put(ins.getOrgi(), hzinsl);
			}
			if(ins.getPlugin()!=null && ins.getPlugin().length()>0){
				ExtensionPoints plugin = EapDataContext.getPlugin(ins.getPlugin()) ;
				InstructPluginInterface instruct;
				try {
					if(plugin!=null && !plugin.equals("null") && plugin.getClazz()!=null){
						instruct = (InstructPluginInterface) Class.forName(plugin.getClazz()).newInstance();
						instruct.initVirInstruct(ins.getOrgi() , ins) ;
					}
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public static void initInstruct(String orgi){
		//初始化之前进行清除
		instructMap.clear();
		instructList.clear();
		List<Instruction> temptructList = EapDataContext.getService().findAllByCriteria(DetachedCriteria.forClass(Instruction.class).add(Restrictions.eq("orgi", orgi))) ;
		for(Instruction ins : temptructList){
			if(ins.getCode()==null || ins.getName()==null){
				continue ;
			}
			Map<String,List<Instruction>> hzinsMap=instructMap.get(ins.getOrgi());
			List<Instruction> hzinsl=instructList.get(ins.getOrgi());
			if(hzinsMap==null){
				hzinsMap= new HashMap<String, List<Instruction>>() ;
			}
			
			if(hzinsMap!=null && hzinsMap.get(ins.getCode().toLowerCase())==null){
				hzinsMap.put(ins.getCode().toLowerCase() , new ArrayList<Instruction>()) ;
			}
			instructMap.put(ins.getOrgi(), hzinsMap);
			if(hzinsMap!=null && hzinsMap.get(ins.getCode().toLowerCase())!=null){
				hzinsMap.get(ins.getCode().toLowerCase()).add(ins) ;
				EapDataContext.instructMap.put(ins.getOrgi(), hzinsMap);
			}
			if(ins.getMessagetype()!=null){
				if(hzinsMap!=null && hzinsMap.get(ins.getMessagetype().toLowerCase())==null){
					hzinsMap.put(ins.getMessagetype().toLowerCase() , new ArrayList<Instruction>()) ;
				}
				if(hzinsMap!=null && hzinsMap.get(ins.getMessagetype().toLowerCase())!=null){
					hzinsMap.get(ins.getMessagetype().toLowerCase()).add(ins) ;
					EapDataContext.instructMap.put(ins.getOrgi(), hzinsMap);
				}
			}
			if(hzinsl==null){
				hzinsl=new ArrayList<Instruction>() ;
			}
			instructList.put(ins.getOrgi(), hzinsl);
			if(hzinsl!=null){
				hzinsl.add(ins);
				instructList.put(ins.getOrgi(), hzinsl);
			}
			if(ins.getPlugin()!=null && ins.getPlugin().length()>0){
				ExtensionPoints plugin = EapDataContext.getPlugin(ins.getPlugin()) ;
				InstructPluginInterface instruct;
				try {
					if(plugin!=null && !plugin.equals("null") && plugin.getClazz()!=null){
						instruct = (InstructPluginInterface) Class.forName(plugin.getClazz()).newInstance();
						instruct.initVirInstruct(ins.getOrgi() , ins) ;
					}
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void initVirInstruct(String orgi , Instruction ins){
		Map<String,List<Instruction>> hzinsMap=instructMap.get(orgi);
		if(hzinsMap == null){
			hzinsMap=new HashMap<String, List<Instruction>>();
		}
		if(hzinsMap.get(ins.getCode().toLowerCase()) == null){
			hzinsMap.put(ins.getCode().toLowerCase(), new ArrayList());
		}
		EapDataContext.instructMap.put(orgi, hzinsMap) ;
		hzinsMap.get(ins.getCode().toLowerCase()).add(ins) ;
		EapDataContext.instructMap.put(orgi, hzinsMap);
	}
	/**
	 * 
	 * @return
	 */
	public static RuntimeData getRuntimeData(){
		return runtimeData;
	}
	/**
	 * 
	 * @return
	 */
	public static RuntimeData setRuntimeData(){
		return runtimeData =  new RuntimeData() ;
	}
	/**
	 * 统计发送消息
	 * @param bytes
	 */
	public static void staticSendRuntimeData(long bytes){
		if(runtimeData.getReport().getStarttime()==null){
			runtimeData.getReport().setStarttime(new Date()) ;
		}
		runtimeData.getReport().getSendpages().incrementAndGet();
		runtimeData.getReport().getSendbytes().addAndGet(bytes) ;
	}
	/**
	 * 统计收到消息
	 * @param bytes
	 */
	public static void staticReciveRuntimeData(long bytes){
		if(runtimeData.getReport().getStarttime()==null){
			runtimeData.getReport().setStarttime(new Date()) ;
		}
		runtimeData.getReport().getRecivepages().incrementAndGet();
		runtimeData.getReport().getRecivebytes().addAndGet(bytes) ;
	}
	
	/**
	 * 统计自动回复消息
	 * @param bytes
	 */
	public static void staticAutoMessageRuntimeData(long bytes){
		if(runtimeData.getReport().getStarttime()==null){
			runtimeData.getReport().setStarttime(new Date()) ;
		}
		runtimeData.getReport().getAutomessagepage().incrementAndGet();
		runtimeData.getReport().getAutomessagebytes().addAndGet(bytes) ;
	}
}
