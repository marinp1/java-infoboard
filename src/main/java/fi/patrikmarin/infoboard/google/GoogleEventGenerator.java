package fi.patrikmarin.infoboard.google;

import java.util.ArrayList;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.tasks.Tasks;

public class GoogleEventGenerator {
	
	protected static ArrayList<GoogleEventContainer> getGoogleEventContainers(Calendar calendarService, Tasks taskService) {
		ArrayList<GoogleEventContainer> googleEventContainers = new ArrayList<GoogleEventContainer>();
		
		
		
		return googleEventContainers;
	}
	
	protected static ArrayList<GoogleCalendarEvent> getGoogleCalendarEvents(Calendar calendarService) {
		ArrayList<GoogleCalendarEvent> googleCalendarEvents = new ArrayList<GoogleCalendarEvent>();
		
		
		
		return googleCalendarEvents;
	}
	
	protected static ArrayList<GoogleTaskEvent> getGoogleCalendarEvents(Tasks taskService) {
		ArrayList<GoogleTaskEvent> googleTaskEvents = new ArrayList<GoogleTaskEvent>();
		
		
		
		return googleTaskEvents;
	}
}
