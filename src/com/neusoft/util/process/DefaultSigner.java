package com.neusoft.util.process;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
/**
 * 
 * @author ranweng.hjk@taobao.com
 * 
 */
public class DefaultSigner implements Signer {
	private static final String ALGORITHM = "MD5WithRSA";
	protected static Logger logger = Logger.getLogger("Pigeon_Main");


	public String sign(byte[] data, PrivateKey priKey) {
		try {
			Signature signature = Signature.getInstance(ALGORITHM);
			signature.initSign(priKey);
			signature.update(data);
			return Base64.encodeBase64URLSafeString(signature.sign());
		} catch (Exception e) {
			logger.error("sign message error", e);
			return null;
		}
	}

	public boolean verify(byte[] data, String sign, PublicKey pubKey) {
		try {
			Signature signature = Signature.getInstance(ALGORITHM);
			signature.initVerify(pubKey);
			signature.update(data);
			return signature.verify(Base64.decodeBase64(sign));
		} catch (Exception e) {
			logger.error("verify message error", e);
			return false;
		}
	}
	
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String data="<Response><TagsList><Tags  type=\"vehicleInfo\"><Tag><Definition name=\"type\">text</Definition><Definition name=\"key\">vehicleModelName</Definition><Definition name=\"label\">品牌型号</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"><![CDATA[http://114.251.230.13:7002/Partner/netVehicleModel.action]]></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">hidden</Definition><Definition name=\"key\">vehicleId</Definition><Definition name=\"label\">车辆代码</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">text</Definition><Definition name=\"key\">engineNo</Definition><Definition name=\"label\">发动机号</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">text</Definition><Definition name=\"key\">vehicleFrameNo</Definition><Definition name=\"label\">车架号</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">date</Definition><Definition name=\"key\">firstRegisterDate</Definition><Definition name=\"label\">注册登记日期</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">radio</Definition><Definition name=\"key\">specialCarFlag</Definition><Definition name=\"label\">是否过户车：</Definition><Definition name=\"value\">0</Definition><Definition name=\"data\"><![CDATA[是:1;否:0]]></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">date</Definition><Definition name=\"label\">过户日期</Definition><Definition name=\"key\">specialCarDate</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">text</Definition><Definition name=\"key\">ownerName</Definition><Definition name=\"label\">车主</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">text</Definition><Definition name=\"key\">ownerIdNo</Definition><Definition name=\"label\">身份证号码</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag></Tags></TagsList></Response>";
		
		
		//"<Response><TagsList><Tags  type=\"vehicleInfo\"><Tag><Definition name=\"type\">text</Definition><Definition name=\"key\">vehicleModelName</Definition><Definition name=\"label\">??????</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"><![CDATA[http://114.251.230.13:7002/Partner/netVehicleModel.action]]></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">hidden</Definition><Definition name=\"key\">vehicleId</Definition><Definition name=\"label\">????????</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">text</Definition><Definition name=\"key\">engineNo</Definition><Definition name=\"label\">???????</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">text</Definition><Definition name=\"key\">vehicleFrameNo</Definition><Definition name=\"label\">?????</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">date</Definition><Definition name=\"key\">firstRegisterDate</Definition><Definition name=\"label\">?????????</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">radio</Definition><Definition name=\"key\">specialCarFlag</Definition><Definition name=\"label\">?????</Definition><Definition name=\"value\">0</Definition><Definition name=\"data\"><![CDATA[??:1;??:0]]></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">date</Definition><Definition name=\"label\">??????</Definition><Definition name=\"key\">specialCarDate</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">text</Definition><Definition name=\"key\">ownerName</Definition><Definition name=\"label\">????</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag><Tag><Definition name=\"type\">text</Definition><Definition name=\"key\">ownerIdNo</Definition><Definition name=\"label\">????????</Definition><Definition name=\"value\"></Definition><Definition name=\"dataUrl\"></Definition><Definition name=\"checkUrl\"></Definition></Tag></Tags></TagsList></Response>";
	
	String key="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCpi714LS6BTL2xVPy7tAGPd2muIebSHrKTLqQ2mmUXjaEfHftIH1-slYQbtfTzimX7LO5VJGZtBohOfhMY-YSGEiMFJAmmHefX0SX6dY80FpC_Wgf-l0FVn4NQ5HcIXYMONAF7HXnhSJfgJ4Rp1x7NP1-0mLnhuHWCv1zYtw9gjwIDAQAB";
	
	String sign ="g60kcXa2a6lIvGiDI1LoLJSnu8T2HH-LJn2QQWaMXBfJtMouskzSCkoc8MI5sSLAongHKmeRUI35kHSG2TAswmtv-xx4NVnD-Na9izZ4KnJFZlvufF13h-iTPxI3Fk80dBj3q55oDaggst8DdyfHmcnc-b2dO1VtqfpPHfXTr_Q";
	
	DefaultSigner ds = new DefaultSigner();
	
	System.out.println(ds.verify(data.getBytes("GBK"), sign, new KeyPairer().getPublicKey(key)));
		
		
	}

}
