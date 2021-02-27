package io.github.cottonmc.cotton.gui.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import io.github.cottonmc.cotton.gui.impl.client.NinePatchInternals;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import org.jetbrains.annotations.Nullable;

/**
 * The nine-patch background painter paints rectangles using a nine-patch texture.
 *
 * <p>Nine-patch textures are separated into nine sections: four corners, four edges and a center part.
 * The edges and the center are either tiled or stretched, depending on the {@linkplain Mode mode},
 * to fill the area between the corners. By default, the texture mode is loaded from the texture metadata.
 * The default mode for that is {@link Mode#STRETCHING}.
 *
 * <p>{@code NinePatch} painters have a customizable padding that can be applied.
 * For example, a GUI panel for a container block might have a padding of 8 pixels, like {@link BackgroundPainter#VANILLA}.
 * You can set the padding using {@link NinePatch#setPadding(int)}.
 *
 * <h2>Nine-patch metadata</h2>
 * You can specify metadata for a nine-patch texture in a resource pack by creating a metadata file.
 * Metadata files can currently specify the filling mode of the painter that paints the texture.
 * <p>The metadata file for a texture has to be placed in the same directory as the texture.
 * The file name must be {@code X.9patch} where X is the texture file name (including .png).
 * <p>Metadata files use {@linkplain java.util.Properties the .properties format} with the following keys:
 * <table border="1">
 *     <caption>Properties</caption>
 *     <tr>
 *         <th>Key</th>
 *         <th>Value</th>
 *         <th>Description</th>
 *     </tr>
 *     <tr>
 *         <td>{@code mode}</td>
 *         <td>{@link Mode#STRETCHING stretching} | {@link Mode#TILING tiling}</td>
 *         <td>The texture filling mode</td>
 *     </tr>
 * </table>
 *
 * @since 1.5.0
 */
@Environment(EnvType.CLIENT)
public class NinePatch implements BackgroundPainter {
	private final Identifier texture;
	private final int cornerSize;
	private final float cornerUv;
	private int topPadding = 0;
	private int leftPadding = 0;
	private int bottomPadding = 0;
	private int rightPadding = 0;
	private Mode mode = null;

	/**
	 * Creates a nine-patch background painter with 4 px corners and a 0.25 corner UV.
	 *
	 * @param texture the texture ID
	 */
	public NinePatch(Identifier texture) {
		this(texture, 4, 0.25f);
	}

	/**
	 * Creates a nine-patch background painter.
	 *
	 * @param texture    the texture ID
	 * @param cornerSize the size of the corners on the screen
	 * @param cornerUv   the fraction of the corners from the whole texture
	 */
	public NinePatch(Identifier texture, int cornerSize, float cornerUv) {
		this.texture = texture;
		this.cornerSize = cornerSize;
		this.cornerUv = cornerUv;
	}

	public int getTopPadding() {
		return topPadding;
	}

	public NinePatch setTopPadding(int topPadding) {
		this.topPadding = topPadding;
		return this;
	}

	public int getLeftPadding() {
		return leftPadding;
	}

	public NinePatch setLeftPadding(int leftPadding) {
		this.leftPadding = leftPadding;
		return this;
	}

	public int getBottomPadding() {
		return bottomPadding;
	}

	public NinePatch setBottomPadding(int bottomPadding) {
		this.bottomPadding = bottomPadding;
		return this;
	}

	public int getRightPadding() {
		return rightPadding;
	}

	public NinePatch setRightPadding(int rightPadding) {
		this.rightPadding = rightPadding;
		return this;
	}

	public NinePatch setPadding(int padding) {
		this.topPadding = this.leftPadding = this.bottomPadding = this.rightPadding = padding;
		return this;
	}

	public NinePatch setPadding(int vertical, int horizontal) {
		this.topPadding = this.bottomPadding = vertical;
		this.leftPadding = this.rightPadding = horizontal;
		return this;
	}

	public NinePatch setPadding(int topPadding, int leftPadding, int bottomPadding, int rightPadding) {
		this.topPadding = topPadding;
		this.leftPadding = leftPadding;
		this.bottomPadding = bottomPadding;
		this.rightPadding = rightPadding;

		return this;
	}

	public Identifier getTexture() {
		return texture;
	}

	public int getCornerSize() {
		return cornerSize;
	}

	@Nullable
	public Mode getMode() {
		return mode;
	}

	/**
	 * Sets the {@linkplain Mode mode} of this painter to the specified mode.
	 * <p>If the {@code mode} is not null, it will override the one specified in the texture metadata.
	 * A null mode uses the texture metadata.
	 */
	public NinePatch setMode(@Nullable Mode mode) {
		this.mode = mode;
		return this;
	}

