package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;

/**
 * A player inventory widget that has a visually separate hotbar.
 */
public class WPlayerInvPanel extends WPlainPanel {
	private final WItemSlot inv;
	private final WItemSlot hotbar;

	public WPlayerInvPanel(PlayerInventory playerInventory) {
		inv = WItemSlot.ofPlayerStorage(playerInventory);
		hotbar = WItemSlot.of(playerInventory, 0, 9, 1);
		this.add(inv, 0, 0);
		this.add(hotbar, 0, 58);
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

