package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.impl.VisualLogger;
import io.github.cottonmc.cotton.gui.impl.client.NarrationMessages;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.data.Rect2i;
import io.github.cottonmc.cotton.gui.widget.focus.Focus;
import io.github.cottonmc.cotton.gui.widget.focus.FocusModel;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

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
 * Item slots can have filters that check whether a player is allowed to insert or take out an item or not.
 * The filters can be set with {@link #setInputFilter(Predicate)} and {@link #setOutputFilter(Predicate)}. For example:
 *
 * <pre>
 * {@code
 * // Only sand can be placed on this slot
 * slot.setInputFilter(stack -> stack.isOf(Items.SAND));
 *
 * // Everything except glass can be taken out of this slot
 * slot.setOutputFilter(stack -> !stack.isOf(Items.GLASS));
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
 *     if (stack.isEmpty() || stack.getCount() < stack.getMaxCount()) {
 *         System.out.println("I'm not full yet!");
 *     }
 * });
 * }
 * </pre>
 */
public class WItemSlot extends WWidget {
	/**
	 * The default texture of item slots and {@link BackgroundPainter#SLOT}.
	 *
	 * @since 6.2.0
	 */
	public static final Identifier SLOT_TEXTURE = LibGuiCommon.id("textures/widget/item_slot.png");

	private static final VisualLogger LOGGER = new VisualLogger(WItemSlot.class);
	private final List<ValidatedSlot> peers = new ArrayList<>();
	@Nullable
	@Environment(EnvType.CLIENT)
	private BackgroundPainter backgroundPainter;
	@Nullable
	private Icon icon = null;
	private boolean iconOnlyPaintedForEmptySlots = false;
	private Inventory inventory;
	private int startIndex = 0;
	private int slotsWide = 1;
	private int slotsHigh = 1;
	private boolean big = false;
	private boolean insertingAllowed = true;
	private boolean takingAllowed = true;
	private int focusedSlot = -1;
	private int hoveredSlot = -1;
	private Predicate<ItemStack> inputFilter = ValidatedSlot.DEFAULT_ITEM_FILTER;
	private Predicate<ItemStack> outputFilter = ValidatedSlot.DEFAULT_ITEM_FILTER;
	private final Set<ChangeListener> listeners = new HashSet<>();
	private final FocusModel<Integer> focusModel = new FocusModel<>() {
		@Override
		public boolean isFocused(Focus<Integer> focus) {
			return focusedSlot == focus.key();
		}

		@Override
		public void setFocused(Focus<Integer> focus) {
			focusedSlot = focus.key();
		}

		@Override
		public Stream<Focus<Integer>> foci() {
			Stream.Builder<Focus<Integer>> builder = Stream.builder();
			int index = 0;

			for (int y = 0; y < slotsHigh; y++) {
				for (int x = 0; x < slotsWide; x++) {
					int slotX = x * 18;
					int slotY = y * 18;
					int size = 18;

					if (big) {
						slotX -= 4;
						slotY -= 4;
						size = 26;
					}

					builder.add(new Focus<>(index, new Rect2i(slotX, slotY, size, size)));
					index++;
				}
			}

			return builder.build();
		}
	};

	public WItemSlot(Inventory inventory, int startIndex, int slotsWide, int slotsHigh, boolean big) {
		this();
		this.inventory = inventory;
		this.startIndex = startIndex;
		this.slotsWide = slotsWide;
		this.slotsHigh = slotsHigh;
		this.big = big;
		//this.ltr = ltr;
	}
	
	private WItemSlot() {
		hoveredProperty().addListener((property, from, to) -> {
			assert to != null;
			if (!to) hoveredSlot = -1;
		});
	}
	
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
		WItemSlot w = new WItemSlot() {
			@Override
			protected Text getNarrationName() {
				return inventory instanceof PlayerInventory inv ? inv.getDisplayName() : NarrationMessages.Vanilla.INVENTORY;
			}
		};
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

	/**
	 * {@return the inventory backing this slot}
	 * @since 11.1.0
	 */
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * {@return the starting index of slots in the backing inventory}
	 * @since 11.1.0
	 */
	public int getStartIndex() {
		return startIndex;
	}

	/**
	 * {@return the width of this slot widget in individual slots}
	 * @since 11.1.0
	 */
	public int getSlotsWide() {
		return slotsWide;
	}

	/**
	 * {@return the height of this slot widget in individual slots}
	 * @since 11.1.0
	 */
	public int getSlotsHigh() {
		return slotsHigh;
	}

	/**
	 * {@return whether this slot is a big slot}
	 * Big slots are commonly used for crafting results and similar outputs.
	 */
	public boolean isBigSlot() {
		return big;
	}

	/**
	 * {@return the icon if set, otherwise null}
	 *
	 * @since 4.1.0
	 */
	@Nullable
	public Icon getIcon() {
		return this.icon;
	}

	/**
	 * Sets the icon to this slot. Can be used for labeling slots for certain activities.
	 *
	 * @param icon the icon
	 * @return this slot widget
	 * @since 4.1.0
	 */
	public WItemSlot setIcon(@Nullable Icon icon) {
		this.icon = icon;

		if (icon != null && (slotsWide * slotsHigh) > 1) {
			LOGGER.warn("Setting icon {} for item slot {} with more than 1 slot ({})", icon, this, slotsWide * slotsHigh);
		}

		return this;
	}

