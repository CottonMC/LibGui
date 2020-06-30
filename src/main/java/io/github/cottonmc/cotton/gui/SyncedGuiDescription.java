package io.github.cottonmc.cotton.gui;

import java.util.ArrayList;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.world.World;

/**
 * A screen handler-based GUI description for GUIs with slots.
 */
public class SyncedGuiDescription extends ScreenHandler implements GuiDescription {
	
	protected Inventory blockInventory;
	protected PlayerInventory playerInventory;
	protected World world;
	protected PropertyDelegate propertyDelegate;
	
	protected WPanel rootPanel = new WGridPanel();
	protected int titleColor = WLabel.DEFAULT_TEXT_COLOR;
	protected int darkTitleColor = WLabel.DEFAULT_DARKMODE_TEXT_COLOR;
	protected boolean fullscreen = false;
	protected boolean titleVisible = true;
	protected HorizontalAlignment titleAlignment = HorizontalAlignment.LEFT;

	protected WWidget focus;

	public SyncedGuiDescription(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory) {
		super(type, syncId);
		this.blockInventory = null;
		this.playerInventory = playerInventory;
		this.world = playerInventory.player.world;
		this.propertyDelegate = null;//new ArrayPropertyDelegate(1);
	}
	
	public SyncedGuiDescription(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, Inventory blockInventory, PropertyDelegate propertyDelegate) {
		super(type, syncId);
		this.blockInventory = blockInventory;
		this.playerInventory = playerInventory;
		this.world = playerInventory.player.world;
		this.propertyDelegate = propertyDelegate;
		if (propertyDelegate!=null && propertyDelegate.size()>0) this.addProperties(propertyDelegate);
	}
	
	public WPanel getRootPanel() {
		return rootPanel;
	}
	
	public int getTitleColor() {
		return (world.isClient && LibGuiClient.config.darkMode) ? darkTitleColor : titleColor;
	}
	
	public SyncedGuiDescription setRootPanel(WPanel panel) {
		this.rootPanel = panel;
		return this;
	}

	@Override
	public SyncedGuiDescription setTitleColor(int color) {
		this.titleColor = color;
		this.darkTitleColor = (color == WLabel.DEFAULT_TEXT_COLOR) ? WLabel.DEFAULT_DARKMODE_TEXT_COLOR : color;
		return this;
	}

	@Override
	public SyncedGuiDescription setTitleColor(int lightColor, int darkColor) {
		this.titleColor = lightColor;
		this.darkTitleColor = darkColor;
		return this;
	}
	
	@Environment(EnvType.CLIENT)
	public void addPainters() {
		if (this.rootPanel!=null && !fullscreen) {
			this.rootPanel.setBackgroundPainter(BackgroundPainter.VANILLA);
		}
	}
	
	public void addSlotPeer(ValidatedSlot slot) {
		this.addSlot(slot);
	}
	
	@Override
	public ItemStack onSlotClick(int slotNumber, int button, SlotActionType action, PlayerEntity player) {
		if (action==SlotActionType.QUICK_MOVE) {
			
			if (slotNumber < 0) {
				return ItemStack.EMPTY;
			}
			
			if (slotNumber>=this.slots.size()) return ItemStack.EMPTY;
			Slot slot = this.slots.get(slotNumber);
			if (slot == null || !slot.canTakeItems(player)) {
				return ItemStack.EMPTY;
			}
			
			ItemStack remaining = ItemStack.EMPTY;
			if (slot != null && slot.hasStack()) {
				ItemStack toTransfer = slot.getStack();
				remaining = toTransfer.copy();
				//if (slot.inventory==blockInventory) {
				if (blockInventory!=null) {
					if (slot.inventory==blockInventory) {
						//Try to transfer the item from the block into the player's inventory
						if (!this.insertItem(toTransfer, this.playerInventory, true, player)) {
							return ItemStack.EMPTY;
						}
					} else if (!this.insertItem(toTransfer, this.blockInventory, false, player)) { //Try to transfer the item from the player to the block
						return ItemStack.EMPTY;
					}
				} else {
					//There's no block, just swap between the player's storage and their hotbar
					if (!swapHotbar(toTransfer, slotNumber, this.playerInventory, player)) {
						return ItemStack.EMPTY;
					}
				}
				
				if (toTransfer.isEmpty()) {
					slot.setStack(ItemStack.EMPTY);
				} else {
					slot.markDirty();
				}
			}
			
			return remaining;
		} else {
			return super.onSlotClick(slotNumber, button, action, player);
		}
	}
	
