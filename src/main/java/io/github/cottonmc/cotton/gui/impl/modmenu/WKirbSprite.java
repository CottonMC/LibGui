package io.github.cottonmc.cotton.gui.impl.modmenu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.widget.WWidget;

import java.util.ArrayList;

public class WKirbSprite extends WWidget {
	private static final Identifier KIRB = new Identifier(LibGuiCommon.MOD_ID, "textures/widget/kirb.png");

	private static final float PX = 1f/416f;
	private static final float KIRB_WIDTH = 32*PX;

	private int currentFrame = 0;
	private long currentFrameTime = 0;
	private int[] toSleep = { 0, 0, 0, 1, 2, 1, 2, 0, 0, 0, 1, 2, 3 };
	private int[] asleep = { 4, 4, 4, 4, 5, 6, 7, 6, 5 };
	private int[] toAwake = { 3, 3, 8, 8, 8, 8, 8, 8, 8 };
	private int[] awake = { 9, 9, 9, 10, 11, 12 };
	private State state = State.ASLEEP;
	private ArrayList<Integer> pendingFrames = new ArrayList<>();

	private int frameTime = 300;
	private long lastFrame;

	public WKirbSprite() {
		state = (shouldRenderInDarkMode()) ? State.ASLEEP : State.AWAKE;
	}

	public void schedule(int[] frames) {
		for (int i : frames) pendingFrames.add(i);
	}

	@Override
	public boolean canResize() {
		return false;
	}

	@Override
	public int getWidth() {
		return 32;
	}

	@Override
	public int getHeight() {
		return 32;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
		long now = System.nanoTime() / 1_000_000L;

		if (pendingFrames.isEmpty()) {
			if (shouldRenderInDarkMode()) {
				state = switch (state) {
					case AWAKE -> State.FALLING_ASLEEP;
					case FALLING_ASLEEP -> State.ASLEEP;
					default -> /* zzzz */ State.ASLEEP;
				};
			} else {
				state = switch (state) {
					case ASLEEP -> State.WAKING_UP;
					case WAKING_UP -> State.AWAKE;
					default -> State.AWAKE;
				};
			}

			switch (state) {
				case ASLEEP -> schedule(asleep);
				case WAKING_UP -> schedule(toAwake);
				case AWAKE -> schedule(awake);
				case FALLING_ASLEEP -> schedule(toSleep);
			}
		}

		float offset = KIRB_WIDTH * currentFrame;
		ScreenDrawing.texturedRect(context, x, y + 8, 32, 32, KIRB, offset, 0, offset + KIRB_WIDTH, 1, 0xFFFFFFFF);

		long elapsed = now - lastFrame;
		currentFrameTime += elapsed;
		if (currentFrameTime >= frameTime) {
			if (!pendingFrames.isEmpty()) currentFrame = pendingFrames.remove(0);
			currentFrameTime = 0;
		}

		this.lastFrame = now;
	}

	public static enum State {
		AWAKE,
		FALLING_ASLEEP,
		ASLEEP,
		WAKING_UP;
	}
}
