package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A builder for widget tooltips.
 *
 * @since 3.0.0
 */
@Environment(EnvType.CLIENT)
public final class TooltipBuilder {
	final List<OrderedText> lines = new ArrayList<>();

	int size() {
		return lines.size();
	}

	/**
	 * Adds the lines to this builder.
	 *
	 * @param lines the lines
	 * @return this builder
	 */
	public TooltipBuilder add(Text... lines) {
		for (Text line : lines) {
			this.lines.add(line.asOrderedText());
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
		Collections.addAll(this.lines, lines);

		return this;
	}
}
