package fi.patrikmarin.data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateParser {
	public static LocalDate parseDate(String s) {
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("ddMMyyyy HH:mm");
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("ddMMyyyy");
		
		try {
			if (s.contains(":")) {
				return LocalDate.parse(s, timeFormat);
			} else {
				return LocalDate.parse(s, dateFormat);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static LocalDateTime parseGDate(String s) {
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
	
	public static DateDifference timeBetween(LocalDateTime dt1, LocalDateTime dt2) {
		LocalDateTime tempDateTime = LocalDateTime.from(dt1);

		long years = tempDateTime.until(dt2, ChronoUnit.YEARS);
		tempDateTime = tempDateTime.plusYears( years );

		long months = tempDateTime.until(dt2, ChronoUnit.MONTHS);
		tempDateTime = tempDateTime.plusMonths( months );

		long days = tempDateTime.until(dt2, ChronoUnit.DAYS);
		tempDateTime = tempDateTime.plusDays( days );

		long hours = tempDateTime.until(dt2, ChronoUnit.HOURS);
		tempDateTime = tempDateTime.plusHours( hours );

		long minutes = tempDateTime.until(dt2, ChronoUnit.MINUTES);
		tempDateTime = tempDateTime.plusMinutes( minutes );

		long seconds = tempDateTime.until(dt2, ChronoUnit.SECONDS);
		
		return new DateDifference(years, months, days, hours, minutes, seconds);
	}
}