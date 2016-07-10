package fi.patrikmarin.infoboard.google;

import javafx.scene.paint.Color;

/**
 * Represents both a Google calendar and a Google task list.
 */
public class GoogleEventContainer {

	private String ID;
	private String name;
	private Color color;
	private Boolean enabled;
	private GoogleContainerType type;
	
	protected GoogleEventContainer(GoogleContainerType type) {
		this.type = type;
	}
	
	protected GoogleEventContainer(String ID, String name, Color color, Boolean enabled, GoogleContainerType type) {
		this.ID = ID;
		this.name = name;
		this.color = color;
		this.enabled = enabled;
		this.type = type;
	}
	
	public String getID() {
		return ID;
	}
	
	public void setID(String ID) {
		this.ID = ID;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public Boolean getEnabled() {
		return enabled;
	}
	
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	public GoogleContainerType getType() {
		return type;
	}
	
	public void setType(GoogleContainerType type) {
		this.type = type;
	}
}
