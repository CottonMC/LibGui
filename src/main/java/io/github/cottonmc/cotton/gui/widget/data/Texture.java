package io.github.cottonmc.cotton.gui.widget.data;

import net.minecraft.util.Identifier;

import java.util.Objects;

/**
 * Represents a texture for a widget.
 *
 * <h2>Types</h2>
 * <p>Each texture has a type: it's either a {@linkplain Type#STANDALONE standalone texture file} or
 * a {@linkplain Type#GUI_SPRITE sprite on the GUI sprite atlas}. Their properties are slightly different.
 *
 * <p>GUI sprites can use their full range of features such as tiling, stretching and nine-slice drawing modes,
 * while standalone textures are only drawn stretched.
 *
 * <p>The format of the image ID depends on the type. See the documentation of the individual type constants and
 * the table below for details.
 * <table>
 *     <caption>Image IDs for each texture type</caption>
 *     <thead>
 *         <tr>
 *             <th>Type</th>
 *             <th>File path</th>
 *             <th>Image ID</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>{@link Type#STANDALONE STANDALONE}</td>
 *             <td>{@code assets/my_mod/textures/widget/example.png}</td>
 *             <td>{@code my_mod:textures/widget/example.png}</td>
 *         </tr>
 *         <tr>
 *             <td>{@link Type#GUI_SPRITE GUI_SPRITE}</td>
 *             <td>{@code assets/my_mod/textures/gui/sprites/example.png}</td>
 *             <td>{@code my_mod:example}</td>
 *         </tr>
 *     </tbody>
 * </table>
 *
 * <p>Note that the image ID can only be passed to non-{@code Texture} overloads of
 * <code>{@link io.github.cottonmc.cotton.gui.client.ScreenDrawing ScreenDrawing}.texturedRect()</code>
 * when the {@link #type() type} is {@link Type#STANDALONE}. GUI sprites need specialised code for drawing them,
 * and they need to be drawn with specific {@code Texture}-accepting methods
 * or {@link net.minecraft.client.gui.DrawContext}.
 *
 * <p>GUI sprite textures don't currently support flipping the texture by flipping UV coordinates.
 *
 * @param image the image of this texture
 * @param type  the type of this texture
 * @param u1 the start U-coordinate, between 0 and 1
 * @param v1 the start V-coordinate, between 0 and 1
 * @param u2 the end U-coordinate, between 0 and 1
 * @param v2 the end V-coordinate, between 0 and 1
 * @since 3.0.0
 */
public record Texture(Identifier image, Type type, float u1, float v1, float u2, float v2) {
	/**
	 * Constructs a new texture that uses the full image.
	 *
	 * @param image the image
	 * @param type  the type
	 * @throws NullPointerException if the image or the type is null
	 */
	public Texture(Identifier image, Type type) {
		this(image, type, 0, 0, 1, 1);
	}

	/**
	 * Constructs a new standalone texture with custom UV values.
	 *
	 * @param image the image of this texture
	 * @param u1 the start U-coordinate, between 0 and 1
	 * @param v1 the start V-coordinate, between 0 and 1
	 * @param u2 the end U-coordinate, between 0 and 1
	 * @param v2 the end V-coordinate, between 0 and 1
	 * @throws NullPointerException if the image is null
	 */
	public Texture(Identifier image, float u1, float v1, float u2, float v2) {
		this(image, Type.STANDALONE, u1, v1, u2, v2);
	}

	/**
	 * Constructs a new standalone texture that uses the full image.
	 *
	 * @param image the image
	 * @throws NullPointerException if the image is null
	 */
	public Texture(Identifier image) {
		this(image, Type.STANDALONE, 0, 0, 1, 1);
	}

	/**
	 * Constructs a new texture with custom UV values.
	 *
	 * @param image the image
	 * @param type  the type
	 * @param u1    the left U coordinate
	 * @param v1    the top V coordinate
	 * @param u2    the right U coordinate
	 * @param v2    the bottom V coordinate
	 * @throws NullPointerException if the image or the type is null
	 */
	public Texture {
		Objects.requireNonNull(image, "image");
		Objects.requireNonNull(type, "type");
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
		return new Texture(image, type, u1, v1, u2, v2);
	}

	/**
	 * A {@link Texture}'s type. It represents the location of the texture.
	 *
	 * @since 9.0.0
	 */
	public enum Type {
		/**
		 * A texture in a standalone texture file.
		 *
		 * <p>The image IDs of standalone textures contain the full file path to the texture inside
		 * the {@code assets/<namespace>} directory. For example, {@code my_mod:textures/widget/example.png} refers to
		 * {@code assets/my_mod/textures/widget/example.png}.
		 */
		STANDALONE,

		/**
		 * A texture in the GUI sprite atlas.
		 *
		 * <p>The image IDs of GUI sprites only contain the subpath to the texture inside the sprite directory without
		 * the file extension. For example, {@code my_mod:example} refers to
		 * {@code assets/my_mod/textures/gui/sprites/example.png}.
		 */
		GUI_SPRITE,
	}
}
