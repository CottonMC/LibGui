package io.github.cottonmc.cotton.gui.widget;

/**
 * A panel that positions children by pixel-perfect positions.
 */
public class WPlainPanel extends WPanel {
	/**
	 * Adds a new widget to this panel.
	 *
	 * <p>If the widget {@linkplain WWidget#canResize() can be resized},
	 * it will be resized to (18, 18).
	 *
	 * @param w the widget
	 * @param x the X position
	 * @param y the Y position
	 */
	public void add(WWidget w, int x, int y) {
		children.add(w);
		w.parent = this;
		w.setLocation(x, y);
		if (w.canResize()) {
			w.setSize(18, 18);
		}
		if (w.shouldExpandToFit) {
			expandToFit(w);
		}
		//valid = false;
	}

	/**
	 * Adds a new widget to this panel and resizes it to a custom size.
	 *
	 * @param w the widget
	 * @param x the X position
	 * @param y the Y position
	 * @param width the new width
	 * @param height the new height
	 */
	public void add(WWidget w, int x, int y, int width, int height) {
		children.add(w);
		w.parent = this;
		w.setLocation(x, y);
		if (w.canResize()) {
			w.setSize(width, height);
		}
		if (w.shouldExpandToFit) {
			expandToFit(w);
		}
		//valid = false;
	}
}
