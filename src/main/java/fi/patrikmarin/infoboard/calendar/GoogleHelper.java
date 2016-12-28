package fi.patrikmarin.infoboard.calendar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.ColorDefinition;
import com.google.api.services.calendar.model.Colors;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;

import javafx.scene.paint.Color;

public class GoogleHelper {
	
	protected static HashMap<String, Color> calendarColorKeys = new HashMap<String, Color>();
	protected static HashMap<String, Color> eventColorKeys = new HashMap<String, Color>();
	
	static void updateCalendarColorKeys(Calendar calendarService) throws IOException {
		Colors colors = calendarService.colors().get().execute();

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
	
	static ArrayList<GoogleCalendarEvent> getGoogleCalendarEvents(Calendar calendarService, CommonEventContainer container) throws IOException {		
		ArrayList<GoogleCalendarEvent> googleCalendarEvents = new ArrayList<GoogleCalendarEvent>();

		// Get at most 5 entries from the calendar
        com.google.api.services.calendar.model.Events ev = calendarService.events().list(container.getID())
                .setMaxResults(5)
                .setTimeMin(new DateTime(System.currentTimeMillis()))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        
        if (ev.getItems() != null) {
            
            List<Event> items = ev.getItems();
                
            // Add each event to the list
            for (Event event : items) {
            	
            	// If end time is not defined, use date
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                
                // If end time is not defined, use date
                DateTime end = event.getEnd().getDateTime();
                if (end == null) {
                	end = event.getEnd().getDate();
                }
                
                // Construct new event
                GoogleCalendarEvent cEvent = new GoogleCalendarEvent(
                		event.getId(),
                		container,
                		event.getSummary(),
                		event.getLocation(),
                		event.getDescription(),
                		eventColorKeys.get(event.getColorId()),
                		start,
                		end
                		);
                
                
                googleCalendarEvents.add(cEvent);
            }
        }
		
		return googleCalendarEvents;
	}

	static ArrayList<GoogleTaskEvent> getGoogleTaskEvents(Tasks taskService, CommonEventContainer container) throws IOException {
		ArrayList<GoogleTaskEvent> googleTaskEvents = new ArrayList<GoogleTaskEvent>();

		// Show all uncompleted tasks
		com.google.api.services.tasks.model.Tasks tasklist = taskService.tasks().list(container.getID())
				.setShowCompleted(false)
				.execute();
		
		// Some reason not having valid tasks means
		// that getItems produces null even though the tasklist exists
		if (tasklist.getItems() != null) {

			// Get all tasks from tasklist
	    	for (Task task : tasklist.getItems()) {
	    	  
	    	  GoogleTaskEvent tEvent = new GoogleTaskEvent(
	    			  task.getId(),
	    			  container,
	    			  task.getTitle(),
	    			  (task.getNotes() == null) ? "" : task.getNotes(),
	    			  (task.getDue() == null) ? null : task.getDue(),
	    			  (task.getCompleted() == null) ? false : true
	    			  );
	    	  
	    	  googleTaskEvents.add(tEvent);
	    	  
	    	}
		}
    	
		return googleTaskEvents;
	}
}
