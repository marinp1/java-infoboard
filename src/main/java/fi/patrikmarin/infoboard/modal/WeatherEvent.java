package fi.patrikmarin.infoboard.modal;

import java.time.LocalDateTime;
import static fi.patrikmarin.infoboard.Utils.*;

import fi.patrikmarin.infoboard.App;

/**
 * Represents a weather forecast event at given hour.
 */
public class WeatherEvent {
	private LocalDateTime time;
	private Integer temperature;
	private String iconContent;
	
	/**
	 * Constructor for the class.
	 * @param time the time for the forecast.
	 * @param temperature the forecasted temperature
	 * @param weatherIcon the weather icon ID for the forecast
	 */
	public WeatherEvent(LocalDateTime time, Integer temperature, Integer weatherIcon) {
		this.time = time;
		this.temperature = temperature;
		
		// Day has suffix _1, night has suffix _2.
		String weatherKey = String.valueOf(weatherIcon) + "_1";
		if (this.time.isAfter(App.sunset) || this.time.isBefore(App.sunrise)) {
			weatherKey = String.valueOf(weatherIcon) + "_2";
		}
		
		this.iconContent = weatherMap.get(weatherKey);
	}

	public LocalDateTime getTime() {
		return time;
	}
	
	public void setTime(LocalDateTime time) {
		this.time = time;
	}
	
	public Integer getTemperature() {
		return temperature;
	}
	
	public void setTemperature(Integer temperature) {
		this.temperature = temperature;
	}

	public String getIconContent() {
		return iconContent;
	}
	
	public void setIconContent(String iconContent) {
		this.iconContent = iconContent;
	}
}