	@Override
	public void paintBackground(MatrixStack matrices, int left, int top, WWidget panel) {
		int width = panel.getWidth() + leftPadding + rightPadding;
		int height = panel.getHeight() + topPadding + bottomPadding;
		left = left - leftPadding;
		top = top - topPadding;
		int x1 = left + cornerSize;
		int x2 = left + width - cornerSize;
		int y1 = top + cornerSize;
		int y2 = top + height - cornerSize;
		float uv1 = cornerUv;
		float uv2 = 1.0f - cornerUv;
		Mode mode = this.mode != null ? this.mode : NinePatchInternals.MetadataLoader.INSTANCE.getProperties(texture).getMode();

		ScreenDrawing.texturedRect(matrices, left, top, cornerSize, cornerSize, texture, 0, 0, uv1, uv1, 0xFF_FFFFFF);
		ScreenDrawing.texturedRect(matrices, x2, top, cornerSize, cornerSize, texture, uv2, 0, 1, uv1, 0xFF_FFFFFF);
		ScreenDrawing.texturedRect(matrices, left, y2, cornerSize, cornerSize, texture, 0, uv2, uv1, 1, 0xFF_FFFFFF);
		ScreenDrawing.texturedRect(matrices, x2, y2, cornerSize, cornerSize, texture, uv2, uv2, 1, 1, 0xFF_FFFFFF);

		if (mode == Mode.TILING) {
			int tileSize = (int) (cornerSize / cornerUv - 2 * cornerSize);
			int widthLeft = width - 2 * cornerSize;
			int heightLeft = height - 2 * cornerSize;
			int tileCountX = MathHelper.ceil((float) widthLeft / tileSize);
			int tileCountY = MathHelper.ceil((float) heightLeft / tileSize);
			for (int i = 0; i < tileCountX; i++) {
				float px = 1 / 16f;
				int tileWidth = Math.min(widthLeft, tileSize);
				float uo = (tileSize - tileWidth) * px; // Used to remove unnecessary pixels on the X axis

				ScreenDrawing.texturedRect(matrices, x1 + i * tileSize, top, tileWidth, cornerSize, texture, uv1, 0, uv2 - uo, uv1, 0xFF_FFFFFF);
				ScreenDrawing.texturedRect(matrices, x1 + i * tileSize, y2, tileWidth, cornerSize, texture, uv1, uv2, uv2 - uo, 1, 0xFF_FFFFFF);

				// Reset the height left each time the Y is looped
				heightLeft = height - 2 * cornerSize;

				for (int j = 0; j < tileCountY; j++) {
					int tileHeight = Math.min(heightLeft, tileSize);
					float vo = (tileSize - tileHeight) * px; // Used to remove unnecessary pixels on the Y axis

					ScreenDrawing.texturedRect(matrices, left, y1 + j * tileSize, cornerSize, tileHeight, texture, 0, uv1, uv1, uv2 - vo, 0xFF_FFFFFF);
					ScreenDrawing.texturedRect(matrices, x2, y1 + j * tileSize, cornerSize, tileHeight, texture, uv2, uv1, 1, uv2 - vo, 0xFF_FFFFFF);

					ScreenDrawing.texturedRect(matrices, x1 + i * tileSize, y1 + j * tileSize, tileWidth, tileHeight, texture, uv1, uv1, uv2 - uo, uv2 - vo, 0xFF_FFFFFF);
					heightLeft -= tileSize;
				}
				widthLeft -= tileSize;
			}
		} else {
			ScreenDrawing.texturedRect(matrices, x1, top, width - 2 * cornerSize, cornerSize, texture, uv1, 0, uv2, uv1, 0xFF_FFFFFF);
			ScreenDrawing.texturedRect(matrices, left, y1, cornerSize, height - 2 * cornerSize, texture, 0, uv1, uv1, uv2, 0xFF_FFFFFF);
			ScreenDrawing.texturedRect(matrices, x1, y2, width - 2 * cornerSize, cornerSize, texture, uv1, uv2, uv2, 1, 0xFF_FFFFFF);
			ScreenDrawing.texturedRect(matrices, x2, y1, cornerSize, height - 2 * cornerSize, texture, uv2, uv1, 1, uv2, 0xFF_FFFFFF);

			ScreenDrawing.texturedRect(matrices, x1, y1, width - 2 * cornerSize, height - 2 * cornerSize, texture, uv1, uv1, uv2, uv2, 0xFF_FFFFFF);
		}
	}

	/**
	 * The mode of a nine-patch painter defines how it fills the area between the corners.
	 */
	public enum Mode {
		/**
		 * The texture is stretched to fill the edges and the center.
		 * This is the default mode.
		 */
		STRETCHING,

		/**
		 * The texture is tiled to fill the edges and the center.
		 */
		TILING;

		/**
		 * Deserializes a nine-patch mode from a string.
		 *
		 * @param str the mode string
		 * @return the mode, or null if the string is invalid
		 * @since 4.0.0
		 */
		@Nullable
		public static Mode fromString(String str) {
			if (str == null) return null;

			if (str.equalsIgnoreCase("stretching")) return STRETCHING;
			if (str.equalsIgnoreCase("tiling")) return TILING;

			return null;
		}
	}

}
