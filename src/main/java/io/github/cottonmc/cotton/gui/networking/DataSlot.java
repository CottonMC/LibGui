package io.github.cottonmc.cotton.gui.networking;

import net.fabricmc.fabric.api.event.Event;

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
 * myData.getValueChangedEvent().register((dataSlot, value) -> {
 * 	   System.out.println("updated data: " + value);
 * });
 * }
 *
 * @param <T> the data slot content type
 * @see io.github.cottonmc.cotton.gui.SyncedGuiDescription#registerDataSlot(ScreenMessageKey, Object, NetworkDirection)
 * @since 13.1.0
 */
@ApiStatus.NonExtendable
public interface DataSlot<T> {
	/**
	 * {@return the current value of the data slot}
	 */
	T get();

	/**
	 * Sets the current value of the data slot.
	 * If it's not equal to the previous value,
	 * {@link #getValueChangedEvent()} will be triggered.
	 *
	 * @param value the new value
	 */
	void set(T value);

	/**
	 * {@return an event triggered when this data slot's value changes}
	 */
	Event<ChangeListener<T>> getValueChangedEvent();

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
	 * A listener for {@link #getValueChangedEvent()}.
	 *
	 * @param <T> the data slot content type
	 */
	@FunctionalInterface
	interface ChangeListener<T> {
		/**
		 * Called when a data slot's value changes.
		 *
		 * @param dataSlot the data slot for which the event was triggered
		 * @param value    the new value
		 */
		void onValueChanged(DataSlot<T> dataSlot, T value);
	}
}
