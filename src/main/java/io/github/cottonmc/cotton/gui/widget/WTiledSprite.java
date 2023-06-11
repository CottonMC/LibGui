package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.data.Texture;

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
	 * Create a tiled sprite.
	 *
	 * @param tileWidth  The width a tile
	 * @param tileHeight The height of a tile
	 * @param image      The image to tile
	 * @since 3.0.0
	 */
	public WTiledSprite(int tileWidth, int tileHeight, Texture image) {
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
	 * @since 3.0.0
	 */
	public WTiledSprite(int tileWidth, int tileHeight, int frameTime, Texture... frames) {
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

	/**
	 * Gets the tile width of this sprite.
	 *
	 * @return the tile width
	 * @since 2.2.0
	 */
	public int getTileWidth() {
		return tileWidth;
	}

	/**
	 * Gets the tile height of this sprite.
	 *
	 * @return the tile height
	 * @since 2.2.0
	 */
	public int getTileHeight() {
		return tileHeight;
	}

	/**
	 * Sets the tile width of this sprite.
	 *
	 * @param tileWidth the new tile width
	 * @return this sprite
	 * @since 2.2.0
	 */
	public WTiledSprite setTileWidth(int tileWidth) {
		this.tileWidth = tileWidth;
		return this;
	}

	/**
	 * Sets the tile height of this sprite.
	 *
	 * @param tileHeight the new tile height
	 * @return this sprite
	 * @since 2.2.0
	 */
	public WTiledSprite setTileHeight(int tileHeight) {
		this.tileHeight = tileHeight;
		return this;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paintFrame(DrawContext context, int x, int y, Texture texture) {
		// Y Direction (down)
		for (int tileYOffset = 0; tileYOffset < height; tileYOffset += tileHeight) {
			// X Direction (right)
			for (int tileXOffset = 0; tileXOffset < width; tileXOffset += tileWidth) {
				// draw the texture
				ScreenDrawing.texturedRect(
						context,
						// at the correct position using tileXOffset and tileYOffset
						x + tileXOffset, y + tileYOffset,
						// but using the set tileWidth and tileHeight instead of the full height and
						// width
						getTileWidth(), getTileHeight(),
						// render the current texture
						texture,
						tint);
			}
		}
	}
}
