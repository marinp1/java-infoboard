package fi.patrikmarin.weather;

import fi.patrikmarin.data.DateParser;
import fi.patrikmarin.infoboard.Infoboard;

import java.time.LocalDate;

public class WeatherEvent {
	private Double temperature;
	private String dateTime;
	private Integer symbol;
	private Boolean isDark;
	
	protected void setTemperature(Double d) {
		temperature = d;
	}
	
	public Double getTemperature() {
		return temperature;
	}
	
	protected void setDateTime(String s) {
		dateTime = s;
	}
	
	public String getDateTime() {
		return dateTime;
	}
	
	protected void setSymbol(Integer s) {
		symbol = s;
		LocalDate sunrise = DateParser.parseDate(Infoboard.ws.getSunrise());
		LocalDate sunset = DateParser.parseDate(Infoboard.ws.getSunset());
		LocalDate now = LocalDate.now();
		
		if (sunrise.isAfter(now) || sunset.isBefore(now)) {
			isDark = true;
		} else {
			isDark = false;
		}
	}
	
	public Integer getSymbol() {
		return symbol;
	}
	
	public Integer getDayNight() {
		return (isDark) ? 1 : 0;
	}
	
	@Override
	public String toString() {
		return dateTime + " - " + temperature + " - " + symbol;
	}
}
