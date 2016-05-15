package fi.patrikmarin.calendar;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;

import fi.patrikmarin.data.Keys;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javafx.scene.paint.Color;

public class CalendarTest {
    /** Application name. */
    private static final String APPLICATION_NAME =
        "Infoboard";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        System.getProperty("user.home"), ".credentials/infoboard.json");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart.json
     */
    private static final List<String> SCOPES =
        Arrays.asList(CalendarScopes.CALENDAR_READONLY);

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
                		HTTP_TRANSPORT, JSON_FACTORY, Keys.GCAL_CLIENT_ID, Keys.GCAL_CLIENT_SECRET, SCOPES)
		        .setDataStoreFactory(DATA_STORE_FACTORY)
		        .setAccessType("offline")
		        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Calendar client service.
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public static com.google.api.services.calendar.Calendar
        getCalendarService() throws IOException {
        Credential credential = authorize();
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args) throws IOException {
        // Build a new authorized API client service.
        // Note: Do not confuse this class with the
        //   com.google.api.services.calendar.model.Calendar class.
        com.google.api.services.calendar.Calendar service =
            getCalendarService();
        
        Colors colors = service.colors().get().execute();
        
        HashMap<String, Color> colorList = new HashMap<String, Color>();

	     // Print available calendar list entry colors
	     for (HashMap.Entry<String, ColorDefinition> color : colors.getCalendar().entrySet()) {
	       String colID = color.getKey();
	       String bg = color.getValue().getBackground();
	       String fg = color.getValue().getForeground();
	       
	       colorList.put(colID, Color.web(bg));
	     }
        
        String pageToken = null;
        do {
        	CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
        	  List<CalendarListEntry> items = calendarList.getItems();

        	  for (CalendarListEntry calendarListEntry : items) {
        		  if (calendarListEntry.getSummaryOverride() == null) {
        			  System.out.println(calendarListEntry.getSummary());
        		  } else {
        			  System.out.println(calendarListEntry.getSummaryOverride());
        		  }
        	     String col = calendarListEntry.getColorId();
        	     System.out.println(calendarListEntry.getId());
        	     System.out.println(colorList.get(col).getRed() + " - "  + colorList.get(col).getGreen() + " - " + colorList.get(col).getBlue());
        	  }
        	  pageToken = calendarList.getNextPageToken();
        } while(pageToken != null);
        
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = service.events().list("primary")
            .setMaxResults(10)
            .setTimeMin(now)
            .setOrderBy("startTime")
            .setSingleEvents(true)
            .execute();
        List<Event> items = events.getItems();
        if (items.size() == 0) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
    }

}