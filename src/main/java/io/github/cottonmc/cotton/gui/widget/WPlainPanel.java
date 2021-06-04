package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.widget.data.Insets;

/**
 * A panel that positions children by pixel-perfect positions.
 */
public class WPlainPanel extends WPanelWithInsets {
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
		w.setLocation(insets.left() + x, insets.top() + y);
		if (w.canResize()) {
			w.setSize(18, 18);
		}
		
		expandToFit(w, insets);
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
		w.setLocation(insets.left() + x, insets.top() + y);
		if (w.canResize()) {
			w.setSize(width, height);
		}

		expandToFit(w, insets);
		//valid = false;
	}

	public WPlainPanel setInsets(Insets insets) {
		super.setInsets(insets);
		return this;
	}
}
