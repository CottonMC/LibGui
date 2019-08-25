package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;

public class WScrollBar extends WWidget {
	protected Axis axis = Axis.HORIZONTAL;
	protected int value;
	protected int maxValue = 100;
	protected int window = 16;
	
	public WScrollBar() {
	}
	
	public WScrollBar(Axis axis) {
		this.axis = axis;
	}
	
	@Override
	public void paintBackground(int x, int y) {
		ScreenDrawing.drawBeveledPanel(x, y, width, height, 0xFFFFFFFF, 0xFF8b8b8b, 0xFF373737);
		
		float barHeight = (window>maxValue) ? 1f : window / maxValue;
		int scrollDistance = maxValue - window;
		
		
		super.paintBackground(x, y);
	}
	
	@Override
	public void setSize(int x, int y) {
		switch(axis) {
		case HORIZONTAL:
			this.width = x;
			this.height = 8;
			break;
		case VERTICAL:
			this.width = 8;
			this.height = y;
			break;
		}
	}
}
