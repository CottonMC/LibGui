package io.github.cottonmc.test.client;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.CottonHud;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.impl.modmenu.ConfigGui;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.test.LibGuiTest;
import io.github.cottonmc.test.ReallySimpleDescription;
import io.github.cottonmc.test.TestDescription;

import java.util.function.Function;

import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class LibGuiTestClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		HandledScreens.<TestDescription, CottonInventoryScreen<TestDescription>>register(
				LibGuiTest.GUI_SCREEN_HANDLER_TYPE,
				CottonInventoryScreen::new
		);

		HandledScreens.<ReallySimpleDescription, CottonInventoryScreen<ReallySimpleDescription>>register(
				LibGuiTest.REALLY_SIMPLE_SCREEN_HANDLER_TYPE,
				CottonInventoryScreen::new
		);

		CottonHud.add(new WHudTest(), 10, -20, 10, 10);
		CottonHud.add(new WLabel(Text.literal("Test label")), 10, -30, 10, 10);

		ClientCommandManager.DISPATCHER.register(
				literal("libgui")
						.then(literal("config").executes(openScreen(client -> new ConfigGui(client.currentScreen))))
						.then(literal("tab").executes(openScreen(client -> new TabTestGui())))
						.then(literal("scrolling").executes(openScreen(client -> new ScrollingTestGui())))
						.then(literal("insets").executes(openScreen(client -> new InsetsTestGui())))
						.then(literal("textfield").executes(openScreen(client -> new TextFieldTestGui())))
		);
	}

	private static Command<FabricClientCommandSource> openScreen(Function<MinecraftClient, LightweightGuiDescription> screenFactory) {
		return context -> {
			var client = context.getSource().getClient();
			client.send(() -> client.setScreen(new CottonClientScreen(screenFactory.apply(client))));
			return Command.SINGLE_SUCCESS;
		};
	}
}
