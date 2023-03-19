package io.github.cottonmc.cotton.gui.widget.focus;

import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Rect2i;

import java.util.stream.Stream;

/**
 * Manages the state of individual {@linkplain Focus foci} in a widget.
 * Each instance should be specific to one widget.
 *
 * @param <K> the focus key type
 * @since 7.0.0
 * @see #simple(WWidget) FocusModel.simple()
 * @see SimpleKeyedFocusModel
 */
public interface FocusModel<K> {
	/**
	 * Checks if a focus is focused in the target widget.
	 * If the target widget is not focused itself, none of its foci should have focus.
	 *
	 * @param focus the focus to check
	 * @return {@code true} if the focus is focused, {@code false} otherwise
	 */
	boolean isFocused(Focus<K> focus);

	/**
	 * Applies a focus to the target widget.
	 *
	 * <p>This method does not need to {@linkplain WWidget#requestFocus request the GUI's focus}
	 * for the widget; that is the responsibility of the caller.
	 *
	 * @param focus the focus
	 */
	void setFocused(Focus<K> focus);

	/**
	 * {@return a stream of all foci in the target widget}
	 */
	Stream<Focus<K>> foci();

	/**
	 * Creates a simple focus model for a focusable widget.
	 * The focus model provides the whole widget area as its only focus area.
	 *
	 * @param widget the widget
	 * @return the focus model
	 */
	static FocusModel<?> simple(WWidget widget) {
		Rect2i widgetArea = new Rect2i(0, 0, widget.getWidth(), widget.getHeight());
		return new SimpleFocusModel(widget, widgetArea);
	}
}
