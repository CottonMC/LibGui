package io.github.cottonmc.test;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class GuiItem extends Item {
	public GuiItem() {
		super(new Item.Settings().rarity(Rarity.EPIC));
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		player.openHandledScreen(createScreenHandlerFactory(player, hand));
		return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
	}

	private NamedScreenHandlerFactory createScreenHandlerFactory(PlayerEntity player, Hand hand) {
		EquipmentSlot slot = switch (hand) {
			case MAIN_HAND -> EquipmentSlot.MAINHAND;
			case OFF_HAND -> EquipmentSlot.OFFHAND;
		};
		ItemStack stack = player.getStackInHand(hand);
		return new ExtendedScreenHandlerFactory<EquipmentSlot>() {
			@Override
			public Text getDisplayName() {
				return stack.getName();
			}

			@Override
			public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
				return new TestItemDescription(syncId, playerInventory, StackReference.of(player, slot));
			}

			@Override
			public EquipmentSlot getScreenOpeningData(ServerPlayerEntity player) {
				return slot;
			}
		};
	}
}
