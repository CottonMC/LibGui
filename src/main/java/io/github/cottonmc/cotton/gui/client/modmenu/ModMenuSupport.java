package io.github.cottonmc.cotton.gui.client.modmenu;

import net.minecraft.text.TranslatableText;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

public class ModMenuSupport implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> new CottonClientScreen(new TranslatableText("options.libgui.libgui_settings"), new ConfigGui(screen)) {
			public void onClose() {
				this.client.openScreen(screen);
			}
		};
	}
}
