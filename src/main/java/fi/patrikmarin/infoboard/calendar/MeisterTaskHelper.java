package fi.patrikmarin.infoboard.calendar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fi.patrikmarin.infoboard.Keys;

/**
 * Methods for retrieving data from the MeisterTask rest API.
 */
public class MeisterTaskHelper {
	
	// Rest API endpoint for a list of projects
	private static final String PROJECTS_URL = "https://www.meistertask.com/api/projects?access_token=" + Keys.MEISTERTASK_API_KEY;
	
	// Pieces for rest API endpoint for a list of tasks for a specific project.
	// The final URL is TASKS_FOR_PROJECT_URL_START + [Project ID] + TASKS_FOR_PROJECT_URL_END
	private static final String TASKS_FOR_PROJECT_URL_START = "https://www.meistertask.com/api/projects/";
	private static final String TASKS_FOR_PROJECT_URL_END = "/tasks?access_token=" + Keys.MEISTERTASK_API_KEY;
    /**
     * Get a list of MeisterTask projects.
     * @return a map of projects with IDs
     */
    protected static TreeMap<String, String> getMeisterTaskProjects() throws Exception {
    	TreeMap<String, String> meisterProjects = new TreeMap<String, String>();
    	
    	String jsonResponse = httpGetRequest(PROJECTS_URL);
    	
    	JsonParser parser = new JsonParser();
    	JsonArray result = parser.parse(jsonResponse).getAsJsonArray();
    	
    	for (int i = 0; i < result.size(); i++) {
    		JsonObject project = result.get(i).getAsJsonObject();
    		String id = project.get("id").getAsString();
    		String name = project.get("name").getAsString();
    		meisterProjects.put(id, name);
    	}
    	
    	return meisterProjects;
    }
    
    /**
     * Get a list of tasks for a specific MeisterTask project.
     * @param ce the MeisterTask project to look for
     * @return list of active tasks from the project
     */
    protected static TreeSet<MeisterTaskEvent> getMeisterTasksByProject(CommonEventContainer ce) throws Exception {
    	TreeSet<MeisterTaskEvent> meisterTasks = new TreeSet<MeisterTaskEvent>();
    	
    	String tasksURL = TASKS_FOR_PROJECT_URL_START + ce.getID() + TASKS_FOR_PROJECT_URL_END;
    	
    	
    	String jsonResponse = httpGetRequest(tasksURL);
    	
    	JsonParser parser = new JsonParser();
    	JsonArray result = parser.parse(jsonResponse).getAsJsonArray();
    	
    	for (int i = 0; i < result.size(); i++) {
    		JsonObject task = result.get(i).getAsJsonObject();
    		String id = task.get("id").getAsString();
    		String name = task.get("name").getAsString();
    		
    		String due = null;
    		if (!task.get("due").isJsonNull()) {
    			due = task.get("due").getAsString();
    		}
    		
    		String notes = "";
    		if (!task.get("notes").isJsonNull()) {
    			notes = task.get("notes").getAsString();
    		}
    		
    		int status = task.get("status").getAsInt();
    		
    		if (due != null && status == 1) {
    			MeisterTaskEvent mEvent = new MeisterTaskEvent(id, ce, name, notes, due);
    			meisterTasks.add(mEvent);
    		}

    	}
    	
    	return meisterTasks;
    }

    /**
     * A simple GET request for a given URI.
     * @param URI
     * @return the result in plain text
     */
    private static String httpGetRequest(String URI) throws Exception {
    	
        URL url = new URL(URI);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        BufferedReader br = new BufferedReader( new InputStreamReader(connection.getInputStream())); 
        
        String output; 
        String result = "";
        
        while ((output = br.readLine()) != null) { 
        	result += output; 
        }
        
        br.close();
        
        return result;
    }
}
