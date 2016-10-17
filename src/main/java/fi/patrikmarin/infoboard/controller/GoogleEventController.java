package fi.patrikmarin.infoboard.controller;

import static fi.patrikmarin.infoboard.utils.Utils.*;

import fi.patrikmarin.infoboard.google.GoogleCalendarEvent;
import fi.patrikmarin.infoboard.google.GoogleContainerType;
import fi.patrikmarin.infoboard.google.GoogleEvent;
import fi.patrikmarin.infoboard.google.GoogleTaskEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class GoogleEventController {
	@FXML
	private GridPane googleEventBox;
	
	@FXML
	private Label googleEventTimeLabel;
	
	@FXML
	private Label googleEventTitleLabel;
	
	@FXML
	private Label googleEventInfoLabel;
	
	//TODO: The label names are completely wrong
	public void addEvent(GoogleEvent ge) {
		
		if (ge.getParent().getType() == GoogleContainerType.CALENDAR) {
			GoogleCalendarEvent gce = (GoogleCalendarEvent) ge;
			
			String location = gce.getLocation();
			
			String titleLabel = gce.getLength();
			
			if (location != null) {
				titleLabel += " - " + location;
			}
			
			if (gce.getLength().equals("All day")) {
				googleEventTimeLabel.setText(gce.getSummary());
				googleEventInfoLabel.setText("");
			} else {
				googleEventTimeLabel.setText(gce.getStartDateTime().format(timeFormat));
				googleEventInfoLabel.setText(gce.getSummary());
			}
			
			googleEventTitleLabel.setText(titleLabel);
			
			
		} else {
			GoogleTaskEvent gte = (GoogleTaskEvent) ge;

			// Dont display task due time
			googleEventTimeLabel.setText(gte.getTitle());
			googleEventTitleLabel.setText(gte.getNotes());
			googleEventInfoLabel.setText("");
		}

	}
}
