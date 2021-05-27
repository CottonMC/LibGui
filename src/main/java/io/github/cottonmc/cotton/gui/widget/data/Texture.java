package io.github.cottonmc.cotton.gui.widget.data;

import net.minecraft.util.Identifier;

import java.util.Objects;

/**
 * Represents a texture for a widget.
 *
 * @param image the image of this texture
 * @param u1 the start U-coordinate, between 0 and 1
 * @param v1 the start V-coordinate, between 0 and 1
 * @param u2 the end U-coordinate, between 0 and 1
 * @param v2 the end V-coordinate, between 0 and 1
 * @since 3.0.0
 */
public record Texture(Identifier image, float u1, float v1, float u2, float v2) {
	/**
	 * Constructs a new texture that uses the full image.
	 *
	 * @param image the image
	 * @throws NullPointerException if the image is null
	 */
	public Texture(Identifier image) {
		this(image, 0, 0, 1, 1);
	}

	/**
	 * Constructs a new texture with custom UV values.
	 *
	 * @param image the image
	 * @param u1    the left U coordinate
	 * @param v1    the top V coordinate
	 * @param u2    the right U coordinate
	 * @param v2    the bottom V coordinate
	 * @throws NullPointerException if the image is null
	 */
	public Texture {
		Objects.requireNonNull(image, "image");
	}

	/**
	 * Creates a new texture with different UV values.
	 *
	 * @param u1 the left U coordinate
	 * @param v1 the top V coordinate
	 * @param u2 the right U coordinate
	 * @param v2 the bottom V coordinate
	 * @return the created texture
	 */
	public Texture withUv(float u1, float v1, float u2, float v2) {
		return new Texture(image, u1, v1, u2, v2);
	}
}
