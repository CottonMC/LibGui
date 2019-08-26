package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;

public class WScrollBar extends WWidget {
	protected Axis axis = Axis.HORIZONTAL;
	protected int value;
	protected int maxValue = 100;
	protected int window = 16;
	
	protected int anchor = -1;
	protected int anchorValue = -1;
	
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
	
	@Override
	public void paintForeground(int x, int y, int mouseX, int mouseY) {
		super.paintForeground(x, y, mouseX, mouseY);
		
		//Sneakily update bar position
		if (anchor!=-1) {
			onMouseDown(mouseX+x, mouseY+y, 0);
		}
	}
	
	/**
	 * Gets the on-axis size of the scrollbar handle in gui pixels 
	 */
	public int getHandleSize() {
		float percentage = (window>=maxValue) ? 1f : window / (float)maxValue;
		int bar = (axis==Axis.HORIZONTAL) ? width-2 : height-2;
		return (int)(percentage*bar);
	}
	
	/**
	 * Gets the number of pixels the scrollbar handle is able to move along its track from one end to the other.
	 */
	public int getMovableDistance() {
		int logicalDistance = maxValue-window;
		if (logicalDistance<0) logicalDistance = 0;
		float percentage = logicalDistance / (float)maxValue;
		
		//System.out.println("MovableDistance! maxValue: "+maxValue+", window: "+window+", logicalDistance: "+logicalDistance+", percentage: "+percentage);
		return (int) ( (axis==Axis.HORIZONTAL) ? (width-2)*percentage : (height-2)*percentage);
	}
	
	public int getHandlePosition() {
		float percent = value / (float)Math.max(maxValue, 1);
		return (int)(percent * getMovableDistance());
	}
	
	/**
	 * Gets the maximum scroll value achievable; this will typically be the maximum value minus the
	 * window size
	 */
	public int getMaxScrollValue() {
		return maxValue - window;
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
		return this;
	}
	
	@Override
	public void onMouseDrag(int x, int y, int button) {
		int delta = 0;
		if (axis==Axis.HORIZONTAL) {
			delta = x-anchor;
		} else {
			delta = y-anchor;
		}
		
		float percentMoved = (delta / (float)getMovableDistance());
		int valueDelta = (int)(percentMoved * maxValue);
		int valueNew = anchorValue + valueDelta;
		if (valueNew>getMaxScrollValue()) valueNew = getMaxScrollValue();
		if (valueNew<0) valueNew = 0;
		this.value = valueNew;
		
		//System.out.println("Anchor: "+anchor+", Delta: "+delta+", PercentMoved: "+percentMoved+", ValueDelta: "+valueDelta);
		
		super.onMouseDrag(x, y, button);
	}
	
	@Override
	public WWidget onMouseUp(int x, int y, int button) {
		//TODO: Clicking before or after the handle should jump instead of scrolling
		anchor = -1;
		anchorValue = -1;
		
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
