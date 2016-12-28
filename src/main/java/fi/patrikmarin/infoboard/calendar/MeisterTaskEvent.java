package fi.patrikmarin.infoboard.calendar;

import java.time.ZonedDateTime;

import fi.patrikmarin.infoboard.utils.Utils;

public class MeisterTaskEvent extends CommonEvent {

	private String title;
	private String notes;
	private ZonedDateTime due;
	
	protected MeisterTaskEvent(String ID, CommonEventContainer parent, String title, String notes, String ts) {
		super(ID, parent);
		
		this.title = title;
		this.notes = notes;
		this.due = Utils.timestampToZonedDateTime(ts);
		
		this.compareBy = this.due;
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

}
