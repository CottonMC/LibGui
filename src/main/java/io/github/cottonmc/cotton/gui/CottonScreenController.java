package io.github.cottonmc.cotton.gui;

import java.util.ArrayList;

import javax.annotation.Nullable;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.ArrayPropertyDelegate;
import net.minecraft.container.BlockContext;
import net.minecraft.container.CraftingContainer;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeType;
import net.minecraft.world.World;

public abstract class CottonScreenController extends CraftingContainer<Inventory> {
	
	protected Inventory blockInventory;
	protected PlayerInventory playerInventory;
	protected RecipeType<?> recipeType;
	protected World world;
	protected PropertyDelegate propertyDelegate;
	
	protected WPanel rootPanel = new WGridPanel();
	protected int titleColor = 0xFF404040;
	
	public CottonScreenController(RecipeType<?> recipeType, int syncId, PlayerInventory playerInventory) {
		super(null, syncId);
		this.blockInventory = null;
		this.playerInventory = playerInventory;
		this.recipeType = recipeType;
		this.world = playerInventory.player.world;
		this.propertyDelegate = null;//new ArrayPropertyDelegate(1);
	}
	
	public CottonScreenController(RecipeType<?> recipeType, int syncId, PlayerInventory playerInventory, Inventory blockInventory, PropertyDelegate propertyDelegate) {
		super(null, syncId);
		this.blockInventory = blockInventory;
		this.playerInventory = playerInventory;
		this.recipeType = recipeType;
		this.world = playerInventory.player.world;
		this.propertyDelegate = propertyDelegate;
		if (propertyDelegate!=null && propertyDelegate.size()>0) this.addProperties(propertyDelegate);
	}
	
	public WPanel getRootPanel() {
		return rootPanel;
	}
	
	public int getTitleColor() {
		return titleColor;
	}
	
	public CottonScreenController setRootPanel(WPanel panel) {
		this.rootPanel = panel;
		return this;
	}
	
	public CottonScreenController setTitleColor(int color) {
		this.titleColor = color;
		return this;
	}
	
	@Environment(EnvType.CLIENT)
	public void addPainters() {
		if (this.rootPanel!=null) {
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
			
			if (slotNumber>=this.slotList.size()) return ItemStack.EMPTY;
			Slot slot = this.slotList.get(slotNumber);
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
						if (!this.insertItem(toTransfer, this.playerInventory, true)) {
							return ItemStack.EMPTY;
						}
					} else if (!this.insertItem(toTransfer, this.blockInventory, false)) { //Try to transfer the item from the player to the block
						return ItemStack.EMPTY;
					}
				} else {
					//There's no block, just swap between the player's storage and their hotbar
					if (!swapHotbar(toTransfer, slotNumber, this.playerInventory)) {
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
	private boolean insertIntoExisting(ItemStack toInsert, Slot slot) {
		ItemStack curSlotStack = slot.getStack();
		if (!curSlotStack.isEmpty() && canStacksCombine(toInsert, curSlotStack)) {
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
	
	private boolean insertItem(ItemStack toInsert, Inventory inventory, boolean walkBackwards) {
		//Make a unified list of slots *only from this inventory*
		ArrayList<Slot> inventorySlots = new ArrayList<>();
		for(Slot slot : slotList) {
			if (slot.inventory==inventory) inventorySlots.add(slot);
		}
		if (inventorySlots.isEmpty()) return false;
		
		//Try to insert it on top of existing stacks
		boolean inserted = false;
		if (walkBackwards) {
			for(int i=inventorySlots.size()-1; i>=0; i--) {
				Slot curSlot = inventorySlots.get(i);
				if (insertIntoExisting(toInsert, curSlot)) inserted = true;
				if (toInsert.isEmpty()) break;
			}
		} else {
			for(int i=0; i<inventorySlots.size(); i++) {
				Slot curSlot = inventorySlots.get(i);
				if (insertIntoExisting(toInsert, curSlot)) inserted = true;
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
	
	private boolean swapHotbar(ItemStack toInsert, int slotNumber, Inventory inventory) {
		//Feel out the slots to see what's storage versus hotbar
		ArrayList<Slot> storageSlots = new ArrayList<>();
		ArrayList<Slot> hotbarSlots = new ArrayList<>();
		boolean swapToStorage = true;
		boolean inserted = false;
		
		for(Slot slot : slotList) {
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
				if (insertIntoExisting(toInsert, curSlot)) inserted = true;
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
				if (insertIntoExisting(toInsert, curSlot)) inserted = true;
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
	
	public void doMouseDrag(int x, int y, int button) {
		if (rootPanel!=null) rootPanel.onMouseDrag(x, y, button);
	}
	
	public void doClick(int x, int y, int button) {
		if (rootPanel!=null) rootPanel.onClick(x, y, button);
	}
	
	/**
	 * Gets the PropertyDelegate associated with this Controller.
	 */
	@Nullable
	public PropertyDelegate getPropertyDelegate() {
		// TODO Auto-generated method stub
		return propertyDelegate;
	}
	
	public WPlayerInvPanel createPlayerInventoryPanel() {
		return new WPlayerInvPanel(this.playerInventory);
	}
	
	public static Inventory getBlockInventory(BlockContext ctx) {
		return ctx.run((world, pos) -> {
			BlockState state = world.getBlockState(pos);
			Block b = state.getBlock();
			
			if (b instanceof InventoryProvider) {
				return ((InventoryProvider)b).getInventory(state, world, pos);
			}
			
			BlockEntity be = world.getBlockEntity(pos);
			if (be!=null) {
				if (be instanceof InventoryProvider) {
					return ((InventoryProvider)be).getInventory(state, world, pos);
				} else if (be instanceof Inventory) {
					return (Inventory)be;
				}
			}
			
			return EmptyInventory.INSTANCE;
		}).orElse(EmptyInventory.INSTANCE);
	}
	
	public static PropertyDelegate getBlockPropertyDelegate(BlockContext ctx) {
		return ctx.run((world, pos) -> {
			BlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			if (block instanceof PropertyDelegateHolder) {
				return ((PropertyDelegateHolder)block).getPropertyDelegate();
			}
			BlockEntity be = world.getBlockEntity(pos);
			if (be!=null && be instanceof PropertyDelegateHolder) {
				return ((PropertyDelegateHolder)be).getPropertyDelegate();
			}
			
			return new ArrayPropertyDelegate(0);
		}).orElse(new ArrayPropertyDelegate(0));
	}
	
	//extends CraftingContainer<Inventory> {
		@Override
		public void populateRecipeFinder(RecipeFinder recipeFinder) {
			if (this.blockInventory instanceof RecipeInputProvider) {
				((RecipeInputProvider)this.blockInventory).provideRecipeInputs(recipeFinder);
			}
		}
		
		@Override
		public void clearCraftingSlots() {
			if (this.blockInventory!=null) this.blockInventory.clear();
		}
		
		@Override
		public boolean matches(Recipe<? super Inventory> recipe) {
			if (blockInventory==null || world==null) return false;
			return false; //TODO recipe support
		}
		
		@Override
		public abstract int getCraftingResultSlotIndex();

		@Override
		public int getCraftingWidth() {
			return 1;
		}

		@Override
		public int getCraftingHeight() {
			return 1;
		}

		@Override
		@Environment(EnvType.CLIENT)
		public int getCraftingSlotCount() {
			return 1;
		}
		
		//(implied) extends Container {
			@Override
			public boolean canUse(PlayerEntity entity) {
				return (blockInventory!=null) ? blockInventory.canPlayerUseInv(entity) : true;
			}
		//}
	//}
}
