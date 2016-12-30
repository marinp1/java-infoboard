package fi.patrikmarin.infoboard.controller;

import static fi.patrikmarin.infoboard.utils.Utils.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import fi.patrikmarin.infoboard.App;
import fi.patrikmarin.infoboard.Parameters;
import fi.patrikmarin.infoboard.calendar.CommonEvent;
import fi.patrikmarin.infoboard.utils.Utils;
import fi.patrikmarin.infoboard.weather.WeatherEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** 
 * Controller class for the main application.
 */
public class InfoboardController {
	
	private App app;
	
	//====================== DATETIME BLOCK =====================================
	@FXML
	private GridPane dateTimeBlock;
	@FXML
	private Label datetimeBlockTime;
	@FXML
	private Label datetimeBlockDate;
	@FXML
	private Label datetimeBlockWeek;
	
	//====================== WEATHER BLOCK ======================================
	@FXML
	private VBox weatherBox;
	
	//===== ============= ADDITIONAL INFO BLOCK =================================
	@FXML
	private Label sunriseLabel;
	@FXML
	private Label sunsetLabel;
	@FXML
	private Label innerTemperatureLabel;
	@FXML
	private Label pressureLabel;
	@FXML
	private Label windIconLabel;
	@FXML
	private Label windSpeedLabel;
	
	
	//====================== GOOGLE BLOCK =======================================
	@FXML
	private VBox calendarBox;
	
	/**
	 * The update loop for the GUI.
	 * @param fullUpdate if ard update should be done
	 */
	public void updateLoop(Boolean fullUpdate) {
		
		// Always update the components that do 
		// not require internet
		setDateTimeComponent();
		setAdditionalInfoComponent();
		
		// On full update update data content
		if (fullUpdate) {
			weatherBox.getChildren().clear();
			calendarBox.getChildren().clear();
			setWeatherComponent();
			setGoogleComponent();
		}
		
	}
	
	/**
	 * Initialise method for the class.
	 * Clears current content.
	 */
	@FXML
	private void initialize() {
		weatherBox.getChildren().clear();
		calendarBox.getChildren().clear();
	}
	
	/**
	 * Updates the application with new data.
	 * @param app the application to update.
	 */
	public void setApp(App app) {
		this.app = app;
		
		setDateTimeComponent();
		
		setWeatherComponent();
		
		setAdditionalInfoComponent();
		
		setGoogleComponent();
	}
	
	/**
	 * Updates the datetimeblock with new data.
	 */
	private void setDateTimeComponent() {
		LocalDateTime ldt = LocalDateTime.now();
		this.datetimeBlockTime.setText(ldt.format(timeFormat));
		this.datetimeBlockDate.setText(ldt.format(dateFormat));
		this.datetimeBlockWeek.setText(ldt.format(weekFormat));
	}
	
	/**
	 * Updates the forecast component with new data.
	 */
	private void setWeatherComponent() {
		// Loop through all forecast components found in the data
		for (WeatherEvent we : app.getWeatherData()) {
			// Generate new HBox to hold the data
			HBox weatherEventBox = new HBox();
			
			Label timeLabel = new Label(we.getTime().format(timeFormat));
			timeLabel.getStyleClass().addAll("secondary-color", "infoblock-secondary");
			
			Label temperatureLabel = new Label(String.valueOf(we.getTemperature()) + " °C");
			temperatureLabel.getStyleClass().addAll("main-color");
			
			Label iconLabel = new Label(we.getIconContent());
			iconLabel.getStyleClass().addAll("main-color", "infoblock-icon");
			
			weatherEventBox.getChildren().addAll(timeLabel, temperatureLabel, iconLabel);
			
			weatherEventBox.setAlignment(Pos.BASELINE_LEFT);
			
			// Add generated HBox to forecast component
			weatherBox.getChildren().add(weatherEventBox);
		}
	}
	
	/**
	 * Updates additional info components with new data.
	 */
	private void setAdditionalInfoComponent() {
		this.sunriseLabel.setText(App.sunrise.format(timeFormat));
		this.sunsetLabel.setText(App.sunset.format(timeFormat));
		this.innerTemperatureLabel.setText(String.valueOf((int) Math.round(App.temperature)) + " °C");
		this.pressureLabel.setText(app.getWeatherData().get(0).getPressure() + " hPa");
		double windSpeed = app.getWeatherData().get(0).getWindSpeed();
		this.windIconLabel.setText(Utils.beaufortIconMapper(windSpeed));
		this.windSpeedLabel.setText(String.valueOf(windSpeed) + " m/s");
	}
	
	/**
	 * Updates the google calendar and event component with new data.
	 */
	private void setGoogleComponent() {
		try {
			
			int eventCount = 0;
			
			// Loop through data found in the application
			for (LocalDate ld : app.getCalendarEventData().keySet()) {
				
				if (eventCount >= Parameters.EVENT_COUNT) {
					break;
				}
				
				// Create container for each day
				VBox dayContainer = new VBox();
				dayContainer.setSpacing(10);
				
				// Add date label to the container
				Label dayLabel = new Label(ld.format(dateFormat));
				dayLabel.setAlignment(Pos.BOTTOM_LEFT);
				dayLabel.getStyleClass().add("secondary-color");
				
				dayContainer.getChildren().add(dayLabel);
				
				// Loop through all events for the day
				for (CommonEvent ge : app.getCalendarEventData().get(ld)) {
					
					if (ge.getParent().getEnabled()) {
						// Get loader for the new google event
				        FXMLLoader loader = new FXMLLoader();
				        loader.setLocation(App.class.getResource("/fxml/CalendarEventBlock.fxml"));
				        
				        // Get the component and update it with data
				        GridPane calendarEventBox = (GridPane) loader.load();
			            CalendarEventController controller = loader.getController();
			            
			            controller.addEvent(ge);

			            // Add the generated google event to the day container
			            dayContainer.getChildren().add(calendarEventBox);
			            
			            eventCount += 1;
			            if (eventCount == Parameters.EVENT_COUNT) {
			            	break;
			            }
					}

				}
				
				// Add margins to box
				VBox.setMargin(dayContainer, new Insets(0,0,20,0));
	            
				// Add the day container to the google component, if there were any visible events for the day
	            if (dayContainer.getChildren().size() > 1) calendarBox.getChildren().add(dayContainer);
	            
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
