package fi.patrikmarin.infoboard.google;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.tasks.TasksScopes;

import fi.patrikmarin.infoboard.utils.LogLevel;
import fi.patrikmarin.infoboard.utils.Logger;

/**
 * The main class for initiating Google API calls.
 */
public class CommonService {
	
	protected static final String APPLICATION_NAME = "Infoboard";

	protected static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();
    
	protected static final List<String> SCOPES =
            Arrays.asList(CalendarScopes.CALENDAR_READONLY, TasksScopes.TASKS_READONLY);

	protected static HttpTransport HTTP_TRANSPORT;
	
	private static GoogleAuthenticator AUTHENTICATOR;
	
	private static ArrayList<CommonEventContainer> eventContainers = new ArrayList<CommonEventContainer>();
	private static ArrayList<CommonEvent> events = new ArrayList<CommonEvent>();

    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
    private static com.google.api.services.calendar.Calendar getGoogleCalendarService() throws IOException {
    	
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            AUTHENTICATOR = new GoogleAuthenticator();
        } catch (Exception e) {
        	Logger.log(LogLevel.ERROR, "Error with initialising Google authentication.");
            e.printStackTrace();
        }
    	
        Credential credential = AUTHENTICATOR.getCredentials();
        return new com.google.api.services.calendar.Calendar.Builder(
        		CommonService.HTTP_TRANSPORT, CommonService.JSON_FACTORY, credential)
                .setApplicationName(CommonService.APPLICATION_NAME)
                .build();
    }
    
    /**
     * Build and return an authorized Tasks client service.
     * @return an authorized Tasks client service
     * @throws IOException
     */
    private static com.google.api.services.tasks.Tasks getGoogleTasksService() throws IOException {
    	Credential credential = AUTHENTICATOR.getCredentials();
        return new com.google.api.services.tasks.Tasks.Builder(
                CommonService.HTTP_TRANSPORT, CommonService.JSON_FACTORY, credential)
                .setApplicationName(CommonService.APPLICATION_NAME)
                .build();
    }
    
    /**
     * Call Google element generation for calendar events and
     * tasks and return combined and sorted hashmap of events.
     * @return
     */
    public static TreeMap<LocalDate, ArrayList<CommonEvent>> getCommonEvents() {
    	TreeMap<LocalDate, ArrayList<CommonEvent>> commonEventMap = new TreeMap<LocalDate, ArrayList<CommonEvent>>();
    	
    	Logger.log(LogLevel.INFO, "Trying to fetch Google events.");
        	
    	try {
    		com.google.api.services.calendar.Calendar gCalendarService = getGoogleCalendarService();
    		com.google.api.services.tasks.Tasks gTasksService = getGoogleTasksService();
    		
    		// Get content from google servers
    		eventContainers = CommonEventGenerator.getEventContainers(gCalendarService, gTasksService);
    		events = CommonEventGenerator.getEvents(eventContainers, gCalendarService, gTasksService);
    		
    		// Populate date map from events
    		for (CommonEvent event : events) {
    			
    			LocalDate ld = event.compareBy.toLocalDate();
    			
    			if (commonEventMap.containsKey(ld)) {
    				commonEventMap.get(ld).add(event);
    			} else {
    				commonEventMap.put(ld, new ArrayList<CommonEvent>());
    				commonEventMap.get(ld).add(event);
    			}
    		}
    		
    		Logger.log(LogLevel.INFO, "Tasks and calendar events fetched successfully.");
    		
    	} catch (Exception e) {
    		Logger.log(LogLevel.ERROR, "Error with Google events: " + e.getMessage());
    	}
    	
    	
    	return commonEventMap;
    }
    
    public static ArrayList<CommonEventContainer> getCommonEventContainers() {
    	return eventContainers;
    }
}
