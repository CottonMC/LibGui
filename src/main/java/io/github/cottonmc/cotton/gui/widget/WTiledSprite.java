package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

/**
 * A sprite whose texture will be tiled.
 *
 * @since 2.0.0
 */
public class WTiledSprite extends WSprite {
	private int tileWidth;
	private int tileHeight;

	/**
	 * Create a tiled sprite.
	 * 
	 * @param tileWidth  The width a tile
	 * @param tileHeight The height of a tile
	 * @param image      The image to tile
	 */
	public WTiledSprite(int tileWidth, int tileHeight, Identifier image) {
		super(image);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	/**
	 * Create a new animated tiled sprite.
	 *
	 * @param tileWidth  The width a tile
	 * @param tileHeight The height of a tile
	 * @param frameTime  How long in milliseconds to display for. (1 tick = 50 ms)
	 * @param frames     The locations of the frames of the animation.
	 */
	public WTiledSprite(int tileWidth, int tileHeight, int frameTime, Identifier... frames) {
		super(frameTime, frames);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	/**
	 * Sets the tiling size. This determines how often the texture will repeat.
	 *
	 * @param width  the new tiling width
	 * @param height the new tiling height
	 */
	public void setTileSize(int width, int height) {
		tileWidth = width;
		tileHeight = height;
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paintFrame(int x, int y, Identifier texture) {
		// Y Direction (down)
		for (int tileYOffset = 0; tileYOffset < height; tileYOffset += tileHeight) {
			// X Direction (right)
			for (int tileXOffset = 0; tileXOffset < width; tileXOffset += tileWidth) {
				// draw the texture
				ScreenDrawing.texturedRect(
						// at the correct position using tileXOffset and tileYOffset
						x + tileXOffset, y + tileYOffset,
						// but using the set tileWidth and tileHeight instead of the full height and
						// width
						getTileWidth(), getTileHeight(),
						// render the current texture
						texture,
						// clips the texture if wanted
						u1, v1, u2, v2, tint);
			}
		}
	}
}
