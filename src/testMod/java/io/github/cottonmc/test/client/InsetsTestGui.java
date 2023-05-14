package io.github.cottonmc.test.client;

import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;

public class InsetsTestGui extends LightweightGuiDescription {
	public InsetsTestGui() {
		WGridPanel root = (WGridPanel) rootPanel;

		root.add(new WLabel(Text.literal("Insets demo")), 0, 0);
		root.add(new WButton(Text.literal("Default")).setOnClick(() -> root.setInsets(Insets.ROOT_PANEL)), 0, 1, 2, 1);
		root.add(new WButton(Text.literal("None")).setOnClick(() -> root.setInsets(Insets.NONE)), 2, 1, 2, 1);
		root.add(new WButton(Text.literal("Large")).setOnClick(() -> root.setInsets(new Insets(16))), 4, 1, 2, 1);
	}
}
