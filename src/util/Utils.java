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

import javax.servlet.ServletContext;

import dataRW.CommentsRW;
import dataRW.EmergencySituationsRW;
import dataRW.TerritoriesRW;
import dataRW.UsersRW;

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
	
	public static String getImagesFilePath(String path){
		return path + File.separator + "images" + File.separator + "users" + File.separator;
	}
	
	public static String getSituationsImagesFilePath(String path){
		return path + File.separator + "images" + File.separator + "emergency-situations" + File.separator;
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
	
	public static boolean checkString(String s){
		return s == null || s.trim().equals("") || s.contains(";") || s.contains(System.getProperty("line.separator"));
	}
	
	public static boolean checkReportFieldString(String s){
		return s == null || s.contains(";") || s.contains(System.getProperty("line.separator"));
	}
	
	public static UsersRW getUsersRW(ServletContext context){
		UsersRW users = (UsersRW)context.getAttribute("users");
		if(users == null){
			TerritoriesRW territories = getTerritoriesRW(context);
			users = new UsersRW(territories.getTerritories());
			users.readUsers(context.getRealPath(""));
			context.setAttribute("users", users);
		}
		return users;
	}
	
	public static TerritoriesRW getTerritoriesRW(ServletContext context){
		TerritoriesRW territories = (TerritoriesRW)context.getAttribute("territories");
		if(territories == null){
			territories = new TerritoriesRW();
			territories.readTerritories(context.getRealPath(""));
			context.setAttribute("territories", territories);
		}
		return territories;
	}
	
	public static EmergencySituationsRW getEmergencySituationsRW(ServletContext context){
		EmergencySituationsRW situations = (EmergencySituationsRW)context.getAttribute("situations");
		if(situations == null){
			UsersRW users = getUsersRW(context);
			TerritoriesRW territories = getTerritoriesRW(context);
			situations = new EmergencySituationsRW(users.getUsers(), territories.getTerritories());
			situations.readEmergencySituations(context.getRealPath(""));
			context.setAttribute("situations", situations);
		}
		return situations;
	}
	
	public static CommentsRW getCommentsRW(ServletContext context){
		CommentsRW comments = (CommentsRW)context.getAttribute("comments");
		if(comments == null){
			UsersRW users = getUsersRW(context);
			EmergencySituationsRW situations = getEmergencySituationsRW(context);
			comments = new CommentsRW(situations.getEmergencySituations(), users.getUsers());
			comments.readComments(context.getRealPath(""));
			context.setAttribute("comments", comments);
		}
		return comments;
	}
	
	public static String escapeString(String s){
		StringBuilder b = new StringBuilder();

	    for (char c : s.toCharArray()) {
	        if (c >= 128)
	            b.append("\\u").append(String.format("%04X", (int) c));
	        else
	            b.append(c);
	    }

	    return b.toString();
	}
}
