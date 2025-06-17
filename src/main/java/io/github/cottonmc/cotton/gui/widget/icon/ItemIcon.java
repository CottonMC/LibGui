package io.github.cottonmc.cotton.gui.widget.icon;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.joml.Matrix3x2fStack;

import java.util.Objects;

/**
 * An icon that draws an item stack.
 *
 * @since 2.2.0
 */
public class ItemIcon implements Icon {
	// Matches the vanilla GhostRecipe class (1.21.6).
	private static final int GHOST_OVERLAY_COLOR = 0x30_FFFFFF;

	private final ItemStack stack;
	private boolean ghost = false;

	/**
	 * Constructs an item icon.
	 *
	 * @param stack the drawn item stack
	 * @throws NullPointerException if the stack is null
	 */
	public ItemIcon(ItemStack stack) {
		this.stack = Objects.requireNonNull(stack, "stack");
	}

	/**
	 * Constructs an item icon with the item's default stack.
	 *
	 * @param item the drawn item
	 * @throws NullPointerException if the item is null
	 * @since 3.2.0
	 */
	public ItemIcon(Item item) {
		this(Objects.requireNonNull(item, "item").getDefaultStack());
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(DrawContext context, int x, int y, int size) {
		float scale = size != 16 ? ((float) size / 16f) : 1f;
		Matrix3x2fStack matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(x, y);
		matrices.scale(scale, scale);
		context.drawItemWithoutEntity(stack, 0, 0);

		if (isGhost()) {
			// TODO: Since 1.21.6, this code just renders a translucent square on top of the item.
			//       This should be fixed to only tint the item.
			context.fill(0, 0, 16, 16, GHOST_OVERLAY_COLOR);
		}

		matrices.popMatrix();
	}

	/**
	 * Checks whether this icon is a ghost item.
	 * Ghost items are rendered with a pale overlay.
	 *
	 * @return {@code true} if this icon is a ghost item, {@code false} otherwise
	 * @since 9.2.0
	 */
	public boolean isGhost() {
		return ghost;
	}

	/**
	 * Marks this icon as a ghost or non-ghost icon.
	 *
	 * @param ghost {@code true} if this icon is a ghost item, {@code false} otherwise
	 * @return this icon
	 * @since 9.2.0
	 */
	public ItemIcon setGhost(boolean ghost) {
		this.ghost = ghost;
		return this;
	}
}
