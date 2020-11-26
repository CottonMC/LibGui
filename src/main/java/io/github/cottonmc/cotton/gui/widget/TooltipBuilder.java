package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * A builder for widget tooltips.
 *
 * @since 3.0.0
 */
@Environment(EnvType.CLIENT)
public final class TooltipBuilder {
	final List<TooltipComponent> components = new ArrayList<>();

	int size() {
		return components.size();
	}

	/**
	 * Adds the lines to this builder.
	 *
	 * @param lines the lines
	 * @return this builder
	 */
	public TooltipBuilder add(Text... lines) {
		for (Text line : lines) {
			components.add(TooltipComponent.createOrderedTextTooltipComponent(line.asOrderedText()));
		}

		return this;
	}

	/**
	 * Adds the lines to this builder.
	 *
	 * @param lines the lines
	 * @return this builder
	 */
	public TooltipBuilder add(OrderedText... lines) {
		for (OrderedText line : lines) {
			components.add(TooltipComponent.createOrderedTextTooltipComponent(line));
		}

		return this;
	}
}
