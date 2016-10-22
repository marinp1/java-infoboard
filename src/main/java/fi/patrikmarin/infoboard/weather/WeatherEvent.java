package fi.patrikmarin.infoboard.weather;

import java.time.LocalDateTime;

import fi.patrikmarin.infoboard.App;
import fi.patrikmarin.infoboard.utils.Utils;

/**
 * Represents a weather forecast event at given hour.
 */
public class WeatherEvent {
	private LocalDateTime time;
	private Integer temperature;
	private String iconContent;
	private Double humidity;
	private Double windSpeed;
	private Double pressure;
	
	/**
	 * Constructor for the class.
	 * @param time the time for the forecast.
	 * @param temperature the forecasted temperature
	 * @param weatherIcon the weather icon ID for the forecast
	 */
	public WeatherEvent(LocalDateTime time, Double temperature, Double weatherIcon) {
		this.temperature = temperature.intValue();
		this.iconContent = Utils.mapWeatherIDToContent(weatherIcon, (this.time.isAfter(App.sunset) || this.time.isBefore(App.sunrise)));
	}
	
	/**
	 * Constructor for the class with just the time.
	 * @param time the time for the forecast.
	 */
	public WeatherEvent(LocalDateTime time) {
		this.time = time;
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
	
	public void setTemperature(Double temperature) {
		this.temperature = temperature.intValue();
	}

	public String getIconContent() {
		return iconContent;
	}
	
	public void setIconContent(Double weatherIcon) {
		this.iconContent = Utils.mapWeatherIDToContent(weatherIcon, (this.time.isAfter(App.sunset) || this.time.isBefore(App.sunrise)));
	}
	
	public Double getHumidity() {
		return humidity;
	}
	
	public void setHumidity(Double humidity) {
		this.humidity = humidity;
	}
	
	public double getWindSpeed() {
		return windSpeed;
	}
	
	public void setWindSpeed(Double windSpeed) {
		this.windSpeed = windSpeed;
	}
	
	public double getPressure() {
		return pressure;
	}
	
	public void setPressure(Double pressure) {
		this.pressure = pressure;
	}
}
