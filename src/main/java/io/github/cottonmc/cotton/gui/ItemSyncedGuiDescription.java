package io.github.cottonmc.cotton.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;

import java.util.Objects;

/**
 * A {@link SyncedGuiDescription} for an {@linkplain ItemStack item stack}
 * in an {@linkplain net.minecraft.inventory.Inventory inventory}.
 *
 * <p>The owning item is represented with a {@link StackReference}, which can be
 * an item in an entity's inventory or a block's container, or any other reference
 * to an item stack.
 *
 * <p>If the owning item stack changes in any way, the screen closes by default (see {@link #canUse(PlayerEntity)}).
 *
 * @since 7.0.0
 */
public class ItemSyncedGuiDescription extends SyncedGuiDescription {
	/**
	 * A reference to the owning item stack of this GUI.
	 */
	protected final StackReference owner;

	/**
	 * The initial item stack of this GUI. This stack must <strong>not</strong> be mutated!
	 */
	protected final ItemStack ownerStack;

	/**
	 * Constructs an {@code ItemSyncedGuiDescription}.
	 *
	 * @param type            the screen handler type
	 * @param syncId          the sync ID
	 * @param playerInventory the inventory of the player viewing this GUI description
	 * @param owner           a reference to the owning item stack of this GUI description
	 */
	public ItemSyncedGuiDescription(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, StackReference owner) {
		super(type, syncId, playerInventory);
		this.owner = Objects.requireNonNull(owner, "Owner cannot be null");
		this.ownerStack = owner.get().copy();
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation for {@code ItemSyncedGuiDescription} returns {@code true} if and only if
	 * the {@linkplain #owner current owning item stack} is {@linkplain ItemStack#areEqual fully equal}
	 * to the {@linkplain #ownerStack original owner}.
	 *
	 * <p>If the item NBT is intended to change, subclasses should override this method to only check
	 * the item and the count. Those subclasses should also take care to respond properly
	 * to any NBT changes in the owning item stack.
	 */
	@Override
	public boolean canUse(PlayerEntity entity) {
		return ItemStack.areEqual(ownerStack, owner.get());
	}
}
