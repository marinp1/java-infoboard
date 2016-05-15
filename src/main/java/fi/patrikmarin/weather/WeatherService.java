package fi.patrikmarin.weather;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TimeZone;

import fi.patrikmarin.data.Keys;

public class WeatherService {

	public static String FOLDER_LOCATION = "target/data";
	public static String FILE_NAME = "data.xml";
	
	public static String FILE_LOCATION = FOLDER_LOCATION + File.separator + FILE_NAME;
	
	String API_KEY = Keys.FMI_API_KEY;
	String LOCATION = "Kivenlahti,Espoo";
	
	String SERVER_URL = "http://data.fmi.fi/fmi-apikey/" + API_KEY + "/wfs";
	String QUERY_ID = "fmi::forecast::hirlam::surface::point::simple";
	
	String FETCH_URL = SERVER_URL + "?request=getFeature&storedquery_id=" + QUERY_ID + "&place=" + LOCATION;

	Double LAT = 60.18395;
	Double LNG = 24.82786;
	
	public ArrayList<WeatherEvent> weatherElements = null;
	Double minTemp = null;
	Double maxTemp = null;
	
	String sunrise = null;
	String sunset = null;
	
	public LocalDateTime lastUpdated = LocalDateTime.now();
	
	public void getData() {
		System.out.println("Trying to get data at http://data.fmi.fi with query " + QUERY_ID + " for " + LOCATION + ":");
		getSunRiseSet();
		
		try {
			URL url = new URL(FETCH_URL);
			URLConnection conn = url.openConnection();

			BufferedReader br = new BufferedReader(
                               new InputStreamReader(conn.getInputStream()));

			String inputLine;
			
			File dir = new File(FOLDER_LOCATION);

			File file = new File(FILE_LOCATION);

			if (!dir.exists()) {
				dir.mkdirs();
				if (!file.exists()) file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			while ((inputLine = br.readLine()) != null) {
				bw.write(inputLine);
			}

			bw.close();
			br.close();

			System.out.println("Forecast saved.");
			
			weatherElements = WeatherElementGenerator.generate();
			
			System.out.println("Elements generated.");
			
			getMinMax();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public WeatherEvent getCurrent() {
		return weatherElements.get(0);
	}
	
	public Double getMinTemp() {
		return minTemp;
	}
	
	public Double getMaxTemp() {
		return maxTemp;
	}
	
	public String getSunrise() {
		return sunrise;
	}
	
	public String getSunset() {
		return sunset;
	}
	
	private void getSunRiseSet() {
		SolarCalculator sc = new SolarCalculator();
		sunrise = sc.calculate(LAT, LNG, true);
		sunset = sc.calculate(LAT, LNG, false);
		
		System.out.println("Sunrise and sunset calculated.");
	}
	
	private void getMinMax() {
		Double min = Double.MAX_VALUE;
		Double max = Double.MIN_VALUE;
		
		LocalDateTime time = LocalDateTime.parse(weatherElements.get(0).getDateTime(), DateTimeFormatter.ISO_DATE_TIME);
		Integer offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
		time = time.plusSeconds(offset / 1000);
		
		for (WeatherEvent we : weatherElements) {
			LocalDateTime newTime = LocalDateTime.parse(we.getDateTime(), DateTimeFormatter.ISO_DATE_TIME);
			newTime = newTime.plusSeconds(offset / 1000);
			
			if (newTime.getDayOfYear() == time.getDayOfYear()) {
				Double temp = we.getTemperature();
				if (temp > max) {
					max = temp;
				}
				if (temp < min) {
					min = temp;
				}
			}
		}
		
		minTemp = min;
		maxTemp = max;
		
		System.out.println("Min and max temperatures defined.");
	}
}