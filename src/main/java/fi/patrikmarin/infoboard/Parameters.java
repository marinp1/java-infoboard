package fi.patrikmarin.infoboard;

/**
 * Adjustable program parameters.
 * TODO: Load/save option
 */
public class Parameters {
	//====================== WEATHER PARAMETERS =====================================
	
	// Forecast location coordinates
	public static Double FORECAST_LOCATION_LAT = 60.190145;
	public static Double FORECAST_LOCATION_LNG = 24.8351184;
	// How many forecast elements are displayed
	public static Integer FORECAST_COUNT = 5;
	// Hours between displayed forecasts (minimum is 1)
	public static Integer FORECAST_SPACING = 2;
	
	//===================== CALENDAR PARAMETERS =====================================
	// How many events are displayed on the screen
	public static Integer EVENT_COUNT = 6;
}
