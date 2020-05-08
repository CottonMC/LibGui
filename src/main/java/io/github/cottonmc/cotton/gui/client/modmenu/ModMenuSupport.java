package io.github.cottonmc.cotton.gui.client.modmenu;


import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.text.TranslatableText;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LibGuiClient;

public class ModMenuSupport implements ModMenuApi {
	
	@Override
	public String getModId() {
		return LibGuiClient.MODID;
	}
	
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> new CottonClientScreen(new TranslatableText("options.libgui.libgui_settings"), new ConfigGui(screen)) {
			public void onClose() {
				this.client.openScreen(screen);
			}
			
			protected void init() {
				super.init();
				this.description.getRootPanel().validate(null);
			};
		};
	}
}