	/** WILL MODIFY toInsert! Returns true if anything was inserted. */
	private boolean insertIntoExisting(ItemStack toInsert, Slot slot, PlayerEntity player) {
		ItemStack curSlotStack = slot.getStack();
		if (!curSlotStack.isEmpty() && canStacksCombine(toInsert, curSlotStack) && slot.canTakeItems(player)) {
			int combinedAmount = curSlotStack.getCount() + toInsert.getCount();
			if (combinedAmount <= toInsert.getMaxCount()) {
				toInsert.setCount(0);
				curSlotStack.setCount(combinedAmount);
				slot.markDirty();
				return true;
			} else if (curSlotStack.getCount() < toInsert.getMaxCount()) {
				toInsert.decrement(toInsert.getMaxCount() - curSlotStack.getCount());
				curSlotStack.setCount(toInsert.getMaxCount());
				slot.markDirty();
				return true;
			}
		}
		return false;
	}
	
	/** WILL MODIFY toInsert! Returns true if anything was inserted. */
	private boolean insertIntoEmpty(ItemStack toInsert, Slot slot) {
		ItemStack curSlotStack = slot.getStack();
		if (curSlotStack.isEmpty() && slot.canInsert(toInsert)) {
			if (toInsert.getCount() > slot.getMaxStackAmount()) {
				slot.setStack(toInsert.split(slot.getMaxStackAmount()));
			} else {
				slot.setStack(toInsert.split(toInsert.getCount()));
			}

			slot.markDirty();
			return true;
		}
		
		return false;
	}
	
	private boolean insertItem(ItemStack toInsert, Inventory inventory, boolean walkBackwards, PlayerEntity player) {
		//Make a unified list of slots *only from this inventory*
		ArrayList<Slot> inventorySlots = new ArrayList<>();
		for(Slot slot : slots) {
			if (slot.inventory==inventory) inventorySlots.add(slot);
		}
		if (inventorySlots.isEmpty()) return false;
		
		//Try to insert it on top of existing stacks
		boolean inserted = false;
		if (walkBackwards) {
			for(int i=inventorySlots.size()-1; i>=0; i--) {
				Slot curSlot = inventorySlots.get(i);
				if (insertIntoExisting(toInsert, curSlot, player)) inserted = true;
				if (toInsert.isEmpty()) break;
			}
		} else {
			for(int i=0; i<inventorySlots.size(); i++) {
				Slot curSlot = inventorySlots.get(i);
				if (insertIntoExisting(toInsert, curSlot, player)) inserted = true;
				if (toInsert.isEmpty()) break;
			}
			
		}
		
		//If we still have any, shove them into empty slots
		if (!toInsert.isEmpty()) {
			if (walkBackwards) {
				for(int i=inventorySlots.size()-1; i>=0; i--) {
					Slot curSlot = inventorySlots.get(i);
					if (insertIntoEmpty(toInsert, curSlot)) inserted = true;
					if (toInsert.isEmpty()) break;
				}
			} else {
				for(int i=0; i<inventorySlots.size(); i++) {
					Slot curSlot = inventorySlots.get(i);
					if (insertIntoEmpty(toInsert, curSlot)) inserted = true;
					if (toInsert.isEmpty()) break;
				}
				
			}
		}
		
		return inserted;
	}
	
	private boolean swapHotbar(ItemStack toInsert, int slotNumber, Inventory inventory, PlayerEntity player) {
		//Feel out the slots to see what's storage versus hotbar
		ArrayList<Slot> storageSlots = new ArrayList<>();
		ArrayList<Slot> hotbarSlots = new ArrayList<>();
		boolean swapToStorage = true;
		boolean inserted = false;
		
		for(Slot slot : slots) {
			if (slot.inventory==inventory && slot instanceof ValidatedSlot) {
				int index = ((ValidatedSlot)slot).getInventoryIndex();
				if (PlayerInventory.isValidHotbarIndex(index)) {
					hotbarSlots.add(slot);
				} else {
					storageSlots.add(slot);
					if (index==slotNumber) swapToStorage = false;
				}
			}
		}
		if (storageSlots.isEmpty() || hotbarSlots.isEmpty()) return false;
		
		if (swapToStorage) {
			//swap from hotbar to storage
			for(int i=0; i<storageSlots.size(); i++) {
				Slot curSlot = storageSlots.get(i);
				if (insertIntoExisting(toInsert, curSlot, player)) inserted = true;
				if (toInsert.isEmpty()) break;
			}
			if (!toInsert.isEmpty()) {
				for(int i=0; i<storageSlots.size(); i++) {
					Slot curSlot = storageSlots.get(i);
					if (insertIntoEmpty(toInsert, curSlot)) inserted = true;
					if (toInsert.isEmpty()) break;
				}
			}
		} else {
			//swap from storage to hotbar
			for(int i=0; i<hotbarSlots.size(); i++) {
				Slot curSlot = hotbarSlots.get(i);
				if (insertIntoExisting(toInsert, curSlot, player)) inserted = true;
				if (toInsert.isEmpty()) break;
			}
			if (!toInsert.isEmpty()) {
				for(int i=0; i<hotbarSlots.size(); i++) {
					Slot curSlot = hotbarSlots.get(i);
					if (insertIntoEmpty(toInsert, curSlot)) inserted = true;
					if (toInsert.isEmpty()) break;
				}
			}
		}
		
		return inserted;
	}
	
