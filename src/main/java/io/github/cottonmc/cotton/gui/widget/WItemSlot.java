package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A widget that displays an item that can be interacted with.
 *
 * <p>Item slot widgets can contain multiple visual slots themselves.
 * For example, a slot widget might be 5x3 with 15 visual slots in total.
 *
 * <p>Item slots are handled with so-called peers in the background.
 * They are instances of {@link ValidatedSlot} that handle the interactions
 * between the player and the widget.
 *
 * <h2>Filters</h2>
 * Item slots can have filters that check whether a player is allowed to insert an item or not.
 * The filter can be set with {@link #setFilter(Predicate)}. For example:
 *
 * <pre>
 * {@code
 * // Only sand in this slot!
 * slot.setFilter(stack -> stack.getItem() == Items.SAND);
 * }
 * </pre>
 *
 * <h2>Listeners</h2>
 * Slot change listeners are instances of {@link WItemSlot.ChangeListener} that can handle changes
 * to item stacks in slots. For example:
 *
 * <pre>
 * {@code
 * slot.addChangeListener((slot, inventory, index, stack) -> {
 *     if (stack.isEmpty() || stack.getCount() < stack.getMaxCount()) {
 *         System.out.println("I'm not full yet!");
 *     }
 * });
 * }
 * </pre>
 */
public class WItemSlot extends WWidget {
	private static final Predicate<ItemStack> DEFAULT_FILTER = stack -> true;
	private final List<ValidatedSlot> peers = new ArrayList<>();
	@Nullable
	@Environment(EnvType.CLIENT)
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
	private final Set<ChangeListener> listeners = new HashSet<>();

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
	public void validate(GuiDescription host) {
		super.validate(host);
		peers.clear();
		int index = startIndex;
		
		for (int y = 0; y < slotsHigh; y++) {
			for (int x = 0; x < slotsWide; x++) {
				// The Slot object is offset +1 because it's the inner area of the slot.
				ValidatedSlot slot = createSlotPeer(inventory, index, this.getAbsoluteX() + (x * 18) + 1, this.getAbsoluteY() + (y * 18) + 1);
				slot.setInsertingAllowed(insertingAllowed);
				slot.setTakingAllowed(takingAllowed);
				slot.setFilter(filter);
				for (ChangeListener listener : listeners) {
					slot.addChangeListener(this, listener);
				}
				peers.add(slot);
				host.addSlotPeer(slot);
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
		if (backgroundPainter != null) {
			backgroundPainter.paintBackground(matrices, x, y, this);
		}
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

	/**
	 * Adds a change listener to this slot.
	 * Does nothing if the listener is already registered.
	 *
	 * @param listener the added listener
	 * @throws NullPointerException if the listener is null
	 * @since 3.0.0
	 */
	public void addChangeListener(ChangeListener listener) {
		Objects.requireNonNull(listener, "listener");
		listeners.add(listener);

		for (ValidatedSlot peer : peers) {
			peer.addChangeListener(this, listener);
		}
	}

	@Override
	public void onShown() {
		for (ValidatedSlot peer : peers) {
			peer.setVisible(true);
		}
	}

	@Override
	public void onHidden() {
		super.onHidden();

		for (ValidatedSlot peer : peers) {
			peer.setVisible(false);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void addPainters() {
		backgroundPainter = BackgroundPainter.SLOT;
	}

	/**
	 * A listener for changes in an item slot.
	 *
	 * @since 3.0.0
	 */
	@FunctionalInterface
	public interface ChangeListener {
		/**
		 * Handles a changed item stack in an item slot.
		 *
		 * @param slot      the item slot widget
		 * @param inventory the item inventory of the slot
		 * @param index     the index of the slot in the inventory
		 * @param stack     the changed item stack
		 */
		void onStackChanged(WItemSlot slot, Inventory inventory, int index, ItemStack stack);
	}
}
