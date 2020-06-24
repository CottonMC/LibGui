package io.github.cottonmc.cotton.gui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import javax.annotation.Nullable;

public class WItemSlot extends WWidget {
	private static final Predicate<ItemStack> DEFAULT_FILTER = stack -> true;
	private final List<ValidatedSlot> peers = new ArrayList<>();
	@Nullable
	@Environment(EnvType.CLIENT)
	// TODO: Set the background painter to SLOT in a new method that sets a widget's default painter.
	private BackgroundPainter backgroundPainter = null;
	private Inventory inventory;
	private int startIndex = 0;
	private int slotsWide = 1;
	private int slotsHigh = 1;
	private boolean big = false;
	private boolean insertingAllowed = true;
	private boolean takingAllowed = true;
	private int focusedSlot = -1;
	private Predicate<ItemStack> filter = DEFAULT_FILTER;

	public WItemSlot(Inventory inventory, int startIndex, int slotsWide, int slotsHigh, boolean big) {
		this.inventory = inventory;
		this.startIndex = startIndex;
		this.slotsWide = slotsWide;
		this.slotsHigh = slotsHigh;
		this.big = big;
		//this.ltr = ltr;
	}
	
	private WItemSlot() {}
	
	public static WItemSlot of(Inventory inventory, int index) {
		WItemSlot w = new WItemSlot();
		w.inventory = inventory;
		w.startIndex = index;
		
		return w;
	}
	
	public static WItemSlot of(Inventory inventory, int startIndex, int slotsWide, int slotsHigh) {
		WItemSlot w = new WItemSlot();
		w.inventory = inventory;
		w.startIndex = startIndex;
		w.slotsWide = slotsWide;
		w.slotsHigh = slotsHigh;
		
		return w;
	}
	
	public static WItemSlot outputOf(Inventory inventory, int index) {
		WItemSlot w = new WItemSlot();
		w.inventory = inventory;
		w.startIndex = index;
		w.big = true;
		
		return w;
	}

	/**
	 * Creates a 9x3 slot widget from the "main" part of a player inventory.
	 *
	 * @param inventory the player inventory
	 * @return the created slot widget
	 * @see WPlayerInvPanel
	 */
	public static WItemSlot ofPlayerStorage(Inventory inventory) {
		WItemSlot w = new WItemSlot();
		w.inventory = inventory;
		w.startIndex = 9;
		w.slotsWide = 9;
		w.slotsHigh = 3;
		//w.ltr = false;
		
		return w;
	}
	
	@Override
	public int getWidth() {
		return slotsWide * 18;
	}
	
	@Override
	public int getHeight() {
		return slotsHigh * 18;
	}

	@Override
	public boolean canFocus() {
		return true;
	}

	public boolean isBigSlot() {
		return big;
	}

	/**
	 * Returns true if the contents of this {@code WItemSlot} can be modified by players.
	 *
	 * @return true if items can be inserted into or taken from this slot widget, false otherwise
	 * @since 1.8.0
	 */
	public boolean isModifiable() {
		return takingAllowed || insertingAllowed;
	}

	public WItemSlot setModifiable(boolean modifiable) {
		this.insertingAllowed = modifiable;
		this.takingAllowed = modifiable;
		for (ValidatedSlot peer : peers) {
			peer.setInsertingAllowed(modifiable);
			peer.setTakingAllowed(modifiable);
		}
		return this;
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
	 * @return this slot widget
	 * @since 1.10.0
	 */
	public WItemSlot setInsertingAllowed(boolean insertingAllowed) {
		this.insertingAllowed = insertingAllowed;
		for (ValidatedSlot peer : peers) {
			peer.setInsertingAllowed(insertingAllowed);
		}
		return this;
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
	 * @return this slot widget
	 * @since 1.10.0
	 */
	public WItemSlot setTakingAllowed(boolean takingAllowed) {
		this.takingAllowed = takingAllowed;
		for (ValidatedSlot peer : peers) {
			peer.setTakingAllowed(takingAllowed);
		}
		return this;
	}

	/**
	 * Gets the currently focused slot index.
	 *
	 * @return the currently focused slot, or -1 if this widget isn't focused
	 * @since 2.0.0
	 */
	public int getFocusedSlot() {
		return focusedSlot;
	}

	@Override
	public void createPeers(GuiDescription c) {
		super.createPeers(c);
		peers.clear();
		int index = startIndex;
		
		for (int y = 0; y < slotsHigh; y++) {
			for (int x = 0; x < slotsWide; x++) {
				// The Slot object is offset +1 because it's the inner area of the slot.
				ValidatedSlot slot = createSlotPeer(inventory, index, this.getAbsoluteX() + (x * 18) + 1, this.getAbsoluteY() + (y * 18) + 1);
				slot.setInsertingAllowed(insertingAllowed);
				slot.setTakingAllowed(takingAllowed);
				slot.setFilter(filter);
				peers.add(slot);
				c.addSlotPeer(slot);
				index++;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onKeyPressed(int ch, int key, int modifiers) {
		if (isActivationKey(ch) && host instanceof ScreenHandler && focusedSlot >= 0) {
			ScreenHandler handler = (ScreenHandler) host;
			MinecraftClient client = MinecraftClient.getInstance();

			ValidatedSlot peer = peers.get(focusedSlot);
			client.interactionManager.clickSlot(handler.syncId, peer.id, 0, SlotActionType.PICKUP, client.player);
		}
	}

	/**
	 * Creates a slot peer for this slot widget.
	 *
	 * @param inventory the slot inventory
	 * @param index     the index in the inventory
	 * @param x         the X coordinate
	 * @param y         the Y coordinate
	 * @return the created slot instance
	 * @since 1.11.0
	 */
	protected ValidatedSlot createSlotPeer(Inventory inventory, int index, int x, int y) {
		return new ValidatedSlot(inventory, index, x, y);
	}

	/**
	 * Gets this slot widget's background painter.
	 *
	 * @return the background painter
	 * @since 2.0.0
	 */
	@Nullable
	@Environment(EnvType.CLIENT)
	public BackgroundPainter getBackgroundPainter() {
		return backgroundPainter;
	}

	/**
	 * Sets this item slot's background painter.
	 *
	 * @param painter the new painter
	 */
	@Environment(EnvType.CLIENT)
	public void setBackgroundPainter(@Nullable BackgroundPainter painter) {
		this.backgroundPainter = painter;
	}

	/**
	 * Gets the item filter of this item slot.
	 *
	 * @return the item filter
	 * @since 2.0.0
	 */
	public Predicate<ItemStack> getFilter() {
		return filter;
	}

	/**
	 * Sets the item filter of this item slot.
	 *
	 * @param filter the new item filter
	 * @return this item slot
	 * @since 2.0.0
	 */
	public WItemSlot setFilter(Predicate<ItemStack> filter) {
		this.filter = filter;
		for (ValidatedSlot peer : peers) {
			peer.setFilter(filter);
		}
		return this;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		(backgroundPainter != null ? backgroundPainter : BackgroundPainter.SLOT).paintBackground(x, y, this);
	}

	@Nullable
	@Override
	public WWidget cycleFocus(boolean lookForwards) {
		if (focusedSlot < 0) {
			focusedSlot = lookForwards ? 0 : (slotsWide * slotsHigh - 1);
			return this;
		}

		if (lookForwards) {
			focusedSlot++;
			if (focusedSlot >= slotsWide * slotsHigh) {
				focusedSlot = -1;
				return null;
			} else {
				return this;
			}
		} else {
			focusedSlot--;
			return focusedSlot >= 0 ? this : null;
		}
	}
}