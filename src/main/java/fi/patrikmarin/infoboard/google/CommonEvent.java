package fi.patrikmarin.infoboard.google;

import java.time.ZonedDateTime;

/**
 * Abstract class representing both
 * Google calendar events and tasks.
 * Sortable.
 */
public abstract class CommonEvent implements Comparable<CommonEvent> {
	
	private String ID;
	private CommonEventContainer parent;
	
	/** 
	 * The constructor for the superclass.
	 * @param ID the ID of the element.
	 */
	protected CommonEvent(String ID, CommonEventContainer parent) {
		this.ID = ID;
		this.parent = parent;
	}
	
	public String getID() {
		return ID;
	}
	
	public void setID(String iD) {
		ID = iD;
	}
	
	public CommonEventContainer getParent() {
		return parent;
	}
	
	public void setParent(CommonEventContainer parent) {
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
	public int compareTo(CommonEvent ev) {
		
		if (this.parent.getType() == CommonContainerType.GOOGLE_CALENDAR 
				&& ev.parent.getType() != CommonContainerType.GOOGLE_CALENDAR) {
			return 1;
		}
		
		if (this.parent.getType() == CommonContainerType.GOOGLE_TASKLIST
				&& ev.parent.getType() == CommonContainerType.GOOGLE_CALENDAR) {
			return -1;
		}
		
		if (this.parent.getType() == CommonContainerType.MEISTERTASK_TASKLIST
				&& ev.parent.getType() == CommonContainerType.GOOGLE_CALENDAR) {
			return -1;
		}
		
		return this.compareBy.compareTo(ev.compareBy);
	}
}
