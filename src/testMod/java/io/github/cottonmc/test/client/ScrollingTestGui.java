package io.github.cottonmc.test.client;

import net.minecraft.item.Items;
import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WBox;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WLabeledSlider;
import io.github.cottonmc.cotton.gui.widget.WScrollPanel;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;

public class ScrollingTestGui extends LightweightGuiDescription {
	public ScrollingTestGui() {
		WGridPanel root = (WGridPanel) rootPanel;
		WBox box = new WBox(Axis.VERTICAL);
		WScrollPanel scrollPanel = new WScrollPanel(box);

		for (int i = 0; i < 20; i++) {
			box.add(new WLabeledSlider(0, 10, Text.literal("Slider #" + i)));
		}

		box.add(new WButton(new ItemIcon(Items.APPLE)));

		WLabeledSlider topSlider = new WLabeledSlider(0, 16, Axis.HORIZONTAL, Text.literal("Top insets"));
		WLabeledSlider bottomSlider = new WLabeledSlider(0, 16, Axis.HORIZONTAL, Text.literal("Bottom insets"));
		WLabeledSlider leftSlider = new WLabeledSlider(0, 16, Axis.HORIZONTAL, Text.literal("Left insets"));
		WLabeledSlider rightSlider = new WLabeledSlider(0, 16, Axis.HORIZONTAL, Text.literal("Right insets"));

		topSlider.setValueChangeListener(top -> {
			Insets insets = scrollPanel.getInsets();
			Insets newInsets = new Insets(top, insets.left(), insets.bottom(), insets.right());
			scrollPanel.setInsets(newInsets);
			scrollPanel.layout();
		});

		bottomSlider.setValueChangeListener(bottom -> {
			Insets insets = scrollPanel.getInsets();
			Insets newInsets = new Insets(insets.top(), insets.left(), bottom, insets.right());
			scrollPanel.setInsets(newInsets);
			scrollPanel.layout();
		});

		leftSlider.setValueChangeListener(left -> {
			Insets insets = scrollPanel.getInsets();
			Insets newInsets = new Insets(insets.top(), left, insets.bottom(), insets.right());
			scrollPanel.setInsets(newInsets);
			scrollPanel.layout();
		});

		rightSlider.setValueChangeListener(right -> {
			Insets insets = scrollPanel.getInsets();
			Insets newInsets = new Insets(insets.top(), insets.left(), insets.bottom(), right);
			scrollPanel.setInsets(newInsets);
			scrollPanel.layout();
		});

		root.setGaps(2, 2);
		root.add(new WLabel(Text.literal("Scrolling test")).setVerticalAlignment(VerticalAlignment.CENTER), 0, 0, 6, 2);
		root.add(topSlider, 0, 2, 3, 1);
		root.add(bottomSlider, 3, 2, 3, 1);
		root.add(leftSlider, 0, 3, 3, 1);
		root.add(rightSlider, 3, 3, 3, 1);
		root.add(scrollPanel, 0, 4, 6, 3);
		root.validate(this);
	}
}
