package fi.patrikmarin.google;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

import fi.patrikmarin.data.SettingsService;
import fi.patrikmarin.google.GoogleService;

public class CalendarService {
	
	private HashMap<String, Color> calendarColorKeys = new HashMap<String, Color>();
	private HashMap<String, Color> eventColorKeys = new HashMap<String, Color>();
	
	public static ArrayList<GCalendar> calendars = new ArrayList<GCalendar>();
	public static ArrayList<GCalendarEvent> events = new ArrayList<GCalendarEvent>();
	
	private com.google.api.services.calendar.Calendar service;
	
	public CalendarService() {
		try {
			service = getCalendarService();
			update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
    private com.google.api.services.calendar.Calendar getCalendarService() throws IOException {
        Credential credential = GoogleService.authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
        		GoogleService.HTTP_TRANSPORT, GoogleService.JSON_FACTORY, credential)
                .setApplicationName(GoogleService.APPLICATION_NAME)
                .build();
    }
    
    
    private void getColors() throws IOException {
        Colors colors = service.colors().get().execute();
        
        calendarColorKeys = new HashMap<String, Color>();

        for (HashMap.Entry<String, ColorDefinition> color : colors.getCalendar().entrySet()) {
        	String colID = color.getKey();
        	String bg = color.getValue().getBackground();
        	//String fg = color.getValue().getForeground();
        	
        	calendarColorKeys.put(colID, Color.web(bg));
        }
        
        eventColorKeys = new HashMap<String, Color>();
        
        for (HashMap.Entry<String, ColorDefinition> color : colors.getEvent().entrySet()) {
        	String colID = color.getKey();
        	String bg = color.getValue().getBackground();
        	//String fg = color.getValue().getForeground();
        	
        	eventColorKeys.put(colID, Color.web(bg));
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
    				
    				gcal.setColor(calendarColorKeys.get(calendarListEntry.getColorId()));
    				gcal.setID(calendarListEntry.getId());
    				
    		    	if (SettingsService.calendarSettings.containsKey(calendarListEntry.getId())) {
    		    		gcal.setEnabled(SettingsService.calendarSettings.get(calendarListEntry.getId()));
    		    	}
    				
    				calendars.add(gcal);
            	  }
    			
    		pageToken = calendarList.getNextPageToken();
    		
    	} while(pageToken != null);
    }
    
    private void getEvents() throws IOException {
    	events = new ArrayList<GCalendarEvent>();
    	
        DateTime now = new DateTime(System.currentTimeMillis());
        
        for (GCalendar cal : calendars) {
            Events ev = service.events().list(cal.getID())
                .setMaxResults(5)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
            
            List<Event> items = ev.getItems();
            
            if (items.size() > 0) {
                for (Event event : items) {
                	GCalendarEvent ce = new GCalendarEvent();
                	
                    DateTime start = event.getStart().getDateTime();
                    if (start == null) {
                        start = event.getStart().getDate();
                    }
                    
                    DateTime end = event.getEnd().getDateTime();
                    if (end == null) {
                    	end = event.getEnd().getDate();
                    }
                    
                    ce.setStart(start);
                    ce.setEnd(end);
                    ce.setDescription(event.getDescription());
                    ce.setID(event.getId());
                    ce.setParent(cal);
                    ce.setLocation(event.getLocation());
                    ce.setSummary(event.getSummary());
                    ce.setColor(eventColorKeys.get(event.getColorId()));
                    
                    events.add(ce);
                }
            }
        }
        
        Collections.sort(events);
    }
    
    public void update() {
    	try {
    		getColors();
    		getCalendars();
    		getEvents();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    }
}
