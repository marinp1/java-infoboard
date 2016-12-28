package fi.patrikmarin.infoboard.google;

import java.time.ZonedDateTime;

import com.google.api.client.util.DateTime;

import fi.patrikmarin.infoboard.utils.Utils;
import javafx.scene.paint.Color;

/**
 * Represents a single Google calendar event.
 */
public class GoogleCalendarEvent extends CommonEvent {
	private String summary;
	private String location;
	private String description;
	private Color color;
	private ZonedDateTime startDateTime;
	private ZonedDateTime endDateTime;
	private String length;
	
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
	public GoogleCalendarEvent(String ID, CommonEventContainer parent, String summary, String location, String description, Color color,
			DateTime startDateTime, DateTime endDateTime) {
		
		super(ID, parent);
		
		this.summary = summary;
		this.location = location;
		this.description = description;
		this.color = color;
		this.startDateTime = Utils.parseGoogleDate(startDateTime.toStringRfc3339());
		this.endDateTime = Utils.parseGoogleDate(endDateTime.toStringRfc3339());
		
		this.length = Utils.dateTimeDifference(this.startDateTime, this.endDateTime);
		
		this.compareBy = this.startDateTime;
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
	
	public ZonedDateTime getStartDateTime() {
		return startDateTime;
	}
	
	public void setStartDateTime(ZonedDateTime startDateTime) {
		this.startDateTime = startDateTime;
	}
	
	public ZonedDateTime getEndDateTime() {
		return endDateTime;
	}
	
	public void setEndDateTime(ZonedDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}
	
	public String getLength() {
		return this.length;
	}
}
