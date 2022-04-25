package io.github.cottonmc.cotton.gui.impl.modmenu;

import net.minecraft.text.Text;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;

public class ModMenuSupport implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> new CottonClientScreen(Text.translatable("options.libgui.libgui_settings"), new ConfigGui(screen)) {
			@Override
			public void close() {
				this.client.setScreen(screen);
			}
		};
	}
}
