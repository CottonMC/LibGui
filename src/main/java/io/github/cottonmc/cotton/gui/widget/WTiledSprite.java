package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.minecraft.util.Identifier;

// It's a Sprite that can be tiled, but does not support animations
public class WTiledSprite extends WSprite {
  public int tileWidth = 20;
  public int tileHeight = 20;

	WTiledSprite(Identifier image) {
		super(image);
	}

	public WTiledSprite(Identifier image, int tileWidth, int tileHeight) {
		super(image);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}
	
	/**
	 * Sets the tiling size. This determines how often the image will repeat.
	 *
	 * @param width the new tiling width
	 * @param height the new tiling height
	 */
	public void setTileSize(int width, int height) {
		tileWidth = width;
		tileHeight = height;
	}

	@Override
	public void paintFrame(int x, int y, Identifier texture) {
		// Y Direction (down)
		for (int tileYOffset = 0; tileYOffset < height; tileYOffset+=tileHeight) {
			// X Direction (right)
			for (int tileXOffset = 0; tileXOffset < width; tileXOffset+=tileWidth) {
				// draw the texture
				ScreenDrawing.texturedRect(
					// at the correct position using tileXOffset and tileYOffset
					x + tileXOffset,
					y + tileYOffset,
					// but using the set tileWidth and tileHeight instead of the full height and width
					tileWidth,
					tileHeight,
					// render the current texture
					texture,
					// clips the texture if wanted
					u1, v1, u2, v2,
					tint
				);
			}
		}
	}
}
