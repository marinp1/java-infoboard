package fi.patrikmarin.infoboard.weather;

import static fi.patrikmarin.infoboard.utils.Utils.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.ArrayList;

import fi.patrikmarin.infoboard.Keys;
import fi.patrikmarin.infoboard.Parameters;
import fi.patrikmarin.infoboard.utils.LogLevel;
import fi.patrikmarin.infoboard.utils.Logger;

public class WeatherService {
	
	private static String WEATHER_DATA_LOCATION = "target/data";
	private static String WEATHER_DATA_FILENAME = "weatherdata.xml";
	
	private static String WEATHER_DATA_FILELOCATION = WEATHER_DATA_LOCATION + File.separator + WEATHER_DATA_FILENAME;
	
	// TODO Read the location from FMI result
	private static Double LOCATION_LAT = 60.18395;
	private static Double LOCATION_LNG = 24.82786;
	
	private static String FMI_QUERY_ID = "fmi::forecast::hirlam::surface::point::simple";
	
	private static String SERVER_URL = "http://data.fmi.fi/fmi-apikey/" + Keys.FMI_API_KEY + "/wfs";
	
	private static String FETCH_URL = SERVER_URL + "?request=getFeature&storedquery_id=" + FMI_QUERY_ID + "&place=" + Parameters.FORECAST_LOCATION;
	
	/**
	 * Gets latest weather forecast for location using
	 * FMI's API and saves that forecast to a file.
	 * After that calls the generator method and returns
	 * generated WeatherEvents.
	 * @return the generated WeatherEvents
	 */
	public static ArrayList<WeatherEvent> getWeatherForecast() {
		
		Logger.log(LogLevel.INFO, "Trying to get data at http://data.fmi.fi with query " + FMI_QUERY_ID + " for " + Parameters.FORECAST_LOCATION);
		
		ArrayList<WeatherEvent> weatherEvents = new ArrayList<WeatherEvent>();
		
		try {
			// Make file instances of the data folder and the data file
			File dir = new File(WEATHER_DATA_LOCATION);
			File file = new File(WEATHER_DATA_FILELOCATION);

			// Initiate the folder and file if they don't exist
			if (!dir.exists()) {
				dir.mkdirs();
				if (!file.exists()) file.createNewFile();
			}
			
			// Open the generated URL
			URL url = new URL(FETCH_URL);
			URLConnection conn = url.openConnection();

			// Create reader for the URL content
			BufferedReader br = new BufferedReader(
                               new InputStreamReader(conn.getInputStream()));
			
			// Write to URL content to a file
			BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
			String inputLine;
			
			while ((inputLine = br.readLine()) != null) {
				bw.write(inputLine);
			}

			// Close the writer and the reader
			bw.close();
			br.close();

			Logger.log(LogLevel.INFO, "Forecast saved to " + WEATHER_DATA_FILELOCATION + ".");
			
			weatherEvents = WeatherEventGenerator.generateWeatherEvents(WEATHER_DATA_FILELOCATION);
			
			Logger.log(LogLevel.INFO, "Weather elements generated successfully.");

		} catch (NoElementsFoundException e) {
			Logger.log(LogLevel.WARNING, e.getMessage());
		} catch (Exception e) {
			Logger.log(LogLevel.ERROR, e.getMessage());
			e.printStackTrace();
		}
		
		return weatherEvents;
	}
	
	/**
	 * Calls the sunrise or sunset calculator.
	 * @param resultType whether to calculate sunrise or sunset
	 * @return the last sunrise or the next sunset
	 */
	public static LocalDateTime getSunriseSet(SolarCalculatorResult resultType) {
		Logger.log(LogLevel.INFO, "Calculating " + resultType.name() + ".");
		return SolarCalculator.calculate(LOCATION_LAT, LOCATION_LNG, resultType, null);
	}
}
