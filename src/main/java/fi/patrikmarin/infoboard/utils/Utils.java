package fi.patrikmarin.infoboard.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import com.google.api.client.util.DateTime;

/**
 * Common utilities for the program.
 */
public class Utils {
    
	//==================== DATETIMEFORMATTERS ================================================
	public static DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
	public static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("E dd.MM.yyyy");
	public static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	public static DateTimeFormatter fmiDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
	public static DateTimeFormatter weekFormat = DateTimeFormatter.ofPattern("'week' w");
	
	//==================== WEATHER MAPPER ====================================================
	private static HashMap<String, String> weatherMap = new HashMap<String, String>(); 
	
	/**
	 * Fill the weather map with correct values.
	 * FMIs weather icon IDs are mapped to correct Weather Icons' content.
	 */
	static {
		weatherMap.put("1_1", "\uf00d");
		weatherMap.put("1_2", "\uf077");
		
		weatherMap.put("2_1", "\uf002");
		weatherMap.put("2_2", "\uf086");
		
		weatherMap.put("21_1", "\uf009");
		weatherMap.put("22_1", "\uf009\uf009");
		weatherMap.put("23_1", "\uf009\uf009\uf009");
		weatherMap.put("21_2", "\uf029");
		weatherMap.put("22_2", "\uf029\uf029");
		weatherMap.put("23_2", "\uf029\uf029\uf029");
		
		weatherMap.put("3_1", "\uf013");
		weatherMap.put("31_1", "\uf019");
		weatherMap.put("32_1", "\uf019\uf019");
		weatherMap.put("33_1", "\uf019\uf019\uf019");
		weatherMap.put("3_2", "\uf013");
		weatherMap.put("31_2", "\uf019");
		weatherMap.put("32_2", "\uf019\uf019");
		weatherMap.put("33_2", "\uf019\uf019\uf019");
		
		weatherMap.put("41_1", "\uf00a");
		weatherMap.put("42_1", "\uf00a\uf00a");
		weatherMap.put("43_1", "\uf00a\uf00a\uf00a");
		weatherMap.put("41_2", "\uf02a");
		weatherMap.put("42_2", "\uf02a\uf02a");
		weatherMap.put("43_2", "\uf02a\uf02a\uf02a");
		
		weatherMap.put("51_1", "\uf01b");
		weatherMap.put("52_1", "\uf01b\uf01b");
		weatherMap.put("53_1", "\uf01b\uf01b\uf01b");
		weatherMap.put("51_2", "\uf01b");
		weatherMap.put("52_2", "\uf01b\uf01b");
		weatherMap.put("53_2", "\uf01b\uf01b\uf01b");
		
		weatherMap.put("61_1", "\uf00e");
		weatherMap.put("62_1", "\uf00e\uf00e");
		weatherMap.put("61_2", "\uf02c");
		weatherMap.put("62_2", "\uf02c\uf02c");
		
		weatherMap.put("63_1", "\uf01e");
		weatherMap.put("64_1", "\uf01e\uf01e");	
		weatherMap.put("63_2", "\uf01e");
		weatherMap.put("64_2", "\uf01e\uf01e");	
		
		weatherMap.put("71_1", "\uf006");
		weatherMap.put("72_1", "\uf006\uf006");
		weatherMap.put("73_1", "\uf006\uf006\uf006");
		weatherMap.put("71_2", "\uf034");
		weatherMap.put("72_2", "\uf034\uf034");
		weatherMap.put("73_2", "\uf034\uf034\uf034");
		
		weatherMap.put("81_1", "\uf017");
		weatherMap.put("82_1", "\uf017\uf017");
		weatherMap.put("83_1", "\uf017\uf017");
		weatherMap.put("81_2", "\uf017");
		weatherMap.put("82_2", "\uf017\uf017");
		weatherMap.put("83_2", "\uf017\uf017");
		
		weatherMap.put("91_1", "\uf0b6");
		weatherMap.put("92_2", "\uf003");
		weatherMap.put("91_1", "\uf04a");
		weatherMap.put("92_2", "\uf014");
		
		weatherMap.put("sunrise", "\uf051");
		weatherMap.put("sunset", "\uf052");
		weatherMap.put("humidity", "\uf07a");
		weatherMap.put("wind", "\uf050");
		
	}
	
	/**
	 * Gives correct iconID content from the map.
	 * @param weatherIcon the weatherIconID from API
	 * @param night if the icon should be night specific
	 * @return the correct content for the ID
	 */
	public static String mapWeatherIDToContent(Double weatherIcon, Boolean night) {
		String weatherKey = String.valueOf(weatherIcon.intValue()) + "_1";
		
		if (night) {
			weatherKey = String.valueOf(weatherIcon.intValue()) + "_2";
		}
		
		return weatherMap.get(weatherKey);
	}
	
	// DATE TIME HELPERS
	
	public static LocalDateTime parseGoogleDate(String s) {
		DateTimeFormatter gDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter gTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		DateTimeFormatter gTimeFormat2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		
		try {
			if (s.contains(":")) {
				if (s.endsWith("Z")) {
					return LocalDateTime.parse(s, gTimeFormat2);
				} else {
	            	int finalInd = s.lastIndexOf(":");
	            	String s2 = s.substring(0, finalInd) + s.substring(finalInd + 1);
					
					return LocalDateTime.parse(s2, gTimeFormat);
				}
			} else {
				return LocalDate.parse(s, gDateFormat).atStartOfDay();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// CUSTOM EXCEPTIONS

	/**
	 * Custom exception for situations where no elements were
	 * found for whatever reason.
	 */
	public static class NoElementsFoundException extends Exception {

		public NoElementsFoundException() {

		}
		
		public NoElementsFoundException(String message)
		{
			super(message);
		}
	}
}
