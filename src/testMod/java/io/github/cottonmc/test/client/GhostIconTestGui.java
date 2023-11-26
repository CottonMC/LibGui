package io.github.cottonmc.test.client;

import net.minecraft.item.Items;
import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;

public class GhostIconTestGui extends LightweightGuiDescription {
	public GhostIconTestGui() {
		WGridPanel root = (WGridPanel) rootPanel;
		root.setGaps(2, 2);

		ItemIcon icon = new ItemIcon(Items.CACTUS);
		WButton button = new WButton(icon, Text.literal("Hello world"));
		WToggleButton ghostToggle = new WToggleButton(Text.literal("Ghost"));
		ghostToggle.setOnToggle(icon::setGhost);

		root.add(button, 0, 0, 5, 1);
		root.add(ghostToggle, 0, 1, 5, 1);
		root.validate(this);
	}
}
