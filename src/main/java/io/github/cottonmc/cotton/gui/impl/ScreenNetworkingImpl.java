package io.github.cottonmc.cotton.gui.impl;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
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

	public record ScreenMessageData(int syncId, Identifier message, PacketByteBuf buf) {
		public static final PacketCodec<RegistryByteBuf, ScreenMessageData> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.INTEGER, ScreenMessageData::syncId,
			Identifier.PACKET_CODEC, ScreenMessageData::message,
			PacketCodec.of(PacketByteBuf::writeBytes, packetBuf -> packetBuf.readBytes(PacketByteBufs.create())), ScreenMessageData::buf,
			ScreenMessageData::new
		);
	}

	public record S2CScreenMessage(ScreenMessageData data) implements CustomPayload {
		public static final Id<S2CScreenMessage> PACKET_ID = new Id<>(new Identifier(LibGuiCommon.MOD_ID, "screen_message_s2c"));
		public static final PacketCodec<RegistryByteBuf, S2CScreenMessage> PACKET_CODEC = ScreenMessageData.PACKET_CODEC.xmap(S2CScreenMessage::new, S2CScreenMessage::data);

		@Override
		public Id<? extends CustomPayload> getId() {
			return PACKET_ID;
		}
	}

	public record C2SScreenMessage(ScreenMessageData data) implements CustomPayload {
		public static final Id<C2SScreenMessage> PACKET_ID = new Id<>(new Identifier(LibGuiCommon.MOD_ID, "screen_message_c2s"));
		public static final PacketCodec<RegistryByteBuf, C2SScreenMessage> PACKET_CODEC = ScreenMessageData.PACKET_CODEC.xmap(C2SScreenMessage::new, C2SScreenMessage::data);

		@Override
		public Id<? extends CustomPayload> getId() {
			return PACKET_ID;
		}
	}

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
		writer.accept(buf);
		ScreenMessageData data = new ScreenMessageData(description.syncId, message, buf);
		description.getPacketSender().sendPacket(side == NetworkSide.SERVER ? new S2CScreenMessage(data) : new C2SScreenMessage(data));
	}

	public static void init() {
		PayloadTypeRegistry.playS2C().register(S2CScreenMessage.PACKET_ID, S2CScreenMessage.PACKET_CODEC);
		PayloadTypeRegistry.playC2S().register(C2SScreenMessage.PACKET_ID, C2SScreenMessage.PACKET_CODEC);
		ServerPlayNetworking.registerGlobalReceiver(C2SScreenMessage.PACKET_ID, (payload, context) -> {
			handle(context.player().server, context.player(), payload.data());
		});
	}

	public static void handle(Executor executor, PlayerEntity player, ScreenMessageData data) {
		ScreenHandler screenHandler = player.currentScreenHandler;

		if (!(screenHandler instanceof SyncedGuiDescription)) {
			LOGGER.error("Received message packet for screen handler {} which is not a SyncedGuiDescription", screenHandler);
			return;
		} else if (data.syncId() != screenHandler.syncId) {
			LOGGER.error("Received message for sync ID {}, current sync ID: {}", data.syncId(), screenHandler.syncId);
			return;
		}

		ScreenNetworkingImpl networking = instanceCache.get(screenHandler);

		if (networking != null) {
			MessageReceiver receiver = networking.messages.get(data.message());

			if (receiver != null) {
				data.buf().retain();
				executor.execute(() -> {
					try {
						receiver.onMessage(data.buf());
					} catch (Exception e) {
						LOGGER.error("Error handling screen message {} for {} on side {}", data.message(), screenHandler, networking.side, e);
					} finally {
						data.buf().release();
					}
				});
			} else {
				LOGGER.warn("Message {} not registered for {} on side {}", data.message(), screenHandler, networking.side);
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
