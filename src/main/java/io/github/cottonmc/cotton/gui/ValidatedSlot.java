package io.github.cottonmc.cotton.gui;

import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public class ValidatedSlot extends Slot {
	private final int slotNumber;
	private boolean insertingAllowed = true;
	private boolean takingAllowed = true;

	public ValidatedSlot(Inventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		if (inventoryIn==null) throw new IllegalArgumentException("Can't make an itemslot from a null inventory!");
		this.slotNumber = index;
	}
	
	@Override
	public boolean canInsert(ItemStack stack) {
		return insertingAllowed && inventory.isValidInvStack(slotNumber, stack);
	}
	
	@Override
	public boolean canTakeItems(PlayerEntity player) {
		return takingAllowed && inventory.canPlayerUseInv(player);
	}
	
	@Override
	public ItemStack getStack() {
		if (inventory==null) {
			System.out.println("Prevented null-inventory from WItemSlot with slot #: "+slotNumber);
			return ItemStack.EMPTY;
		}
		
		ItemStack result = super.getStack();
		if (result==null) {
			System.out.println("Prevented null-itemstack crash from: "+inventory.getClass().getCanonicalName());
			return ItemStack.EMPTY;
		}
		
		return result;
	}

	/**
	 * Returns true if the item in this slot can be modified by players.
	 *
	 * @return true if items can be inserted into or taken from this slot widget, false otherwise
	 * @since 1.8.0
	 * @deprecated Replaced with {@link #isInsertingAllowed()} and {@link #isTakingAllowed()}.
	 */
	@Deprecated
	public boolean isModifiable() {
		return insertingAllowed || takingAllowed;
	}

	/**
	 * @deprecated Replaced with {@link #setInsertingAllowed(boolean)} and {@link #setTakingAllowed(boolean)}.
	 */
	@Deprecated
	public void setModifiable(boolean modifiable) {
		this.insertingAllowed = modifiable;
		this.takingAllowed = modifiable;
	}

	public int getInventoryIndex() {
		return slotNumber;
	}

	/**
	 * Returns whether items can be inserted into this slot.
	 *
	 * @return true if items can be inserted, false otherwise
	 * @since 1.10.0
	 */
	public boolean isInsertingAllowed() {
		return insertingAllowed;
	}

	/**
	 * Sets whether inserting items into this slot is allowed.
	 *
	 * @param insertingAllowed true if items can be inserted, false otherwise
	 * @since 1.10.0
	 */
	public void setInsertingAllowed(boolean insertingAllowed) {
		this.insertingAllowed = insertingAllowed;
	}

	/**
	 * Returns whether items can be taken from this slot.
	 *
	 * @return true if items can be taken, false otherwise
	 * @since 1.10.0
	 */
	public boolean isTakingAllowed() {
		return takingAllowed;
	}

	/**
	 * Sets whether taking items from this slot is allowed.
	 *
	 * @param takingAllowed true if items can be taken, false otherwise
	 * @since 1.10.0
	 */
	public void setTakingAllowed(boolean takingAllowed) {
		this.takingAllowed = takingAllowed;
	}
}