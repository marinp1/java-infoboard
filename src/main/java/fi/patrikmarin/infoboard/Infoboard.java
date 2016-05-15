package fi.patrikmarin.infoboard;


import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import fi.patrikmarin.weather.WeatherEvent;
import fi.patrikmarin.weather.WeatherService;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Infoboard extends Application {
	public static String versionNumber = "0.4";
	public static WeatherService ws = new WeatherService();
	
    private VBox program = new VBox();
	private FlowPane dateTimeComponent;
	private FlowPane currentWeatherComponent;
	private VBox forecastComponent;
	
	private Integer secondsSinceLastUpdate = 0;
	
	public static void main(String args[]) {
		launch(args);
	}
	
	@Override
	public void start(Stage mainStage) {
		
        mainStage.setTitle("Infoboard v." + versionNumber);
        mainStage.setMinHeight(800);
        mainStage.setMinWidth(600);
        
        StackPane root = new StackPane();
        root.setId("pane");
        
        Scene scene = new Scene(root, 500, 400);
        
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        if(mainStage.isFullScreen()) {
                        	mainStage.setFullScreen(false);
                        	mainStage.setAlwaysOnTop(false);
                        } else {
                        	mainStage.setFullScreen(true);
                        	mainStage.setAlwaysOnTop(true);
                        }
                    }
                }
            }
        });
        
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        mainStage.setScene(scene);
        mainStage.show();
        
        ws.getData();

        update(root);
        
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (true) {
                    javafx.application.Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                        	System.out.println("Updating... " + secondsSinceLastUpdate);
                        	
                        	if (secondsSinceLastUpdate > 300) {
                        		ws.getData();
                        		secondsSinceLastUpdate = 0;
                        	}
                        	
                        	update(root);
                        }
                    });
                    Thread.sleep(10000);
                    secondsSinceLastUpdate += 10;
                }
            }
        };
        
        Thread th = new Thread(task);
        th.start();
        
        mainStage.setOnCloseRequest(
                event -> {
                	try {
                    	th.interrupt();
                    	th.join();
                    	System.out.println("Exit successful");
                	} catch (Exception e) {
                		System.out.println("Couldn't close update thread.");
                		e.printStackTrace();
                	}
                }
        );

	}
	
	public void update(StackPane root) {
		root.getChildren().remove(program);
		program = new VBox();
		
		getDateTimeComponent();
		getCurrentWeatherComponent();
		getForecastComponent();

        VBox.setMargin(dateTimeComponent, new Insets(20,20,20,20));
        
		Separator timeSeparator = new Separator();
		timeSeparator.getStyleClass().add("separator");
		VBox.setMargin(timeSeparator, new Insets(0,25,0,25));
		
        program.getChildren().addAll(dateTimeComponent, timeSeparator);
        
        VBox.setMargin(currentWeatherComponent, new Insets(20,20,20,20));
        
		Separator weatherSeparator = new Separator();
		weatherSeparator.getStyleClass().add("separator");
		VBox.setMargin(weatherSeparator, new Insets(0,25,0,25));
        
        program.getChildren().addAll(currentWeatherComponent, weatherSeparator);
        
        VBox.setMargin(forecastComponent, new Insets(20,20,20,20));
        
        program.getChildren().addAll(forecastComponent);
        
        program.setFillWidth(true);
        
        program.setAlignment(Pos.TOP_LEFT);
        root.getChildren().addAll(program);
	}
	
	private void getDateTimeComponent() {
		FlowPane comp = new FlowPane();
		comp.setOrientation(Orientation.HORIZONTAL);
		
		Date date = new Date();
		
	    DateFormat timeFormat = new SimpleDateFormat("HH:mm");
	    String curTime = timeFormat.format(date);
	    
	    DateFormat dateFormat = new SimpleDateFormat("E dd.MM.yyyy\n'Viikko' w");
	    String curDate = dateFormat.format(date);
		
		Label timeLabel = new Label(curTime);
		timeLabel.getStyleClass().add("text");
		timeLabel.setFont(new Font("Arial", 160));
		
		Label dateLabel = new Label(curDate.substring(0,1).toUpperCase() + curDate.substring(1));
		dateLabel.getStyleClass().add("text");
		dateLabel.setFont(new Font("Arial", 55));
		dateLabel.setAlignment(Pos.CENTER);
		
		FlowPane.setMargin(timeLabel, new Insets(10,10,10,10));
		FlowPane.setMargin(dateLabel, new Insets(10,10,10,50));
		comp.getChildren().addAll(timeLabel, dateLabel);
		dateTimeComponent = comp;
	}
	
	private void getCurrentWeatherComponent() {
		WeatherEvent we = ws.getCurrent();
		
		FlowPane comp = new FlowPane();
		comp.setOrientation(Orientation.HORIZONTAL);
		
		URL fileURI = Infoboard.class.getClass().getResource("/images/" + + we.getDayNight() + "_" + we.getSymbol() + ".png");
		
        ImageView currentIcon = new ImageView();
        currentIcon.getStyleClass().add("text");
        Image icon = new Image(fileURI.toString(), 250, 250, true, true);
        currentIcon.setImage(icon);
        
        Integer temperature = (int) Math.round(we.getTemperature());
        
        Label tempLabel = new Label(temperature + " °C");
        tempLabel.getStyleClass().add("text");
        tempLabel.setFont(new Font("Arial", 180));
		
		FlowPane.setMargin(currentIcon, new Insets(10,10,10,10));
		FlowPane.setMargin(tempLabel, new Insets(10,10,10,50));
		comp.getChildren().addAll(currentIcon, tempLabel);
		currentWeatherComponent = comp;
	}
	
	private void getForecastComponent() {
		VBox comp = new VBox();
		comp.setFillWidth(true);
		comp.setAlignment(Pos.TOP_CENTER);
		
		for (Integer i = 1; i < 6; i++) {
			FlowPane hourForecast = new FlowPane();
			
        	WeatherEvent we = ws.weatherElements.get(i);
        	
    		LocalDateTime time = LocalDateTime.parse(we.getDateTime(), DateTimeFormatter.ISO_DATE_TIME);
    		Integer offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
    		time = time.plusSeconds(offset / 1000);
    		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

			Label hourLabel = new Label(time.format(timeFormat));
			hourLabel.getStyleClass().add("text");
			hourLabel.setFont(new Font("Arial", 100));
			
			FlowPane.setMargin(hourLabel, new Insets(10,10,10,10));
			
			URL fileURI = Infoboard.class.getClass().getResource("/images/" + + we.getDayNight() + "_" + we.getSymbol() + ".png");
			
	        ImageView weatherIcon = new ImageView();
	        weatherIcon.getStyleClass().add("text");
	        Image icon = new Image(fileURI.toString(), 100, 100, true, true);
	        weatherIcon.setImage(icon);
	        
	        FlowPane.setMargin(weatherIcon, new Insets(10,10,10,50));
			
	        Integer temperature = (int) Math.round(we.getTemperature());
	        
	        Label tempLabel = new Label(temperature + " °C");
			tempLabel.getStyleClass().add("text");
			tempLabel.setFont(new Font("Arial", 100));
			
			FlowPane.setMargin(tempLabel, new Insets(10,10,10,50));
			
			hourForecast.getChildren().addAll(hourLabel, weatherIcon, tempLabel);
			VBox.setMargin(hourForecast, new Insets(10,10,10,10));
			comp.getChildren().add(hourForecast);
		}
		
		forecastComponent = comp;
	}
}
