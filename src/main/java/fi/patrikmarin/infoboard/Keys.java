package fi.patrikmarin.infoboard;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import fi.patrikmarin.infoboard.utils.Utils.InvalidAPIKeyFileException;;

public class Keys {
	
	private final String API_KEY_FILE_PATH = System.getProperty("user.dir") + File.separator + "API_KEYS.properties";
	private final File API_KEY_FILE = new File(API_KEY_FILE_PATH);
	
	public String FMI_API_KEY = "";
	public String GOOGLE_CLIENT_ID = "";
	public String GOOGLE_CLIENT_SECRET = "";
	public String MEISTERTASK_API_KEY = "";
	
	public Keys() throws Exception {
		
		if (!API_KEY_FILE.exists()) {
			throw new InvalidAPIKeyFileException("API key file at " + API_KEY_FILE_PATH + " couldn't be found.");
		}
		
		Properties prop = new Properties();
		FileInputStream apiKeys = new FileInputStream(API_KEY_FILE_PATH);
		prop.load(apiKeys);
		apiKeys.close();
		
		FMI_API_KEY = prop.getProperty("FMI_API_KEY");
		GOOGLE_CLIENT_ID = prop.getProperty("GOOGLE_CLIENT_ID");
		GOOGLE_CLIENT_SECRET = prop.getProperty("GOOGLE_CLIENT_SECRET");
		MEISTERTASK_API_KEY = prop.getProperty("MEISTERTASK_API_KEY");
		
	}
	
}
