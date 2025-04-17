package io.github.cottonmc.cotton.gui.networking;

/**
 * The directions of network packets.
 *
 * @since 13.1.0
 */
public enum NetworkDirection {
	/**
	 * {@linkplain NetworkSide#CLIENT Client} to {@linkplain NetworkSide#SERVER server}.
	 */
	CLIENT_TO_SERVER(NetworkSide.CLIENT, NetworkSide.SERVER),
	/**
	 * {@linkplain NetworkSide#SERVER Server} to {@linkplain NetworkSide#CLIENT client}.
	 */
	SERVER_TO_CLIENT(NetworkSide.SERVER, NetworkSide.CLIENT);

	private final NetworkSide from;
	private final NetworkSide to;

	NetworkDirection(NetworkSide from, NetworkSide to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * {@return the source network side}
	 */
	public NetworkSide from() {
		return from;
	}

	/**
	 * {@return the destination network side}
	 */
	public NetworkSide to() {
		return to;
	}
}
