package io.github.cottonmc.cotton.gui.widget;

import net.minecraft.client.util.math.MatrixStack;

import io.github.cottonmc.cotton.gui.widget.data.Axis;

/**
 * Similar to the JScrollPane in Swing, this widget represents a scrollable widget.
 *
 * @since 2.0.0
 */
public class WScrollPanel extends WClippedPanel {
	private final WWidget widget;

	private boolean scrollingHorizontally = false;
	private boolean scrollingVertically = true;

	/**
	 * The horizontal scroll bar of this panel.
	 */
	protected WScrollBar horizontalScrollBar = new WScrollBar(Axis.HORIZONTAL);

	/**
	 * The vertical scroll bar of this panel.
	 */
	protected WScrollBar verticalScrollBar = new WScrollBar(Axis.VERTICAL);

	private int lastHorizontalScroll = -1;
	private int lastVerticalScroll = -1;

	/**
	 * Creates a vertically scrolling panel.
	 *
	 * @param widget the viewed widget
	 */
	public WScrollPanel(WWidget widget) {
		this.widget = widget;

		widget.setParent(this);
		horizontalScrollBar.setParent(this);
		verticalScrollBar.setParent(this);

		children.add(widget);
		children.add(verticalScrollBar); // Only vertical scroll bar
	}

	/**
	 * Returns whether this scroll panels has a horizontal scroll bar.
	 *
	 * @return true if there is a horizontal scroll bar, false otherwise
	 */
	public boolean isScrollingHorizontally() {
		return scrollingHorizontally;
	}

	public WScrollPanel setScrollingHorizontally(boolean scrollingHorizontally) {
		if (scrollingHorizontally != this.scrollingHorizontally) {
			this.scrollingHorizontally = scrollingHorizontally;
			layout();
		}

		return this;
	}

	/**
	 * Returns whether this scroll panels has a vertical scroll bar.
	 *
	 * @return true if there is a vertical scroll bar, false otherwise
	 */
	public boolean isScrollingVertically() {
		return scrollingVertically;
	}

	public WScrollPanel setScrollingVertically(boolean scrollingVertically) {
		if (scrollingVertically != this.scrollingVertically) {
			this.scrollingVertically = scrollingVertically;
			layout();
		}

		return this;
	}

	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		if (verticalScrollBar.getValue() != lastVerticalScroll || horizontalScrollBar.getValue() != lastHorizontalScroll) {
			layout();
			lastHorizontalScroll = horizontalScrollBar.getValue();
			lastVerticalScroll = verticalScrollBar.getValue();
		}

		super.paint(matrices, x, y, mouseX, mouseY);
	}

	@Override
	public void layout() {
		children.clear();
		verticalScrollBar.setLocation(this.width - verticalScrollBar.getWidth(), 0);
		verticalScrollBar.setSize(8, this.height);
		horizontalScrollBar.setLocation(0, this.height - horizontalScrollBar.getHeight());
		horizontalScrollBar.setSize(scrollingVertically ? (this.width - verticalScrollBar.getWidth()) : this.width, 8);

		if (widget instanceof WPanel) ((WPanel) widget).layout();
		children.add(widget);
		int x = scrollingHorizontally ? -horizontalScrollBar.getValue() : 0;
		int y = scrollingVertically ? -verticalScrollBar.getValue() : 0;
		widget.setLocation(x, y);

		verticalScrollBar.setWindow(this.height);
		verticalScrollBar.setMaxValue(widget.getHeight() + 1);
		horizontalScrollBar.setWindow(this.width);
		horizontalScrollBar.setMaxValue(widget.getWidth() + 1);

		if (scrollingVertically) children.add(verticalScrollBar);
		if (scrollingHorizontally) children.add(horizontalScrollBar);
	}
}
