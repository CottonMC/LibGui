package io.github.cottonmc.test;

import io.github.cottonmc.cotton.gui.CottonInventoryController;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;

public class TestController extends CottonInventoryController {
	
	public TestController(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(syncId, playerInventory, getBlockInventory(context), null);
		
		WGridPanel root = (WGridPanel)this.getRootPanel();
		
		root.add(WItemSlot.of(blockInventory, 0, 4, 1), 0, 1);

		root.add(new WButton(new LiteralText("Button A")), 0, 3, 4, 1);
		root.add(new WButton(new LiteralText("Button B")), 5, 3, 4, 1);
		root.add(new WButton(new LiteralText("Button C")), 0, 5, 4, 1);
		root.add(new WButton(new LiteralText("Button D")), 5, 5, 4, 1);
		root.add(new WTextField(new LiteralText("Type something...")), 0, 7, 5, 1);

		root.add(createPlayerInventoryPanel(), 0, 9);
		System.out.println(root.toString());

		this.getRootPanel().validate(this);
	}
}
