package io.github.cottonmc.cotton.gui.widget;

import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.MinecraftClient;

/**
 * A panel that is clipped to only render widgets inside its bounds.
 */
public class WClippedPanel extends WPanel {
	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		if (getBackgroundPainter()!=null) getBackgroundPainter().paintBackground(x, y, this);

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		MinecraftClient mc = MinecraftClient.getInstance();
		int rawHeight = mc.getWindow().getHeight();
		double scaleFactor = mc.getWindow().getScaleFactor();
		int scaledWidth = (int) (getWidth() * scaleFactor);
		int scaledHeight = (int) (getHeight() * scaleFactor);

		// Expression for Y coordinate adapted from vini2003's Spinnery (code snippet released under WTFPL)
		GL11.glScissor((int) (x * scaleFactor), (int) (rawHeight - (y * scaleFactor) - scaledHeight), scaledWidth, scaledHeight);

		for(WWidget child : children) {
			child.paint(matrices, x + child.getX(), y + child.getY(), mouseX-child.getX(), mouseY-child.getY());
		}

		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
}
