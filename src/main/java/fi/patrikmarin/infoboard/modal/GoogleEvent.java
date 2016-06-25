package fi.patrikmarin.infoboard.modal;

import java.time.LocalDateTime;

import javafx.scene.paint.Color;

public class GoogleEvent {
	private String ID;
	private String summary;
	private String location;
	private String description;
	private Color color;
	private LocalDateTime start;
	private LocalDateTime end;
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
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
	public LocalDateTime getStart() {
		return start;
	}
	public void setStart(LocalDateTime start) {
		this.start = start;
	}
	public LocalDateTime getEnd() {
		return end;
	}
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}
	public GoogleEvent(String iD, String summary, String location, String description, Color color, LocalDateTime start,
			LocalDateTime end) {
		super();
		ID = iD;
		this.summary = summary;
		this.location = location;
		this.description = description;
		this.color = color;
		this.start = start;
		this.end = end;
	}


	
}
