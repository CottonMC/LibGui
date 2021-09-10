package io.github.cottonmc.cotton.gui.widget.data;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * A read-only {@linkplain ObservableProperty observable property}.
 *
 * @experimental
 * @param <T> the contained value type
 * @since 4.2.0
 */
@ApiStatus.Experimental
public interface ObservableView<T> extends Supplier<T> {
	/**
	 * Adds a change listener to this property view.
	 *
	 * @param listener the added listener
	 */
	void addListener(ChangeListener<? super T> listener);

	/**
	 * Removes a change listener from this property view if present.
	 *
	 * @param listener the removed listener
	 */
	void removeListener(ChangeListener<? super T> listener);

	/**
	 * A listener for changes in observable views and properties.
	 *
	 * @param <T> the value type listened to
	 */
	@FunctionalInterface
	interface ChangeListener<T> {
		/**
		 * Handles a change in an observable property.
		 *
		 * @param property the changed property or view
		 * @param from     the previous value
		 * @param to       the new value
		 */
		void onPropertyChange(ObservableView<? extends T> property, @Nullable T from, @Nullable T to);
	}
}
