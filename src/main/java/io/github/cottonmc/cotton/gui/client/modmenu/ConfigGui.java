package io.github.cottonmc.cotton.gui.client.modmenu;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import net.minecraft.text.TranslatableText;

public class ConfigGui extends LightweightGuiDescription {

	public ConfigGui() {
		WGridPanel root = new WGridPanel();
		setRootPanel(root);
		
		WToggleButton darkmodeButton = new WToggleButton(new TranslatableText("option.libgui.darkmode")) {
			@Override
			public void onToggle(boolean on) {
				LibGuiClient.config.darkMode = on;
				LibGuiClient.saveConfig(LibGuiClient.config);
			}
		};
		darkmodeButton.setToggle(LibGuiClient.config.darkMode);
		root.add(darkmodeButton, 0, 2, 8, 1); //Why isn't it toggling wider?
		
		root.setBackgroundPainter(BackgroundPainter.VANILLA);
		root.setSize(9*18, 6*18);
	}
}
