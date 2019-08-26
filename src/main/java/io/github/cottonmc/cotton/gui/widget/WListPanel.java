package io.github.cottonmc.cotton.gui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;

/**
 * Similar to the RecyclerView in Android, this widget represents a scrollable list of items.
 * 
 * <p> D is the type of data represented. The data must reside in some ordered backing {@code List<D>}.
 *     D's *must* have working equals and hashCode methods to distinguish them from each other!
 * <p> W is the WWidget class that will represent a single D of data.
 */
public class WListPanel<D, W extends WWidget> extends WPanel {
	protected List<D> data;
	protected Class<W> listItemClass;
	protected Supplier<W> supplier;
	protected BiConsumer<D, W> configurator;
	
	protected HashMap<D, W> configured = new HashMap<>();
	protected List<W> unconfigured = new ArrayList<>();
	protected int cellHeight = 20;
	protected boolean fixedHeight = false;
	
	protected int margin = 4;
	
	protected WScrollBar scrollBar = new WScrollBar(Axis.VERTICAL);
	
	public WListPanel(List<D> data, Class<W> listItemClass, Supplier<W> supplier, BiConsumer<D, W> configurator) {
		this.data = data;
		this.listItemClass = listItemClass;
		this.supplier = supplier;
		this.configurator = configurator;
		scrollBar.setMaxValue(data.size());
	}
	
	@Override
	public void paintBackground(int x, int y) {
		if (getBackgroundPainter()!=null) {
			getBackgroundPainter().paintBackground(x, y, this);
		} else {
			ScreenDrawing.drawBeveledPanel(x, y, width, height);
		}
		
		for(WWidget child : children) {
			child.paintBackground(x + child.getX(), y + child.getY());
		}
	}
	
	@Override
		public void layout() {
			super.layout();
			
			int scrollOffset = scrollBar.value;
			
			System.out.println("Validating");
			
			//Recompute cellHeight if needed
			if (!fixedHeight) {
				if (unconfigured.isEmpty()) {
					if (configured.isEmpty()) {
						W exemplar = supplier.get();
						unconfigured.add(exemplar);
						if (!exemplar.canResize()) cellHeight = exemplar.getHeight();
					} else {
						W exemplar = configured.values().iterator().next();
						if (!exemplar.canResize()) cellHeight = exemplar.getHeight();
					}
				} else {
					W exemplar = unconfigured.get(0);
					if (!exemplar.canResize()) cellHeight = exemplar.getHeight();
				}
			}
			if (cellHeight<4) cellHeight=4;
			
			int layoutHeight = this.getHeight()-(margin*2);
			int cellsHigh = layoutHeight / cellHeight;
			int presentCells = Math.min(data.size()-scrollOffset, cellsHigh);
			
			System.out.println("Adding children...");
			
			this.children.clear();
			this.children.add(scrollBar);
			scrollBar.setLocation(this.width-4, 0);
			scrollBar.setSize(4, this.height);
			scrollBar.window = cellsHigh;
			scrollBar.setMaxValue(data.size());
			
			if (presentCells>0) {
				for(int i=0; i<presentCells; i++) {
					int index = i+scrollOffset;
					D d = data.get(index);
					W w = configured.get(d);
					if (w==null) {
						if (unconfigured.isEmpty()) {
							w = supplier.get();
						} else {
							w = unconfigured.remove(0);
						}
						configurator.accept(d, w);
					}
					
					//At this point, w is nonnull and configured by d
					if (w.canResize()) {
						w.setSize(this.width-(margin*2), cellHeight);
					}
					w.x = margin;
					w.y = margin + (cellHeight * i);
					this.children.add(w);
				}
			}
			
			System.out.println("Children: "+children.size());
		}
	
	public WListPanel<D, W> setListItemHeight(int height) {
		cellHeight = height;
		fixedHeight = true;
		return this;
	}
}
