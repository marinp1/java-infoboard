package fi.patrikmarin.infoboard.google;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.Calendar.Events;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.ColorDefinition;
import com.google.api.services.calendar.model.Colors;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

import javafx.scene.paint.Color;

public class GoogleEventGenerator {

	protected static HashMap<String, Color> calendarColorKeys = new HashMap<String, Color>();
	protected static HashMap<String, Color> eventColorKeys = new HashMap<String, Color>();

	/**
	 * Gets google event containers from Google.
	 * @param calendarService
	 * @param taskService
	 * @return
	 * @throws IOException
	 */
	protected static ArrayList<GoogleEventContainer> getGoogleEventContainers(Calendar calendarService, Tasks taskService) throws IOException {
		// Update color hashmaps
		updateCalendarColorKeys(calendarService);

		ArrayList<GoogleEventContainer> googleEventContainers = new ArrayList<GoogleEventContainer>();

		//================== Get calendars ==========================================
		String pageToken = null;

		do {
			CalendarList calendarList = calendarService.calendarList().list().setPageToken(pageToken).execute();
			List<CalendarListEntry> items = calendarList.getItems();

			for (CalendarListEntry calendarListEntry : items) {

				GoogleEventContainer gcal = new GoogleEventContainer(GoogleContainerType.CALENDAR);

				if (calendarListEntry.getSummaryOverride() == null) {
					gcal.setName(calendarListEntry.getSummary());
				} else {
					gcal.setName(calendarListEntry.getSummaryOverride());
				}

				gcal.setColor(calendarColorKeys.get(calendarListEntry.getColorId()));
				gcal.setID(calendarListEntry.getId());
				gcal.setEnabled(GoogleService.hiddenContainerIDs.contains(gcal.getID()));

				googleEventContainers.add(gcal);
			}

			pageToken = calendarList.getNextPageToken();

		} while(pageToken != null);

		// ==================== Get tasklists ===========================================
		TaskLists taskQuery = taskService.tasklists().list()
				.setMaxResults(Long.valueOf(10))
				.execute();

		List<TaskList> taskLists = taskQuery.getItems();

		if (taskLists != null) {
			for (TaskList tasklist : taskLists) {

				GoogleEventContainer taskList = new GoogleEventContainer(GoogleContainerType.TASKLIST);

				taskList.setID(tasklist.getId());
				taskList.setName(tasklist.getTitle());
				taskList.setEnabled(GoogleService.hiddenContainerIDs.contains(taskList.getID()));
				taskList.setColor(Color.BEIGE);

				googleEventContainers.add(taskList);
			}
		}
		
		return googleEventContainers;
	}
	
	
	protected static ArrayList<GoogleEvent> getGoogleEvents(ArrayList<GoogleEventContainer> eventContainers, Calendar calendarService, Tasks taskService) throws IOException {
		
		ArrayList<GoogleEvent> events = new ArrayList<GoogleEvent>();
		
		for (GoogleEventContainer eventContainer : eventContainers) {
			if (eventContainer.getType() == GoogleContainerType.CALENDAR) {
				for (GoogleCalendarEvent cEvent : getGoogleCalendarEvents(calendarService, eventContainer)) {
					events.add(cEvent);
				}
			} else {
				for (GoogleTaskEvent tEvent : getGoogleTaskEvents(taskService, eventContainer)) {
					events.add(tEvent);
				}
			}
		}
		
		Collections.sort(events);
		
		return events;
	}

	private static ArrayList<GoogleCalendarEvent> getGoogleCalendarEvents(Calendar calendarService, GoogleEventContainer container) throws IOException {		
		ArrayList<GoogleCalendarEvent> googleCalendarEvents = new ArrayList<GoogleCalendarEvent>();

		// Get at most 5 entries from the calendar
        com.google.api.services.calendar.model.Events ev = calendarService.events().list(container.getID())
                .setMaxResults(5)
                .setTimeMin(new DateTime(System.currentTimeMillis()))
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        
        List<Event> items = ev.getItems();
            
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
		
		return googleCalendarEvents;
	}

	private static ArrayList<GoogleTaskEvent> getGoogleTaskEvents(Tasks taskService, GoogleEventContainer container) {
		ArrayList<GoogleTaskEvent> googleTaskEvents = new ArrayList<GoogleTaskEvent>();

		

		return googleTaskEvents;
	}

	private static void updateCalendarColorKeys(Calendar calendarService) throws IOException {
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
}
