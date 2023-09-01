package io.github.cottonmc.test.client;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;

import net.fabricmc.fabric.api.util.TriState;

public class ScrollBarTestGui extends LightweightGuiDescription {
	private boolean darkMode = false;
	public ScrollBarTestGui(){
		WPlainPanel root = new WPlainPanel();
		setRootPanel(root);
		root.setSize(256, 240);
		root.setInsets(Insets.ROOT_PANEL);

		WScrollBar scrollBarTest = new WScrollBar(Axis.HORIZONTAL);
		root.add(scrollBarTest,0,0,256,8);

		WScrollBar scrollBar = new WScrollBar(Axis.HORIZONTAL);
		root.add(scrollBar,0,240 - scrollBar.getHeight(),256,8);

		root.validate(this);
	}

	@Override
	public TriState isDarkMode() {
		return TriState.of(darkMode);
	}

}
