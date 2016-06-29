package fi.patrikmarin.infoboard.google;

import com.google.api.client.util.DateTime;

import javafx.scene.paint.Color;

/**
 * Represents a single Google calendar event.
 */
public class GoogleCalendarEvent extends GoogleEvent {
	private String summary;
	private String location;
	private String description;
	private Color color;
	private DateTime startDateTime;
	private DateTime endDateTime;
	
	/**
	 * The constructor for calendar event.
	 * @param ID
	 * @param parent
	 * @param summary
	 * @param location
	 * @param description
	 * @param color
	 * @param startDateTime
	 * @param endDateTime
	 */
	public GoogleCalendarEvent(String ID, GoogleEventContainer parent, String summary, String location, String description, Color color,
			DateTime startDateTime, DateTime endDateTime) {
		
		super(ID, parent);
		this.summary = summary;
		this.location = location;
		this.description = description;
		this.color = color;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public DateTime getStartDateTime() {
		return startDateTime;
	}
	
	public void setStartDateTime(DateTime startDateTime) {
		this.startDateTime = startDateTime;
	}
	
	public DateTime getEndDateTime() {
		return endDateTime;
	}
	
	public void setEndDateTime(DateTime endDateTime) {
		this.endDateTime = endDateTime;
	}
}
