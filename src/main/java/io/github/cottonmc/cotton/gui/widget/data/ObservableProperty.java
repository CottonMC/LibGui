package io.github.cottonmc.cotton.gui.widget.data;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An observable mutable property. Observable properties are containers for values
 * that can be modified and listened to.
 *
 * <p>The naming convention for {@code ObservableProperty} getters follows the convention
 * {@code <property name>Property}. For example, the {@code WWidget.hovered} property can be retrieved with
 * {@link io.github.cottonmc.cotton.gui.widget.WWidget#hoveredProperty() hoveredProperty()}.
 *
 * @experimental
 * @param <T> the contained value type
 * @since 4.2.0
 */
@ApiStatus.Experimental
public final class ObservableProperty<T> implements ObservableView<T> {
	private boolean hasValue;
	private T value;
	private final List<ChangeListener<? super T>> listeners = new ArrayList<>();
	private boolean allowNull = true;
	private String name = "<unnamed>";

	private ObservableProperty(@Nullable T value, boolean hasValue) {
		this.value = value;
		this.hasValue = hasValue;
	}

	public static <T> ObservableProperty<T> lateinit() {
		return new ObservableProperty<>(null, false);
	}

	public static <T> ObservableProperty<T> of(T initialValue) {
		return new ObservableProperty<>(initialValue, true);
	}

	@Override
	public boolean hasValue() {
		return hasValue;
	}

	@Override
	public T get() {
		if (!hasValue) {
			throw new IllegalStateException("Property " + name + " not initialized!");
		}

		return value;
	}

	/**
	 * Sets this property to a constant value.
	 *
	 * @param value the new value
	 * @throws NullPointerException if the value is null and nulls aren't allowed
	 */
	public void set(T value) {
		if (value == null && !allowNull) throw new NullPointerException("Trying to set null value for nonnull property " + name);
		T oldValue = this.value;
		this.value = value;
		hasValue = true;

		if (oldValue != value) {
			for (ChangeListener<? super T> listener : listeners) {
				listener.onPropertyChange(this, oldValue, value);
			}
		}
	}

	/**
	 * Clears the current value, if any, from this property.
	 */
	public void clear() {
		T oldValue = value;
		value = null;
		hasValue = false;

		if (oldValue != null) {
			for (ChangeListener<? super T> listener : listeners) {
				listener.onPropertyChange(this, oldValue, null);
			}
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

	/**
	 * Returns a read-only view of this property.
	 * The result is not an instance of {@link ObservableProperty},
	 * and thus can't be mutated.
	 *
	 * @return an observable view of this property
	 */
	public ObservableView<T> readOnly() {
		// Missing delegates from Kotlin... :(
		return new ObservableView<>() {
			@Override
			public boolean hasValue() {
				return ObservableProperty.this.hasValue();
			}

			@Override
			public T get() {
				return ObservableProperty.this.get();
			}

			@Override
			public void addListener(ChangeListener<? super T> listener) {
				ObservableProperty.this.addListener(listener);
			}

			@Override
			public void removeListener(ChangeListener<? super T> listener) {
				ObservableProperty.this.removeListener(listener);
			}
		};
	}

	/**
	 * {@return the name of this property}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this property, which is used in debug messages.
	 *
	 * @param name the new name
	 * @return this property
	 */
	public ObservableProperty<T> setName(String name) {
		this.name = Objects.requireNonNull(name, "name");
		return this;
	}

	@Override
	public void addListener(ChangeListener<? super T> listener) {
		Objects.requireNonNull(listener);
		listeners.add(listener);
	}

	@Override
	public void removeListener(ChangeListener<? super T> listener) {
		Objects.requireNonNull(listener);
		listeners.remove(listener);
	}
}
