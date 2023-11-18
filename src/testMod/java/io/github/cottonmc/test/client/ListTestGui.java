package io.github.cottonmc.test.client;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WLabeledSlider;
import io.github.cottonmc.cotton.gui.widget.WListPanel;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ListTestGui extends LightweightGuiDescription {
	private static final String[] LOREM = {
			"Eius",
			"architecto",
			"dolores",
			"in",
			"delectus",
			"omnis",
			"Exercitationem",
			"fugit",
			"dolorem",
			"sapiente",
			"impedit",
			"Occaecati",
			"consequatur",
			"omnis",
			"nam",
			"eveniet",
			"eius",
			"Eos",
			"quasi",
			"numquam",
			"placeat",
			"eaque",
			"sapiente",
			"Dolorum",
			"magnam",
			"eius",
			"labore",
			"voluptatem",
			"est",
			"voluptatem",
			"aut",
			"qui"
	};

	public ListTestGui() {
		WGridPanel root = (WGridPanel) rootPanel;

		List<Formatting> formattings = Arrays.stream(Formatting.values())
				.filter(Formatting::isColor)
				.toList();
		Random random = new Random();
		List<Text> data = Arrays.stream(LOREM)
				.<Text>map(s -> {
					Formatting formatting = formattings.get(random.nextInt(formattings.size()));
					return Text.literal(s).formatted(formatting, Formatting.BOLD);
				})
				.toList();

		WListPanel<Text, WLorem> listPanel = new WListPanel<>(data, WLorem::new, (text, widget) -> widget.label.setText(text));
		WLabeledSlider topSlider = new WLabeledSlider(0, 16, Axis.HORIZONTAL, Text.literal("Top insets"));
		WLabeledSlider bottomSlider = new WLabeledSlider(0, 16, Axis.HORIZONTAL, Text.literal("Bottom insets"));
		WLabeledSlider leftSlider = new WLabeledSlider(0, 16, Axis.HORIZONTAL, Text.literal("Left insets"));
		WLabeledSlider rightSlider = new WLabeledSlider(0, 16, Axis.HORIZONTAL, Text.literal("Right insets"));
		WLabeledSlider gapSlider = new WLabeledSlider(0, 16, Axis.VERTICAL, Text.literal("Gap"));

		topSlider.setValue(listPanel.getInsets().top());
		topSlider.setValueChangeListener(top -> {
			Insets insets = listPanel.getInsets();
			Insets newInsets = new Insets(top, insets.left(), insets.bottom(), insets.right());
			listPanel.setInsets(newInsets);
			listPanel.layout();
		});

		bottomSlider.setValue(listPanel.getInsets().bottom());
		bottomSlider.setValueChangeListener(bottom -> {
			Insets insets = listPanel.getInsets();
			Insets newInsets = new Insets(insets.top(), insets.left(), bottom, insets.right());
			listPanel.setInsets(newInsets);
			listPanel.layout();
		});

		leftSlider.setValue(listPanel.getInsets().left());
		leftSlider.setValueChangeListener(left -> {
			Insets insets = listPanel.getInsets();
			Insets newInsets = new Insets(insets.top(), left, insets.bottom(), insets.right());
			listPanel.setInsets(newInsets);
			listPanel.layout();
		});

		rightSlider.setValue(listPanel.getInsets().right());
		rightSlider.setValueChangeListener(right -> {
			Insets insets = listPanel.getInsets();
			Insets newInsets = new Insets(insets.top(), insets.left(), insets.bottom(), right);
			listPanel.setInsets(newInsets);
			listPanel.layout();
		});

		gapSlider.setValue(listPanel.getGap());
		gapSlider.setValueChangeListener(gap -> {
			listPanel.setGap(gap);
			listPanel.layout();
		});

		root.setGaps(2, 2);
		root.add(topSlider, 0, 0, 3, 1);
		root.add(bottomSlider, 3, 0, 3, 1);
		root.add(leftSlider, 0, 1, 3, 1);
		root.add(rightSlider, 3, 1, 3, 1);
		root.add(gapSlider, 6, 0, 1, 2);
		root.add(listPanel, 0, 2, 6, 6);
		root.validate(this);
	}

	private static class WLorem extends WGridPanel {
		private final WLabel label = new WLabel(Text.empty());

		private WLorem() {
			setInsets(Insets.ROOT_PANEL);
			add(label, 0, 0);
		}

		@Override
		public void addPainters() {
			setBackgroundPainter(BackgroundPainter.VANILLA);
		}
	}
}
