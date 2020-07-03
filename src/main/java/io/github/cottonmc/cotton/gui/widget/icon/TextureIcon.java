package io.github.cottonmc.cotton.gui.widget.icon;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * An icon that draws a texture.
 */
public class TextureIcon implements Icon {
	private final Identifier texture;

	/**
	 * Constructs a new texture icon.
	 *
	 * @param texture the identifier of the icon texture
	 */
	public TextureIcon(Identifier texture) {
		this.texture = texture;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(MatrixStack matrices, int x, int y, int size) {
		ScreenDrawing.texturedRect(x, y, size, size, texture, 0xFF_FFFFFF);
	}
}
