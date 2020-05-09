package io.github.cottonmc.test;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class GuiBlockEntity extends BlockEntity implements ImplementedInventory {
	
	DefaultedList<ItemStack> items =  DefaultedList.ofSize(8, ItemStack.EMPTY);
	
	public GuiBlockEntity() {
		super(LibGuiTest.GUI_BLOCKENTITY_TYPE);
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return items;
	}
	
	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return pos.isWithinDistance(player.getBlockPos(), 4.5);
	}

}
