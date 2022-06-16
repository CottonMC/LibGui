package io.github.cottonmc.cotton.gui.impl.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public final class NarrationHelper {
	public static void addNarrations(WPanel rootPanel, NarrationMessageBuilder builder) {
		List<WWidget> narratableWidgets = getAllWidgets(rootPanel)
			.filter(WWidget::isNarratable)
			.collect(Collectors.toList());

		for (int i = 0, childCount = narratableWidgets.size(); i < childCount; i++) {
			WWidget child = narratableWidgets.get(i);
			if (!child.isFocused() && !child.isHovered()) continue;

			// replicates Screen.addElementNarrations
			if (narratableWidgets.size() > 1) {
				builder.put(NarrationPart.POSITION, Text.translatable(NarrationMessages.Vanilla.SCREEN_POSITION_KEY, i + 1, childCount));

				if (child.isFocused()) {
					builder.put(NarrationPart.USAGE, NarrationMessages.Vanilla.COMPONENT_LIST_USAGE);
				}
			}

			child.addNarrations(builder.nextMessage());
		}
	}

	private static Stream<WWidget> getAllWidgets(WPanel panel) {
		return Stream.concat(Stream.of(panel), panel.streamChildren().flatMap(widget -> {
			if (widget instanceof WPanel nested) {
				return getAllWidgets(nested);
			}

			return Stream.of(widget);
		}));
	}
}
