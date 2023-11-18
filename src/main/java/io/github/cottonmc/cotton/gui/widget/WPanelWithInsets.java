package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.widget.data.Insets;

import java.util.Objects;

/**
 * A panel that has {@linkplain Insets layout insets}.
 *
 * @since 4.0.0
 */
public abstract class WPanelWithInsets extends WPanel {
	/**
	 * The layout insets of this panel.
	 * They control how far from the panel's edges the widgets are placed.
	 */
	protected Insets insets = Insets.NONE;

	/**
	 * Gets the layout insets of this panel.
	 *
	 * @return the insets
	 */
	public Insets getInsets() {
		return insets;
	}

	/**
	 * Sets the layout insets of this panel.
	 * Subclasses are encouraged to override this method to return their more specific type
	 * (such as {@link WGridPanel}).
	 *
	 * <p>If there are already widgets in this panel when the insets are modified,
	 * the panel is resized and the widgets are moved according to the insets.
	 *
	 * @param insets the insets, should not be null
	 * @return this panel
	 */
	public WPanelWithInsets setInsets(Insets insets) {
		Insets old = this.insets;
		this.insets = Objects.requireNonNull(insets, "insets");

		setSize(getWidth() - old.width(), getHeight() - old.height());

		for (WWidget child : children) {
			child.setLocation(child.getX() - old.left() + insets.left(), child.getY() - old.top() + insets.top());
			expandToFit(child, insets);
		}

		return this;
	}

	@Override
	protected void expandToFit(WWidget w) {
		expandToFit(w, insets);
	}
}
