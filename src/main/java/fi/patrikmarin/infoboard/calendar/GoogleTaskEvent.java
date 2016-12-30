package fi.patrikmarin.infoboard.calendar;

import fi.patrikmarin.infoboard.utils.Utils;
import java.time.ZonedDateTime;

import com.google.api.client.util.DateTime;

/**
 * Represents a single Google task event.
 */
public class GoogleTaskEvent extends CommonEvent {

	private String title;
	private String notes;
	private ZonedDateTime due;
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
	public GoogleTaskEvent(String ID, CommonEventContainer parent, String title, String notes, DateTime due,
			Boolean completed) {
		
		super(ID, parent);
		
		this.title = title;
		this.notes = notes;
		this.due = Utils.parseGoogleDate(due.toStringRfc3339());
		this.completed = completed;
		
		this.compareBy = Utils.parseGoogleDate(due.toStringRfc3339());
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
	
	public ZonedDateTime getDue() {
		return due;
	}
	
	public void setDue(ZonedDateTime due) {
		this.due = due;
	}
	
	public Boolean getCompleted() {
		return completed;
	}
	
	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}
	
}
