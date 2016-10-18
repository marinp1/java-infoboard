package fi.patrikmarin.infoboard.google;

import java.time.ZonedDateTime;

/**
 * Abstract class representing both
 * Google calendar events and tasks.
 * Sortable.
 */
public abstract class GoogleEvent implements Comparable<GoogleEvent> {
	
	private String ID;
	private GoogleEventContainer parent;
	
	/** 
	 * The constructor for the superclass.
	 * @param ID the ID of the element.
	 */
	protected GoogleEvent(String ID, GoogleEventContainer parent) {
		this.ID = ID;
		this.parent = parent;
	}
	
	public String getID() {
		return ID;
	}
	
	public void setID(String iD) {
		ID = iD;
	}
	
	public GoogleEventContainer getParent() {
		return parent;
	}
	
	public void setParent(GoogleEventContainer parent) {
		this.parent = parent;
	}
	
	/**
	 * The DateTime which is used for comparing objects.
	 * Must be defined for all subclasses.
	 */
	protected ZonedDateTime compareBy;
	
	/**
	 * The comparison function for Google events.
	 * The comparison parameter is the start time of the event for
	 * calendar events and the deadline for tasks.
	 * 
	 * Update:
	 * Tasks are always first
	 */
	@Override
	public int compareTo(GoogleEvent ev) {
		
		if (this.parent.getType() == GoogleContainerType.CALENDAR 
				&& ev.parent.getType() == GoogleContainerType.TASKLIST) {
			return 1;
		}
		
		if (this.parent.getType() == GoogleContainerType.TASKLIST 
				&& ev.parent.getType() == GoogleContainerType.CALENDAR) {
			return -1;
		}
		
		return this.compareBy.compareTo(ev.compareBy);
	}
}
