package fi.patrikmarin.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fi.patrikmarin.google.CalendarService;
import fi.patrikmarin.google.GCalendar;
import fi.patrikmarin.google.GTaskList;
import fi.patrikmarin.google.TasksService;

public class SettingsService {
	static File SETTINGS_STORE = new java.io.File(System.getProperty("user.home"), ".infoboard/settings.json");
	
	public static HashMap<String, Boolean> calendarSettings = new HashMap<String, Boolean>();
	public static HashMap<String, Boolean> taskSettings = new HashMap<String, Boolean>();
	
	private static String SETTINGS_CAL_NAME = "calendars";
	private static String SETTINGS_TL_NAME = "tasklists";
	
	static {
		if (!SETTINGS_STORE.exists()) {
			try {
				if (!SETTINGS_STORE.getParentFile().exists()) {
					SETTINGS_STORE.getParentFile().mkdirs();
				}
				
				PrintWriter writer = new PrintWriter(SETTINGS_STORE, "UTF-8");
				writer.println("{");
				writer.println("}");
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void readSettings() {
        try {
        	
        	FileReader fr = new FileReader(SETTINGS_STORE);
 
        	JsonParser parser = new JsonParser();
        	JsonObject o = parser.parse(fr).getAsJsonObject();
        	
        	fr.close();
        	
        	//=========== CALENDAR ==========================
        	
        	Boolean calObj = o.has(SETTINGS_CAL_NAME);
        	
        	if (calObj) {
            	JsonArray calendars = o.get(SETTINGS_CAL_NAME).getAsJsonArray();
            	
            	for (Integer i = 0; i < calendars.size(); i++) {
            		JsonObject calendar = calendars.get(i).getAsJsonObject();
            		String id = calendar.get("ID").getAsString();
            		Boolean enabled = calendar.get("enabled").getAsBoolean();
            		
            		calendarSettings.put(id, enabled);
            	}
        	}
        	
        	//=========== TASKLISTS =========================
        	
        	Boolean taskObj = o.has(SETTINGS_TL_NAME);
        	
        	if (taskObj) {
            	JsonArray tasklists = o.get(SETTINGS_TL_NAME).getAsJsonArray();
            	
            	for (Integer i = 0; i < tasklists.size(); i++) {
            		JsonObject tasklist = tasklists.get(i).getAsJsonObject();
            		String id = tasklist.get("ID").getAsString();
            		Boolean enabled = tasklist.get("enabled").getAsBoolean();
            		
            		taskSettings.put(id, enabled);
            	}
        	}
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void saveSettings() {
       try {
        	
        	FileReader fr = new FileReader(SETTINGS_STORE);
 
        	JsonParser parser = new JsonParser();
        	JsonObject o = parser.parse(fr).getAsJsonObject();
        	
        	fr.close();
        	
        	
        	//=========== CALENDAR ==========================

        	Boolean calObj = o.has(SETTINGS_CAL_NAME);
        	
        	if (calObj) o.remove(SETTINGS_CAL_NAME);
        	
    		JsonArray calendars = new JsonArray();
    		o.add(SETTINGS_CAL_NAME, calendars);
        	
        	for (GCalendar gc : CalendarService.calendars) {
        		JsonObject no = new JsonObject();
        		no.addProperty("ID", gc.getID());
        		no.addProperty("enabled", gc.getEnabled());
        		calendars.add(no);
        	}
        	
        	//=========== TASKLISTS =========================

        	Boolean taskObj = o.has(SETTINGS_TL_NAME);
        	
        	if (taskObj) o.remove(SETTINGS_TL_NAME);
        	
    		JsonArray tasklists = new JsonArray();
    		o.add(SETTINGS_TL_NAME, tasklists);
        	
        	for (GTaskList gt : TasksService.tasklists) {
        		JsonObject no = new JsonObject();
        		no.addProperty("ID", gt.getID());
        		no.addProperty("enabled", gt.getEnabled());
        		tasklists.add(no);
        	}
        	
        	//============= SAVE DATA =======================
        	
			FileWriter fw = new FileWriter(SETTINGS_STORE);
			BufferedWriter bw = new BufferedWriter(fw);
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String prettyJsonString = gson.toJson(o);
			
			bw.write(prettyJsonString);
			
			bw.close();
			
			System.out.println("Settings saved.");
 
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
