package io.github.cottonmc.test;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class GuiBlockEntity extends BlockEntity implements ImplementedInventory, NamedScreenHandlerFactory {
	static final int INVENTORY_SIZE = 8;
	
	DefaultedList<ItemStack> items =  DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
	
	public GuiBlockEntity(BlockPos pos, BlockState state) {
		super(LibGuiTest.GUI_BLOCKENTITY_TYPE, pos, state);
	}

	@Override
	public DefaultedList<ItemStack> getItems() {
		return items;
	}
	
	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return pos.isWithinDistance(player.getBlockPos(), 4.5);
	}

	@Override
	public Text getDisplayName() {
		return Text.literal("test title");
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return new TestDescription(LibGuiTest.GUI_SCREEN_HANDLER_TYPE, syncId, inv, ScreenHandlerContext.create(world, pos));
	}
}
