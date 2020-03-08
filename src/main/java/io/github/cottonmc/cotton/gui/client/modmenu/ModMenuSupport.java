package io.github.cottonmc.cotton.gui.client.modmenu;

import java.util.function.Function;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
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
