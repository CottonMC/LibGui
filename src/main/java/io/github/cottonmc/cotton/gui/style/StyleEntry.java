package io.github.cottonmc.cotton.gui.style;

import io.github.cottonmc.cotton.gui.widget.data.Color;

import java.util.HashMap;

public class StyleEntry {
	private String selector = "*";
	private HashMap<String, String> customEntries = new HashMap<>();
	
	private Color foreground;
	private Color background;
	
	public Color getForeground() {
		return (foreground!=null) ? foreground : Color.WHITE;
	}
}
