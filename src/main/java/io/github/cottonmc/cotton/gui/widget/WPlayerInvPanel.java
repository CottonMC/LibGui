package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;

import javax.annotation.Nullable;

/**
 * A player inventory widget that has a visually separate hotbar.
 */
public class WPlayerInvPanel extends WPlainPanel {
	private final WItemSlot inv;
	private final WItemSlot hotbar;

	/**
	 * Constructs a player inventory panel with a title.
	 *
	 * @param playerInventory the player inventory
	 */
	public WPlayerInvPanel(PlayerInventory playerInventory) {
		this(playerInventory, true);
	}

	/**
	 * Constructs a player inventory panel.
	 *
	 * @param playerInventory the player inventory
	 * @param hasTitle        whether there should be an "Inventory" title
	 * @since 2.0.0
	 */
	public WPlayerInvPanel(PlayerInventory playerInventory, boolean hasTitle) {
		this(playerInventory, hasTitle ? new WLabel(playerInventory.getDisplayName()) : null);
	}

	/**
	 * Constructs a player inventory panel.
	 *
	 * @param playerInventory the player inventory
	 * @param title           the title widget, can be null
	 * @since 2.0.0
	 */
	public WPlayerInvPanel(PlayerInventory playerInventory, @Nullable WWidget title) {
		int y = 0;

		if (title != null) {
			this.add(title, 0, 0, 9*18, 11);
			y += title.getHeight();
		}

		inv = WItemSlot.ofPlayerStorage(playerInventory);
		hotbar = WItemSlot.of(playerInventory, 0, 9, 1);
		this.add(inv, 0, y);
		this.add(hotbar, 0, y + 58);
	}

	/**
	 * Sets the background painter of this inventory widget's slots.
	 *
	 * @param painter the new painter
	 * @return this panel
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public WPanel setBackgroundPainter(BackgroundPainter painter) {
		super.setBackgroundPainter(null);
		inv.setBackgroundPainter(painter);
		hotbar.setBackgroundPainter(painter);
		return this;
	}
}

