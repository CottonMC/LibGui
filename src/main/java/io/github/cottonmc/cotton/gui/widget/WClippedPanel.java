package io.github.cottonmc.cotton.gui.widget;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.minecraft.util.Identifier;

public class WClippedPanel extends WPanel {
	protected Identifier mask;
	
	public WClippedPanel setClippingMask(Identifier mask) {
		this.mask = mask;
		return this;
	}
	
	@Override
	public void paintBackground(int x, int y, int mouseX, int mouseY) {
		if (getBackgroundPainter()!=null) getBackgroundPainter().paintBackground(x, y, this);
		
		RenderSystem.translatef(0, 0, 10);
		//RenderSystem.depthFunc(GL11.GL_LEQUAL);
		//RenderSystem.disableDepthTest();
		
		
		RenderSystem.colorMask(false, false, false, true);
		if (mask!=null) {
			ScreenDrawing.texturedRect(x, y, getWidth(), getHeight(), mask, 0xFFFFFFFF);
		} else {
			ScreenDrawing.coloredRect(x, y, getWidth(), getHeight(), 0xFFFFFFFF);
		}
		RenderSystem.colorMask(true, true, true, true);
		
		for(WWidget child : children) {
			RenderSystem.enableDepthTest();
			RenderSystem.depthFunc(GL11.GL_GEQUAL);
			child.paintBackground(x + child.getX(), y + child.getY(), mouseX-child.getX(), mouseY-child.getY());
		}
		RenderSystem.translated(0, 0, -10);
		RenderSystem.depthFunc(GL11.GL_LEQUAL);
		RenderSystem.disableDepthTest();
	}
}
