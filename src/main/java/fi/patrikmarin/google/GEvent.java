package fi.patrikmarin.google;

import com.google.api.client.util.DateTime;


public abstract class GEvent implements Comparable<GEvent> {
	protected DateTime compareBy;
	
	@Override
	public int compareTo(GEvent ev) {
		return Long.compare(this.compareBy.getValue(), ev.compareBy.getValue());
	}
	
	public DateTime getCompareBy() {
		return compareBy;
	}
}
