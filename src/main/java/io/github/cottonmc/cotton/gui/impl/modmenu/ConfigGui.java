package io.github.cottonmc.cotton.gui.impl.modmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.impl.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import org.jspecify.annotations.Nullable;

public class ConfigGui extends LightweightGuiDescription {
	public ConfigGui(@Nullable Screen previous) {
		WGridPanel root = new WGridPanel(20);
		root.setInsets(Insets.ROOT_PANEL);
		setRootPanel(root);

		WToggleButton darkmodeButton = new WToggleButton(Component.translatable("option.libgui.darkmode")) {
			@Override
			public void onToggle(boolean on) {
				LibGuiClient.config.darkMode = on;
				LibGuiClient.saveConfig(LibGuiClient.config);
			}
		};
		darkmodeButton.setToggle(LibGuiClient.config.darkMode);
		root.add(darkmodeButton, 0, 1, 6, 1);

		root.add(new WKirbSprite(), 5, 2);
		
		WButton doneButton = new WButton(CommonComponents.GUI_DONE);
		doneButton.setOnClick(()->{
			Minecraft.getInstance().gui.setScreen(previous);
		});
		root.add(doneButton, 0, 3, 3, 1);
		
		root.setBackgroundPainter(BackgroundPainter.VANILLA);
		
		root.validate(this);
	}
}
