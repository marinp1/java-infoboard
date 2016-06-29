package fi.patrikmarin.infoboard;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import fi.patrikmarin.infoboard.controller.InfoboardController;
import fi.patrikmarin.infoboard.google.GoogleEvent;
import fi.patrikmarin.infoboard.utils.LogLevel;
import fi.patrikmarin.infoboard.utils.Logger;
import fi.patrikmarin.infoboard.weather.SolarCalculator;
import fi.patrikmarin.infoboard.weather.SolarCalculatorResult;
import fi.patrikmarin.infoboard.weather.WeatherEvent;
import fi.patrikmarin.infoboard.weather.WeatherService;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class App extends Application {
	
	//============================ APPLICATION VARIABLES ======================================================
    private Stage primaryStage;
    private StackPane root;
    private Thread updateThread;
    
    //============================= UPDATE VARIABLE ===========================================================
	private Integer SECONDS_SINCE_LAST_UPDATE = 0;
	private Integer VIEW_UPDATE_INTERVAL = 10;
	private Integer DATA_UPDATE_INTERVAL = 300;
    
    //==================================== DATA ===============================================================
    private ArrayList<WeatherEvent> weatherData = new ArrayList<WeatherEvent>();
    private HashMap<LocalDate, ArrayList<GoogleEvent>> eventData = new HashMap<LocalDate, ArrayList<GoogleEvent>>();
    public static LocalDateTime sunrise;
    public static LocalDateTime sunset;
    
    /**
     * Constructor for the main application.
     * TODO: Replace dummy data with service classes
     */
    public App() {
    	updateData();
    	
        // Add some sample data
//    	weatherData.add(new WeatherEvent(LocalDateTime.now(), -10, 1));
//    	weatherData.add(new WeatherEvent(LocalDateTime.now(), 15, 2));
//    	
//    	ArrayList<GoogleEvent> eventList = new ArrayList<GoogleEvent>();
//    	eventList.add(new GoogleEvent("ID", "summary", "paikka", "kuvaus", Color.BLACK, LocalDateTime.now(), LocalDateTime.now().plusHours(2)));
//    	eventList.add(new GoogleEvent("ID", "summary", "paikka", "kuvaus", Color.BLACK, LocalDateTime.now(), LocalDateTime.now().plusHours(2)));
//    	
//    	eventData.put(LocalDate.now(), eventList);
    }

    /**
     * Updates application data with service classes.
     * TODO: Service classes
     */
    private void updateData() {
    	sunrise = WeatherService.getSunriseSet(SolarCalculatorResult.SUNRISE);
    	sunset = WeatherService.getSunriseSet(SolarCalculatorResult.SUNSET);
    	weatherData = WeatherService.getWeatherForecast();
    }

    /**
     * Returns gathered weather data.
     * @return
     */
    public ArrayList<WeatherEvent> getWeatherData() {
        return weatherData;
    }
    
    /**
     * Returns gathered data from google calendar and google tasks.
     * @return
     */
    public HashMap<LocalDate, ArrayList<GoogleEvent>> getGoogleEventData() {
    	return eventData;
    }
    
    /**
     * The start method for the program.
     */
    @Override
    public void start(Stage primaryStage) {    	
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Infoboard 2.0");

        initialiseProgram();
        
        primaryStage.setOnCloseRequest(
                event -> {
                	try {
                    	updateThread.interrupt();
                    	updateThread.join();
                    	//FIXME: Create settings service
                		//SettingsService.saveSettings();
                    	Logger.log(LogLevel.INFO, "Program closed.");
                	} catch (Exception e) {
                		Logger.log(LogLevel.ERROR, "Couldn't close update thread.");
                		e.printStackTrace();
                	}
                }
        );
    }
    
    /**
     * Initialises program. Loads the root element from
     * the fxml file and initialises the controller.
     */
    private void initialiseProgram() {
        try {
        	
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(App.class.getResource("/fxml/InfoboardWindow.fxml"));
            root = (StackPane) loader.load();
            
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
            
            InfoboardController controller = loader.getController();
            controller.setApp(this);
            
            initialiseUpdater(controller);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initialises the update loop for the program.
     * @param controller the InfoboardController which to call in the loop.
     */
    private void initialiseUpdater(InfoboardController controller) {
    	
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
            	// Loop indefinitely
                while (true) {
                    javafx.application.Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                        	// Every 5 minutes update data content and do a hard update on the view
                        	if (SECONDS_SINCE_LAST_UPDATE > DATA_UPDATE_INTERVAL) {
                        		updateData();
                        		SECONDS_SINCE_LAST_UPDATE = 0;
                        		controller.updateLoop(true);
                        	} else {
                        		controller.updateLoop(false);
                        	}
                        	
                        }
                    });
                    // Loop every ten seconds
                    Thread.sleep(VIEW_UPDATE_INTERVAL * 1000);
                    SECONDS_SINCE_LAST_UPDATE += VIEW_UPDATE_INTERVAL;
                }
            }
        };
        
        updateThread = new Thread(task);
        updateThread.start();
    }
    
    /**
     * Returns the primary stage of the application.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /** 
     * Calls JavaFX's launch method.
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
