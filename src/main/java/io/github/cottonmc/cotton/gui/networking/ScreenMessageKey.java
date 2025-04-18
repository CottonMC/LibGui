package io.github.cottonmc.cotton.gui.networking;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;

import java.util.Objects;

/**
 * Contains the ID and codec of a screen message.
 *
 * @param id    the message id
 * @param codec the codec used to encode and decode message data
 * @param <D>   the message data type
 * @since 13.1.0
 */
public record ScreenMessageKey<D>(Identifier id, PacketCodec<? super RegistryByteBuf, D> codec) {
	public ScreenMessageKey {
		Objects.requireNonNull(id, "id");
		Objects.requireNonNull(codec, "codec");
	}

	public ScreenMessageKey(Identifier id, Codec<D> codec) {
		this(id, PacketCodecs.codec(Objects.requireNonNull(codec, "codec")));
	}
}
