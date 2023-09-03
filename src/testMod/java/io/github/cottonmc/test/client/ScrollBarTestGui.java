package io.github.cottonmc.test.client;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;

import net.fabricmc.fabric.api.util.TriState;

public class ScrollBarTestGui extends LightweightGuiDescription {
	private boolean darkMode = false;

	public ScrollBarTestGui() {
		WPlainPanel root = new WPlainPanel();
		setRootPanel(root);
		root.setSize(256, 240);
		root.setInsets(Insets.ROOT_PANEL);

		WScrollBar scrollBar1 = new WScrollBar(Axis.HORIZONTAL);
		root.add(scrollBar1, 0, 0, 256, 16);

		WScrollBar scrollBar2 = new WScrollBar(Axis.HORIZONTAL);
		root.add(scrollBar2, 0, 240 - scrollBar2.getHeight(), 256, 8);

		WScrollBar scrollBar3 = new WScrollBar(Axis.VERTICAL);
		root.add(scrollBar3, 0, 18, 16, 202);

		WScrollBar scrollBar4 = new WScrollBar(Axis.VERTICAL);
		root.add(scrollBar4, 248, 18, 8, 202);

		WToggleButton toggleButton = new WToggleButton();
		toggleButton.setOnToggle(on -> darkMode = on);
		root.add(toggleButton, 128 - (toggleButton.getWidth() / 2), 120 - (toggleButton.getHeight() / 2));

		root.validate(this);
	}

	@Override
	public TriState isDarkMode() {
		return TriState.of(darkMode);
	}

}
