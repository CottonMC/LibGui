package io.github.cottonmc.cotton.gui.widget.icon;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;

/**
 * A square icon for a widget such as a button.
 *
 * @see ItemIcon
 * @see TextureIcon
 * @since 2.2.0
 */
public interface Icon {
	/**
	 * Paints this icon.
	 *
	 * @param context the draw context
	 * @param x       the X coordinate
	 * @param y       the Y coordinate
	 * @param size    the size of this icon in pixels (size N means a N*N square)
	 */
	@Environment(EnvType.CLIENT)
	void paint(DrawContext context, int x, int y, int size);
}
