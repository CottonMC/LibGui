package io.github.cottonmc.cotton.gui.style;

import java.util.HashMap;

import io.github.cottonmc.cotton.gui.widget.data.Color;

public class StyleEntry {
	private String selector = "*";
	private HashMap<String, String> customEntries = new HashMap<>();
	
	private Color foreground;
	private Color background;
	
	public Color getForeground() {
		return (foreground!=null) ? foreground : Color.WHITE;
	}
}
