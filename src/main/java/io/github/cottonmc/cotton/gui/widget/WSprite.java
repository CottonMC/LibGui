package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class WSprite extends WWidget {
	protected int currentFrame= 0;
	protected long currentFrameTime = 0;
	protected Identifier[] frames;
	protected int frameTime;
	protected long lastFrame;
	protected boolean singleImage = false;
	protected int tint = 0xFFFFFFFF;

	/** The left edge of the texture as a fraction. */
	protected float u1 = 0;
	/** The top edge of the texture as a fraction. */
	protected float v1 = 0;
	/** The right edge of the texture as a fraction. */
	protected float u2 = 1;
	/** The bottom edge of the texture as a fraction. */
	protected float v2 = 1;

	/**
	 * Create a new sprite with a single image.
	 * @param image The location of the image to display.
	 */
	public WSprite(Identifier image) {
		this.frames = new Identifier[]{image};
		this.singleImage = true;
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
		this(image);
		this.u1 = u1;
		this.v1 = v1;
		this.u2 = u2;
		this.v2 = v2;
	}

	/**
	 * Create a new animated sprite.
	 * @param frameTime How long in milliseconds to display for. (1 tick = 50 ms)
	 * @param frames The locations of the frames of the animation.
	 */
	public WSprite(int frameTime, Identifier... frames) {
		this.frameTime = frameTime;
		this.frames = frames;
		if (frames.length==1) this.singleImage = true;
	}

	public WSprite setImage(Identifier image) {
		this.frames = new Identifier[]{image};
		this.singleImage = true;
		this.currentFrame = 0;
		this.currentFrameTime = 0;
		return this;
	}

	public WSprite setFrames(Identifier... frames) {
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
	 */
	public WSprite setTint(int tint) {
		this.tint = tint;
		return this;
	}

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
		this.u1 = u1;
		this.v1 = v1;
		this.u2 = u2;
		this.v2 = v2;

		return this;
	}

	@Override
	public boolean canResize() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		if (singleImage) {
			ScreenDrawing.texturedRect(x, y, getWidth(), getHeight(), frames[0], u1, v1, u2, v2, tint);
		} else {
			//grab the system time at the very start of the frame.
			long now = System.nanoTime() / 1_000_000L;

			//check bounds so the Identifier isn't passed a bad number
			boolean inBounds = (currentFrame >= 0) && (currentFrame < frames.length);
			if (!inBounds) currentFrame = 0;
			//assemble and draw the frame calculated last iteration.
			Identifier currentFrameTex = frames[currentFrame];
			ScreenDrawing.texturedRect(x, y, getWidth(), getHeight(), currentFrameTex, u1, v1, u2, v2, tint);

			//calculate how much time has elapsed since the last animation change, and change the frame if necessary.
			long elapsed = now - lastFrame;
			currentFrameTime += elapsed;
			if (currentFrameTime >= frameTime) {
				currentFrame++;
				//if we've hit the end of the animation, go back to the beginning
				if (currentFrame >= frames.length - 1) {
					currentFrame = 0;
				}
				currentFrameTime = 0;
			}

			//frame is over; this frame is becoming the last frame so write the time to lastFrame
			this.lastFrame = now;
		}
	}
}
