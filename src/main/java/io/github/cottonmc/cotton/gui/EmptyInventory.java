package io.github.cottonmc.cotton.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

/**
 * An empty inventory that cannot hold any items.
 */
public class EmptyInventory implements Inventory {
	public static final EmptyInventory INSTANCE = new EmptyInventory();
	
	private EmptyInventory() {}
	
	@Override
	public void clear() {}
	
	@Override
	public int size() {
		return 0;
	}
	
	@Override
	public boolean isEmpty() {
		return true;
	}
	
	@Override
	public ItemStack getStack(int slot) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack removeStack(int slot, int count) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack removeStack(int slot) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public void setStack(int slot, ItemStack stack) {
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return true;
	}

}
