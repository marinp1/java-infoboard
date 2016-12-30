package fi.patrikmarin.infoboard.controller;

import static fi.patrikmarin.infoboard.utils.Utils.*;

import fi.patrikmarin.infoboard.calendar.CommonContainerType;
import fi.patrikmarin.infoboard.calendar.CommonEvent;
import fi.patrikmarin.infoboard.calendar.GoogleCalendarEvent;
import fi.patrikmarin.infoboard.calendar.GoogleTaskEvent;
import fi.patrikmarin.infoboard.calendar.MeisterTaskEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class CalendarEventController {
	@FXML
	private GridPane calendarEventBox;
	
	@FXML
	private Label calendarEventTimeLabel;
	
	@FXML
	private Label calendarEventTitleLabel;
	
	@FXML
	private Label calendarEventInfoLabel;
	
	//TODO: The label names are completely wrong
	public void addEvent(CommonEvent ge) {
		
		if (ge.getParent().getType() == CommonContainerType.GOOGLE_CALENDAR) {
			GoogleCalendarEvent gce = (GoogleCalendarEvent) ge;
			
			String location = gce.getLocation();
			
			String titleLabel = gce.getLength();
			
			if (location != null) {
				titleLabel += " - " + location;
			}
			
			if (gce.getLength().equals("All day")) {
				calendarEventTimeLabel.setText(gce.getSummary());
				calendarEventInfoLabel.setText("");
			} else {
				calendarEventTimeLabel.setText(gce.getStartDateTime().format(timeFormat));
				calendarEventInfoLabel.setText(gce.getSummary());
			}
			
			calendarEventTitleLabel.setText(titleLabel);
			
			
		} else if (ge.getParent().getType() == CommonContainerType.GOOGLE_TASKLIST) {
			GoogleTaskEvent gte = (GoogleTaskEvent) ge;

			// Dont display task due time
			calendarEventTimeLabel.setText(gte.getTitle());
			calendarEventTitleLabel.setText(gte.getNotes());
			calendarEventInfoLabel.setText("");
			
		} else if (ge.getParent().getType() == CommonContainerType.MEISTERTASK_TASKLIST) {
			
			MeisterTaskEvent me = (MeisterTaskEvent) ge;
			
			// Dont display task due time
			calendarEventTimeLabel.setText(me.getTitle());
			calendarEventTitleLabel.setText(me.getNotes());
			calendarEventInfoLabel.setText("");
			
		}

	}
}
