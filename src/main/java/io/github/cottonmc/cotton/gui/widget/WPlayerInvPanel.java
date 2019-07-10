package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import net.minecraft.entity.player.PlayerInventory;

public class WPlayerInvPanel extends WPlainPanel {
	private WItemSlot inv;
	private WItemSlot hotbar;

	public WPlayerInvPanel(PlayerInventory playerInventory) {
		inv = WItemSlot.ofPlayerStorage(playerInventory);
		hotbar = WItemSlot.of(playerInventory, 0, 9, 1);
		this.add(inv, 0, 0);
		this.add(hotbar, 0, 58);
	}

	@Override
	public WPanel setBackgroundPainter(BackgroundPainter painter) {
		super.setBackgroundPainter(null);
		inv.setBackgroundPainter(painter);
		hotbar.setBackgroundPainter(painter);
		return this;
	}
}

