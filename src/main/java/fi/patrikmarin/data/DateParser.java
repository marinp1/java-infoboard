package fi.patrikmarin.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
}