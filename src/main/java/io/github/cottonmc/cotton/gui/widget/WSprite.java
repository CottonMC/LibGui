package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class WSprite extends WWidget {
	private int currentFrame= 0;
	private long currentFrameTime = 0;
	private Identifier[] frames;
	private int frameTime;
	private long lastFrame;
	private boolean singleImage = false;

	/**
	 * Create a new sprite with a single image.
	 * @param image The location of the image to display.
	 */
	public WSprite(Identifier image) {
		this.frames = new Identifier[]{image};
		this.singleImage = true;
	}

	/**
	 * Create a new animated sprite.
	 * @param frameTime How long in milliseconds to display for. (1 tick = 50 ms)
	 * @param frames The locations of the frames of the animation.
	 */
	public WSprite(int frameTime, Identifier... frames) {
		this.frameTime = frameTime;
		this.frames = frames;
	}

	@Override
	public boolean canResize() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		if (singleImage) {
			ScreenDrawing.rect(frames[0], x, y, getWidth(), getHeight(), 0xFFFFFFFF);
		} else {
			//grab the system time at the very start of the frame.
			long now = System.nanoTime() / 1_000_000L;

			//check bounds so the Identifier isn't passed a bad number
			boolean inBounds = (currentFrame >= 0) && (currentFrame < frames.length);
			if (!inBounds) currentFrame = 0;
			//assemble and draw the frame calculated last iteration.
			Identifier currentFrameTex = frames[currentFrame];
			ScreenDrawing.rect(currentFrameTex, x, y, getWidth(), getHeight(), 0xFFFFFFFF);

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
