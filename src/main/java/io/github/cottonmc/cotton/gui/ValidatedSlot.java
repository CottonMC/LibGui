package io.github.cottonmc.cotton.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Predicate;

public class ValidatedSlot extends Slot {
	private static final Logger LOGGER = LogManager.getLogger();
	private final int slotNumber;
	private boolean insertingAllowed = true;
	private boolean takingAllowed = true;
	private Predicate<ItemStack> filter;

	public ValidatedSlot(Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
		if (inventory==null) throw new IllegalArgumentException("Can't make an itemslot from a null inventory!");
		this.slotNumber = index;
	}
	
	@Override
	public boolean canInsert(ItemStack stack) {
		return insertingAllowed && inventory.isValid(slotNumber, stack) && filter.test(stack);
	}
	
	@Override
	public boolean canTakeItems(PlayerEntity player) {
		return takingAllowed && inventory.canPlayerUse(player);
	}
	
	@Override
	public ItemStack getStack() {
		if (inventory==null) {
			LOGGER.warn("Prevented null-inventory from WItemSlot with slot #: {}", slotNumber);
			return ItemStack.EMPTY;
		}
		
		ItemStack result = super.getStack();
		if (result==null) {
			LOGGER.warn("Prevented null-itemstack crash from: {}", inventory.getClass().getCanonicalName());
			return ItemStack.EMPTY;
		}
		
		return result;
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

	/**
	 * Gets the item stack filter of this slot.
	 *
	 * @return the item filter
	 * @since 2.0.0
	 */
	public Predicate<ItemStack> getFilter() {
		return filter;
	}

	/**
	 * Sets the item stack filter of this slot.
	 *
	 * @param filter the new item filter
	 * @since 2.0.0
	 */
	public void setFilter(Predicate<ItemStack> filter) {
		this.filter = filter;
	}
}