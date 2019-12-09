package io.github.cottonmc.cotton.gui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.data.Axis;

/**
 * Similar to the RecyclerView in Android, this widget represents a scrollable list of items.
 * 
 * <p> D is the type of data represented. The data must reside in some ordered backing {@code List<D>}.
 *     D's *must* have working equals and hashCode methods to distinguish them from each other!
 * <p> W is the WWidget class that will represent a single D of data.
 */
public class WListPanel<D, W extends WWidget> extends WClippedPanel {
	protected List<D> data;
	protected Supplier<W> supplier;
	protected BiConsumer<D, W> configurator;
	
	protected HashMap<D, W> configured = new HashMap<>();
	protected List<W> unconfigured = new ArrayList<>();
	protected int cellHeight = 20;
	protected boolean fixedHeight = false;
	
	protected int margin = 4;
	
	protected WScrollBar scrollBar = new WScrollBar(Axis.VERTICAL);
	int lastScroll = -1;

	public WListPanel(List<D> data, Supplier<W> supplier, BiConsumer<D, W> configurator) {
		this.data = data;
		this.supplier = supplier;
		this.configurator = configurator;
		scrollBar.setMaxValue(data.size());
	}

	/**
	 * @deprecated Use {@link #WListPanel(List, Supplier, BiConsumer)} instead.
	 */
	@Deprecated
	public WListPanel(List<D> data, Class<W> listItemClass, Supplier<W> supplier, BiConsumer<D, W> configurator) {
		this(data, supplier, configurator);
	}
	
	@Override
	public void paintBackground(int x, int y, int mouseX, int mouseY) {
		if (scrollBar.getValue()!=lastScroll) {
			layout();
			lastScroll = scrollBar.getValue();
		}
		
		super.paintBackground(x, y, mouseX, mouseY);
		/*
		if (getBackgroundPainter()!=null) {
			getBackgroundPainter().paintBackground(x, y, this);
		} else {
			ScreenDrawing.drawBeveledPanel(x, y, width, height);
		}
		
		
		
		for(WWidget child : children) {
			child.paintBackground(x + child.getX(), y + child.getY(), mouseX - child.getX(), mouseY - child.getY());
		}*/
	}
	
	@Override
	public void layout() {
		
		this.children.clear();
		this.children.add(scrollBar);
		scrollBar.setLocation(this.width-scrollBar.getWidth(), 0);
		scrollBar.setSize(8, this.height);
		//scrollBar.window = 6;
		scrollBar.setMaxValue(data.size());
		
		//super.layout();
		
		//System.out.println("Validating");
		
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
		
		
		//System.out.println("Adding children...");
		
		//this.children.clear();
		//this.children.add(scrollBar);
		//scrollBar.setLocation(this.width-scrollBar.getWidth(), 0);
		//scrollBar.setSize(8, this.height);
		
		//Fix up the scrollbar handle and track metrics
		scrollBar.window = cellsHigh;
		//scrollBar.setMaxValue(data.size());
		int scrollOffset = scrollBar.value;
		//System.out.println(scrollOffset);
		
		int presentCells = Math.min(data.size()-scrollOffset, cellsHigh);
		
		if (presentCells>0) {
			for(int i=0; i<presentCells+1; i++) {
				int index = i+scrollOffset;
				if (index>=data.size()) break;
				if (index<0) continue; //THIS IS A THING THAT IS HAPPENING >:(
				D d = data.get(index);
				W w = configured.get(d);
				if (w==null) {
					if (unconfigured.isEmpty()) {
						w = supplier.get();
					} else {
						w = unconfigured.remove(0);
					}
					configurator.accept(d, w);
					configured.put(d, w);
				}
				
				//At this point, w is nonnull and configured by d
				if (w.canResize()) {
					w.setSize(this.width-(margin*2) - scrollBar.getWidth(), cellHeight);
				}
				w.x = margin;
				w.y = margin + ((cellHeight+margin) * i);
				this.children.add(w);
			}
		}
		
		//System.out.println("Children: "+children.size());
	}
	
	public WListPanel<D, W> setListItemHeight(int height) {
		cellHeight = height;
		fixedHeight = true;
		return this;
	}
}
