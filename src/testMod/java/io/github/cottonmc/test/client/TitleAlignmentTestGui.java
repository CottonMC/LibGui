package io.github.cottonmc.test.client;

import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabeledSlider;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;

public class TitleAlignmentTestGui extends LightweightGuiDescription {
	private static final HorizontalAlignment[] TITLE_ALIGNMENTS = {
			HorizontalAlignment.LEFT,
			HorizontalAlignment.CENTER,
			HorizontalAlignment.RIGHT
	};
	private HorizontalAlignment titleAlignment = HorizontalAlignment.LEFT;

	public TitleAlignmentTestGui() {
		WLabeledSlider titleSlider = new WLabeledSlider(0, TITLE_ALIGNMENTS.length - 1);
		titleSlider.setLabel(getLabel(HorizontalAlignment.LEFT));
		titleSlider.setLabelUpdater(value -> getLabel(TITLE_ALIGNMENTS[value]));
		titleSlider.setValueChangeListener(value -> titleAlignment = TITLE_ALIGNMENTS[value]);
		((WGridPanel) rootPanel).add(titleSlider, 0, 1, 4, 1);
		rootPanel.validate(this);
	}

	private Text getLabel(HorizontalAlignment alignment) {
		return Text.literal(alignment.name());
	}

	@Override
	public HorizontalAlignment getTitleAlignment() {
		return titleAlignment;
	}
}
