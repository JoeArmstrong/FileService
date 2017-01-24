package org.armstrongonline.fileservice.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.web.multipart.MultipartFile;

public class FileService {
	
	private final String storagePath = "/data/";

	public String saveFile(  MultipartFile file, Long id) {
		OutputStream os = null;
		String filtered = filterFilename(file.getOriginalFilename());
		System.out.println("Saving file " + filtered + " for id " + id);
		String filename = id.toString() + "-" + filtered;
		
		try {
			InputStream iStr = file.getInputStream();
			os = new FileOutputStream(filePathFromId(filename));
	        int read = 0;
	        byte[] bytes = new byte[1024];

	        while ((read = iStr.read(bytes)) != -1) {
	            os.write(bytes, 0, read);
	        }
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
	        
	        if(os != null) {
	            try {
	                os.close();
	            } catch (IOException e) {
	            	e.printStackTrace();
	            	return null;
	            }
	        }
	    }
		
		return filename;
	}
	
	public String filePathFromId( String filename ) {
		return storagePath + filename;
	}
	
	private String filterFilename(String filename) {
		return filename.replaceAll("[^a-zA-Z0-9.]", "");
	}
	
}
