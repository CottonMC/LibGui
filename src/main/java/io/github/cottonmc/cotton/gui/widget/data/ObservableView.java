package io.github.cottonmc.cotton.gui.widget.data;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A read-only {@linkplain ObservableProperty observable property}.
 *
 * @param <T> the contained value type
 * @since 4.2.0
 */
public interface ObservableView<T> extends Supplier<T> {
	/**
	 * {@return whether this property has been set to a value}
	 */
	boolean hasValue();

	/**
	 * {@return the value of this property}
	 * @throws IllegalStateException if not initialized
	 * @see #hasValue()
	 */
	@Override
	T get();

	/**
	 * {@return the value of this property, or null if not initialized}
	 */
	default @Nullable T getOrNull() {
		return hasValue() ? get() : null;
	}

	/**
	 * {@return the nonnull value of this property, or {@link Optional#empty()} if null or not initialized}
	 */
	default Optional<T> find() {
		return Optional.ofNullable(getOrNull());
	}

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
		 * @param from     the previous value, or null if not set before
		 * @param to       the new value, or null if cleared
		 */
		void onPropertyChange(ObservableView<? extends T> property, @Nullable T from, @Nullable T to);
	}
}
