package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

import io.github.cottonmc.cotton.gui.client.Scissors;

/**
 * A panel that is clipped to only render widgets inside its bounds.
 */
public class WClippedPanel extends WPanel {
	@Environment(EnvType.CLIENT)
	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		if (getBackgroundPainter()!=null) getBackgroundPainter().paintBackground(matrices, x, y, this);

		Scissors.push(x, y, width, height);
		for(WWidget child : children) {
			child.paint(matrices, x + child.getX(), y + child.getY(), mouseX-child.getX(), mouseY-child.getY());
		}
		Scissors.pop();
	}
}