	@Nullable
	public WWidget doMouseUp(int x, int y, int state) {
		if (rootPanel!=null) return rootPanel.onMouseUp(x, y, state);
		return null;
	}
	
	@Nullable
	public WWidget doMouseDown(int x, int y, int button) {
		if (rootPanel!=null) return rootPanel.onMouseDown(x, y, button);
		return null;
	}
	
	public void doMouseDrag(int x, int y, int button, double deltaX, double deltaY) {
		if (rootPanel!=null) rootPanel.onMouseDrag(x, y, button, deltaX, deltaY);
	}
	
	public void doClick(int x, int y, int button) {
		if (focus!=null) {
			int wx = focus.getAbsoluteX();
			int wy = focus.getAbsoluteY();
			
			if (x>=wx && x<wx+focus.getWidth() && y>=wy && y<wy+focus.getHeight()) {
				//Do nothing, focus will get the click soon
			} else {
				//Invalidate the component first
				WWidget lastFocus = focus;
				focus = null;
				lastFocus.onFocusLost();
			}
		}
		
		//if (rootPanel!=null) rootPanel.onClick(x, y, button);
	}
	
	public void doCharType(char ch) {
		if (focus!=null) focus.onCharTyped(ch);
	}
	
	//public void doKeyPress(int key) {
	//	if (focus!=null) focus.onKeyPressed(key);
	//}
	
	//public void doKeyRelease(int key) {
	//	if (focus!=null) focus.onKeyReleased(key);
	//}
	
	@Nullable
	@Override
	public PropertyDelegate getPropertyDelegate() {
		return propertyDelegate;
	}
	
	@Override
	public GuiDescription setPropertyDelegate(PropertyDelegate delegate) {
		this.propertyDelegate = delegate;
		return this;
	}

	/**
	 * Creates a player inventory widget from this panel's {@linkplain #playerInventory player inventory}.
	 *
	 * @return the created inventory widget
	 */
	public WPlayerInvPanel createPlayerInventoryPanel() {
		return new WPlayerInvPanel(this.playerInventory);
	}

	/**
	 * Creates a player inventory widget from this panel's {@linkplain #playerInventory player inventory}.
	 *
	 * @param hasLabel whether the "Inventory" label should be displayed
	 * @return the created inventory widget
	 * @since 2.0.0
	 */
	public WPlayerInvPanel createPlayerInventoryPanel(boolean hasLabel) {
		return new WPlayerInvPanel(this.playerInventory, hasLabel);
	}

	/**
	 * Creates a player inventory widget from this panel's {@linkplain #playerInventory player inventory}.
	 *
	 * @param label the inventory label widget
	 * @return the created inventory widget
	 * @since 2.0.0
	 */
	public WPlayerInvPanel createPlayerInventoryPanel(WWidget label) {
		return new WPlayerInvPanel(this.playerInventory, label);
	}

	/**
	 * Gets the block inventory at the context.
	 *
	 * <p>If no inventory is found, returns {@link EmptyInventory#INSTANCE}.
	 *
	 * <p>Searches for these implementations in the following order:
	 * <ol>
	 *     <li>Blocks implementing {@code InventoryProvider}</li>
	 *     <li>Block entities implementing {@code InventoryProvider}</li>
	 *     <li>Block entities implementing {@code Inventory}</li>
	 * </ol>
	 *
	 * @param ctx the context
	 * @return the found inventory
	 */
	public static Inventory getBlockInventory(ScreenHandlerContext ctx) {
		return getBlockInventory(ctx, () -> EmptyInventory.INSTANCE);
	}

