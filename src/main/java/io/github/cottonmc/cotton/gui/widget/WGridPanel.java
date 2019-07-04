package io.github.cottonmc.cotton.gui.widget;

public class WGridPanel extends WPanel {
	public void add(WWidget w, int x, int y) {
		children.add(w);
		w.parent = this;
		w.setLocation(x * 18, y * 18);
		if (w.canResize()) {
			w.setSize(18, 18);
		}
		
		expandToFit(w);
		//valid = false;
	}
	
	public void add(WWidget w, int x, int y, int width, int height) {
		children.add(w);
		w.parent = this;
		w.setLocation(x * 18, y * 18);
		if (w.canResize()) {
			w.setSize(width * 18, height * 18);
		}
		
		expandToFit(w);
		//valid = false;
	}
}