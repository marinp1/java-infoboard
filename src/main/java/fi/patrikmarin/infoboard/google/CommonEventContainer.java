package fi.patrikmarin.infoboard.google;

import javafx.scene.paint.Color;

/**
 * Represents all kinds of containers for events
 */
public class CommonEventContainer {

	private String ID;
	private String name;
	private Color color;
	private Boolean enabled;
	private CommonContainerType type;
	
	protected CommonEventContainer(CommonContainerType type) {
		this.type = type;
	}
	
	protected CommonEventContainer(String ID, String name, Color color, Boolean enabled, CommonContainerType type) {
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
	
	public CommonContainerType getType() {
		return type;
	}
	
	public void setType(CommonContainerType type) {
		this.type = type;
	}
}