	/**
	 * Gets the block inventory at the context.
	 *
	 * <p>If no inventory is found, returns a simple mutable inventory
	 * with the specified number of slots.
	 *
	 * <p>Searches for these implementations in the following order:
	 * <ol>
	 *     <li>Blocks implementing {@code InventoryProvider}</li>
	 *     <li>Block entities implementing {@code InventoryProvider}</li>
	 *     <li>Block entities implementing {@code Inventory}</li>
	 * </ol>
	 *
	 * @param ctx  the context
	 * @param size the fallback inventory size
	 * @return the found inventory
	 * @since 2.0.0
	 */
	public static Inventory getBlockInventory(ScreenHandlerContext ctx, int size) {
		return getBlockInventory(ctx, () -> new SimpleInventory(size));
	}

	private static Inventory getBlockInventory(ScreenHandlerContext ctx, Supplier<Inventory> fallback) {
		return ctx.run((world, pos) -> {
			BlockState state = world.getBlockState(pos);
			Block b = state.getBlock();

			if (b instanceof InventoryProvider) {
				Inventory inventory = ((InventoryProvider)b).getInventory(state, world, pos);
				if (inventory != null) {
					return inventory;
				}
			}

			BlockEntity be = world.getBlockEntity(pos);
			if (be!=null) {
				if (be instanceof InventoryProvider) {
					Inventory inventory = ((InventoryProvider)be).getInventory(state, world, pos);
					if (inventory != null) {
						return inventory;
					}
				} else if (be instanceof Inventory) {
					return (Inventory)be;
				}
			}

			return fallback.get();
		}).orElseGet(fallback);
	}

	/**
	 * Gets the property delegate at the context.
	 *
	 * <p>If no property delegate is found, returns an empty property delegate with no properties.
	 *
	 * <p>Searches for block entities implementing {@link PropertyDelegateHolder}.
	 *
	 * @param ctx the context
	 * @return the found property delegate
	 */
	public static PropertyDelegate getBlockPropertyDelegate(ScreenHandlerContext ctx) {
		return ctx.run((world, pos) -> {
			BlockEntity be = world.getBlockEntity(pos);
			if (be!=null && be instanceof PropertyDelegateHolder) {
				return ((PropertyDelegateHolder)be).getPropertyDelegate();
			}
			
			return new ArrayPropertyDelegate(0);
		}).orElse(new ArrayPropertyDelegate(0));
	}

	/**
	 * Gets the property delegate at the context.
	 *
	 * <p>If no property delegate is found, returns an array property delegate
	 * with the specified number of properties.
	 *
	 * <p>Searches for block entities implementing {@link PropertyDelegateHolder}.
	 *
	 * @param ctx  the context
	 * @param size the number of properties
	 * @return the found property delegate
	 * @since 2.0.0
	 */
	public static PropertyDelegate getBlockPropertyDelegate(ScreenHandlerContext ctx, int size) {
		return ctx.run((world, pos) -> {
			BlockEntity be = world.getBlockEntity(pos);
			if (be!=null && be instanceof PropertyDelegateHolder) {
				return ((PropertyDelegateHolder)be).getPropertyDelegate();
			}

			return new ArrayPropertyDelegate(size);
		}).orElse(new ArrayPropertyDelegate(size));
	}
	
	//extends ScreenHandler {
		@Override
		public boolean canUse(PlayerEntity entity) {
			return (blockInventory!=null) ? blockInventory.canPlayerUse(entity) : true;
		}
	//}

	@Override
	public boolean isFocused(WWidget widget) {
		return focus == widget;
	}

	@Override
	public WWidget getFocus() {
		return focus;
	}

	@Override
	public void requestFocus(WWidget widget) {
		//TODO: Are there circumstances where focus can't be stolen?
		if (focus==widget) return; //Nothing happens if we're already focused
		if (!widget.canFocus()) return; //This is kind of a gotcha but needs to happen
		if (focus!=null) focus.onFocusLost();
		focus = widget;
		focus.onFocusGained();
	}

	@Override
	public void releaseFocus(WWidget widget) {
		if (focus==widget) {
			focus = null;
			widget.onFocusLost();
		}
	}

	@Override
	public boolean isFullscreen() {
		return fullscreen;
	}

	@Override
	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	@Override
	public boolean isTitleVisible() {
		return titleVisible;
	}

	@Override
	public void setTitleVisible(boolean titleVisible) {
		this.titleVisible = titleVisible;
	}

	@Override
	public HorizontalAlignment getTitleAlignment() {
		return titleAlignment;
	}

	@Override
	public void setTitleAlignment(HorizontalAlignment titleAlignment) {
		this.titleAlignment = titleAlignment;
	}
}
