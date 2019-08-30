package io.github.cottonmc.test;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.LiteralText;

public class TestContainer extends CottonCraftingController {
	
	public TestContainer(int syncId, PlayerInventory playerInventory, BlockContext context) {
		super(null, syncId, playerInventory, getBlockInventory(context), null);
		
		WGridPanel root = (WGridPanel)this.getRootPanel();
		
		root.add(WItemSlot.of(blockInventory, 0, 4, 1), 0, 1);
		
		WButton button = new WButton(new LiteralText("Test Button"));
		root.add(button, 0, 3, 5, 1);
		
		
		root.add(new WPlayerInvPanel(playerInventory), 0, 5);
		
		
		this.getRootPanel().validate(this);
	}
}
