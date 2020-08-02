package io.github.cottonmc.cotton.gui.widget.data;

import net.minecraft.util.Identifier;

import java.util.Objects;

/**
 * Represents a texture for a widget.
 *
 * @since 3.0.0
 */
public final class Texture {
	/**
	 * The image of this texture.
	 */
	public final Identifier image;

	/**
	 * The UV coordinates of this texture, between 0 and 1.
	 */
	public final float u1, v1, u2, v2;

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
	public Texture(Identifier image, float u1, float v1, float u2, float v2) {
		this.image = Objects.requireNonNull(image, "image");

		this.u1 = u1;
		this.v1 = v1;
		this.u2 = u2;
		this.v2 = v2;
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
