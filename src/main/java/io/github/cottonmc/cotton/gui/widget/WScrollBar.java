package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.data.Axis;

public class WScrollBar extends WWidget {
	protected Axis axis = Axis.HORIZONTAL;
	protected int value;
	protected int maxValue = 100;
	protected int window = 16;
	
	protected int anchor = -1;
	protected int anchorValue = -1;
	protected boolean sliding = false;
	
	public WScrollBar() {
	}
	
	public WScrollBar(Axis axis) {
		this.axis = axis;
	}
	
	@Override
	public void paintBackground(int x, int y, int mouseX, int mouseY) {
		if (LibGuiClient.config.darkMode) {
			ScreenDrawing.drawBeveledPanel(x, y, width, height, 0xFF_212121, 0xFF_2F2F2F, 0xFF_5D5D5D);
		} else {
			ScreenDrawing.drawBeveledPanel(x, y, width, height, 0xFF_373737, 0xFF_8B8B8B, 0xFF_FFFFFF);
		}
		if (maxValue<=0) return;

		// Handle colors
		int top, middle, bottom;

		if (sliding) {
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
		} else {
			ScreenDrawing.drawBeveledPanel(x+1, y+1+getHandlePosition(), width-2, getHandleSize(), top, middle, bottom);
		}
	}
	
	/**
	 * Gets the on-axis size of the scrollbar handle in gui pixels 
	 */
	public int getHandleSize() {
		float percentage = (window>=maxValue) ? 1f : window / (float)maxValue;
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
	
	public int pixelsToValues(int pixels) {
		int bar = (axis==Axis.HORIZONTAL) ? width-2 : height-2;
		//int bar = getMovableDistance();
		float percent = pixels / (float)bar;
		return (int)(percent*(maxValue-window));
	}
	
	public int getHandlePosition() {
		float percent = value / (float)Math.max(maxValue-window, 1);
		return (int)(percent * getMovableDistance());
	}
	
	/**
	 * Gets the maximum scroll value achievable; this will typically be the maximum value minus the
	 * window size
	 */
	public int getMaxScrollValue() {
		return maxValue - window;
	}
	
	protected void adjustSlider(int x, int y) {
		
		int delta = 0;
		if (axis==Axis.HORIZONTAL) {
			delta = x-anchor;
		} else {
			delta = y-anchor;
		}
		
		int valueDelta = pixelsToValues(delta);
		int valueNew = anchorValue + valueDelta;
		
		if (valueNew>getMaxScrollValue()) valueNew = getMaxScrollValue();
		if (valueNew<0) valueNew = 0;
		this.value = valueNew;
	}
	
	@Override
	public WWidget onMouseDown(int x, int y, int button) {
		//TODO: Clicking before or after the handle should jump instead of scrolling
		
		if (axis==Axis.HORIZONTAL) {
			anchor = x;
			anchorValue = value;
		} else {
			anchor = y;
			anchorValue = value;
		}
		sliding = true;
		return this;
	}
	
	@Override
	public void onMouseDrag(int x, int y, int button) {
		adjustSlider(x, y);
	}
	
	@Override
	public WWidget onMouseUp(int x, int y, int button) {
		//TODO: Clicking before or after the handle should jump instead of scrolling
		anchor = -1;
		anchorValue = -1;
		sliding = false;
		return this;
	}
	
	public int getValue() {
		return value;
	}

	public WScrollBar setValue(int value) {
		this.value = value;
		checkValue();
		return this;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public WScrollBar setMaxValue(int max) {
		this.maxValue = max;
		checkValue();
		return this;
	}

	public int getWindow() {
		return window;
	}

	public WScrollBar setWindow(int window) {
		this.window = window;
		return this;
	}

	/**
	 * Checks that the current value is in the correct range
	 * and adjusts it if needed.
	 */
	protected void checkValue() {
		if (this.value>maxValue-window) {
			this.value = maxValue-window;
		}
		if (this.value<0) this.value = 0;
	}
}