	/**
	 * Checks whether icons should be rendered when the first slot of this widget
	 * contains an item.
	 *
	 * <p>This property is {@code true} by default.
	 *
	 * @return {@code true} if the icon should always be painted, {@code false} otherwise
	 * @since 9.1.0
	 */
	public boolean isIconOnlyPaintedForEmptySlots() {
		return iconOnlyPaintedForEmptySlots;
	}

	/**
	 * Sets whether icons should be rendered when the first slot of this widget
	 * contains an item.
	 *
	 * @param iconOnlyPaintedForEmptySlots {@code true} if the icon should always be painted, {@code false} otherwise
	 * @return this item slot
	 * @since 9.1.0
	 */
	public WItemSlot setIconOnlyPaintedForEmptySlots(boolean iconOnlyPaintedForEmptySlots) {
		this.iconOnlyPaintedForEmptySlots = iconOnlyPaintedForEmptySlots;
		return this;
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
				slot.setInputFilter(inputFilter);
				slot.setOutputFilter(outputFilter);
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
	public InputResult onKeyPressed(KeyInput input) {
		if (isActivationKey(input.key()) && host instanceof ScreenHandler handler && focusedSlot >= 0) {
			MinecraftClient client = MinecraftClient.getInstance();

			ValidatedSlot peer = peers.get(focusedSlot);
			client.interactionManager.clickSlot(handler.syncId, peer.id, 0, SlotActionType.PICKUP, client.player);
			return InputResult.PROCESSED;
		}

		return InputResult.IGNORED;
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
	 * {@return an unmodifiable list containing the current slot peers}
	 *
	 * @since 11.1.0
	 */
	public @UnmodifiableView List<? extends ValidatedSlot> getPeers() {
		return Collections.unmodifiableList(peers);
	}

	/**
	 * Gets the starting {@linkplain net.minecraft.screen.slot.Slot#id ID} for the slot peers.
	 *
	 * @return the starting ID for the slot peers, or -1 if this slot widget has no peers
	 * @since 11.1.0
	 */
	public int getPeerStartId() {
		return !peers.isEmpty() ? peers.getFirst().id : -1;
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
	 * Gets the item stack input filter of this slot.
	 *
	 * @return the item input filter
	 * @since 8.1.0
	 */
	public Predicate<ItemStack> getInputFilter() {
		return inputFilter;
	}

	/**
	 * Sets the item input filter of this item slot.
	 *
	 * @param inputFilter the new item input filter
	 * @return this item slot
	 * @since 8.1.0
	 */
	public WItemSlot setInputFilter(Predicate<ItemStack> inputFilter) {
		this.inputFilter = inputFilter;
		for (ValidatedSlot peer : peers) {
			peer.setInputFilter(inputFilter);
		}
		return this;
	}

	/**
	 * Gets the item stack output filter of this slot.
	 *
	 * @return the item output filter
	 * @since 8.1.0
	 */
	public Predicate<ItemStack> getOutputFilter() {
		return outputFilter;
	}

	/**
	 * Sets the item output filter of this item slot.
	 *
	 * @param outputFilter the new item output filter
	 * @return this item slot
	 * @since 8.1.0
	 */
	public WItemSlot setOutputFilter(Predicate<ItemStack> outputFilter) {
		this.outputFilter = outputFilter;
		for (ValidatedSlot peer : peers) {
			peer.setOutputFilter(outputFilter);
		}
		return this;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
		if (backgroundPainter != null) {
			backgroundPainter.paintBackground(context, x, y, this);
		}

		if (icon != null && (!iconOnlyPaintedForEmptySlots || inventory.getStack(startIndex).isEmpty())) {
			icon.paint(context, x + 1, y + 1, 16);
		}
	}

	@Nullable
	@Override
	public FocusModel<?> getFocusModel() {
		return focusModel;
	}

	@Override
	public void onFocusLost() {
		focusedSlot = -1;
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
	public InputResult onMouseMove(int x, int y) {
		int slotX = x / 18;
		int slotY = y / 18;
		hoveredSlot = slotX + slotY * slotsWide;
		return InputResult.PROCESSED;
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

	@Environment(EnvType.CLIENT)
	@Override
	public void addNarrations(NarrationMessageBuilder builder) {
		List<Text> parts = new ArrayList<>();
		Text name = getNarrationName();
		if (name != null) parts.add(name);

		if (focusedSlot >= 0) {
			parts.add(Text.translatable(NarrationMessages.ITEM_SLOT_TITLE_KEY, focusedSlot + 1, slotsWide * slotsHigh));
		} else if (hoveredSlot >= 0) {
			parts.add(Text.translatable(NarrationMessages.ITEM_SLOT_TITLE_KEY, hoveredSlot + 1, slotsWide * slotsHigh));
		}

		builder.put(NarrationPart.TITLE, parts.toArray(new Text[0]));
	}

	/**
	 * Returns a "narration name" for this slot.
	 * It's narrated before the slot index. One example of a narration name would be "hotbar" for the player's hotbar.
	 *
	 * @return the narration name, or null if there's none for this slot
	 * @since 4.2.0
	 */
	@Nullable
	protected Text getNarrationName() {
		return null;
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
