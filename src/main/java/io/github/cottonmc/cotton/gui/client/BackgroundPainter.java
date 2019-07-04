package io.github.cottonmc.cotton.gui.client;

import io.github.cottonmc.cotton.gui.widget.WWidget;

public interface BackgroundPainter {
	/**
	 * Paint the specified panel to the screen.
	 * @param left The absolute position of the left of the panel, in gui-screen coordinates
	 * @param top The absolute position of the top of the panel, in gui-screen coordinates
	 * @param panel The panel being painted
	 */
	public void paintBackground(int left, int top, WWidget panel);
	
	
	
	public static BackgroundPainter VANILLA = (left, top, panel) -> {
		ScreenDrawing.drawGuiPanel(left-8, top-8, panel.getWidth()+16, panel.getHeight()+16);
		
	};
	
	
	
	public static BackgroundPainter createColorful(int panelColor) {
		return (left, top, panel) -> {
			ScreenDrawing.drawGuiPanel(left-8, top-8, panel.getWidth()+16, panel.getHeight()+16, panelColor);
		};
	}
	
	public static BackgroundPainter createColorful(int panelColor, float contrast) {
		return (left, top, panel) -> {
			int shadowColor = ScreenDrawing.multiplyColor(panelColor, 1.0f - contrast);
			int hilightColor = ScreenDrawing.multiplyColor(panelColor, 1.0f + contrast);
			
			ScreenDrawing.drawGuiPanel(left-8, top-8, panel.getWidth()+16, panel.getHeight()+16, shadowColor, panelColor, hilightColor, 0xFF000000);
		};
	}
}
