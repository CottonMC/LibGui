package io.github.cottonmc.test.client;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.cottonmc.test.LibGuiTest;
import io.github.cottonmc.test.TestContainer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.container.BlockContext;
import net.minecraft.util.Identifier;

public class LibGuiTestClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(LibGuiTest.MODID, "gui"), (syncId, identifier, player, buf)->new CottonInventoryScreen<TestContainer>(new TestContainer(syncId, player.inventory, BlockContext.create(player.getEntityWorld(), buf.readBlockPos())), player));
	}

}
