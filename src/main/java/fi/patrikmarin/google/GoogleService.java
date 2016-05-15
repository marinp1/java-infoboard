package fi.patrikmarin.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.tasks.TasksScopes;

import fi.patrikmarin.data.Keys;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GoogleService {
    /** Application name. */
    protected static final String APPLICATION_NAME =
        "Infoboard";

    /** Directory to store user credentials for this application. */
    protected static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/infoboard.json");

    /** Global instance of the {@link FileDataStoreFactory}. */
    protected static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    protected static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    protected static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart.json
     */
    protected static final List<String> SCOPES =
        Arrays.asList(CalendarScopes.CALENDAR_READONLY, TasksScopes.TASKS_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {        
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                		HTTP_TRANSPORT, JSON_FACTORY, Keys.GOOGLE_CLIENT_ID, Keys.GOOGLE_CLIENT_SECRET, SCOPES)
		        .setDataStoreFactory(DATA_STORE_FACTORY)
		        .setAccessType("offline")
		        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    public static void main(String[] args) throws IOException {
    	CalendarService cs = new CalendarService();
    	
    	for (GCalendar gc : cs.calendars) {
    		System.out.println(gc.getName());
    		System.out.println(gc.getColor());
    	}
    }

}