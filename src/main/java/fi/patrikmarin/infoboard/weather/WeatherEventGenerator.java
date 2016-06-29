package fi.patrikmarin.infoboard.weather;

import static fi.patrikmarin.infoboard.utils.Parameters.*;
import static fi.patrikmarin.infoboard.utils.Utils.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fi.patrikmarin.infoboard.utils.Utils;

/**
 * Generates WeatherEvent objects
 * from downloaded forecast file.
 */
public class WeatherEventGenerator {

	protected static ArrayList<WeatherEvent> generateWeatherEvents(String fileLocation) throws NoElementsFoundException, IOException, SAXException, ParserConfigurationException, XPathExpressionException {
		
		ArrayList<WeatherEvent> weatherEvents = new ArrayList<WeatherEvent>();
		
		// Create xml document factory
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        
        // Parse and read the XML file
		DocumentBuilder builder = factory.newDocumentBuilder();
		FileInputStream fis = new FileInputStream(fileLocation);
		Document doc = builder.parse(fis);
		fis.close();
		
		// Initiate the XPath
		XPathFactory xPathfactory = XPathFactory.newInstance();
		XPath xpath = xPathfactory.newXPath();
		
		// XPath to get all time elements
		String timeXPATH = "//*[local-name()='Time']";
		XPathExpression expr = xpath.compile(timeXPATH);
		
		// Get list of time elements
		NodeList times = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		
		// Add all different time strings to the sorted set
		SortedSet<String> timeValues = new TreeSet<String>();
		for (int i = 0; i < times.getLength(); i++) {
			timeValues.add(times.item(i).getTextContent());
		}
		
		String[] timeArray = Arrays.copyOf(timeValues.toArray(), timeValues.size(), String[].class);
		
		// Loop through all times and generate weatherEvents
		for (Integer t = 0; t < FORECAST_COUNT * FORECAST_SPACING; t+= FORECAST_SPACING) {
			String time = timeArray[t];
			// XPath to get the main element
			String elementXPath = "//*[local-name()='BsWfsElement'][*[local-name()='Time']='" + time + "']";
			XPathExpression elementExpr = xpath.compile(elementXPath);
			
			// The nodes from the XPath
			NodeList elements = (NodeList) elementExpr.evaluate(doc, XPathConstants.NODESET);
			
			LocalDateTime dateTime = LocalDateTime.parse(time, Utils.fmiDateTimeFormat);
			dateTime = dateTime.plusSeconds((TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings()) / 1000);
			
			WeatherEvent weatherEvent = new WeatherEvent(dateTime);
			
			// Loop through each element for time instance
			for (int i = 0; i < elements.getLength(); i++) {
			
				// Create XPaths for name and value
				String parameterNameXPath = "./*[local-name()='ParameterName']/text()";
				String parameterValueXPath = "./*[local-name()='ParameterValue']/text()";
				
				// Find name and value
				String parameterName = (String) xpath.compile(parameterNameXPath).evaluate(elements.item(i), XPathConstants.STRING);
				String parameterValue = (String) xpath.compile(parameterValueXPath).evaluate(elements.item(i), XPathConstants.STRING);
				
				// Handle different parameters differently
				// TODO: Add pressure, wind and humidity?
				switch(parameterName) {
					case "Temperature": weatherEvent.setTemperature(Double.parseDouble(parameterValue));
										break;
										
					case "Pressure":	
										break;
										
					case "WindDirection":
										break;
										
					case "WindSpeedMS":
										break;
										
					case "Humidity":
										break;
										
					case "WeatherSymbol3":	weatherEvent.setIconContent(Double.parseDouble(parameterValue));
											break;
										
					default:
				}
				
			}
			
			weatherEvents.add(weatherEvent);
		}
		
		// Return results
		if (weatherEvents.isEmpty()) {
			throw new NoElementsFoundException("No weather elements were generated.");
		} else {
			return weatherEvents;
		}
	}
}
