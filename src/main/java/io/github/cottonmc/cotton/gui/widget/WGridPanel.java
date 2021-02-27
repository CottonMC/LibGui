package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.widget.data.Insets;

import java.util.Objects;

/**
 * A panel that positions children in a grid.
 */
public class WGridPanel extends WPanel {
	private Insets insets = Insets.NONE;

	/**
	 * The grid size in pixels.
	 * Defaults to 18, which is the size of one item slot.
	 */
	protected int grid = 18;

	/**
	 * Constructs a grid panel with the default grid size.
	 */
	public WGridPanel() {}

	/**
	 * Constructs a grid panel with a custom grid size.
	 *
	 * @param gridSize the grid size in pixels
	 */
	public WGridPanel(int gridSize) { this.grid = gridSize; }

	/**
	 * Adds a widget to this panel.
	 *
	 * <p>If the widget {@linkplain WWidget#canResize() can be resized},
	 * it will be resized to ({@link #grid}, {@link #grid}).
	 *
	 * @param w the widget
	 * @param x the X position in grid cells
	 * @param y the Y position in grid cells
	 */
	public void add(WWidget w, int x, int y) {
		children.add(w);
		w.parent = this;
		w.setLocation(x * grid + insets.left, y * grid + insets.top);
		if (w.canResize()) {
			w.setSize(grid, grid);
		}
		
		expandToFit(w, insets);
	}

	/**
	 * Adds a widget to this panel and resizes it to a custom size.
	 *
	 * @param w the widget
	 * @param x the X position in grid cells
	 * @param y the Y position in grid cells
	 * @param width the new width in grid cells
	 * @param height the new height in grid cells
	 */
	public void add(WWidget w, int x, int y, int width, int height) {
		children.add(w);
		w.parent = this;
		w.setLocation(x * grid + insets.left, y * grid + insets.top);
		if (w.canResize()) {
			w.setSize(width * grid, height * grid);
		}
		
		expandToFit(w, insets);
	}

	/**
	 * Gets the layout insets of this panel.
	 *
	 * @return the insets
	 * @since 4.0.0
	 */
	public Insets getInsets() {
		return insets;
	}

	/**
	 * Sets the layout insets of this panel.
	 *
	 * <p>The insets should be set <i>before</i> adding any widgets
	 * to this panel.
	 *
	 * @param insets the insets, should not be null
	 * @return this panel
	 * @since 4.0.0
	 */
	public WGridPanel setInsets(Insets insets) {
		this.insets = Objects.requireNonNull(insets, "insets");
		return this;
	}
}
