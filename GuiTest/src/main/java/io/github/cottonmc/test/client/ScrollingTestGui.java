package io.github.cottonmc.test.client;

import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WBox;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WLabeledSlider;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;

public class ScrollingTestGui extends LightweightGuiDescription {
	public ScrollingTestGui() {
		WGridPanel root = (WGridPanel) rootPanel;
		WBox box = new WBox(Axis.VERTICAL);

		for (int i = 0; i < 20; i++) {
			box.add(new WLabeledSlider(0, 10, new LiteralText("Slider #" + i)));
		}

		box.add(new WButton(new ItemIcon(Items.APPLE)));

		root.add(new WLabel(new LiteralText("Scrolling test")).setVerticalAlignment(VerticalAlignment.CENTER), 0, 0, 5, 2);
		root.add(new WScrollPanel(box), 0, 2, 5, 3);
		root.validate(this);
	}
}
