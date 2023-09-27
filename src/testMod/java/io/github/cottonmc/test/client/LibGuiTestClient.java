package io.github.cottonmc.test.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.screen.ScreenTexts;
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
import io.github.cottonmc.test.TestItemDescription;

import java.util.function.Function;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

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

		HandledScreens.<TestItemDescription, CottonInventoryScreen<TestItemDescription>>register(
				LibGuiTest.ITEM_SCREEN_HANDLER_TYPE,
				CottonInventoryScreen::new
		);

		CottonHud.add(new WHudTest(), 10, -20, 10, 10);
		CottonHud.add(new WLabel(Text.literal("Test label")), 10, -30, 10, 10);

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess) -> dispatcher.register(
				literal("libgui")
						.then(literal("config").executes(openScreen(client -> new ConfigGui(client.currentScreen))))
						.then(literal("tab").executes(openScreen(client -> new TabTestGui())))
						.then(literal("scrolling").executes(openScreen(client -> new ScrollingTestGui())))
						.then(literal("scrollbar").executes(openScreen(client -> new ScrollBarTestGui())))
						.then(literal("insets").executes(openScreen(client -> new InsetsTestGui())))
						.then(literal("textfield").executes(openScreen(client -> new TextFieldTestGui())))
						.then(literal("paddings")
								.then(argument("horizontal", IntegerArgumentType.integer(0))
										.then(argument("vertical", IntegerArgumentType.integer(0))
												.executes(context -> {
													var hori = IntegerArgumentType.getInteger(context, "horizontal");
													var vert = IntegerArgumentType.getInteger(context, "vertical");
													return openScreen(client -> new PaddingTestGui(hori, vert)).run(context);
												}))))
						.then(literal("#182").executes(openScreen(client -> new Issue182TestGui())))
						.then(literal("#196").executes(openScreen(client -> new Issue196TestGui())))
						.then(literal("darkmode").executes(openScreen(client -> new DarkModeTestGui())))
						.then(literal("titlealignment").executes(openScreen(Text.literal("test title"), client -> new TitleAlignmentTestGui())))
						.then(literal("texture").executes(openScreen(client -> new TextureTestGui())))
		));
	}

	private static Command<FabricClientCommandSource> openScreen(Function<MinecraftClient, LightweightGuiDescription> screenFactory) {
		return openScreen(ScreenTexts.EMPTY, screenFactory);
	}

	private static Command<FabricClientCommandSource> openScreen(Text title, Function<MinecraftClient, LightweightGuiDescription> screenFactory) {
		return context -> {
			var client = context.getSource().getClient();
			client.send(() -> client.setScreen(new CottonClientScreen(title, screenFactory.apply(client))));
			return Command.SINGLE_SUCCESS;
		};
	}
}
