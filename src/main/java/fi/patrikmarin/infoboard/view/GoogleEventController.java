package fi.patrikmarin.infoboard.view;

import static fi.patrikmarin.infoboard.utils.Utils.*;

import fi.patrikmarin.infoboard.google.GoogleEvent;
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
	
	public void addEvent(GoogleEvent ge) {
		googleEventTimeLabel.setText(ge.getStart().format(timeFormat));
		googleEventTitleLabel.setText(ge.getSummary());
		googleEventInfoLabel.setText(ge.getDescription());
	}
}
