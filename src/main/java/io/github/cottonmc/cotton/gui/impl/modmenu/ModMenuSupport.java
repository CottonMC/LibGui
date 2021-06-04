package io.github.cottonmc.cotton.gui.impl.modmenu;

import net.minecraft.text.TranslatableText;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;

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
