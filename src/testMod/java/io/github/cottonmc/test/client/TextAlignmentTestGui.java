package io.github.cottonmc.test.client;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WLabeledSlider;
import io.github.cottonmc.cotton.gui.widget.WTabPanel;
import io.github.cottonmc.cotton.gui.widget.WText;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public final class TextAlignmentTestGui extends LightweightGuiDescription {
	public TextAlignmentTestGui() {
		WTabPanel tabPanel = new WTabPanel();

		WGridPanel labelPanel = new WGridPanel();
		labelPanel.setInsets(Insets.ROOT_PANEL);
		labelPanel.setGaps(0, 1);
		Text labelStyled = Text.literal("world")
				.styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("test"))));
		Text labelText = Text.literal("hello, ").append(labelStyled);
		WLabel label = new WLabel(labelText);
		WLabeledSlider labelSliderH = forEnum(HorizontalAlignment.class, label::setHorizontalAlignment);
		WLabeledSlider labelSliderV = forEnum(VerticalAlignment.class, label::setVerticalAlignment);
		labelPanel.add(label, 0, 0, 5, 3);
		labelPanel.add(labelSliderH, 0, 3, 5, 1);
		labelPanel.add(labelSliderV, 0, 4, 5, 1);

		WGridPanel textPanel = new WGridPanel();
		textPanel.setInsets(Insets.ROOT_PANEL);
		textPanel.setGaps(0, 1);
		Text textText = IntStream.rangeClosed(1, 3)
				.mapToObj(line -> {
					Text textStyled = Text.literal("world").styled(style -> style
							.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("test")))
							.withColor(Formatting.values()[line + 9])
					);
					return Text.literal("hell" + "o".repeat(line * 3) + ", ").append(textStyled).append("\n");
				})
				.reduce(Text.empty(), MutableText::append);
		WText text = new WText(textText);
		WLabeledSlider textSliderH = forEnum(HorizontalAlignment.class, text::setHorizontalAlignment);
		WLabeledSlider textSliderV = forEnum(VerticalAlignment.class, text::setVerticalAlignment);
		textPanel.add(text, 0, 0, 5, 4);
		textPanel.add(textSliderH, 0, 4, 5, 1);
		textPanel.add(textSliderV, 0, 5, 5, 1);

		tabPanel.add(labelPanel, builder -> builder.title(Text.of("WLabel")));
		tabPanel.add(textPanel, builder -> builder.title(Text.of("WText")));
		setRootPanel(tabPanel);
		tabPanel.validate(this);
	}

	private static <E extends Enum<E>> WLabeledSlider forEnum(Class<E> type, Consumer<E> consumer) {
		E[] values = type.getEnumConstants();
		var slider = new WLabeledSlider(1, values.length);
		slider.setLabel(Text.of(type.getSimpleName() + ": " + values[0]));
		slider.setLabelUpdater(value -> Text.of(type.getSimpleName() + ": " + values[value - 1]));
		slider.setValueChangeListener(value -> consumer.accept(values[value - 1]));
		return slider;
	}

	@Override
	public void addPainters() {
	}
}
