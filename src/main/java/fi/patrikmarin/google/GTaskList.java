package fi.patrikmarin.google;

public class GTaskList {
	private String ID;
	private String title;
	private String link;
	private Boolean enabled;
	
	public String getID() {
		return ID;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getLink() {
		return link;
	}
	
	public Boolean getEnabled() {
		return enabled;
	}
	
	public void setID(String iD) {
		ID = iD;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
	
	public void setEnabled(Boolean b) {
		enabled = b;
	}
	
	public GTaskList() {
		enabled = true;
	}
}
