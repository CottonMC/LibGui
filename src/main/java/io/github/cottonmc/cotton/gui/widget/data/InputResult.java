package io.github.cottonmc.cotton.gui.widget.data;

/**
 * Specifies whether an input event was ignored or processed.
 * Used for mouse and keyboard input events.
 *
 * @since 4.0.0
 */
public enum InputResult {
	PROCESSED,
	IGNORED;

	/**
	 * Gets the corresponding input result for a {@code processed} boolean.
	 *
	 * @param processed whether an input event was processed
	 * @return {@link #PROCESSED} if true, {@link #IGNORED} otherwise
	 */
	public static InputResult of(boolean processed) {
		return processed ? PROCESSED : IGNORED;
	}
}
