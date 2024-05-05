package io.github.cottonmc.cotton.gui.networking;

import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.impl.ScreenNetworkingImpl;

/**
 * {@code ScreenNetworking} handles screen-related network messages sent between the server and the client.
 *
 * <h2>Registering a message receiver</h2>
 * {@linkplain MessageReceiver Message receivers} can be registered by calling {@link #receive}
 * on a {@code ScreenNetworking} for the receiving side. The {@code message} ID is a unique ID that matches between
 * the sender and the receiver.
 *
 * <p>Message receivers should be registered in the constructor of a {@link SyncedGuiDescription}.
 *
 * <h2>Sending messages</h2>
 * Messages can be sent by calling {@link #send} on a {@code ScreenNetworking}
 * for the sending side. The message ID and codec should match up with a receiver registered on the <i>opposite</i>
 * side.
 *
 * <h2>Example</h2>
 * {@snippet :
 * private static final Identifier MESSAGE_ID = new Identifier("my_mod", "some_message");
 *
 * // Receiver
 * ScreenNetworking.of(this, NetworkSide.SERVER).receive(MESSAGE_ID, Codec.INT, data -> {
 * 	   // Example data: a lucky number as an int
 *     System.out.println("Your lucky number is " + data + "!");
 * });
 *
 * // Sending
 *
 * // We're sending from a button. The packet data is our lucky number, 123.
 * WButton button = ...;
 * button.setOnClick(() -> {
 *     ScreenNetworking.of(this, NetworkSide.CLIENT).send(MESSAGE_ID, Codec.INT, 123);
 * });
 * }
 *
 * @since 3.3.0
 */
public interface ScreenNetworking {
	/**
	 * Gets a networking handler for the GUI description that is active on the specified side.
	 *
	 * @param description the GUI description
	 * @param networkSide the network side
	 * @return the network handler
	 * @throws NullPointerException if either parameter is null
	 */
	static ScreenNetworking of(SyncedGuiDescription description, NetworkSide networkSide) {
		return ScreenNetworkingImpl.of(description, networkSide);
	}

	/**
	 * Registers a message receiver for the message.
	 *
	 * <p>The decoder can depend on registry data and {@link net.minecraft.registry.RegistryOps} is available.
	 *
	 * @param message  the screen message ID
	 * @param decoder  the message codec
	 * @param receiver the message receiver
	 * @param <D> the message data type
	 * @throws IllegalStateException if the message has already been registered
	 * @throws NullPointerException  if any parameter is null
	 */
	<D> void receive(Identifier message, Decoder<D> decoder, MessageReceiver<D> receiver);

	/**
	 * Sends a screen message to the other side of the connection.
	 *
	 * <p>The encoder can depend on registry data and {@link net.minecraft.registry.RegistryOps} is available.
	 *
	 * @param message the screen message ID
	 * @param encoder the message encoder
	 * @param data    the message data
	 * @throws NullPointerException if the message ID or the encoder is null
	 */
	<D> void send(Identifier message, Encoder<D> encoder, D data);

	/**
	 * A handler for received screen messages.
	 *
	 * @param <D> the message data type
	 */
	@FunctionalInterface
	interface MessageReceiver<D> {
		/**
		 * Handles a received screen message.
		 *
		 * @param data the message data
		 */
		void onMessage(D data);
	}
}
