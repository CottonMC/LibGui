package io.github.cottonmc.cotton.gui.impl.modmenu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.client.LibGui;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.widget.WWidget;

import java.util.ArrayList;

public class WKirbSprite extends WWidget {
	private static final Identifier KIRB = new Identifier(LibGuiCommon.MOD_ID, "textures/widget/kirb.png");
	
	private static final float PX = 1f/416f;
	private static final float KIRB_WIDTH = 32*PX;
	
	private int currentFrame= 0;
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
		state = (LibGui.isDarkMode()) ? State.ASLEEP : State.AWAKE;
	}
	
	public void schedule(int[] frames) {
		for(int i : frames) pendingFrames.add(i);
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
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		long now = System.nanoTime() / 1_000_000L;
		
		
		if (pendingFrames.isEmpty()) {
			
			if (LibGui.isDarkMode()) {
				switch(state) {
				case AWAKE:
					state = State.FALLING_ASLEEP;
					break;
				case FALLING_ASLEEP:
					state = State.ASLEEP;
					break;
				default:
					//zzzz
					state = State.ASLEEP;
					break;
				}
			} else {
				switch(state) {
				case ASLEEP:
					state = State.WAKING_UP;
					break;
				case WAKING_UP:
					state = State.AWAKE;
					break;
				default:
					state = State.AWAKE;
					break;
				}
			}
			
			switch (state) {
			case ASLEEP: schedule(asleep); break;
			case WAKING_UP: schedule(toAwake); break;
			case AWAKE: schedule(awake); break;
			case FALLING_ASLEEP: schedule(toSleep); break;
			}
		}
		
		float offset = KIRB_WIDTH * currentFrame;
		ScreenDrawing.texturedRect(matrices, x, y+8, 32, 32, KIRB, offset, 0, offset+KIRB_WIDTH, 1, 0xFFFFFFFF);
		
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
