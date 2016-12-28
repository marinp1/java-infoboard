package fi.patrikmarin.infoboard.controller;

import static fi.patrikmarin.infoboard.utils.Utils.*;

import fi.patrikmarin.infoboard.google.CommonContainerType;
import fi.patrikmarin.infoboard.google.CommonEvent;
import fi.patrikmarin.infoboard.google.GoogleCalendarEvent;
import fi.patrikmarin.infoboard.google.GoogleTaskEvent;
import fi.patrikmarin.infoboard.google.MeisterTaskEvent;
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
	public void addEvent(CommonEvent ge) {
		
		if (ge.getParent().getType() == CommonContainerType.GOOGLE_CALENDAR) {
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
			
			
		} else if (ge.getParent().getType() == CommonContainerType.GOOGLE_TASKLIST) {
			GoogleTaskEvent gte = (GoogleTaskEvent) ge;

			// Dont display task due time
			googleEventTimeLabel.setText(gte.getTitle());
			googleEventTitleLabel.setText(gte.getNotes());
			googleEventInfoLabel.setText("");
			
		} else if (ge.getParent().getType() == CommonContainerType.MEISTERTASK_TASKLIST) {
			
			MeisterTaskEvent me = (MeisterTaskEvent) ge;
			
			// Dont display task due time
			googleEventTimeLabel.setText(me.getTitle());
			googleEventTitleLabel.setText(me.getNotes());
			googleEventInfoLabel.setText("");
			
		}

	}
}
