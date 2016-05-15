package fi.patrikmarin.google;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.scene.paint.Color;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.ColorDefinition;
import com.google.api.services.calendar.model.Colors;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import fi.patrikmarin.google.GoogleService;

public class CalendarService {
	
	private HashMap<String, Color> colorKeys = new HashMap<String, Color>();
	public ArrayList<GCalendar> calendars = new ArrayList<GCalendar>();
	private com.google.api.services.calendar.Calendar service;
	
	public CalendarService() throws IOException {
		service = getCalendarService();
		getCalendarColors();
		getCalendars();
	}
	
    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public com.google.api.services.calendar.Calendar getCalendarService() throws IOException {
        Credential credential = GoogleService.authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
        		GoogleService.HTTP_TRANSPORT, GoogleService.JSON_FACTORY, credential)
                .setApplicationName(GoogleService.APPLICATION_NAME)
                .build();
    }
    
    
    private void getCalendarColors() throws IOException {
        Colors colors = service.colors().get().execute();
        
        colorKeys = new HashMap<String, Color>();

        for (HashMap.Entry<String, ColorDefinition> color : colors.getCalendar().entrySet()) {
        	String colID = color.getKey();
        	String bg = color.getValue().getBackground();
        	//String fg = color.getValue().getForeground();
        	
        	colorKeys.put(colID, Color.web(bg));
        }
    }
    
    private void getCalendars() throws IOException {
    	calendars = new ArrayList<GCalendar>();
    	
    	String pageToken = null;
    	
    	do {
    		CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
    		List<CalendarListEntry> items = calendarList.getItems();
    		
    			for (CalendarListEntry calendarListEntry : items) {
    				GCalendar gcal = new GCalendar();
    				
    				if (calendarListEntry.getSummaryOverride() == null) {
    					gcal.setName(calendarListEntry.getSummary());
    				} else {
    					gcal.setName(calendarListEntry.getSummaryOverride());
    				}
    				
    				gcal.setColor(colorKeys.get(calendarListEntry.getColorId()));
    				gcal.setID(calendarListEntry.getId());
    				
    				calendars.add(gcal);
            	  }
    			
    		pageToken = calendarList.getNextPageToken();
    		
    	} while(pageToken != null);
    }
}
