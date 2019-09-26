package io.github.cottonmc.cotton.gui.client;

import net.minecraft.text.Text;

/**
 * Implemented by LibGui screens to access {@code Screen.renderComponentHoverEffect()}.
 */
public interface TextHoverRendererScreen {
	void renderTextHover(Text text, int x, int y);
}
