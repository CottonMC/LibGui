package io.github.cottonmc.test.client;

import io.github.cottonmc.cotton.gui.widget.WLabeledSlider;

import net.minecraft.text.LiteralText;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WBox;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.data.Axis;

public class ScrollingTestGui extends LightweightGuiDescription {
	public ScrollingTestGui() {
		WGridPanel root = (WGridPanel) rootPanel;
		WBox box = new WBox(Axis.VERTICAL);

		for (int i = 0; i < 20; i++) {
			box.add(new WLabeledSlider(0, 10, new LiteralText("Slider #" + i)));
		}

		root.add(new WScrollPanel(box), 0, 0, 5, 3);
		root.validate(this);
	}
}
