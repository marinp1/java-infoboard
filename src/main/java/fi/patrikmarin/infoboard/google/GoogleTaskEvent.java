package fi.patrikmarin.infoboard.google;

import com.google.api.client.util.DateTime;

/**
 * Represents a single Google task event.
 */
public class GoogleTaskEvent extends GoogleEvent {

	private String title;
	private String notes;
	private DateTime due;
	private Boolean completed;
	
	/**
	 * The constructor for a task event.
	 * @param ID
	 * @param parent
	 * @param title
	 * @param notes
	 * @param due
	 * @param completed
	 */
	public GoogleTaskEvent(String ID, GoogleEventContainer parent, String title, String notes, DateTime due,
			Boolean completed) {
		
		super(ID, parent);
		
		this.title = title;
		this.notes = notes;
		this.due = due;
		this.completed = completed;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public DateTime getDue() {
		return due;
	}
	
	public void setDue(DateTime due) {
		this.due = due;
	}
	
	public Boolean getCompleted() {
		return completed;
	}
	
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
	
}
