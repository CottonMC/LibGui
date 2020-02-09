package io.github.cottonmc.cotton.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class EmptyInventory implements Inventory {
	public static final EmptyInventory INSTANCE = new EmptyInventory();
	
	private EmptyInventory() {}
	
	@Override
	public void clear() {}
	
	@Override
	public int getInvSize() {
		return 0;
	}
	
	@Override
	public boolean isInvEmpty() {
		return true;
	}
	
	@Override
	public ItemStack getInvStack(int slot) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack takeInvStack(int slot, int count) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack removeInvStack(int slot) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public void setInvStack(int slot, ItemStack stack) {
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity player) {
		return true;
	}

}
