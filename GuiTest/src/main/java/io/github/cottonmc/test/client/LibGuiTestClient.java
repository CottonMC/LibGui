package io.github.cottonmc.test.client;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.cottonmc.test.LibGuiTest;
import io.github.cottonmc.test.TestDescription;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Identifier;

public class LibGuiTestClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(LibGuiTest.MODID, "gui"), (syncId, identifier, player, buf)->new CottonInventoryScreen<TestDescription>(new TestDescription(syncId, player.inventory, ScreenHandlerContext.create(player.getEntityWorld(), buf.readBlockPos())), player));
	}

}
