package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.minecraft.client.util.math.MatrixStack;

public class WScrollBar extends WAbstractSlider {
	protected int window = 16;

	/**
	 * Constructs a horizontal scroll bar.
	 */
	public WScrollBar() {
		super(0, 100, Axis.HORIZONTAL);
	}

	/**
	 * Constructs a scroll bar with a custom axis.
	 *
	 * @param axis the axis
	 */
	public WScrollBar(Axis axis) {
		super(0, 100, axis);

		if (axis == Axis.VERTICAL) {
			setDirection(Direction.DOWN);
		}
	}

	@Override
	protected int getThumbWidth() {
		return Math.round(window * coordToValueRatio);
	}

	@Override
	protected boolean isMouseInsideBounds(int x, int y) {
		return axis == Axis.HORIZONTAL
				? (x >= getHandlePosition() + 1 && x <= getHandlePosition() + getHandleSize())
				: (y >= getHandlePosition() + 1 && y <= getHandlePosition() + getHandleSize());
	}

	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		if (LibGuiClient.config.darkMode) {
			ScreenDrawing.drawBeveledPanel(x, y, width, height, 0xFF_212121, 0xFF_2F2F2F, 0xFF_5D5D5D);
		} else {
			ScreenDrawing.drawBeveledPanel(x, y, width, height, 0xFF_373737, 0xFF_8B8B8B, 0xFF_FFFFFF);
		}
		if (getMaxValue()<=0) return;

		// Handle colors
		int top, middle, bottom;

		if (dragging) {
			if (LibGuiClient.config.darkMode) {
				top = 0xFF_6C6C6C;
				middle = 0xFF_2F2F2F;
				bottom = 0xFF_212121;
			} else {
				top = 0xFF_FFFFFF;
				middle = 0xFF_8B8B8B;
				bottom = 0xFF_555555;
			}
		} else if (isWithinBounds(mouseX, mouseY)) {
			if (LibGuiClient.config.darkMode) {
				top = 0xFF_5F6A9D;
				middle = 0xFF_323F6E;
				bottom = 0xFF_0B204A;
			} else {
				top = 0xFF_CFD0F7;
				middle = 0xFF_8791C7;
				bottom = 0xFF_343E75;
			}
		} else {
			if (LibGuiClient.config.darkMode) {
				top = 0xFF_6C6C6C;
				middle = 0xFF_414141;
				bottom = 0xFF_212121;
			} else {
				top = 0xFF_FFFFFF;
				middle = 0xFF_C6C6C6;
				bottom = 0xFF_555555;
			}
		}

		if (axis==Axis.HORIZONTAL) {
			ScreenDrawing.drawBeveledPanel(x+1+getHandlePosition(), y+1, getHandleSize(), height-2, top, middle, bottom);

			if (isFocused()) {
				drawBeveledOutline(x+1+getHandlePosition(), y+1, getHandleSize(), height-2, 0xFF_FFFFA7, 0xFF_C9CA71, 0xFF_8C8F39);
			}
		} else {
			ScreenDrawing.drawBeveledPanel(x+1, y+1+getHandlePosition(), width-2, getHandleSize(), top, middle, bottom);

			if (isFocused()) {
				drawBeveledOutline(x+1, y+1+getHandlePosition(), width-2, getHandleSize(), 0xFF_FFFFA7, 0xFF_C9CA71, 0xFF_8C8F39);
			}
		}
	}

	private static void drawBeveledOutline(int x, int y, int width, int height, int topleft, int center, int bottomright) {
		ScreenDrawing.coloredRect(x,             y,              width - 1, 1,          topleft); //Top shadow
		ScreenDrawing.coloredRect(x,             y + 1,          1,         height - 2, topleft); //Left shadow
		ScreenDrawing.coloredRect(x + width - 1, y + 1,          1,         height - 1, bottomright); //Right hilight
		ScreenDrawing.coloredRect(x + 1,         y + height - 1, width - 1, 1,          bottomright); //Bottom hilight
	}
	
	/**
	 * Gets the on-axis size of the scrollbar handle in gui pixels 
	 */
	public int getHandleSize() {
		float percentage = (window>=getMaxValue()) ? 1f : window / (float)getMaxValue();
		int bar = (axis==Axis.HORIZONTAL) ? width-2 : height-2;
		int result = (int)(percentage*bar);
		if (result<6) result = 6;
		return result;
	}
	
	/**
	 * Gets the number of pixels the scrollbar handle is able to move along its track from one end to the other.
	 */
	public int getMovableDistance() {
		int bar = (axis==Axis.HORIZONTAL) ? width-2 : height-2;
		return bar-getHandleSize();
	}
	
	public int getHandlePosition() {
		float percent = value / (float)Math.max(getMaxValue(), 1);
		return (int)(percent * getMovableDistance());
	}

	public void setMaxValue(int max) {
		super.setMaxValue(max - window);
	}

	public int getWindow() {
		return window;
	}

	public WScrollBar setWindow(int window) {
		this.window = window;
		return this;
	}
}
