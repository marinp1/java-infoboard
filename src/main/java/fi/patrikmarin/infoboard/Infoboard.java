package fi.patrikmarin.infoboard;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import fi.patrikmarin.data.DateDifference;
import fi.patrikmarin.data.DateParser;
import fi.patrikmarin.data.SettingsService;
import fi.patrikmarin.google.CalendarService;
import fi.patrikmarin.google.GCalendar;
import fi.patrikmarin.google.GCalendarEvent;
import fi.patrikmarin.google.GEvent;
import fi.patrikmarin.google.GTaskEvent;
import fi.patrikmarin.google.GTaskList;
import fi.patrikmarin.google.TasksService;
import fi.patrikmarin.weather.WeatherEvent;
import fi.patrikmarin.weather.WeatherService;

//BorderPane (root)
	//MenuBar (mainMenu)
	//StackPane	(programRoot)
		//VBox (program)
			//CalendarItem
			//CalendarItem2...
		//StackPane (settingsWindow)

public class Infoboard extends Application {
	
	public static BorderPane root;
	public static StackPane programRoot;
	
	CalendarService cs;
	TasksService ts;
	WeatherService ws;
	
	SettingsWindow sWindow = null;
	
	Stage mainStage;
	
    VBox program = new VBox();
    
	private Integer secondsSinceLastUpdate = 0;
	
	public ArrayList<GEvent> combinedEvents = new ArrayList<GEvent>();
	
	@Override
	public void init() {
        SettingsService.readSettings();
        cs = new CalendarService();
        ts = new TasksService();
        ws = new WeatherService();
	}
	
	public static void main(String args[]) {
		launch(args);
	}
	
