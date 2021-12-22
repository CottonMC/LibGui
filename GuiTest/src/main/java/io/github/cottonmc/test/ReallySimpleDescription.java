package io.github.cottonmc.test;

import net.minecraft.entity.player.PlayerInventory;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;

// A really simple GUI description that only contains a player inventory panel.
public class ReallySimpleDescription extends SyncedGuiDescription {
	public ReallySimpleDescription(int syncId, PlayerInventory playerInventory) {
		super(LibGuiTest.REALLY_SIMPLE_SCREEN_HANDLER_TYPE, syncId, playerInventory);
		setTitleVisible(false);
		((WGridPanel) getRootPanel()).add(createPlayerInventoryPanel(), 0, 0);
		getRootPanel().validate(this);
	}
}
