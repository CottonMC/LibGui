package io.github.cottonmc.test;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.test.client.TestClientGui;

public class GuiItem extends Item {
	public GuiItem() {
		super(new Item.Settings().rarity(Rarity.EPIC));
	}
	
	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (world.isClient) {
			openScreen(); // In its own method to prevent class loading issues
		}
		
		return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
	}

	@Environment(EnvType.CLIENT)
	private void openScreen() {
		MinecraftClient.getInstance().setScreen(new CottonClientScreen(new TestClientGui()));
	}
}
