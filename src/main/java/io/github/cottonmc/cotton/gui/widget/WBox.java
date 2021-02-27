package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;

import java.util.Objects;

/**
 * Similar to the BoxLayout in Swing, this widget represents a list of widgets along an axis.
 *
 * @since 2.0.0
 */
public class WBox extends WPanel {
	/**
	 * The spacing between widgets.
	 */
	protected int spacing = 4;

	/**
	 * The axis that the widgets are laid out on.
	 */
	protected Axis axis;

	/**
	 * The horizontal alignment for this box's children.
	 *
	 * @since 2.1.0
	 */
	protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;

	/**
	 * The vertical alignment for this box's children.
	 *
	 * @since 2.1.0
	 */
	protected VerticalAlignment verticalAlignment = VerticalAlignment.TOP;

	private Insets insets = Insets.NONE;

	/**
	 * Constructs a box.
	 *
	 * @param axis the box axis
	 * @throws NullPointerException if the axis is null
	 */
	public WBox(Axis axis) {
		this.axis = Objects.requireNonNull(axis, "axis");
	}

	/**
	 * Adds a widget to this box.
	 * If the widget is resizeable, resizes it to the provided dimensions.
	 *
	 * @param widget the widget
	 * @param width  the new width of the widget
	 * @param height the new height of the widget
	 */
	public void add(WWidget widget, int width, int height) {
		widget.setParent(this);
		children.add(widget);
		if (canResize()) {
			widget.setSize(width, height);
		}
	}

	/**
	 * Adds a widget to this box.
	 * If the widget is resizeable, resizes it to 18x18.
	 *
	 * @param widget the widget
	 */
	public void add(WWidget widget) {
		add(widget, 18, 18);
	}

	@Override
	public void layout() {
		int dimension = axis.choose(insets.left, insets.top);

		// Set position offset from alignment along the box axis
		if (axis == Axis.HORIZONTAL && horizontalAlignment != HorizontalAlignment.LEFT) {
			int widgetWidth = spacing * (children.size() - 1);
			for (WWidget child : children) {
				widgetWidth += child.getWidth();
			}

			if (horizontalAlignment == HorizontalAlignment.CENTER) {
				dimension = (getWidth() - widgetWidth) / 2;
			} else { // right
				dimension = getWidth() - widgetWidth;
			}
		} else if (verticalAlignment != VerticalAlignment.TOP) {
			int widgetHeight = spacing * (children.size() - 1);
			for (WWidget child : children) {
				widgetHeight += child.getHeight();
			}

			if (verticalAlignment == VerticalAlignment.CENTER) {
				dimension = (getHeight() - widgetHeight) / 2;
			} else { // bottom
				dimension = getHeight() - widgetHeight;
			}
		}

		for (int i = 0; i < children.size(); i++) {
			WWidget child = children.get(i);

			if (axis == Axis.HORIZONTAL) {
				int y;

				switch (verticalAlignment) {
					case TOP:
					default:
						y = insets.top;
						break;
					case CENTER:
						y = insets.top + (getHeight() - insets.top - insets.bottom - child.getHeight()) / 2;
						break;
					case BOTTOM:
						y = getHeight() - insets.bottom - child.getHeight();
						break;
				}

				child.setLocation(dimension, y);
			} else {
				int x;

				switch (horizontalAlignment) {
					case LEFT:
					default:
						x = insets.left;
						break;
					case CENTER:
						x = insets.left + (getWidth() - insets.left - insets.right - child.getWidth()) / 2;
						break;
					case RIGHT:
						x = getWidth() - insets.right - child.getWidth();
						break;
				}

				child.setLocation(x, dimension);
			}

			if (child instanceof WPanel) ((WPanel) child).layout();
			expandToFit(child, insets);

			if (i != children.size() - 1) {
				dimension += spacing;
			}

			dimension += axis.choose(child.getWidth(), child.getHeight());
		}
	}

	/**
	 * Gets the spacing between widgets.
	 *
	 * @return the spacing
	 */
	public int getSpacing() {
		return spacing;
	}

	/**
	 * Sets the spacing between widgets in this box.
	 *
	 * @param spacing the new spacing
	 * @return this box
	 */
	public WBox setSpacing(int spacing) {
		this.spacing = spacing;

		return this;
	}

	/**
	 * Gets the axis of this box.
	 *
	 * @return the axis
	 */
	public Axis getAxis() {
		return axis;
	}

	/**
	 * Sets the axis of this box.
	 *
	 * @param axis the new axis
	 * @return this box
	 * @throws NullPointerException if the axis is null
	 */
	public WBox setAxis(Axis axis) {
		this.axis = Objects.requireNonNull(axis, "axis");
		return this;
	}

	/**
	 * Gets the horizontal alignment of this box.
	 *
	 * @return the alignment
	 * @since 2.1.0
	 */
	public HorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	/**
	 * Sets the horizontal alignment of this box.
	 *
	 * @param alignment the new alignment
	 * @return this box
	 * @throws NullPointerException if the alignment is null
	 * @since 2.1.0
	 */
	public WBox setHorizontalAlignment(HorizontalAlignment alignment) {
		this.horizontalAlignment = Objects.requireNonNull(alignment, "alignment");
		return this;
	}

	/**
	 * Gets the vertical alignment of this box.
	 *
	 * @return the alignment
	 * @since 2.1.0
	 */
	public VerticalAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	/**
	 * Sets the vertical alignment of this box.
	 *
	 * @param alignment the new alignment
	 * @return this box
	 * @throws NullPointerException if the alignment is null
	 * @since 2.1.0
	 */
	public WBox setVerticalAlignment(VerticalAlignment alignment) {
		this.verticalAlignment = Objects.requireNonNull(alignment, "alignment");
		return this;
	}

	/**
	 * Gets the layout insets of this box.
	 *
	 * @return the insets
	 * @since 4.0.0
	 */
	public Insets getInsets() {
		return insets;
	}

	/**
	 * Sets the layout insets of this box.
	 *
	 * <p>The insets should be set <i>before</i> adding any widgets
	 * to this box.
	 *
	 * @param insets the insets, should not be null
	 * @return this box
	 * @since 4.0.0
	 */
	public WBox setInsets(Insets insets) {
		this.insets = Objects.requireNonNull(insets, "insets");
		return this;
	}
}
