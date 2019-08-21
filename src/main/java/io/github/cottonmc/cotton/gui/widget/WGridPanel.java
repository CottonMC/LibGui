package io.github.cottonmc.cotton.gui.widget;

public class WGridPanel extends WPanel {
	protected int grid = 18;
	
	public WGridPanel() {}
	public WGridPanel(int gridSize) { this.grid = gridSize; }
	
	public void add(WWidget w, int x, int y) {
		children.add(w);
		w.parent = this;
		w.setLocation(x * grid, y * grid);
		if (w.canResize()) {
			w.setSize(grid, grid);
		}
		
		expandToFit(w);
	}
	
	public void add(WWidget w, int x, int y, int width, int height) {
		children.add(w);
		w.parent = this;
		w.setLocation(x * grid, y * grid);
		if (w.canResize()) {
			w.setSize(width * grid, height * grid);
		}
		
		expandToFit(w);
	}
}