package io.github.cottonmc.cotton.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LibGui;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WPlayerInvPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.Vec2i;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * A screen handler-based GUI description for GUIs with slots.
 */
public class SyncedGuiDescription extends ScreenHandler implements GuiDescription {
	
	protected Inventory blockInventory;
	protected PlayerInventory playerInventory;
	protected World world;
	protected PropertyDelegate propertyDelegate;
	
	protected WPanel rootPanel = new WGridPanel().setInsets(Insets.ROOT_PANEL);
	protected int titleColor = WLabel.DEFAULT_TEXT_COLOR;
	protected int darkTitleColor = WLabel.DEFAULT_DARKMODE_TEXT_COLOR;
	protected boolean fullscreen = false;
	protected boolean titleVisible = true;
	protected HorizontalAlignment titleAlignment = HorizontalAlignment.LEFT;

	protected WWidget focus;
	private Vec2i titlePos = new Vec2i(8, 6);

	/**
	 * Constructs a new synced GUI description without a block inventory or a property delegate.
	 *
	 * @param type            the {@link ScreenHandlerType} of this GUI description
	 * @param syncId          the current sync ID
	 * @param playerInventory the player inventory of the player viewing this screen
	 */
	public SyncedGuiDescription(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory) {
		super(type, syncId);
		this.blockInventory = null;
		this.playerInventory = playerInventory;
		this.world = playerInventory.player.getWorld();
		this.propertyDelegate = null;//new ArrayPropertyDelegate(1);
	}

	/**
	 * Constructs a new synced GUI description.
	 *
	 * @param type             the {@link ScreenHandlerType} of this GUI description
	 * @param syncId           the current sync ID
	 * @param playerInventory  the player inventory of the player viewing this screen
	 * @param blockInventory   the block inventory of a corresponding container block, or null if not found or applicable
	 * @param propertyDelegate a property delegate whose properties, if any, will automatically be {@linkplain #addProperties(PropertyDelegate) added}
	 */
	public SyncedGuiDescription(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, @Nullable Inventory blockInventory, @Nullable PropertyDelegate propertyDelegate) {
		super(type, syncId);
		this.blockInventory = blockInventory;
		this.playerInventory = playerInventory;
		this.world = playerInventory.player.getWorld();
		this.propertyDelegate = propertyDelegate;
		if (propertyDelegate!=null && propertyDelegate.size()>0) this.addProperties(propertyDelegate);
		if (blockInventory != null) blockInventory.onOpen(playerInventory.player);
	}
	
	public WPanel getRootPanel() {
		return rootPanel;
	}
	
	public int getTitleColor() {
		return (world.isClient && isDarkMode().orElse(LibGui.isDarkMode())) ? darkTitleColor : titleColor;
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
	public ItemStack quickMove(PlayerEntity player, int index) {
		ItemStack result = ItemStack.EMPTY;
		Slot slot = slots.get(index);

		if (slot.hasStack()) {
			ItemStack slotStack = slot.getStack();
			result = slotStack.copy();

			if (blockInventory!=null) {
				if (slot.inventory==blockInventory) {
					//Try to transfer the item from the block into the player's inventory
					if (!this.insertItem(slotStack, this.playerInventory, true, player)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.insertItem(slotStack, this.blockInventory, false, player)) { //Try to transfer the item from the player to the block
					return ItemStack.EMPTY;
				}
			} else {
				//There's no block, just swap between the player's storage and their hotbar
				if (!swapHotbar(slotStack, index, this.playerInventory, player)) {
					return ItemStack.EMPTY;
				}
			}

			if (slotStack.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}

		return result;
	}

	/** WILL MODIFY toInsert! Returns true if anything was inserted. */
	private boolean insertIntoExisting(ItemStack toInsert, Slot slot, PlayerEntity player) {
		ItemStack curSlotStack = slot.getStack();
		if (!curSlotStack.isEmpty() && ItemStack.canCombine(toInsert, curSlotStack) && slot.canInsert(toInsert)) {
			int combinedAmount = curSlotStack.getCount() + toInsert.getCount();
			int maxAmount = Math.min(toInsert.getMaxCount(), slot.getMaxItemCount(toInsert));
			if (combinedAmount <= maxAmount) {
				toInsert.setCount(0);
				curSlotStack.setCount(combinedAmount);
				slot.markDirty();
				return true;
			} else if (curSlotStack.getCount() < maxAmount) {
				toInsert.decrement(maxAmount - curSlotStack.getCount());
				curSlotStack.setCount(maxAmount);
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
			if (toInsert.getCount() > slot.getMaxItemCount(toInsert)) {
				slot.setStack(toInsert.split(slot.getMaxItemCount(toInsert)));
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
					if (slot.id==slotNumber) swapToStorage = false;
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
		return ctx.get((world, pos) -> {
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
		return ctx.get((world, pos) -> {
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
		return ctx.get((world, pos) -> {
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

		@Override
		public void onClosed(PlayerEntity player) {
			super.onClosed(player);
			if (blockInventory != null) blockInventory.onClose(player);
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

	@Override
	public Vec2i getTitlePos() {
		return titlePos;
	}

	@Override
	public void setTitlePos(Vec2i titlePos) {
		this.titlePos = titlePos;
	}

	/**
	 * Gets the network side this GUI description runs on.
	 *
	 * @return this GUI's network side
	 * @since 3.3.0
	 */
	public final NetworkSide getNetworkSide() {
		return world instanceof ServerWorld ? NetworkSide.SERVER : NetworkSide.CLIENT;
	}

	/**
	 * Gets the packet sender corresponding to this GUI's network side.
	 *
	 * @return the packet sender
	 * @since 3.3.0
	 */
	public final PacketSender getPacketSender() {
		if (getNetworkSide() == NetworkSide.SERVER) {
			return ServerPlayNetworking.getSender((ServerPlayerEntity) playerInventory.player);
		} else {
			return getClientPacketSender();
		}
	}

	@Environment(EnvType.CLIENT)
	private PacketSender getClientPacketSender() {
		return ClientPlayNetworking.getSender();
	}
}
