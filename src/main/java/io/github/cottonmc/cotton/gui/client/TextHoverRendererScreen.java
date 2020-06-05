package io.github.cottonmc.cotton.gui.client;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;

/**
 * Implemented by LibGui screens to access {@code Screen.renderTextHoverEffect()}.
 */
public interface TextHoverRendererScreen {
	void renderTextHover(MatrixStack matrices, Style textStyle, int x, int y);
}
