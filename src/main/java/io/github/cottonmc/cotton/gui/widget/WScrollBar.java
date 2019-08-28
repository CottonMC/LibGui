package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;

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
	public void paintBackground(int x, int y) {
		ScreenDrawing.drawBeveledPanel(x, y, width, height, 0xFF373737, 0xFF_000000, 0xFFFFFFFF);
		if (maxValue<=0) return;
		
		int color = 0xFF_FFFFFF;
		if (axis==Axis.HORIZONTAL) {
			ScreenDrawing.rect(x+1+getHandlePosition(), y+1, getHandleSize(), height-2, color);
		} else {
			ScreenDrawing.rect(x+1, y+1+getHandlePosition(), width-2, getHandleSize(), color);
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
	
	public WScrollBar setMaxValue(int max) {
		this.maxValue = max;
		if (this.value>maxValue-window) {
			this.value = maxValue-window;
		}
		return this;
	}
}
