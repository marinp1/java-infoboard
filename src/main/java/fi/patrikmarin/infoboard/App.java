package fi.patrikmarin.infoboard;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import fi.patrikmarin.infoboard.controller.InfoboardController;
import fi.patrikmarin.infoboard.google.CommonEvent;
import fi.patrikmarin.infoboard.google.CommonService;
import fi.patrikmarin.infoboard.utils.LogLevel;
import fi.patrikmarin.infoboard.utils.Logger;
import fi.patrikmarin.infoboard.utils.SettingsService;
import fi.patrikmarin.infoboard.sensors.TemperatureSensor;
import fi.patrikmarin.infoboard.weather.SolarCalculatorResult;
import fi.patrikmarin.infoboard.weather.WeatherEvent;
import fi.patrikmarin.infoboard.weather.WeatherService;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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
    private TreeMap<LocalDate, ArrayList<CommonEvent>> eventData = new TreeMap<LocalDate, ArrayList<CommonEvent>>();
    public static LocalDateTime sunrise;
    public static LocalDateTime sunset;
    
    // The calendar and event list ID that are not displayed
	public static HashMap<String, Boolean> containerStatus = new HashMap<String, Boolean>();
    
    //=============================== SENSOR DATA =============================================================
    public static double temperature = 0.0;

    /**
     * Updates application data with service classes.
     */
    private void updateData() {
    	sunrise = WeatherService.getSunriseSet(SolarCalculatorResult.SUNRISE);
    	sunset = WeatherService.getSunriseSet(SolarCalculatorResult.SUNSET);
    	weatherData = WeatherService.getWeatherForecast();
    	eventData = CommonService.getGoogleEvents();
    }
    
    /**
     * Updates values from sensors.
     */
    private void updateSensorData() {
    	try {
    		double newTemperature = TemperatureSensor.updateValue();
    		if (newTemperature != Double.MIN_VALUE) temperature = newTemperature;
    	} catch (Exception e) {
    		Logger.log(LogLevel.WARNING, "Couldn't read sensor data.");
    	}
	
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
    public TreeMap<LocalDate, ArrayList<CommonEvent>> getGoogleEventData() {
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
                		SettingsService.saveSettings();
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
            
            SettingsService.readSettings();
        	updateData();
            
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
                        	updateSensorData();
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
