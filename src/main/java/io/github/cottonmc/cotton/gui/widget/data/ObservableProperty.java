package io.github.cottonmc.cotton.gui.widget.data;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class ObservableProperty<T> {
	private Supplier<? extends T> value;
	private final List<ChangeListener<? super T>> listeners = new ArrayList<>();
	private boolean allowNull = true;

	private ObservableProperty() {
	}

	private ObservableProperty(Supplier<? extends T> value) {
		this.value = value;
	}

	public static <T> ObservableProperty<T> lateinit() {
		return new ObservableProperty<>();
	}

	public static <T> ObservableProperty<T> of(T initialValue) {
		return new ObservableProperty<>(() -> initialValue);
	}

	public static <T> ObservableProperty<T> bound(Supplier<? extends T> initialValue) {
		return new ObservableProperty<>(initialValue);
	}

	/**
	 * {@return the value of this property}
	 * @throws IllegalStateException if not initialized
	 * @throws NullPointerException if the value is null and null values aren't allowed
	 */
	public T get() {
		if (value == null) {
			throw new IllegalStateException("Property not initialized!");
		}

		T ret = value.get();
		if (ret == null && !allowNull) throw new NullPointerException("Null value for nonnull property!");
		return ret;
	}

	/**
	 * Sets this property to a constant value.
	 *
	 * @param value the new value
	 * @throws NullPointerException if the value is null and nulls aren't allowed
	 */
	public void set(T value) {
		if (value == null && !allowNull) throw new NullPointerException("value");
		bind(() -> value);
	}

	/**
	 * Binds this property to a supplier.
	 *
	 * @param value the new value supplier
	 */
	public void bind(Supplier<? extends T> value) {
		Objects.requireNonNull(value, "value");
		T oldValue = this.value != null ? this.value.get() : null;
		this.value = value;

		for (ChangeListener<? super T> listener : listeners) {
			listener.onPropertyChange(this, oldValue, value.get());
		}
	}

	/**
	 * Clears the current value, if any, from this property.
	 */
	public void clear() {
		T oldValue = this.value != null ? this.value.get() : null;
		value = null;

		for (ChangeListener<? super T> listener : listeners) {
			listener.onPropertyChange(this, oldValue, null);
		}
	}

	/**
	 * Prevents this property from accepting null values.
	 *
	 * @return this property
	 */
	public ObservableProperty<T> nonnullValues() {
		allowNull = false;
		return this;
	}

	public void addListener(ChangeListener<? super T> listener) {
		listeners.add(listener);
	}

	public void removeListener(ChangeListener<? super T> listener) {
		listeners.remove(listener);
	}

	@FunctionalInterface
	public interface ChangeListener<T> {
		void onPropertyChange(ObservableProperty<? extends T> property, @Nullable T from, @Nullable T to);
	}
}
