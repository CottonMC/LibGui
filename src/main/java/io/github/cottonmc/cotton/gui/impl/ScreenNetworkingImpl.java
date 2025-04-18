package io.github.cottonmc.cotton.gui.impl;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.PacketEncoder;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenMessageKey;
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

public class ScreenNetworkingImpl implements ScreenNetworking {
	public static final ScreenMessageKey<Unit> CLIENT_READY_MESSAGE_KEY = new ScreenMessageKey<>(
		LibGuiCommon.id("client_ready"),
		Codec.unit(Unit.INSTANCE)
	);

	public record ScreenMessage(int syncId, Identifier message, byte[] content) implements CustomPayload {
		public static final Id<ScreenMessage> ID = new Id<>(LibGuiCommon.id("screen_message"));
		public static final PacketCodec<RegistryByteBuf, ScreenMessage> CODEC = PacketCodec.tuple(
			PacketCodecs.INTEGER, ScreenMessage::syncId,
			Identifier.PACKET_CODEC, ScreenMessage::message,
			PacketCodecs.BYTE_ARRAY, ScreenMessage::content,
			ScreenMessage::new
		);

		@Override
		public Id<? extends CustomPayload> getId() {
			return ID;
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ScreenNetworkingImpl.class);

	private final Map<Identifier, ReceiverData<?>> receivers = new HashMap<>();
	private final SyncedGuiDescription description;
	private final NetworkSide side;
	private final Event<ReadyListener> readyEvent;
	private boolean ready = false;

	public ScreenNetworkingImpl(SyncedGuiDescription description, NetworkSide side) {
		this.description = description;
		this.side = side;
		this.readyEvent = EventFactory.createArrayBacked(ReadyListener.class, listeners -> screenNetworking -> {
			for (ReadyListener listener : listeners) {
				listener.onConnected(screenNetworking);
			}
		});

		if (side == NetworkSide.SERVER) {
			receive(CLIENT_READY_MESSAGE_KEY, data -> markReady());
		}
	}

	@Override
	public <D> void receive(Identifier message, PacketDecoder<? super RegistryByteBuf, D> decoder, MessageReceiver<D> receiver) {
		Objects.requireNonNull(message, "message");
		Objects.requireNonNull(decoder, "decoder");
		Objects.requireNonNull(receiver, "receiver");

		if (!receivers.containsKey(message)) {
			receivers.put(message, new ReceiverData<>(decoder, receiver));
		} else {
			throw new IllegalStateException("Message " + message + " on side " + side + " already registered");
		}
	}

	@Override
	public <D> void send(Identifier message, PacketEncoder<? super RegistryByteBuf, D> encoder, D data) {
		Objects.requireNonNull(message, "message");
		Objects.requireNonNull(encoder, "encoder");

		byte[] content;
		var buf = new RegistryByteBuf(PacketByteBufs.create(), description.getWorld().getRegistryManager());
		try {
			encoder.encode(buf, data);
			content = new byte[buf.readableBytes()];
			buf.getBytes(buf.readerIndex(), content);
		} finally {
			buf.release();
		}

		ScreenMessage packet = new ScreenMessage(description.syncId, message, content);
		description.getPacketSender().sendPacket(packet);
	}

	@Override
	public Event<ReadyListener> getReadyEvent() {
		return readyEvent;
	}

	public boolean isReady() {
		return ready;
	}

	public void markReady() {
		ready = true;
		getReadyEvent().invoker().onConnected(this);
	}

	public static void init() {
		PayloadTypeRegistry.playS2C().register(ScreenMessage.ID, ScreenMessage.CODEC);
		PayloadTypeRegistry.playC2S().register(ScreenMessage.ID, ScreenMessage.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ScreenMessage.ID, (payload, context) -> {
			handle(context.player().server, context.player(), payload);
		});
	}

	public static void handle(Executor executor, PlayerEntity player, ScreenMessage packet) {
		ScreenHandler screenHandler = player.currentScreenHandler;

		if (!(screenHandler instanceof SyncedGuiDescription guiDescription)) {
			LOGGER.error("Received message packet for screen handler {} which is not a SyncedGuiDescription", screenHandler);
			return;
		} else if (packet.syncId() != screenHandler.syncId) {
			LOGGER.error("Received message for sync ID {}, current sync ID: {}", packet.syncId(), screenHandler.syncId);
			return;
		}

		var networking = (ScreenNetworkingImpl) guiDescription.getNetworking(guiDescription.getNetworkSide());
		ReceiverData<?> receiverData = networking.receivers.get(packet.message());
		if (receiverData != null) {
			processMessage(executor, player, packet, screenHandler, receiverData);
		} else {
			LOGGER.error("Message {} not registered for {} on side {}", packet.message(), screenHandler, networking.side);
		}
	}

	private static <D> void processMessage(Executor executor, PlayerEntity player, ScreenMessage packet, ScreenHandler description, ReceiverData<D> receiverData) {
		var buf = new RegistryByteBuf(Unpooled.wrappedBuffer(packet.content()), player.getRegistryManager());

		try {
			D data = receiverData.decoder().decode(buf);

			executor.execute(() -> {
				try {
					receiverData.receiver().onMessage(data);
				} catch (Exception e) {
					LOGGER.error("Error handling screen message {} for {}", packet.message(), description, e);
				}
			});
		} catch (Exception e) {
			LOGGER.error("Could not parse screen message {} for {}", packet.message(), description, e);
		} finally {
			buf.release();
		}
	}

	private record ReceiverData<D>(PacketDecoder<? super RegistryByteBuf, D> decoder, MessageReceiver<D> receiver) {
	}

	public static final class DummyNetworking extends ScreenNetworkingImpl {
		public DummyNetworking() {
			super(null, null);
		}

		@Override
		public <D> void receive(Identifier message, PacketDecoder<? super RegistryByteBuf, D> decoder, MessageReceiver<D> receiver) {
			// NO-OP
		}

		@Override
		public <D> void send(Identifier message, PacketEncoder<? super RegistryByteBuf, D> encoder, D data) {
			// NO-OP
		}
	}
}
