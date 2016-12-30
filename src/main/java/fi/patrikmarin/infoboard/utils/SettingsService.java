package fi.patrikmarin.infoboard.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import fi.patrikmarin.infoboard.App;
import fi.patrikmarin.infoboard.calendar.CommonEventContainer;
import fi.patrikmarin.infoboard.calendar.CommonService;

public class SettingsService {
	static File SETTINGS_STORE = new java.io.File(System.getProperty("user.home"), ".infoboard/settings.json");
	
	static final String SETTINGS_CALENDAR_NAME = "CalendarSettings";
	
	// Initialize the settings file if not found
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
			} catch (IOException e) {
				Logger.log(LogLevel.WARNING, "Couldn't initialise settings!");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Reads google event container status to the containerStatus - variable.
	 */
	public static void readSettings() {
        try {
        	
        	FileReader fr = new FileReader(SETTINGS_STORE);
 
        	JsonParser parser = new JsonParser();
        	JsonObject o = parser.parse(fr).getAsJsonObject();
        	
        	fr.close();
        	
        	//=========== GOOGLE CONTAINERS ==========================
        	
        	Boolean settingsObject = o.has(SETTINGS_CALENDAR_NAME);
        	
        	if (settingsObject) {
        		
            	JsonArray containers = o.get(SETTINGS_CALENDAR_NAME).getAsJsonArray();
            	
            	// Get values for each container
            	for (Integer i = 0; i < containers.size(); i++) {
            		JsonObject container = containers.get(i).getAsJsonObject();
            		String id = container.get("ID").getAsString();
            		Boolean enabled = container.get("enabled").getAsBoolean();
            		
            		App.containerStatus.put(id, enabled);
            	}
        	}
 
        } catch (Exception e) {
        	Logger.log(LogLevel.ERROR, "Couldn't read settings file!");
            e.printStackTrace();
        }
	}
	
	/**
	 * Saves google container visibility settings to file.
	 */
	public static void saveSettings() {
		try {

			FileReader fr = new FileReader(SETTINGS_STORE);

			JsonParser parser = new JsonParser();
			JsonObject o = parser.parse(fr).getAsJsonObject();

			fr.close();


			//============== GOOGLE CONTAINERS =============

			Boolean calObj = o.has(SETTINGS_CALENDAR_NAME);

			if (calObj) o.remove(SETTINGS_CALENDAR_NAME);

			JsonArray googleContainers = new JsonArray();
			o.add(SETTINGS_CALENDAR_NAME, googleContainers);

			// Save values for each container
			for (CommonEventContainer gec : CommonService.getCommonEventContainers()) {
				JsonObject no = new JsonObject();
				no.addProperty("ID", gec.getID());
				no.addProperty("enabled", gec.getEnabled());
				googleContainers.add(no);
			}
			
			//============= SAVE DATA =======================

			FileWriter fw = new FileWriter(SETTINGS_STORE);
			BufferedWriter bw = new BufferedWriter(fw);

			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String prettyJsonString = gson.toJson(o);

			bw.write(prettyJsonString);

			bw.close();

			Logger.log(LogLevel.INFO, "Settings saved.");

		} catch (Exception e) {
			Logger.log(LogLevel.ERROR, "Couldn't save settings file!");
			e.printStackTrace();
		}
	}
}
