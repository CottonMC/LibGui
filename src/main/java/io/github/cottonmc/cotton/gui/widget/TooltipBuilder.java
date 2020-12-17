package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
	 * @throws NullPointerException if the lines are null
	 */
	public TooltipBuilder add(Text... lines) {
		Objects.requireNonNull(lines, "lines");
		for (Text line : lines) {
			components.add(TooltipComponent.of(line.asOrderedText()));
		}

		return this;
	}

	/**
	 * Adds the lines to this builder.
	 *
	 * @param lines the lines
	 * @return this builder
	 * @throws NullPointerException if the lines are null
	 */
	public TooltipBuilder add(OrderedText... lines) {
		Objects.requireNonNull(lines, "lines");
		for (OrderedText line : lines) {
			components.add(TooltipComponent.of(line));
		}

		return this;
	}

	/**
	 * Adds the components to this builder.
	 *
	 * @param components the components
	 * @return this builder
	 * @throws NullPointerException if the components are null
	 * @since 4.0.0
	 */
	public TooltipBuilder add(TooltipComponent... components) {
		Objects.requireNonNull(components, "components");
		this.components.addAll(Arrays.asList(components));

		return this;
	}

	/**
	 * Adds a tooltip component created from tooltip data to this builder.
	 *
	 * @param tooltipData the data
	 * @return this builder
	 * @throws NullPointerException if the data is null
	 * @since 4.0.0
	 */
	public TooltipBuilder add(TooltipData tooltipData) {
		Objects.requireNonNull(tooltipData, "tooltipData");
		components.add(TooltipComponent.of(tooltipData));

		return this;
	}

	/**
	 * Adds the widget to this builder.
	 *
	 * <p>Tooltip widgets should usually be cached inside the widget they are created in.
	 *
	 * @param widget the widget
	 * @return this builder
	 * @throws NullPointerException if the widget is null
	 * @since 4.0.0
	 */
	public TooltipBuilder add(WWidget widget) {
		Objects.requireNonNull(widget, "widget");
		components.add(new WidgetTooltipComponent(widget));

		return this;
	}

	/**
	 * Adds the widget to this builder and resizes it if resizeable.
	 *
	 * <p>Tooltip widgets should usually be cached inside the widget they are created in.
	 *
	 * @param widget the widget
	 * @param width  the new width
	 * @param height the new height
	 * @return this builder
	 * @throws NullPointerException if the widget is null
	 * @since 4.0.0
	 */
	public TooltipBuilder add(WWidget widget, int width, int height) {
		Objects.requireNonNull(widget, "widget");
		components.add(new WidgetTooltipComponent(widget));

		if (widget.canResize()) {
			widget.setSize(width, height);
		}

		return this;
	}

	private static class WidgetTooltipComponent implements TooltipComponent {
		private final WWidget widget;

		WidgetTooltipComponent(WWidget widget) {
			this.widget = widget;
		}

		@Override
		public int getWidth(TextRenderer textRenderer) {
			return widget.getWidth();
		}

		@Override
		public int getHeight() {
			return widget.getHeight();
		}

		@Override
		public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrices, ItemRenderer itemRenderer, int z, TextureManager textureManager) {
			widget.paint(matrices, x, y, /* mouse coords: nowhere in sight */ -x, -y);
			widget.tick(); // Screens are ticked every time they're rendered, so why not tooltip widgets?
		}
	}
}
