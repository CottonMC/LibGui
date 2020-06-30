package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.widget.data.Axis;

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
	 * The alignment for this box's children.
	 *
	 * @since 2.1.0
	 */
	protected Alignment alignment = Alignment.START;

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
		int dimension = 0;

		for (int i = 0; i < children.size(); i++) {
			WWidget child = children.get(i);

			if (axis == Axis.HORIZONTAL) {
				int y;

				switch (alignment) {
					case START:
					default:
						y = 0;
						break;
					case CENTER:
						y = (getHeight() - child.getHeight()) / 2;
						break;
					case END:
						y = getHeight() - child.getHeight();
						break;
				}

				child.setLocation(dimension, y);
			} else {
				int x;

				switch (alignment) {
					case START:
					default:
						x = 0;
						break;
					case CENTER:
						x = (getWidth() - child.getWidth()) / 2;
						break;
					case END:
						x = getWidth() - child.getWidth();
						break;
				}

				child.setLocation(x, dimension);
			}

			if (child instanceof WPanel) ((WPanel) child).layout();
			expandToFit(child);

			if (i != children.size() - 1) {
				dimension += spacing;
			}

			dimension += axis == Axis.HORIZONTAL ? child.getWidth() : child.getHeight();
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
	 * Gets the alignment of this box.
	 *
	 * @return the alignment
	 * @since 2.1.0
	 */
	public Alignment getAlignment() {
		return alignment;
	}

	/**
	 * Sets the alignment of this box.
	 *
	 * @param alignment the new alignment
	 * @return this box
	 * @throws NullPointerException if the alignment is null
	 * @since 2.1.0
	 */
	public WBox setAlignment(Alignment alignment) {
		this.alignment = Objects.requireNonNull(alignment, "alignment");
		return this;
	}

	/**
	 * All possible alignments for children in a {@link WBox}.
	 *
	 * @since 2.1.0
	 */
	public enum Alignment {
		/** Aligned on the start of the axis, i.e. left or up. This is the default. */
		START,
		/** Centered on the axis. */
		CENTER,
		/** Aligned on the end of the axis, i.e. right or down. */
		END;
	}
}
