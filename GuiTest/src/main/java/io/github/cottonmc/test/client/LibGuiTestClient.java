package io.github.cottonmc.test.client;

import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.cottonmc.test.LibGuiTest;
import io.github.cottonmc.test.TestDescription;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;

public class LibGuiTestClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ScreenRegistry.<TestDescription, CottonInventoryScreen<TestDescription>>register(
				LibGuiTest.GUI_SCREEN_HANDLER_TYPE,
				(desc, inventory, title) -> new CottonInventoryScreen<>(desc, inventory.player, title)
		);

		CottonHud.add(new WHudTest(), 10, -20, 10, 10);
	}

}
