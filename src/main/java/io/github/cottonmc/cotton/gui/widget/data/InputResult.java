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
	 * Combines this input result with another.
	 * The combination is {@link #PROCESSED} is at least one of the inputs is.
	 *
	 * @param other the other input result
	 * @return the combined input result
	 * @since 9.0.0
	 */
	public InputResult or(InputResult other) {
		return this == IGNORED && other == IGNORED ? IGNORED : PROCESSED;
	}

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
