package fi.patrikmarin.infoboard.google;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.ColorDefinition;
import com.google.api.services.calendar.model.Colors;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

import fi.patrikmarin.infoboard.App;
import fi.patrikmarin.infoboard.utils.LogLevel;
import fi.patrikmarin.infoboard.utils.Logger;
import javafx.scene.paint.Color;

public class CommonEventGenerator {

	/**
	 * Gets google event containers from Google.
	 * @param calendarService
	 * @param taskService
	 * @return
	 * @throws IOException
	 */
	protected static ArrayList<CommonEventContainer> getEventContainers(Calendar gCalendarService, Tasks gTaskService) throws IOException {
		// Update color hashmaps
		GoogleHelper.updateCalendarColorKeys(gCalendarService);

		ArrayList<CommonEventContainer> eventContainers = new ArrayList<CommonEventContainer>();

		//================== Get calendars ==========================================
		String pageToken = null;

		do {
			CalendarList calendarList = gCalendarService.calendarList().list().setPageToken(pageToken).execute();
			List<CalendarListEntry> items = calendarList.getItems();

			for (CalendarListEntry calendarListEntry : items) {

				CommonEventContainer gcal = new CommonEventContainer(CommonContainerType.GOOGLE_CALENDAR);

				if (calendarListEntry.getSummaryOverride() == null) {
					gcal.setName(calendarListEntry.getSummary());
				} else {
					gcal.setName(calendarListEntry.getSummaryOverride());
				}

				gcal.setColor(GoogleHelper.calendarColorKeys.get(calendarListEntry.getColorId()));
				gcal.setID(calendarListEntry.getId());
				
				// Check if the settings service contains the container
				// If not, add it to the list
				if (!App.containerStatus.containsKey(gcal.getID())) {
					App.containerStatus.put(gcal.getID(), true);
				}
				gcal.setEnabled(App.containerStatus.get(gcal.getID()));
				

				eventContainers.add(gcal);
			}

			pageToken = calendarList.getNextPageToken();

		} while(pageToken != null);

		// ==================== Get tasklists ===========================================
		TaskLists taskQuery = gTaskService.tasklists().list()
				.setMaxResults(Long.valueOf(10))
				.execute();

		List<TaskList> taskLists = taskQuery.getItems();

		if (taskLists != null) {
			for (TaskList tasklist : taskLists) {

				CommonEventContainer taskList = new CommonEventContainer(CommonContainerType.GOOGLE_TASKLIST);

				taskList.setID(tasklist.getId());
				taskList.setName(tasklist.getTitle());
				
				// Check if the settings service contains the container
				// If not, add it to the list
				if (!App.containerStatus.containsKey(taskList.getID())) {
					App.containerStatus.put(taskList.getID(), true);
				}
				taskList.setEnabled(App.containerStatus.get(taskList.getID()));
				
				taskList.setColor(Color.BEIGE);

				eventContainers.add(taskList);
			}
		}
		
		// =================== Get MeisterTask projects ====================================
		
		TreeMap<String, String> meisterProjects = MeisterTaskHelper.getMeisterTaskProjects();
		
		for (String projectID : meisterProjects.keySet()) {
			
			CommonEventContainer meisterProject = new CommonEventContainer(CommonContainerType.MEISTERTASK_TASKLIST);
			
			meisterProject.setID(projectID);
			meisterProject.setName(meisterProjects.get(projectID));

			if (!App.containerStatus.containsKey(projectID)) {
				App.containerStatus.put(projectID, true);
			}
			
			meisterProject.setEnabled(App.containerStatus.get(projectID));
			
			meisterProject.setColor(Color.BLUE);
			
			eventContainers.add(meisterProject);
		}
		
		return eventContainers;
	}
	
	
	protected static ArrayList<CommonEvent> getEvents(ArrayList<CommonEventContainer> eventContainers, Calendar gCalendarService, Tasks gTaskService) throws IOException {
		
		ArrayList<CommonEvent> events = new ArrayList<CommonEvent>();
		
		for (CommonEventContainer eventContainer : eventContainers) {
			if (eventContainer.getType() == CommonContainerType.GOOGLE_CALENDAR) {
				for (GoogleCalendarEvent cEvent : GoogleHelper.getGoogleCalendarEvents(gCalendarService, eventContainer)) {
					events.add(cEvent);
				}
			} else if (eventContainer.getType() == CommonContainerType.GOOGLE_TASKLIST) {
				for (GoogleTaskEvent tEvent : GoogleHelper.getGoogleTaskEvents(gTaskService, eventContainer)) {
					events.add(tEvent);
				}
			} else if (eventContainer.getType() == CommonContainerType.MEISTERTASK_TASKLIST) {
				for (MeisterTaskEvent mEvent : MeisterTaskHelper.getMeisterTasksByProject(eventContainer)) {
					events.add(mEvent);
				}
			}
		}
		
		Collections.sort(events);
		
		return events;
	}
}
