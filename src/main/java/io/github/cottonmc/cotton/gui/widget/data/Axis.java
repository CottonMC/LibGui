package io.github.cottonmc.cotton.gui.widget.data;

public enum Axis {
	HORIZONTAL,
	VERTICAL;

	/**
	 * Chooses a value based on this axis.
	 *
	 * @param horizontal the value returned if this axis is horizontal
	 * @param vertical   the value returned if this axis is vertical
	 * @param <T>        the type of the value
	 * @return the corresponding value for this axis
	 */
	public <T> T choose(T horizontal, T vertical) {
		return this == HORIZONTAL ? horizontal : vertical;
	}
}
