package io.github.cottonmc.cotton.gui.widget;

/**
 * A panel that positions children in a grid.
 */
public class WGridPanel extends WPanel {
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
		w.setLocation(x * grid, y * grid);
		if (w.canResize()) {
			w.setSize(grid, grid);
		}
		if (w.shouldExpandToFit()) {
			expandToFit(w);
		}
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
		w.setLocation(x * grid, y * grid);
		if (w.canResize()) {
			w.setSize(width * grid, height * grid);
		}
		if (w.shouldExpandToFit()) {
			expandToFit(w);
		}
	}
}
