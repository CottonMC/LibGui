package io.github.cottonmc.cotton.gui.impl;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public class ScreenNetworkingImpl implements ScreenNetworking {
	// Packet structure:
	//   syncId: int
	//   message: identifier
	//   rest: buf

	public static final Identifier SCREEN_MESSAGE_S2C = new Identifier(LibGuiCommon.MOD_ID, "screen_message_s2c");
	public static final Identifier SCREEN_MESSAGE_C2S = new Identifier(LibGuiCommon.MOD_ID, "screen_message_c2s");

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<SyncedGuiDescription, ScreenNetworkingImpl> instanceCache = new WeakHashMap<>();

	private final Map<Identifier, MessageReceiver> messages = new HashMap<>();
	private SyncedGuiDescription description;
	private final NetworkSide side;

	private ScreenNetworkingImpl(SyncedGuiDescription description, NetworkSide side) {
		this.description = description;
		this.side = side;
	}

	public void receive(Identifier message, MessageReceiver receiver) {
		Objects.requireNonNull(message, "message");
		Objects.requireNonNull(receiver, "receiver");

		if (!messages.containsKey(message)) {
			messages.put(message, receiver);
		} else {
			throw new IllegalStateException("Message " + message + " on side " + side + " already registered");
		}
	}

	@Override
	public void send(Identifier message, Consumer<PacketByteBuf> writer) {
		Objects.requireNonNull(message, "message");
		Objects.requireNonNull(writer, "writer");

		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(description.syncId);
		buf.writeIdentifier(message);
		writer.accept(buf);
		description.getPacketSender().sendPacket(side == NetworkSide.SERVER ? SCREEN_MESSAGE_S2C : SCREEN_MESSAGE_C2S, buf);
	}

	public static void init() {
		ServerPlayNetworking.registerGlobalReceiver(SCREEN_MESSAGE_C2S, (server, player, networkHandler, buf, responseSender) -> {
			handle(server, player, buf);
		});
	}

	public static void handle(Executor executor, PlayerEntity player, PacketByteBuf buf) {
		ScreenHandler screenHandler = player.currentScreenHandler;

		// Packet data
		int syncId = buf.readVarInt();
		Identifier messageId = buf.readIdentifier();

		if (!(screenHandler instanceof SyncedGuiDescription)) {
			LOGGER.error("Received message packet for screen handler {} which is not a SyncedGuiDescription", screenHandler);
			return;
		} else if (syncId != screenHandler.syncId) {
			LOGGER.error("Received message for sync ID {}, current sync ID: {}", syncId, screenHandler.syncId);
			return;
		}

		ScreenNetworkingImpl networking = instanceCache.get(screenHandler);

		if (networking != null) {
			MessageReceiver receiver = networking.messages.get(messageId);

			if (receiver != null) {
				buf.retain();
				executor.execute(() -> {
					try {
						receiver.onMessage(buf);
					} catch (Exception e) {
						LOGGER.error("Error handling screen message {} for {} on side {}", messageId, screenHandler, networking.side, e);
					} finally {
						buf.release();
					}
				});
			} else {
				LOGGER.warn("Message {} not registered for {} on side {}", messageId, screenHandler, networking.side);
			}
		} else {
			LOGGER.warn("GUI description {} does not use networking", screenHandler);
		}
	}

	public static ScreenNetworking of(SyncedGuiDescription description, NetworkSide networkSide) {
		Objects.requireNonNull(description, "description");
		Objects.requireNonNull(networkSide, "networkSide");

		if (description.getNetworkSide() == networkSide) {
			return instanceCache.computeIfAbsent(description, it -> new ScreenNetworkingImpl(description, networkSide));
		} else {
			return DummyNetworking.INSTANCE;
		}
	}

	private static final class DummyNetworking extends ScreenNetworkingImpl {
		static final DummyNetworking INSTANCE = new DummyNetworking();

		private DummyNetworking() {
			super(null, null);
		}

		@Override
		public void receive(Identifier message, MessageReceiver receiver) {
			// NO-OP
		}

		@Override
		public void send(Identifier message, Consumer<PacketByteBuf> writer) {
			// NO-OP
		}
	}
}
