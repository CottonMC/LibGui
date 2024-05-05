package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.data.Texture;

public class WSprite extends WWidget {
	protected int currentFrame= 0;
	protected long currentFrameTime = 0;
	protected Texture[] frames;
	protected int frameTime;
	protected long lastFrame;
	protected boolean singleImage = false;
	protected int tint = 0xFFFFFFFF;

	/**
	 * Create a new sprite with a single image.
	 * @param texture The image texture to display.
	 * @since 3.0.0
	 */
	public WSprite(Texture texture) {
		this.frames = new Texture[]{texture};
		this.singleImage = true;
	}

	/**
	 * Create a new sprite with a single image.
	 * @param image The location of the image to display.
	 */
	public WSprite(Identifier image) {
		this(new Texture(image));
	}

	/**
	 * Create a new sprite with a single image and custom UV values.
	 *
	 * @param image The location of the image to display.
	 * @param u1 the left edge of the texture
	 * @param v1 the top edge of the texture
	 * @param u2 the right edge of the texture
	 * @param v2 the bottom edge of the texture
	 */
	public WSprite(Identifier image, float u1, float v1, float u2, float v2) {
		this(new Texture(image, u1, v1, u2, v2));
	}

	/**
	 * Create a new animated sprite.
	 * @param frameTime How long in milliseconds to display for. (1 tick = 50 ms)
	 * @param frames The locations of the frames of the animation.
	 */
	public WSprite(int frameTime, Identifier... frames) {
		this.frameTime = frameTime;
		this.frames = new Texture[frames.length];

		for (int i = 0; i < frames.length; i++) {
			this.frames[i] = new Texture(frames[i]);
		}

		if (frames.length==1) this.singleImage = true;
	}

	/**
	 * Create a new animated sprite.
	 * @param frameTime How long in milliseconds to display for. (1 tick = 50 ms)
	 * @param frames The locations of the frames of the animation.
	 * @since 3.0.0
	 */
	public WSprite(int frameTime, Texture... frames) {
		this.frameTime = frameTime;
		this.frames = frames;
		if (frames.length==1) this.singleImage = true;
	}

	/**
	 * Sets the image of this sprite.
	 *
	 * @param image the new image
	 * @return this sprite
	 */
	public WSprite setImage(Identifier image) {
		return setImage(new Texture(image));
	}

	/**
	 * Sets the animation frames of this sprite.
	 *
	 * @param frames the frames
	 * @return this sprite
	 */
	public WSprite setFrames(Identifier... frames) {
		Texture[] textures = new Texture[frames.length];
		for (int i = 0; i < frames.length; i++) {
			textures[i] = new Texture(frames[i]);
		}
		return setFrames(textures);
	}

	/**
	 * Sets the image of this sprite.
	 *
	 * @param image the new image
	 * @return this sprite
	 * @since 3.0.0
	 */
	public WSprite setImage(Texture image) {
		this.frames = new Texture[]{image};
		this.singleImage = true;
		this.currentFrame = 0;
		this.currentFrameTime = 0;
		return this;
	}

	/**
	 * Sets the animation frames of this sprite.
	 *
	 * @param frames the frames
	 * @return this sprite
	 * @since 3.0.0
	 */
	public WSprite setFrames(Texture... frames) {
		this.frames = frames;
		if (frames.length==1) singleImage = true;
		if (currentFrame>=frames.length) {
			currentFrame = 0;
			currentFrameTime = 0;
		}
		return this;
	}

	/**
	 * Sets the tint for this sprite to the following color-with-alpha. If you don't want to specify
	 * alpha, use {@link #setOpaqueTint(int)} instead.
	 *
	 * @param tint the new tint
	 * @return this sprite
	 */
	public WSprite setTint(int tint) {
		this.tint = tint;
		return this;
	}

	/**
	 * Sets the tint for this sprite to the following opaque color.
	 *
	 * @param tint the new tint
	 * @return this sprite
	 */
	public WSprite setOpaqueTint(int tint) {
		this.tint = tint | 0xFF000000;
		return this;
	}

	/**
	 * Sets the UV values of this sprite.
	 *
	 * @param u1 the left edge of the texture
	 * @param v1 the top edge of the texture
	 * @param u2 the right edge of the texture
	 * @param v2 the bottom edge of the texture
	 *
	 * @return this sprite
	 * @since 1.8.0
	 */
	public WSprite setUv(float u1, float v1, float u2, float v2) {
		Texture[] newFrames = new Texture[frames.length];
		for (int i = 0; i < frames.length; i++) {
			newFrames[i] = frames[i].withUv(u1, v1, u2, v2);
		}

		return setFrames(newFrames);
	}

	@Override
	public boolean canResize() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
		if (singleImage) {
			paintFrame(context, x, y, frames[0]);
		} else {
			//grab the system time at the very start of the frame.
			long now = System.nanoTime() / 1_000_000L;

			//check bounds so the Identifier isn't passed a bad number
			boolean inBounds = (currentFrame >= 0) && (currentFrame < frames.length);
			if (!inBounds) currentFrame = 0;
			//assemble and draw the frame calculated last iteration.
			Texture currentFrameTex = frames[currentFrame];
			paintFrame(context, x, y, currentFrameTex);

			//calculate how much time has elapsed since the last animation change, and change the frame if necessary.
			long elapsed = now - lastFrame;
			currentFrameTime += elapsed;
			if (currentFrameTime >= frameTime) {
				currentFrame++;
				//if we've hit the end of the animation, go back to the beginning
				if (currentFrame >= frames.length) {
					currentFrame = 0;
				}
				currentFrameTime = 0;
			}

			//frame is over; this frame is becoming the last frame so write the time to lastFrame
			this.lastFrame = now;
		}
	}

	/**
	 * Paints a single frame for this sprite.
	 *
	 * @param context the draw context
	 * @param x       the X coordinate to draw it at
	 * @param y       the Y coordinate to draw it at
	 * @param texture the texture to draw
	 */
	@Environment(EnvType.CLIENT)
	protected void paintFrame(DrawContext context, int x, int y, Texture texture) {
		ScreenDrawing.texturedRect(context, x, y, getWidth(), getHeight(), texture, tint);
	}
}
