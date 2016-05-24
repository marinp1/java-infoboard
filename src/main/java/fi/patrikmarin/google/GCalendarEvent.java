package fi.patrikmarin.google;

import java.util.Comparator;

import com.google.api.client.util.DateTime;

import javafx.scene.paint.Color;

public class GCalendarEvent extends GEvent {
	private GCalendar parent;
	private String ID;
	private String summary;
	private String location;
	private String description;
	private Color color;
	private DateTime start;
	private DateTime end;
	
	public GCalendar getParent() {
		return parent;
	}
	
	public String getID() {
		return ID;
	}
	
	public String getSummary() {
		return summary;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Color getColor() {
		return color;
	}
	
	public DateTime getStart() {
		return start;
	}
	
	public DateTime getEnd() {
		return end;
	}
	
	public void setParent(GCalendar parent) {
		this.parent = parent;
	}
	
	public void setID(String iD) {
		ID = iD;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setStart(DateTime start) {
		this.start = start;
		this.compareBy = start;
	}
	
	public void setEnd(DateTime end) {
		this.end = end;
	}
}