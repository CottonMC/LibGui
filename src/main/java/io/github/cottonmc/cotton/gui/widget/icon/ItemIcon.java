package io.github.cottonmc.cotton.gui.widget.icon;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

/**
 * An icon that draws an item stack.
 *
 * @since 2.2.0
 */
public class ItemIcon implements Icon {
	private final ItemStack stack;

	/**
	 * Constructs an item icon.
	 *
	 * @param stack the drawn item stack
	 */
	public ItemIcon(ItemStack stack) {
		this.stack = stack;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(MatrixStack matrices, int x, int y, int size) {
		MinecraftClient client = MinecraftClient.getInstance();
		ItemRenderer renderer = client.getItemRenderer();

		float scale = size != 16 ? ((float) size / 16f) : 1f;

		RenderSystem.pushMatrix();
		RenderSystem.translatef(x, y, 0);
		RenderSystem.scalef(scale, scale, 1);
		renderer.renderInGui(stack, 0, 0);
		RenderSystem.popMatrix();
	}
}
