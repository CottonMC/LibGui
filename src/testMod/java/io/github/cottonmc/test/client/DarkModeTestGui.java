package io.github.cottonmc.test.client;

import io.github.cottonmc.cotton.gui.widget.WToggleButton;

import io.github.cottonmc.cotton.gui.widget.data.Insets;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WLabeledSlider;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.WText;
import io.github.cottonmc.cotton.gui.widget.data.Axis;

public final class DarkModeTestGui extends LightweightGuiDescription {
	private boolean darkMode = false;

	public DarkModeTestGui() {
		var root = new WGridPanel(20);
		root.setInsets(Insets.ROOT_PANEL);
		root.setGaps(3, 3);

		root.add(new WButton(Text.literal("Button A")), 0, 0, 3, 1);
		root.add(new WButton(Text.literal("Button B")), 0, 1, 3, 1);
		root.add(new WText(Text.literal("Text")), 0, 2, 3, 1);
		root.add(new WLabeledSlider(1, 100, Text.literal("Slider A")), 3, 0, 3, 1);
		root.add(new WSlider(1, 100, Axis.HORIZONTAL), 3, 1, 3, 1);
		root.add(new WLabel(Text.literal("Label")), 3, 2, 3, 1);
		root.add(new WScrollBar(Axis.HORIZONTAL), 0, 3, 3, 1);
		root.add(new WDynamicLabel(() -> "Dynamic label: " + (darkMode ? "dark mode" : "light mode")),
				3, 3, 3, 1);
		var toggle = new WToggleButton(Text.literal("Toggle button"));
		toggle.setOnToggle(on -> darkMode = on);
		root.add(toggle, 0, 4, 6, 1);

		root.validate(this);
		setRootPanel(root);
	}

	@Override
	public TriState isDarkMode() {
		return TriState.of(darkMode);
	}
}
