package fi.patrikmarin.infoboard.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.util.store.FileDataStoreFactory;

import fi.patrikmarin.infoboard.Keys;
import fi.patrikmarin.infoboard.utils.LogLevel;
import fi.patrikmarin.infoboard.utils.Logger;

/**
 * Handles Google authentication and authorization flow.
 */
public class GoogleAuthenticator {
	
	private Credential credential = null;

	private final java.io.File CREDENTIAL_DIR =
    		new java.io.File(System.getProperty("user.home"), ".credentials/infoboard.json");
	
	private FileDataStoreFactory DATA_STORE_FACTORY;
	
	protected GoogleAuthenticator() throws Exception {
		DATA_STORE_FACTORY = new FileDataStoreFactory(CREDENTIAL_DIR);
	}

    /**
     * Authorizes application. Saves the generated
     * credentials to credential variable.
     */
    private Credential authorize() {

    	try {
    		// Create Google authentication flow
            GoogleAuthorizationCodeFlow flow =
                    new GoogleAuthorizationCodeFlow.Builder(
                    		GoogleService.HTTP_TRANSPORT, GoogleService.JSON_FACTORY, Keys.GOOGLE_CLIENT_ID, Keys.GOOGLE_CLIENT_SECRET, GoogleService.SCOPES)
    		        .setDataStoreFactory(DATA_STORE_FACTORY)
    		        .setAccessType("offline")
    		        .build();
            
            return new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");

    	} catch (Exception e) {
    		Logger.log(LogLevel.ERROR, "Google authentication failed.");
    		e.printStackTrace();
    		
    		return null;
    	}
    }
    
    /**
     * Gets generated Google credentials.
     * Should be called only from Google services.
     * @return
     */
    protected Credential getCredentials() {
    	if (credential == null || credential.getExpiresInSeconds() <= 300) {
    		authorize();
    	}
    	
    	if (credential == null) {
    		Logger.log(LogLevel.ERROR, "No credentials.");
    	}
    	
    	return credential;
    }
    
}
