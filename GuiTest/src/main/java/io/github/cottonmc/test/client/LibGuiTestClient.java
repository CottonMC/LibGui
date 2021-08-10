package io.github.cottonmc.test.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.text.LiteralText;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.cottonmc.cotton.gui.impl.modmenu.ConfigGui;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.test.LibGuiTest;
import io.github.cottonmc.test.TestDescription;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class LibGuiTestClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ScreenRegistry.<TestDescription, CottonInventoryScreen<TestDescription>>register(
				LibGuiTest.GUI_SCREEN_HANDLER_TYPE,
				(desc, inventory, title) -> new CottonInventoryScreen<>(desc, inventory.player, title)
		);

		CottonHud.add(new WHudTest(), 10, -20, 10, 10);
		CottonHud.add(new WLabel(new LiteralText("Test label")), 10, -30, 10, 10);

		ClientCommandManager.DISPATCHER.register(
				literal("libgui")
						.then(literal("config").executes(context -> {
							var client = context.getSource().getClient();
							client.send(() -> {
								client.setScreen(new CottonClientScreen(new ConfigGui(client.currentScreen)));
							});
							return 0;
						}))
						.then(literal("tab").executes(context -> {
							var client = context.getSource().getClient();
							client.send(() -> {
								client.setScreen(new CottonClientScreen(new TabTestGui()));
							});
							return 0;
						}))
						.then(literal("scrolling").executes(context -> {
							var client = context.getSource().getClient();
							client.send(() -> {
								client.setScreen(new CottonClientScreen(new ScrollingTestGui()));
							});
							return 0;
						}))
						.then(literal("insets").executes(context -> {
							var client = context.getSource().getClient();
							client.send(() -> {
								client.setScreen(new CottonClientScreen(new InsetsTestGui()));
							});
							return 0;
						}))
		);
	}

}
