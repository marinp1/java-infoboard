package fi.patrikmarin.infoboard.sensors;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import fi.patrikmarin.infoboard.utils.LogLevel;
import fi.patrikmarin.infoboard.utils.Logger;


//TODO: This should be probably be on its own thread
/**
 * Represents the temperature sensor on the Pi.
 * Sensor classes should probably be combined when there more
 * are implemented.
 */
public class TemperatureSensor {
	
	// Path to the temperature device file
	private static final String DEVICE_FILE_PATH = "/sys/bus/w1/devices/28-800000267724/w1_slave";
	
	// Flag to tell if the sensor works
	private static boolean status = false;
	
	/**
	 * Getter for status parameter.
	 * @return if the temperature sensor is readable.
	 */
	public static boolean getStatus() {
		return status;
	}
	
	// Initialize the program on start
	static {
		init();
	}
	
	/**
	 * Initializes the temperature sensor in Raspberry Pi.
	 */
	private static void init() {
		try {
			Runtime.getRuntime().exec("modprobe w1-gpio");
			Runtime.getRuntime().exec("modprobe w1-therm");
			status = true;
		} catch (Exception e) {
			Logger.log(LogLevel.ERROR, "Couldn't initialise temperature sensor.");
		}
	}

	
	/**
	 * Reads the sensor data from the device file.
	 * @return the lines from device file in an array list.
	 */
	private static ArrayList<String> readData() {
		try (BufferedReader br = new BufferedReader(new FileReader(new File(DEVICE_FILE_PATH)))) {
		    ArrayList<String> lines = new ArrayList<String>();
		    
		    String line = br.readLine();
		    
		    while (line != null) {
		    	lines.add(line);
		    	line = br.readLine();
		    }
		    
		    return lines;
		
		// On error disable the sensor
		} catch (FileNotFoundException e) {
			Logger.log(LogLevel.WARNING, "Device file not found.");
			status = false;
		} catch (IOException e) {
			Logger.log(LogLevel.WARNING, "Device file couldn't be read.");
			status = false;
		}
		
		return null;
	}
	
	/**
	 * Returns the temperature value from the sensor.
	 * Try at most 2 seconds before quitting.
	 * @return the temperature in Celsius, DOUBLE_MIN_VALUE if unsuccessful.
	 * @throws InterruptedException 
	 */
	public static double updateValue() throws InterruptedException {
		
		// If device is functioning correctly
		if (getStatus()) {
			ArrayList<String> lines = readData();
			int tryNumber = 0;
			
			// If the data was read successfully
			if (lines != null) {
				// Check if data is correct for at most 10 times
				while (!lines.get(0).endsWith("YES") && tryNumber < 10) {
					Thread.sleep(200);
					lines = readData();
					tryNumber += 1;
				}
			}
			
			if (tryNumber > 10) {
				status = false;
				return Double.MIN_VALUE;
			}
			
			// Temperature starting position in the second line
			int indexPos = lines.get(1).lastIndexOf("t=");
			
			// If the temperature parameter exists
			if (indexPos != -1) {
				return Double.parseDouble(lines.get(1).substring(indexPos + 2)) / 1000.0;
			}

		}

		return Double.MIN_VALUE;
	}
}
