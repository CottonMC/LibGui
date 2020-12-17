package io.github.cottonmc.cotton.gui.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.impl.ScreenNetworkingImpl;

import java.util.function.Consumer;

/**
 * {@code ScreenNetworking} handles screen-related network messages sent between the server and the client.
 *
 * <h2>Registering a message receiver</h2>
 * {@linkplain MessageReceiver Message receivers} can be registered by calling {@link #receive(Identifier, MessageReceiver)}
 * on a {@code ScreenNetworking} for the receiving side. The {@code message} ID is a unique ID that matches between
 * the sender and the receiver.
 *
 * <p>Message receivers should be registered in the constructor of a {@link SyncedGuiDescription}.
 *
 * <h2>Sending messages</h2>
 * Messages can be sent by calling {@link #send(Identifier, Consumer)} on a {@code ScreenNetworking}
 * for the sending side. The {@code message} ID should match up with a receiver registered on the <i>opposite</i>
 * side.
 *
 * <h2>Example</h2>
 * <pre>
 * {@code
 * private static final Identifier MESSAGE_ID = new Identifier("my_mod", "some_message");
 *
 * // Receiver
 * ScreenNetworking.of(this, NetworkSide.SERVER).receive(MESSAGE_ID, buf -> {
 * 	   // Example data: a lucky number as an int
 *     System.out.println("Your lucky number is " + buf.readInt() + "!");
 * });
 *
 * // Sending
 *
 * // We're sending from a button. The packet data is our lucky number, 123.
 * WButton button = ...;
 * button.setOnClick(() -> {
 *     ScreenNetworking.of(this, NetworkSide.CLIENT).send(MESSAGE_ID, buf -> buf.writeInt(123));
 * });
 * }
 * </pre>
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
	 * @param message  the screen message ID
	 * @param receiver the message receiver
	 * @throws IllegalStateException if the message has already been registered
	 * @throws NullPointerException  if either parameter is null
	 */
	void receive(Identifier message, MessageReceiver receiver);

	/**
	 * Sends a screen message to the other side of the connection.
	 *
	 * @param message the screen message ID
	 * @param writer  a writer that writes the message contents to a packet buffer;
	 *                should not read the buffer
	 * @throws NullPointerException if either parameter is null
	 */
	void send(Identifier message, Consumer<PacketByteBuf> writer);

	/**
	 * A handler for received screen messages.
	 */
	@FunctionalInterface
	interface MessageReceiver {
		/**
		 * Handles a received screen message.
		 *
		 * <p>This method should only read from the buffer, not write to it.
		 *
		 * @param buf the message packet buffer
		 */
		void onMessage(PacketByteBuf buf);
	}
}
