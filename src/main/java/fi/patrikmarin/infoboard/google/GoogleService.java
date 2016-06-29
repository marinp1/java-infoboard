package fi.patrikmarin.infoboard.google;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
public class GoogleService {
	
	protected static final String APPLICATION_NAME = "Infoboard";

	protected static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();
    
	protected static final List<String> SCOPES =
            Arrays.asList(CalendarScopes.CALENDAR_READONLY, TasksScopes.TASKS_READONLY);

	protected static HttpTransport HTTP_TRANSPORT;
	
	private static GoogleAuthenticator AUTHENTICATOR;
	
	public static ArrayList<String> hiddenContainerIDs = new ArrayList<String>();
	
	private static ArrayList<GoogleEventContainer> googleEventContainers = new ArrayList<GoogleEventContainer>();
	
    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            AUTHENTICATOR = new GoogleAuthenticator();
        } catch (Exception e) {
        	Logger.log(LogLevel.ERROR, "Error with initialising Google authentication.");
            e.printStackTrace();
        }
    }
    
    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client serviceprotected
     * @throws IOException
     */
    private static com.google.api.services.calendar.Calendar getCalendarService() throws IOException {
        Credential credential = AUTHENTICATOR.getCredentials();
        return new com.google.api.services.calendar.Calendar.Builder(
        		GoogleService.HTTP_TRANSPORT, GoogleService.JSON_FACTORY, credential)
                .setApplicationName(GoogleService.APPLICATION_NAME)
                .build();
    }
    
    /**
     * Build and return an authorized Tasks client service.
     * @return an authorized Tasks client service
     * @throws IOException
     */
    private static com.google.api.services.tasks.Tasks getTasksService() throws IOException {
    	Credential credential = AUTHENTICATOR.getCredentials();
        return new com.google.api.services.tasks.Tasks.Builder(
                GoogleService.HTTP_TRANSPORT, GoogleService.JSON_FACTORY, credential)
                .setApplicationName(GoogleService.APPLICATION_NAME)
                .build();
    }
    
    /**
     * Call Google element generation for calendar events and
     * tasks and return combined and sorted hashmap of events.
     * @return
     */
    public static HashMap<LocalDate, ArrayList<GoogleEvent>> getGoogleEvents() {
    	HashMap<LocalDate, ArrayList<GoogleEvent>> googleEvents = new HashMap<LocalDate, ArrayList<GoogleEvent>>();
    	
    	
    	
    	return googleEvents;
    }
}
