package fi.patrikmarin.data;

public class DateDifference {
	private long years;
	private long months;
	private long days;
	private long hours;
	private long minutes;
	private long seconds;
	
	public DateDifference(long years, long months, long days, long hours, long minutes, long seconds) {
		super();
		this.years = years;
		this.months = months;
		this.days = days;
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
	}

	public long getYears() {
		return years;
	}
	public long getMonths() {
		return months;
	}
	public long getDays() {
		return days;
	}
	public long getHours() {
		return hours;
	}
	public long getMinutes() {
		return minutes;
	}
	public long getSeconds() {
		return seconds;
	}
	public void setYears(long years) {
		this.years = years;
	}
	public void setMonths(long months) {
		this.months = months;
	}
	public void setDays(long days) {
		this.days = days;
	}
	public void setHours(long hours) {
		this.hours = hours;
	}
	public void setMinutes(long minutes) {
		this.minutes = minutes;
	}
	public void setSeconds(long seconds) {
		this.seconds = seconds;
	}
	
	public Boolean isWholeDay() {
		return (this.minutes == 0 && this.hours == 0 && this.days == 1);
	}
	
	public String getCalendarReadable() {
		if (isWholeDay()) {
			return "Koko päivä";
		}
		
		if (this.hours == 0) {
			return this.minutes + "min";
		}

		if (this.minutes == 0) {
			return this.hours + "h";
		} else {
			return this.hours + "h " + this.minutes + "min";
		}
	}
}
