package fi.patrikmarin.google;

import javafx.scene.paint.Color;

public class GCalendar {
	private Color color;
	private String name;
	private String ID;
	
	public void setColor(Color c) {
		color = c;
	}
	
	public void setName(String s) {
		name = s;
	}
	
	public void setID(String s) {
		ID = s;
	}
	
	public Color getColor() {
		return color;
	}
	
	public String getName() {
		return name;
	}
	
	public String getID() {
		return ID;
	}
	
	public GCalendar() {
		
	}
	
	public GCalendar(String id, String name, Color col) {
		setID(id);
		setName(name);
		setColor(col);
	}
}
