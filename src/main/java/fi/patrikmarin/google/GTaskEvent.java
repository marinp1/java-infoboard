package fi.patrikmarin.google;

import com.google.api.client.util.DateTime;


public class GTaskEvent extends GEvent  {
	private String ID;
	private String title;
	private String notes;
	private DateTime due;
	private Boolean completed;
	private GTaskList parent;

	public String getID() {
		return ID;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public DateTime getDue() {
		return due;
	}
	
	public Boolean getCompleted() {
		return completed;
	}
	
	public GTaskList getParent() {
		return parent;
	}
	
	public void setID(String id) {
		ID = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public void setDue(DateTime due) {
		this.due = due;
		compareBy = due;
	}
	
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public void setParent(GTaskList parent) {
		this.parent = parent;
	}
}
