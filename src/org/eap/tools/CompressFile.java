package org.eap.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipInputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import com.neusoft.core.EapDataContext;
import com.neusoft.tools.MD5;
import com.neusoft.tools.ZipData;

/**
 * 压缩文件
 * @author iceworld
 *
 */
public class CompressFile {
	/**
	 * 
	 * @param inputFile
	 * @param zipFilename
	 * @throws IOException
	 */
	public static void zip(InputStream input, String id , String fileName) throws IOException {
		FileOutputStream fileOutput = null ;
		File shareFile = new File(createDir(id , EapDataContext.SAVE_FILE_DIR),id);
        ZipOutputStream out = new ZipOutputStream(fileOutput = new FileOutputStream(shareFile));
        try {    
        	out.putNextEntry(new ZipEntry(fileName));   
        	int length = 0 ;    
            byte[] data = new byte[1024];    
            while ((length = input.read(data)) != -1) {    
                out.write(data, 0, length);    
            }    
        } catch (IOException e) {    
            e.printStackTrace();  
        } finally {    
            out.close();    
            if(fileOutput!=null){
            	fileOutput.close() ;
            }
        }    
        //上传服务器
        SCPFile scp = new SCPFile();
        scp.uploadFile(shareFile.getAbsolutePath());
    } 
	/**
	 * 
	 * @param inputFile
	 * @param zipFilename
	 * @throws IOException
	 */
	public static ZipData zip(ZipData zipData,InputStream input, String id , String fileName,String append) throws IOException {  
		if(zipData == null){
			zipData = new ZipData();
		}
		if(zipData.getZipOut()==null){
			FileOutputStream outputStream ;
			zipData.setZipOut(new ZipOutputStream(outputStream = new FileOutputStream(new File(createDir(id , EapDataContext.SAVE_FILE_DIR),id))));
			zipData.setOutput(outputStream) ;
		}
        try {    
        	zipData.setZipOut(zipData.getZipOut()) ;
        	ZipEntry entry = new ZipEntry(fileName) ;
        	zipData.getZipOut().putNextEntry(entry);   
        	int length = 0 ;    
            byte[] data = new byte[102400];    
            while ((length = input.read(data)) != -1) {    
            	zipData.getZipOut().write(data, 0, length);    
            }    
        } catch (IOException e) {    
            e.printStackTrace();  
        }
        return zipData ;
    }  
	/**
	 * 
	 * @param inputFile
	 * @param zipFilename
	 * @throws IOException
	 */
	public static ZipData zip(InputStream input, String id , String fileName , boolean ret) throws IOException {    
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(new File(createDir(id , EapDataContext.SAVE_FILE_DIR),id)));    
        try {    
        	out.putNextEntry(new ZipEntry(fileName));   
        	int length = 0 ;    
            byte[] data = new byte[1024];    
            while ((length = input.read(data)) != -1) {    
                out.write(data, 0, length);    
            }    
        } catch (IOException e) {    
            e.printStackTrace();  
        } finally {    
            out.close();    
        }   
        return unzip(id) ;
    }
	/**
	 * 
	 * @param inputFile
	 * @param zipFilename
	 * @throws IOException
	 */
	public static ZipData unzip(String id , int index) throws IOException {
		if(index<1) index =1 ;
		File zipDataFile = new File(createDir(id , EapDataContext.SAVE_FILE_DIR),id) ;
		ZipData zipData = new ZipData();
		ZipFile zipFile = null ;
		zipData.setId(id) ;
		ZipEntry zipEntry = null;   
		if(zipDataFile!=null && zipDataFile.exists()){
			zipFile = new ZipFile(zipDataFile);
			zipData.setZipFile(zipFile) ;
			Enumeration<?> en = zipFile.getEntries(); 
			int i = 1 ;
	        while(en.hasMoreElements()) {    
	            zipEntry = (ZipEntry) en.nextElement();  
	            zipData.setName(zipEntry.getName()) ;
	            zipData.setInput(zipFile.getInputStream(zipEntry)) ;
	            if(i++ == index) break ;
	        }
		}
        return zipData;
    }
	/**
	 * 
	 * @param inputFile
	 * @param zipFilename
	 * @throws IOException
	 */
	public static ZipData unzip(String id) throws IOException {
		File file = new File(EapDataContext.SAVE_FILE_DIR + File.separator + "temp");
		if(!file.exists()){
			file.mkdir();
		}
		SCPFile scpFile = new SCPFile();
		scpFile.downloadFile(id, file.getAbsolutePath());
		File zipDataFile = new File(file.getAbsolutePath(), id);
		ZipData zipData = new ZipData();
		ZipFile zipFile = null ;
		zipData.setId(id) ;
		ZipEntry zipEntry = null;   
		if(zipDataFile!=null && zipDataFile.exists()){
			zipFile = new ZipFile(zipDataFile);
			zipData.setZipFile(zipFile) ;
			Enumeration<?> en = zipFile.getEntries();    
	        if(en.hasMoreElements()) {    
	            zipEntry = (ZipEntry) en.nextElement();  
	            zipData.setName(zipEntry.getName()) ;
	            zipData.setInput(zipFile.getInputStream(zipEntry)) ;
	        }
		}
        return zipData;
    }  
	/**
	 * 
	 * @param inputFile
	 * @param zipFilename
	 * @throws IOException
	 */
	public static ZipData unzip(ZipData zipData,String id) throws IOException {
		File zipDataFile = new File(createDir(id , EapDataContext.SAVE_FILE_DIR),id) ;
		if(zipData==null){
			zipData = new ZipData();
		}
		zipData.setId(id) ;
		ZipEntry zipEntry = null;   
		if(zipDataFile!=null && zipDataFile.exists()){
			if(zipData.getZipFile() == null){
				zipData.setZipFile(new ZipFile(zipDataFile));
			}
			if(zipData.getEn()==null){
				zipData.setEn(zipData.getZipFile().getEntries());
			}
	        if(zipData.getEn().hasMoreElements()) {    
	            zipEntry = (ZipEntry) zipData.getEn().nextElement();  
	            zipData.setName(zipEntry.getName()) ;
	            zipData.setInput(zipData.getZipFile().getInputStream(zipEntry)) ;
	        }else{
	        	zipData.setInput(null) ;
	        }
		}
        return zipData;
    }  
	/**
	 * 创建文件目录
	 * @param id
	 * @param parent
	 * @return
	 */
	private static File createDir(String id , String parent){
		String path = MD5.encoding(id);
		File file = new File(parent , path.substring(0,1)) ;
		File subFile = new File(file , path.substring(8,9)) ;
		File subFile2 = new File(subFile , path.substring(16,18)) ;
		File subFile3 = new File(subFile2 , path.substring(24,26)) ;
		if(!subFile3.exists())
		{
			subFile3.mkdirs() ;
		}
		return subFile3;
	}
}
