package io.github.cottonmc.cotton.gui.networking;

import io.github.cottonmc.cotton.gui.widget.data.ObservableProperty;
import org.jetbrains.annotations.ApiStatus;

/**
 * A holder for an automatically synced data value.
 * This is similar to vanilla {@linkplain net.minecraft.screen.PropertyDelegate property delegates},
 * which only work for ints.
 *
 * <p>You can use data slots to sync a single data value from the server to the client or vice versa.
 * (A data slot can only sync data in one direction, however.)
 * For example, you can use it to create the label of a button on the server
 * and send it to the client.
 *
 * <h2>Example</h2>
 * {@snippet :
 * // Register a server-to-client data slot holding a double.
 * private static final ScreenMessageKey<Double> MY_DATA_SLOT_KEY = new ScreenMessageKey<>(
 *     Identifier.of("my_mod", "my_data"),
 *     Codec.DOUBLE
 * );
 *
 * // This line should be called on both sides of the connection.
 * DataSlot<Double> myData = registerDataSlot(MY_DATA_SLOT_KEY, 123.456);
 * // The initial value of 123.456 will never be synced!
 * // If you want to sync a value regardless, use DataSlot.set:
 * if (!getWorld().isClient() && someCondition) {
 *     myData.set(Math.PI);
 * }
 *
 * // You can listen to data slot updates on both sides:
 * myData.addChangeListener((dataSlot, from, to) -> {
 *     System.out.println("updated data: " + value);
 * });
 * }
 *
 * @properties
 * @param <T> the data slot content type
 * @see io.github.cottonmc.cotton.gui.SyncedGuiDescription#registerDataSlot(ScreenMessageKey, Object, NetworkDirection)
 * @since 13.1.0
 */
@ApiStatus.NonExtendable
public interface DataSlot<T> {
	/**
	 * Returns the current value of the data slot.
	 * The result is an <em>observable property</em> that can be modified and listened to.
	 *
	 * @return the {@code value} property
	 */
	ObservableProperty<T> valueProperty();

	/**
	 * {@return the current value of the data slot}
	 */
	default T get() {
		return valueProperty().get();
	}

	/**
	 * Sets the current value of the data slot.
	 *
	 * @param value the new value
	 */
	default void set(T value) {
		valueProperty().set(value);
	}

	/**
	 * Adds a change listener to this data slot.
	 *
	 * @param listener the added listener
	 */
	default void addChangeListener(ChangeListener<T> listener) {
		valueProperty().addListener((property, from, to) -> listener.onValueChanged(this, from, to));
	}

	/**
	 * {@return the key of the message that syncs this data slot}
	 * The message's content is the new {@code T} value.
	 */
	ScreenMessageKey<T> getKey();

	/**
	 * {@return the sync direction of this data slot}
	 */
	NetworkDirection getNetworkDirection();

	/**
	 * A listener for data slot value changes.
	 *
	 * @param <T> the data slot content type
	 */
	@FunctionalInterface
	interface ChangeListener<T> {
		/**
		 * Called when a data slot's value changes.
		 *
		 * @param dataSlot the data slot for which the event was triggered
		 * @param from     the old value
		 * @param to       the new value
		 */
		void onValueChanged(DataSlot<T> dataSlot, T from, T to);
	}
}
