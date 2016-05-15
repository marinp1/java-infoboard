package fi.patrikmarin.weather;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WeatherElementGenerator {
   public static ArrayList<WeatherEvent> generate() {
	   
	   ArrayList<WeatherEvent> weatherElements = new ArrayList<WeatherEvent>();

	    try {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		DefaultHandler handler = new DefaultHandler() {
			
			boolean paramName = false;
			boolean paramValue = false;
			boolean paramTime = false;
			
			boolean isTemperature = false;
			boolean isSymbol = false;
			
			String currentID = null;
			
			WeatherEvent we = null;

		public void startElement(String uri, String localName,String qName, 
	                Attributes attributes) throws SAXException {
			
			/*
			if (qName.equalsIgnoreCase("wfs:FeatureCollection")) {
				ts = true;
			}
			*/
			
			if (qName.equalsIgnoreCase("BsWfs:BsWfsElement")) {
				
				String id = attributes.getValue("gml:id");
				id = id.substring(0, id.lastIndexOf("."));
				
				if ((currentID == null || !id.equals(currentID)) && id != null) {
					currentID = id;
					if (we != null) weatherElements.add(we);
					we = new WeatherEvent();
				}
			}

			if (qName.equalsIgnoreCase("BsWfs:ParameterName")) {
				paramName = true;
			}

			if (qName.equalsIgnoreCase("BsWfs:ParameterValue")) {
				paramValue = true;
			}

			if (qName.equalsIgnoreCase("BsWfs:Time")) {
				paramTime = true;
			}

		}

		public void endElement(String uri, String localName,
			String qName) throws SAXException {
		}

		public void characters(char ch[], int start, int length) throws SAXException {
			
			if (paramTime) {
				if (we != null) {
					we.setDateTime(new String(ch, start, length));
				}
				paramTime = false;
			}
			
			if (paramName) {
				String val = new String(ch, start, length);
				if (val.equals("Temperature")) {
					isTemperature = true;
				} else if(val.startsWith("WeatherSymbol")) {
					isSymbol = true;
				}
				paramName = false;
			}
			
			if (paramValue) {
				if (we != null && isTemperature) {
					we.setTemperature(Double.parseDouble(new String(ch, start, length)));
					isTemperature = false;
				} else if (we != null && isSymbol) {
					we.setSymbol((int)Double.parseDouble(new String(ch, start, length)));
					isSymbol = false;
				}
				paramValue = false;
			}
		}

	     };

	       saxParser.parse(WeatherService.FILE_LOCATION, handler);
	 
	     } catch (Exception e) {
	       e.printStackTrace();
	     }
	    
	    return weatherElements;
	  
	   }
}
