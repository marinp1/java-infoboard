package fi.patrikmarin.infoboard.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.HashMap;

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
		weatherMap.put("22_1", "\uf009 \uf009");
		weatherMap.put("23_1", "\uf009 \uf009 \uf009");
		weatherMap.put("21_2", "\uf029");
		weatherMap.put("22_2", "\uf029 \uf029");
		weatherMap.put("23_2", "\uf029 \uf029 \uf029");
		
		weatherMap.put("3_1", "\uf013");
		weatherMap.put("31_1", "\uf019");
		weatherMap.put("32_1", "\uf019 \uf019");
		weatherMap.put("33_1", "\uf019 \uf019 \uf019");
		weatherMap.put("3_2", "\uf013");
		weatherMap.put("31_2", "\uf019");
		weatherMap.put("32_2", "\uf019 \uf019");
		weatherMap.put("33_2", "\uf019 \uf019 \uf019");
		
		weatherMap.put("41_1", "\uf00a");
		weatherMap.put("42_1", "\uf00a \uf00a");
		weatherMap.put("43_1", "\uf00a \uf00a \uf00a");
		weatherMap.put("41_2", "\uf02a");
		weatherMap.put("42_2", "\uf02a \uf02a");
		weatherMap.put("43_2", "\uf02a \uf02a \uf02a");
		
		weatherMap.put("51_1", "\uf01b");
		weatherMap.put("52_1", "\uf01b \uf01b");
		weatherMap.put("53_1", "\uf01b \uf01b \uf01b");
		weatherMap.put("51_2", "\uf01b");
		weatherMap.put("52_2", "\uf01b \uf01b");
		weatherMap.put("53_2", "\uf01b \uf01b \uf01b");
		
		weatherMap.put("61_1", "\uf00e");
		weatherMap.put("62_1", "\uf00e \uf00e");
		weatherMap.put("61_2", "\uf02c");
		weatherMap.put("62_2", "\uf02c \uf02c");
		
		weatherMap.put("63_1", "\uf01e");
		weatherMap.put("64_1", "\uf01e \uf01e");	
		weatherMap.put("63_2", "\uf01e");
		weatherMap.put("64_2", "\uf01e \uf01e");	
		
		weatherMap.put("71_1", "\uf006");
		weatherMap.put("72_1", "\uf006 \uf006");
		weatherMap.put("73_1", "\uf006 \uf006 \uf006");
		weatherMap.put("71_2", "\uf034");
		weatherMap.put("72_2", "\uf034 \uf034");
		weatherMap.put("73_2", "\uf034 \uf034 \uf034");
		
		weatherMap.put("81_1", "\uf017");
		weatherMap.put("82_1", "\uf017 \uf017");
		weatherMap.put("83_1", "\uf017 \uf017");
		weatherMap.put("81_2", "\uf017");
		weatherMap.put("82_2", "\uf017 \uf017");
		weatherMap.put("83_2", "\uf017 \uf017");
		
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
	

	/**
	 * Gives appropriate icon for given wind speed,
	 * mapped to the Beaufort scale.
	 * 
	 * @param windSpeed the wind speed in meters / second
	 * @return the appropriate icon for the wind
	 */
	public static String beaufortIconMapper(double windSpeed) {
		if (windSpeed < 0.3) return "\uf0b7";
		if (windSpeed <= 1.5) return "\uf0b8";
		if (windSpeed <= 3.3) return "\uf0b9";
		if (windSpeed <= 5.5) return "\uf0ba";
		if (windSpeed <= 7.9) return "\uf0bb";
		if (windSpeed <= 10.7) return "\uf0bc";
		if (windSpeed <= 13.8) return "\uf0bd";
		if (windSpeed <= 17.9) return "\uf0be";
		if (windSpeed <= 20.7) return "\uf0bf";
		if (windSpeed <= 24.4) return "\uf0c0";
		if (windSpeed <= 28.4) return "\uf0c1";
		if (windSpeed <= 32.6) return "\uf0c2";
		return "\uf0c3";
	}
	
	// DATE TIME HELPERS
	
	public static ZonedDateTime parseGoogleDate(String s) {
		
		DateTimeFormatter gDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter gTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		DateTimeFormatter gTimeFormat2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		
		try {
			if (s.contains(":")) {
				
				// The parsed zonedDateTime
				ZonedDateTime result;
				
				// Total time offset from UTC
				long timeOffset = Calendar.getInstance().getTimeZone().getRawOffset() + Calendar.getInstance().getTimeZone().getDSTSavings();
				
				// Some events don't have specified timezone
				if (s.endsWith("Z")) {
					result = ZonedDateTime.parse(s, gTimeFormat2.withZone(ZoneId.systemDefault())).plus(timeOffset, ChronoUnit.MILLIS);
				} else {
	            	int finalInd = s.lastIndexOf(":");
	            	String s2 = s.substring(0, finalInd) + s.substring(finalInd + 1);
					result = ZonedDateTime.parse(s2, gTimeFormat);
				}
				
				return result;
				
			} else {
				return LocalDate.parse(s, gDateFormat).atStartOfDay().atZone(ZoneId.systemDefault());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String dateTimeDifference(ZonedDateTime start, ZonedDateTime end) {
		LocalDateTime tempDateTime = LocalDateTime.from(start);

		long years = tempDateTime.until(end, ChronoUnit.YEARS);
		tempDateTime = tempDateTime.plusYears( years );

		long months = tempDateTime.until(end, ChronoUnit.MONTHS);
		tempDateTime = tempDateTime.plusMonths( months );

		long days = tempDateTime.until(end, ChronoUnit.DAYS);
		tempDateTime = tempDateTime.plusDays( days );

		long hours = tempDateTime.until(end, ChronoUnit.HOURS);
		tempDateTime = tempDateTime.plusHours( hours );

		long minutes = tempDateTime.until(end, ChronoUnit.MINUTES);
		tempDateTime = tempDateTime.plusMinutes( minutes );

		long seconds = tempDateTime.until(end, ChronoUnit.SECONDS);
		
		String result = "";
		
		if (days == 1 && minutes == 0 && hours == 0) {
			return "All day";
		}
		
		if (days > 0) {
			result += days + "d ";
		}
		
		if (hours > 0) {
			result += hours + "h ";
		}
		
		if (minutes > 0) {
			result += minutes + "min";
		}
		
		return result.trim();
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
