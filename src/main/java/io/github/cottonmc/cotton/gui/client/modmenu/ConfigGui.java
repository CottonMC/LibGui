package io.github.cottonmc.cotton.gui.client.modmenu;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import net.minecraft.util.Identifier;

public class ConfigGui extends LightweightGuiDescription {

	public ConfigGui() {
		WGridPanel root = new WGridPanel();
		setRootPanel(root);
		
		root.add(new WSprite(new Identifier("libgui:icon.png")), 0, 0, 2, 2);
		root.setBackgroundPainter(BackgroundPainter.VANILLA);
		root.setSize(64, 64);
	}
}
