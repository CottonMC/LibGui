package io.github.cottonmc.test.client;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;

import net.minecraft.text.Text;

public class PaddingTestGui extends LightweightGuiDescription {
	public PaddingTestGui(int hori, int vert) {
		var root = new WGridPanel();
		root.setGaps(hori, vert);
		setRootPanel(root);
		root.setInsets(Insets.ROOT_PANEL);

		addBox(root, 0, 0, 2, 1);
		addBox(root, 0, 1, 1, 2);
		addBox(root, 1, 1, 1, 1);
		addBox(root, 1, 2, 1, 1);

		root.validate(this);
	}

	void addBox(WGridPanel root, int x, int y, int w, int h) {
		root.add(new TestClientGui.WColorBox(0xffff0000), x, y, w, h);
		var l = new WLabel(Text.literal(w + "x" + h), 0xff00ffff);
		l.setVerticalAlignment(VerticalAlignment.CENTER);
		l.setHorizontalAlignment(HorizontalAlignment.CENTER);
		root.add(l, x, y, w, h);
	}
}
