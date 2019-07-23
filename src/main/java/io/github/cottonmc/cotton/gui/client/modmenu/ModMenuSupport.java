package io.github.cottonmc.cotton.gui.client.modmenu;

import java.util.function.Function;

import io.github.cottonmc.cotton.gui.client.ClientCottonScreen;
import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

public class ModMenuSupport implements ModMenuApi {
	
	@Override
	public String getModId() {
		return LibGuiClient.MODID;
	}
	
	@Override
	public Function<Screen, ? extends Screen> getConfigScreenFactory() {
		return screen -> new ClientCottonScreen(new TranslatableText("options.libgui.libgui_settings"), new ConfigGui(screen)) {
			public void onClose() {
				this.minecraft.openScreen(screen);
			}
			
			protected void init() {
				super.init();
				this.description.getRootPanel().validate(null);
			};
		};
	}
}