	@Override
	public void start(Stage mainStage) {
		this.mainStage = mainStage;
		
        mainStage.setTitle("Infoboard v. B");
        mainStage.setMinHeight(800);
        mainStage.setMinWidth(600);
        
        root = new BorderPane();
        root.setId("pane");
 
        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(mainStage.widthProperty());
        
        Menu viewMenu = new Menu("View");
        
        Menu settingsMenu = new Menu("Settings");
        
        MenuItem weatherSettings = new MenuItem("Weather");
        MenuItem calendarSettings = new MenuItem("Calendar");
        
        calendarSettings.setOnAction(event -> displaySettings());
        
        settingsMenu.getItems().addAll(weatherSettings, calendarSettings);
        
        menuBar.getMenus().addAll(viewMenu, settingsMenu);
        
        root.setTop(menuBar);
        
        programRoot = new StackPane();
        
        root.setCenter(programRoot);
        
        Scene scene = new Scene(root, 800, 600);
        
        scene.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                    if(mouseEvent.getClickCount() == 2){
                        if(mainStage.isFullScreen()) {
                        	mainStage.setFullScreen(false);
                        	mainStage.setAlwaysOnTop(false);
                        	menuBar.setVisible(true);
                        } else {
                        	mainStage.setFullScreen(true);
                        	mainStage.setAlwaysOnTop(true);
                        	menuBar.setVisible(false);
                        }
                    }
                    updateView();
                }
            }
        });
        
        program.setFillWidth(true);
        program.setAlignment(Pos.TOP_CENTER);

        ws.getData();
        
        updateView();
        
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        mainStage.setScene(scene);
        mainStage.show();
        
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (true) {
                    javafx.application.Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                        	System.out.println("Updating... " + secondsSinceLastUpdate);
                        	
                        	if (secondsSinceLastUpdate > 300) {
                        		dataupdate();
                        		secondsSinceLastUpdate = 0;
                        	}
                        	
                        	updateView();
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
                		SettingsService.saveSettings();
                    	System.out.println("Exit successful.");
                	} catch (Exception e) {
                		System.out.println("Couldn't close update thread.");
                		e.printStackTrace();
                	}
                }
        );
	}
	
	private void displaySettings() {
		if (sWindow != null) {
			programRoot.getChildren().remove(sWindow);
			sWindow = null;
		} else {
			sWindow = new SettingsWindow();
			programRoot.getChildren().add(sWindow);
		}
		
	}
	
	public void dataupdate() {
		cs.update();
		ts.update();
		ws.getData();
		updateView();
	}
	
	public void updateView() {
		programRoot.getChildren().remove(program);
		
		program = new VBox();
		
        combineEvents();
        
        DateTimeComponent comp = new DateTimeComponent();
        
        VBox calendarItems = getItems();
        
        WeatherElement we = new WeatherElement();
        
        program.getChildren().addAll(comp, we, calendarItems);
        program.setAlignment(Pos.TOP_CENTER);
        
        
        programRoot.getChildren().add(program);
	}

	private void combineEvents() {
		combinedEvents = new ArrayList<GEvent>();
		
		for (GCalendarEvent ge : cs.events) {
			if (ge.getParent().getEnabled()) combinedEvents.add(ge);
		}
		
		for (GTaskEvent ge : ts.tasks) {
			if (!ge.getCompleted()) combinedEvents.add(ge);
		}
		
		Collections.sort(combinedEvents);
	}
	
	private VBox getItems() {
		
		VBox calendarItems = new VBox();
		calendarItems.setFillWidth(true);

    	int lastDay = -1;
    
    	for (Integer i = 0; i < Math.min(6, combinedEvents.size()); i++) {
    		GEvent mainGE = combinedEvents.get(i);
    		
    		int currentDay = DateParser.parseGDate(mainGE.getCompareBy().toString()).getDayOfYear();
    		
    		if (lastDay == -1 || currentDay != lastDay) {
	    		DayElement de = new DayElement(calendarItems.getWidth(), 40, DateParser.parseGDate(mainGE.getCompareBy().toString()));
	            
	            VBox.setMargin(de, new Insets(20, 20, 0, 20));
	            
	            calendarItems.getChildren().add(de);
    			lastDay = currentDay;
    		}
    		
    		CalendarElement ce = new CalendarElement(calendarItems.getWidth(), 100, mainGE);
    		
            VBox.setMargin(ce, new Insets(0, 20, 0, 20));
    		calendarItems.getChildren().add(ce);
    		
    	}
    	
    	return calendarItems;
	}
	
	class WeatherElement extends VBox {
		
		public WeatherElement() {
			
			this.setFillWidth(true);
			this.setAlignment(Pos.TOP_CENTER);
			
			for (Integer i = 0; i < 2; i++) {
				FlowPane hourForecast = new FlowPane();
				
	        	WeatherEvent we = ws.weatherElements.get(i);
	        	
	    		LocalDateTime time = LocalDateTime.parse(we.getDateTime(), DateTimeFormatter.ISO_DATE_TIME);
	    		Integer offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
	    		time = time.plusSeconds(offset / 1000);
	    		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

				Label hourLabel = new Label(time.format(timeFormat));
				hourLabel.getStyleClass().add("timedatetext");
				hourLabel.setFont(new Font("Arial", 100));
				
				FlowPane.setMargin(hourLabel, new Insets(10,10,10,10));
				
				URL fileURI = Infoboard.class.getClass().getResource("/images/" + + we.getDayNight() + "_" + we.getSymbol() + ".png");
				
		        ImageView weatherIcon = new ImageView();
		        weatherIcon.getStyleClass().add("timedatetext");
		        Image icon = new Image(fileURI.toString(), 100, 100, true, true);
		        weatherIcon.setImage(icon);
		        
		        FlowPane.setMargin(weatherIcon, new Insets(10,10,10,50));
				
		        Integer temperature = (int) Math.round(we.getTemperature());
		        
		        Label tempLabel = new Label(temperature + " °C");
				tempLabel.getStyleClass().add("timedatetext");
				tempLabel.setFont(new Font("Arial", 100));
				
				FlowPane.setMargin(tempLabel, new Insets(10,10,10,50));
				
				hourForecast.getChildren().addAll(hourLabel, weatherIcon, tempLabel);
				hourForecast.setAlignment(Pos.CENTER);
				VBox.setMargin(hourForecast, new Insets(10,10,10,10));
				this.getChildren().add(hourForecast);
				
				this.setPadding(new Insets(10));
				this.setAlignment(Pos.TOP_CENTER);
			}
		}
		
	}
	
	class DayElement extends StackPane {
		DayElement(double w, double h, LocalDateTime dt) {
            super.setWidth(w);
            super.setHeight(h);
            
            Rectangle bg = new Rectangle(w, h);
            bg.getStyleClass().add("calendarDayElement");
            bg.widthProperty().bind(this.widthProperty().subtract(20));
            
            DateTimeFormatter dayFormat = DateTimeFormatter.ofPattern("EEE dd.MM.yyyy");

            Label dateLabel = new Label(dt.format(dayFormat));
            dateLabel.setFont(new Font("Arial", 24));
            dateLabel.getStyleClass().add("calendarEventSecondaryText");
            
            StackPane.setMargin(dateLabel, new Insets(0,0,0,20));
            StackPane.setAlignment(dateLabel, Pos.CENTER);
            
            this.setPadding(new Insets(5));
            this.getChildren().addAll(bg, dateLabel);
		}
	}
	
	class DateTimeComponent extends FlowPane {
		DateTimeComponent() {
			this.setOrientation(Orientation.HORIZONTAL);
			
			Date date = new Date();
			
		    DateFormat timeFormat = new SimpleDateFormat("HH:mm");
		    String curTime = timeFormat.format(date);
		    
		    DateFormat dateFormat = new SimpleDateFormat("E dd.MM.yyyy\n'Viikko' w");
		    String curDate = dateFormat.format(date);
			
			Label timeLabel = new Label(curTime);
			timeLabel.getStyleClass().add("timedatetext");
			timeLabel.setFont(new Font("Arial", 120));
			
			Label dateLabel = new Label(curDate.substring(0,1).toUpperCase() + curDate.substring(1));
			dateLabel.getStyleClass().add("timedatetext");
			dateLabel.setFont(new Font("Arial", 50));
			dateLabel.setAlignment(Pos.CENTER);
			
			FlowPane.setMargin(timeLabel, new Insets(10,10,10,10));
			FlowPane.setMargin(dateLabel, new Insets(10,10,10,50));
			
			this.getChildren().addAll(timeLabel, dateLabel);
			
			this.setPadding(new Insets(10));
			this.setAlignment(Pos.TOP_CENTER);
		}
	}
	
	class SettingsWindow extends StackPane {
		SettingsWindow() {
			
            super.setWidth(programRoot.getWidth());
            super.setHeight(programRoot.getHeight());
            
            Rectangle bg = new Rectangle(programRoot.getWidth() - 0, programRoot.getHeight() - 0);
            bg.getStyleClass().add("settingElement");
            bg.widthProperty().bind(this.widthProperty().subtract(0));
            bg.heightProperty().bind(this.heightProperty().subtract(0));
            
            VBox calendarList = new VBox();
            calendarList.setFillWidth(true);
            
            Label calendars = new Label ("Visible calendars:");
            calendars.setFont(new Font("Arial", 25));
            calendars.setPadding(new Insets(0,0,10,0));
            
            calendarList.getChildren().add(calendars);
            
            for (GCalendar gc : cs.calendars) {
            	CheckBox cb = new CheckBox(gc.getName());
            	cb.setSelected(gc.getEnabled());
            	
            	cb.setFont(new Font("Arial", 18));
            	cb.setPadding(new Insets(10));
            	cb.setWrapText(true);
            	
                cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    public void changed(ObservableValue<? extends Boolean> ov,
                      Boolean old_val, Boolean new_val) {
                    	gc.setEnabled(cb.isSelected());
                   }
                 });
            	
            	calendarList.getChildren().add(cb);
            }
            
            Label tasks = new Label ("Visible tasklists:");
            tasks.setFont(new Font("Arial", 25));
            tasks.setPadding(new Insets(20,0,10,0));
            
            calendarList.getChildren().add(tasks);
            
            for (GTaskList gc : ts.tasklists) {
            	CheckBox cb = new CheckBox(gc.getTitle());
            	cb.setSelected(gc.getEnabled());
            	
            	cb.setFont(new Font("Arial", 18));
            	cb.setPadding(new Insets(10));
            	cb.setWrapText(true);
            	
                cb.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    public void changed(ObservableValue<? extends Boolean> ov,
                      Boolean old_val, Boolean new_val) {
                    	gc.setEnabled(cb.isSelected());
                   }
                 });
            	
            	calendarList.getChildren().add(cb);
            }
            
            Button saveBtn = new Button("Save changes");
            saveBtn.setAlignment(Pos.BOTTOM_CENTER);
            saveBtn.setOnAction(event -> {
            	updateView();
            	displaySettings();
            });
            
            VBox.setMargin(saveBtn,new Insets(30, 10, 10, 10));

            calendarList.getChildren().add(saveBtn);
            StackPane.setMargin(calendarList, new Insets(30));

            this.getChildren().addAll(bg, calendarList);
		}
	}
	
	class CalendarElement extends StackPane {

		CalendarElement(double w, double h, GEvent event) {
            super.setWidth(w);
            
            Rectangle bg = new Rectangle(w, h);
            bg.getStyleClass().add("calendarElement");
            bg.widthProperty().bind(this.widthProperty().subtract(20));
            bg.heightProperty().bind(this.heightProperty());
            
            this.getChildren().add(bg);
            
            HBox fp = new HBox();
            
            StackPane icon = getIcon(h, event);
            
            GridPane gp = getGrid(event);
    		
            HBox.setMargin(gp, new Insets(10,0,10,20));
            HBox.setMargin(icon, new Insets(10,0,10,10));

            fp.getChildren().addAll(icon, gp);
            
            StackPane.setMargin(fp, new Insets(0, 0, 0, 10));
            StackPane.setAlignment(fp, Pos.CENTER_LEFT);
            this.getChildren().add(fp);
        }
		
		private StackPane getIcon(Double h, GEvent event) {
			Color fillColor;
			String squareText;
			
			if (event instanceof GCalendarEvent) {
				GCalendarEvent ge = (GCalendarEvent) event;
				
				fillColor = ge.getParent().getColor();
				squareText = ge.getParent().getName().substring(0, 1).toUpperCase();
			} else {
				GTaskEvent ge = (GTaskEvent) event;
				
				fillColor = Color.BLUE;
				squareText = "N";
			}
			
            StackPane icon = new StackPane();
            
            Rectangle iconBG = new Rectangle(h - 20, h - 20);
            iconBG.setFill(fillColor);
            iconBG.getStyleClass().add("calendarIcon");
            
            Label iconLabel = new Label(squareText);
            iconLabel.getStyleClass().add("calendarRectangleText");
            iconLabel.setFont(new Font("Arial", 45));
            iconLabel.setAlignment(Pos.CENTER);
            iconLabel.setTextAlignment(TextAlignment.CENTER);
            
            icon.getChildren().addAll(iconBG, iconLabel);
            
            return icon;
		}
		
		private GridPane getGrid(GEvent event) {
			if (event instanceof GCalendarEvent) {
				return getCalendarGrid((GCalendarEvent) event);
			} else {
				return getTaskGrid((GTaskEvent) event);
			}
		}
		
		private GridPane getCalendarGrid(GCalendarEvent event) {
			GridPane gp = new GridPane();
			
            String startAsString = event.getStart().toStringRfc3339();
            LocalDateTime parsedStart = DateParser.parseGDate(startAsString);
            
            String endAsString = event.getEnd().toStringRfc3339();
            LocalDateTime parsedEnd = DateParser.parseGDate(endAsString);
            
            DateDifference dd = DateParser.timeBetween(parsedStart, parsedEnd);
            
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
            
            Label mainLabel = new Label(parsedStart.format(timeFormat));
            mainLabel.getStyleClass().add("calendarEventMainTime");
            mainLabel.setFont(new Font("Arial", 30));
    		mainLabel.setAlignment(Pos.CENTER_LEFT);
    		mainLabel.setMinWidth(Region.USE_PREF_SIZE);
    		
            Label secondLabel = new Label(event.getSummary().toString());
            secondLabel.getStyleClass().add("calendarEventMainText");
            secondLabel.setFont(new Font("Arial", 30));
    		secondLabel.setAlignment(Pos.CENTER_LEFT);
    		secondLabel.setTextOverrun(OverrunStyle.WORD_ELLIPSIS);
    		
    		String finalString = dd.getCalendarReadable();
    		finalString += (event.getLocation() == null) ? "" : " - " + event.getLocation();
    		
            Label bottomLabel = new Label(finalString);
            bottomLabel.getStyleClass().add("calendarEventSecondaryText");
            bottomLabel.setFont(new Font("Arial", 20));
            bottomLabel.setAlignment(Pos.CENTER_LEFT);
            bottomLabel.setTextOverrun(OverrunStyle.WORD_ELLIPSIS);
    		
            if (dd.isWholeDay()) {
        		GridPane.setConstraints(secondLabel, 0, 0);
        		GridPane.setConstraints(bottomLabel, 0, 1);
        		gp.getChildren().addAll(secondLabel, bottomLabel);
            } else {
        		GridPane.setConstraints(mainLabel, 0, 0);
        		GridPane.setConstraints(secondLabel, 1, 0);
        		GridPane.setConstraints(bottomLabel, 0, 1);
        		GridPane.setColumnSpan(bottomLabel, 2);
        		gp.getChildren().addAll(mainLabel, secondLabel, bottomLabel);
            }
            
    		gp.setVgap(5);
    		gp.setHgap(15);
    		gp.setAlignment(Pos.CENTER_LEFT);
    		
    		return gp;
		}
		
		private GridPane getTaskGrid(GTaskEvent event) {
			GridPane gp = new GridPane();

            Label titleLabel = new Label(event.getTitle());
            titleLabel.getStyleClass().add("calendarEventMainText");
            titleLabel.setFont(new Font("Arial", 30));
            titleLabel.setAlignment(Pos.CENTER_LEFT);
            titleLabel.setTextOverrun(OverrunStyle.WORD_ELLIPSIS);
            
            String dueAsString = event.getDue().toStringRfc3339();
            LocalDateTime parsedDue = DateParser.parseGDate(dueAsString);
    		
            DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
            
            Label descriptionLabel = new Label("Due: " + parsedDue.format(timeFormat));
            descriptionLabel.getStyleClass().add("calendarEventSecondaryText");
            descriptionLabel.setFont(new Font("Arial", 20));
            descriptionLabel.setAlignment(Pos.CENTER_LEFT);
            descriptionLabel.setTextOverrun(OverrunStyle.WORD_ELLIPSIS);

    		GridPane.setConstraints(titleLabel, 0, 0);
    		GridPane.setConstraints(descriptionLabel, 0, 1);
    		gp.getChildren().addAll(titleLabel, descriptionLabel);
            
    		gp.setVgap(5);
    		gp.setHgap(15);
    		gp.setAlignment(Pos.CENTER_LEFT);
    		
    		return gp;
		}
	}
}
	
