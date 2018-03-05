package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
	
	public static Date stringToDate(String s) throws ParseException{
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date convertedDate = format.parse(s);
		return convertedDate;
	}
	
	public static String dateToString(Date d){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");       
		String convertedDate = df.format(d);
		return convertedDate;
	}

	public static String readFile(String path) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, StandardCharsets.UTF_8);
	}
	
	public static String getImagesFilePath(){
		String filePath = System.getProperty("catalina.base");
		filePath = filePath + File.separator + "webapps" + File.separator + "WPSIIT2017" + File.separator + "images" + File.separator + "users" + File.separator;
		return filePath;
	}
	
	public static void saveToFile(InputStream uploadedInputStream, String uploadedFileLocation) {
	    try {
	        OutputStream out = null;
	        int read = 0;
	        byte[] bytes = new byte[1024];
	        out = new FileOutputStream(new File(uploadedFileLocation));
	        while ((read = uploadedInputStream.read(bytes)) != -1) {
	            out.write(bytes, 0, read);
	        }
	        out.flush();
	        out.close();
	    } catch (IOException e) {

	        e.printStackTrace();
	    }
	}
}
