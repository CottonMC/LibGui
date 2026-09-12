package io.github.cottonmc.cotton.gui.impl;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenMessageKey;
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;

public class ScreenNetworkingImpl implements ScreenNetworking {
	// Matches the one used in PacketCodecs.codec() etc
	private static final long MAX_NBT_SIZE = 0x200000L;
	public static final ScreenMessageKey<Unit> CLIENT_READY_MESSAGE_KEY = new ScreenMessageKey<>(
		LibGuiCommon.id("client_ready"),
		MapCodec.unitCodec(Unit.INSTANCE)
	);

	public record ScreenMessage(int syncId, Identifier message, Tag nbt) implements CustomPacketPayload {
		public static final Type<ScreenMessage> ID = new Type<>(LibGuiCommon.id("screen_message"));
		public static final StreamCodec<RegistryFriendlyByteBuf, ScreenMessage> CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, ScreenMessage::syncId,
			Identifier.STREAM_CODEC, ScreenMessage::message,
			ByteBufCodecs.tagCodec(() -> NbtAccounter.create(MAX_NBT_SIZE)), ScreenMessage::nbt,
			ScreenMessage::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
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

	private static RegistryOps<Tag> getRegistryOps(RegistryAccess registryManager) {
		return registryManager.createSerializationContext(NbtOps.INSTANCE);
	}

	@Override
	public <D> void receive(Identifier message, Decoder<D> decoder, MessageReceiver<D> receiver) {
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
	public <D> void send(Identifier message, Encoder<D> encoder, D data) {
		Objects.requireNonNull(message, "message");
		Objects.requireNonNull(encoder, "encoder");

		var ops = getRegistryOps(description.getLevel().registryAccess());
		Tag encoded = encoder.encodeStart(ops, data).getOrThrow();
		ScreenMessage packet = new ScreenMessage(description.containerId, message, encoded);
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
		PayloadTypeRegistry.clientboundPlay().register(ScreenMessage.ID, ScreenMessage.CODEC);
		PayloadTypeRegistry.serverboundPlay().register(ScreenMessage.ID, ScreenMessage.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(ScreenMessage.ID, (payload, context) -> {
			handle(context.server(), context.player(), payload);
		});
	}

	public static void handle(Executor executor, Player player, ScreenMessage packet) {
		AbstractContainerMenu screenHandler = player.containerMenu;

		if (!(screenHandler instanceof SyncedGuiDescription guiDescription)) {
			LOGGER.error("Received message packet for screen handler {} which is not a SyncedGuiDescription", screenHandler);
			return;
		} else if (packet.syncId() != screenHandler.containerId) {
			LOGGER.error("Received message for sync ID {}, current sync ID: {}", packet.syncId(), screenHandler.containerId);
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

	private static <D> void processMessage(Executor executor, Player player, ScreenMessage packet, AbstractContainerMenu description, ReceiverData<D> receiverData) {
		var ops = getRegistryOps(player.registryAccess());
		var result = receiverData.decoder().parse(ops, packet.nbt());

		switch (result) {
			case DataResult.Success(D data, Lifecycle lifecycle) -> executor.execute(() -> {
				try {
					receiverData.receiver().onMessage(data);
				} catch (Exception e) {
					LOGGER.error("Error handling screen message {} for {}", packet.message(), description, e);
				}
			});

			case DataResult.Error<D> error -> LOGGER.error(
				"Could not parse screen message {}: {}",
				packet.message(),
				error.message()
			);
		}
	}

	private record ReceiverData<D>(Decoder<D> decoder, MessageReceiver<D> receiver) {
	}

	public static final class DummyNetworking extends ScreenNetworkingImpl {
		public DummyNetworking() {
			// Skip the IDEA inspection on these nulls. They're guaranteed to be fine.
			// noinspection DataFlowIssue
			super(null, null);
		}

		@Override
		public <D> void receive(Identifier message, Decoder<D> decoder, MessageReceiver<D> receiver) {
			// NO-OP
		}

		@Override
		public <D> void send(Identifier message, Encoder<D> encoder, D data) {
			// NO-OP
		}
	}
}
