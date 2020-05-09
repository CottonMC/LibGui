package io.github.cottonmc.cotton.gui.widget;

/**
 * Similar to the BoxLayout in Swing, this widget represents a vertical list of widgets.
 *
 * @since 2.0.0
 */
public class WVerticalBox extends WPanel {
	/**
	 * The spacing between widgets.
	 */
	protected int spacing = 4;

	public WVerticalBox() {
	}

	public void add(WWidget widget, int width, int height) {
		widget.setParent(this);
		children.add(widget);
		if (canResize()) {
			widget.setSize(width, height);
		}
	}

	public void add(WWidget widget) {
		add(widget, 18, 18);
	}

	@Override
	public void layout() {
		int height = 0;

		for (int i = 0; i < children.size(); i++) {
			WWidget child = children.get(i);
			child.setLocation(0, height);

			if (child instanceof WPanel) ((WPanel) child).layout();
			expandToFit(child);

			if (i != children.size() - 1) {
				height += spacing;
			}

			height += child.getHeight();
		}
	}

	public int getSpacing() {
		return spacing;
	}

	public WVerticalBox setSpacing(int spacing) {
		this.spacing = spacing;

		return this;
	}
}
