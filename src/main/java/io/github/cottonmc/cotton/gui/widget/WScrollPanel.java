package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Similar to the JScrollPane in Swing, this widget represents a vertically scrollable list of widgets.
 *
 * @since 2.0.0
 */
public class WScrollPanel extends WClippedPanel {
	/**
	 * The spacing between widgets.
	 */
	protected int spacing = 4;

	/**
	 * The scroll bar of this panel.
	 */
	protected WScrollBar scrollBar = new WScrollBar(Axis.VERTICAL);

	private int lastScroll = -1;

	// The list of *all* children as opposed to visible children, excluding the scroll bar.
	private final List<WWidget> allChildren = new ArrayList<>();

	public WScrollPanel() {
		scrollBar.setParent(this);
		children.add(scrollBar);
	}

	public void add(WWidget widget, int width, int height) {
		widget.setParent(this);
		allChildren.add(widget);
		children.add(widget);
		if (canResize()) {
			widget.setSize(width, height);
		}
	}

	public void add(WWidget widget) {
		add(widget, 18, 18);
	}

	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		if (scrollBar.getValue() != lastScroll) {
			layout();
			lastScroll = scrollBar.getValue();
		}

		super.paint(matrices, x, y, mouseX, mouseY);
	}

	@Override
	public void layout() {
		children.clear();
		scrollBar.setLocation(this.width - scrollBar.getWidth(), 0);
		scrollBar.setSize(8, this.height);

		int offset = scrollBar.getValue();
		int height = 0;

		for (int i = 0; i < allChildren.size(); i++) {
			WWidget child = allChildren.get(i);
			int minY = height - offset;
			child.setLocation(0, minY);
			int maxY = minY + child.getHeight();

			if ((minY >= 0 && minY < getHeight()) || (maxY >= 0 && maxY < getHeight()) || (minY < 0 && maxY >= getHeight())) {
				children.add(child);
			}

			if (i != allChildren.size() - 1) {
				height += spacing;
			}

			height += child.getHeight();
		}

		children.add(scrollBar);
		scrollBar.setWindow(Math.min(height / 4, 18));
		scrollBar.setMaxValue(height);
	}

	@Override
	public void validate(GuiDescription c) {
		for (WWidget child : allChildren) {
			child.validate(c);
		}
		super.validate(c);
	}

	@Override
	public void createPeers(GuiDescription c) {
		super.createPeers(c);
		for (WWidget child : allChildren) {
			child.createPeers(c);
		}
	}

	public int getSpacing() {
		return spacing;
	}

	public WScrollPanel setSpacing(int spacing) {
		this.spacing = spacing;

		return this;
	}
}
