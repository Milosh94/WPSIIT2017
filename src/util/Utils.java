package util;

import java.io.IOException;
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
}
