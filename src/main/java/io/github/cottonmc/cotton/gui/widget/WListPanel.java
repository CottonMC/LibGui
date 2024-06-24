package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.impl.Proxy;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.data.Insets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Similar to the RecyclerView in Android, this widget represents a scrollable list of items.
 *
 * <p> D is the type of data represented. The data must reside in some ordered backing {@code List<D>}.
 *     D's *must* have working equals and hashCode methods to distinguish them from each other!
 * <p> W is the WWidget class that will represent a single D of data.
 */
public class WListPanel<D, W extends WWidget> extends WClippedPanel {
	/**
	 * The list of data that this list represents.
	 */
	protected List<D> data;

	/**
	 * The supplier of new empty widgets.
	 */
	protected Supplier<W> supplier;

	/**
	 * The widget configurator that configures the passed widget
	 * to display the passed data.
	 */
	protected BiConsumer<D, W> configurator;

	protected HashMap<D, W> configured = new HashMap<>();
	protected List<W> unconfigured = new ArrayList<>();

	/**
	 * The height of each child cell.
	 */
	protected int cellHeight = 20;

	/**
	 * Whether this list has a fixed height for items.
	 */
	protected boolean fixedHeight = false;

	private Insets insets = new Insets(4, 4);
	private int gap = 4;

	/**
	 * The scroll bar of this list.
	 */
	protected WScrollBar scrollBar = new WScrollBar(Axis.VERTICAL);
	private int lastScroll = -1;

	/**
	 * The widgets whose host hasn't been set yet.
	 */
	private final List<W> requiresHost = new ArrayList<>();

	/**
	 * Constructs a list panel.
	 *
	 * @param data         the list data
	 * @param supplier     the widget supplier that creates unconfigured widgets
	 * @param configurator the widget configurator that configures widgets to display the passed data
	 */
	public WListPanel(List<D> data, Supplier<W> supplier, BiConsumer<D, W> configurator) {
		this.data = data;
		this.supplier = supplier;
		this.configurator = configurator;
		scrollBar.setMaxValue(data.size());
		scrollBar.setParent(this);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
		if (scrollBar.getValue()!=lastScroll) {
			layout();
			lastScroll = scrollBar.getValue();
		}

		super.paint(context, x, y, mouseX, mouseY);
	}

	private W createChild() {
		W child = supplier.get();
		child.setParent(this);
		// Set up the widget's host
		if (host != null) {
			// setHost instead of validate since we cannot have independent validations
			// TODO: System for independently validating widgets?
			child.setHost(host);
		} else {
			requiresHost.add(child);
		}
		Proxy.proxy.addPainters(child);
		return child;
	}

	@Override
	public void validate(GuiDescription c) {
		super.validate(c);
		setRequiredHosts(c);
	}

	@Override
	public void setHost(GuiDescription host) {
		super.setHost(host);
		setRequiredHosts(host);
	}

	@Override
	public void addPainters() {
		// This is handled separately for our children.
	}

	private void setRequiredHosts(GuiDescription host) {
		for (W widget : requiresHost) {
			widget.setHost(host);
		}
		requiresHost.clear();
	}

	@Override
	public void layout() {

		this.children.clear();
		this.children.add(scrollBar);
		scrollBar.setLocation(this.width-scrollBar.getWidth(), 0);
		scrollBar.setSize(8, this.height);

		//super.layout();

		//System.out.println("Validating");

		//Recompute cellHeight if needed
		if (!fixedHeight) {
			if (unconfigured.isEmpty()) {
				if (configured.isEmpty()) {
					W exemplar = createChild();
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

		Insets insets = getInsets();
		int gap = getGap();
		int layoutHeight = this.getHeight() - insets.height();
		int cellsHigh = Math.max((layoutHeight + gap) / (cellHeight + gap), 1); // At least one cell is always visible

		//System.out.println("Adding children...");

		//this.children.clear();
		//this.children.add(scrollBar);
		//scrollBar.setLocation(this.width-scrollBar.getWidth(), 0);
		//scrollBar.setSize(8, this.height);

		//Fix up the scrollbar handle and track metrics
		scrollBar.setWindow(cellsHigh);
		scrollBar.setMaxValue(data.size());
		int scrollOffset = scrollBar.getValue();
		//System.out.println(scrollOffset);

		int presentCells = Math.min(data.size()-scrollOffset, cellsHigh);

		if (presentCells>0) {
			for(int i=0; i<presentCells; i++) {
				int index = i+scrollOffset;
				if (index>=data.size()) break;
				if (index<0) continue; //THIS IS A THING THAT IS HAPPENING >:(
				D d = data.get(index);
				W w = configured.get(d);
				if (w==null) {
					if (unconfigured.isEmpty()) {
						w = createChild();
					} else {
						w = unconfigured.remove(0);
					}
					configurator.accept(d, w);
					configured.put(d, w);
				}

				//At this point, w is nonnull and configured by d
				if (w.canResize()) {
					w.setSize(this.width - insets.width() - scrollBar.getWidth(), cellHeight);
				}
				w.x = insets.left();
				w.y = insets.top() + ((cellHeight + gap) * i);
				this.children.add(w);
			}
		}

		//System.out.println("Children: "+children.size());
	}

	/**
	 * Sets the height of this list's items to a constant value.
	 *
	 * @param height the item height
	 * @return this list
	 */
	public WListPanel<D, W> setListItemHeight(int height) {
		cellHeight = height;
		fixedHeight = true;
		return this;
	}

	@Override
	public InputResult onMouseScroll(int x, int y, double horizontalAmount, double verticalAmount) {
		return scrollBar.onMouseScroll(0, 0, horizontalAmount, verticalAmount);
	}

	/**
	 * Gets the {@link io.github.cottonmc.cotton.gui.widget.WScrollBar} attached to this panel.
	 *
	 * @return the scroll bar bundled
	 * @since 5.3.0
	 */
	public WScrollBar getScrollBar() {
		return scrollBar;
	}

	/**
	 * {@return the layout insets used for the contents of this list}
	 * @since 9.1.0
	 */
	public Insets getInsets() {
		// Returns the *effective* insets of this list for backwards compat.
		return insets;
	}

	/**
	 * Sets the layout insets used for the contents of this list.
	 *
	 * @param insets the layout insets
	 * @return this list
	 * @since 9.1.0
     */
	public WListPanel<D, W> setInsets(Insets insets) {
		this.insets = Objects.requireNonNull(insets, "Insets cannot be null");
		return this;
	}

	/**
	 * {@return the gap between list items}
	 * @since 9.1.0
	 */
	public int getGap() {
		// Returns the *effective* gap of this list for backwards compat.
		return gap;
	}

	/**
	 * Sets the gap between list items.
	 *
	 * @param gap the gap, must be non-negative
	 * @return this list
	 * @since 9.1.0
	 */
	public WListPanel<D, W> setGap(int gap) {
		if (gap < 0) {
			throw new IllegalArgumentException("Gap cannot be negative (was " + gap + ")");
		}

		this.gap = gap;
		return this;
	}
}
