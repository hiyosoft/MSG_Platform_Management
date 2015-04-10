package com.neusoft.web.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.common.SolrDocument;

import com.neusoft.core.datasource.handler.DCriteriaPageSupport;

public class ResponseData {
	private int p ;
	private String page ;
	private String result;
	private List<?> dataList ;
	private long docNum ;
	private int total ; 		//database data
	private String error ;
	private String message ;	//正常提示信息
	private FacetField facet ;
	private boolean throwError = true;
	private SolrDocument doc ;
	private RequestData rqdata ;
	private Object data ;
	private List<?> valueList ;
	private Map values ;
	private Exception ex ;
	private FieldStatsInfo stats ;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH") ;
	private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd") ;
	private static SimpleDateFormat toFormat = new SimpleDateFormat("dd'日'HH'时'") ;
	private static SimpleDateFormat toFormat2 = new SimpleDateFormat("dd'日'") ;
	
	public ResponseData(String result , String page , List<?> dataList){
		this.page = page; 
		this.result = result ;
		this.dataList = dataList ;
		if(dataList instanceof DCriteriaPageSupport){
			this.docNum = ((DCriteriaPageSupport<?>)dataList).getTotalCount() ;
		}
	}
	
	public ResponseData( String result  , String page){
		this.page = page;
		this.result = result ;
	}
	public ResponseData( String result  , String page , int p , List<?> valueList){
		this.page = page;
		this.result = result ;
		this.p = p ;
		this.valueList = valueList ;
	}
	
	
	public ResponseData(String page , List<?> dataList , RequestData rqdata){
		this.page = page; 
		this.dataList = dataList ;
		this.rqdata = rqdata ;
		if(dataList instanceof DCriteriaPageSupport){
			this.docNum = ((DCriteriaPageSupport<?>)dataList).getTotalCount() ;
		}
	}
	 
	public ResponseData(String page , List<?> dataList){
		this.page = page; 
		this.dataList = dataList ;
	}
	
	/**
	 * @description add construtor
	 * @author herb
	 * @version 0.1
	 * @param result
	 * @param data
	 * @since Oct 19, 2012 1:21:59 PM
	 */
	public ResponseData(String page , Object data){
		this.page = page; 
		this.data = data ;
	}
	
	public ResponseData(String page , List<?> dataList , Object data){
		this.page = page; 
		this.dataList = dataList ;
		this.data = data ;
		if(data instanceof SolrDocument){
			this.doc = (SolrDocument) data ;
		}
	}
	
	public ResponseData(String page , String error , boolean showError, Object data){
		this.page = page; 
		this.error = error ;
		this.data = data ;
	}
	
	public ResponseData(String page){
		this.page = page ;
	}
	public double sin(int i , float r , float x ){
		double ci = Math.sqrt(Math.sqrt(r)) ;
		return Math.sin((Math.PI/180) * i)*(ci*x);
	}
	public double random(){
		return RandomUtils.nextInt(360) ;
	}
	public double cos(int i , float r, float x){
		return Math.cos((Math.PI/180) * i)*(Math.sqrt(Math.sqrt(r))*x);
	}
	
	public String convertDate(String datestr  , int r){
		String value ;
		try {
			if(r<=1){
				value = toFormat.format(dateFormat.parse(datestr)) ;
			}else if(r>=2){
				value = toFormat2.format(dateFormat2.parse(datestr)) ;
			}else{
				value = String.valueOf(datestr) ;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			value = String.valueOf(datestr) ;
		}
		return value ;
	}
	public String replace(String value){
		return value.replaceAll("\\pP|\\pS", "") ;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public List<?> getDataList() {
		return dataList;
	}
	public void setDataList(List<?> dataList) {
		this.dataList = dataList;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public FacetField getFacet() {
		return facet;
	}

	public void setFacet(FacetField facet) {
		this.facet = facet;
	}

	public SolrDocument getDoc() {
		return doc;
	}

	public void setDoc(SolrDocument doc) {
		this.doc = doc;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}


	public long getDocNum() {
		return docNum;
	}

	public void setDocNum(long docNum) {
		this.docNum = docNum;
	}

	
	public List<?> getValueList() {
		return valueList;
	}

	public void setValueList(List<?> valueList) {
		this.valueList = valueList;
	}

	public Map getValues() {
		return values;
	}

	public void setValues(Map values) {
		this.values = values;
	}

	public FieldStatsInfo getStats() {
		return stats;
	}

	public void setStats(FieldStatsInfo stats) {
		this.stats = stats;
	}

	public RequestData getRqdata() {
		return rqdata;
	}

	public void setRqdata(RequestData rqdata) {
		this.rqdata = rqdata;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public boolean isThrowError() {
		return throwError;
	}

	public void setThrowError(boolean throwError) {
		this.throwError = throwError;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Exception getEx() {
		return ex;
	}

	public void setEx(Exception ex) {
		this.ex = ex;
	}

	public int getP() {
		return p;
	}

	public void setP(int p) {
		this.p = p;
	}
}
