package io.github.cottonmc.cotton.gui.widget.data;

import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * The data of a tab.
 *
 * @see io.github.cottonmc.cotton.gui.widget.WTabPanel
 */
public final class Tab {
	@Nullable
	private final Text title;
	@Nullable
	private final Icon icon;
	private final WWidget widget;
	@Nullable
	private final Consumer<TooltipBuilder> tooltip;

	/**
	 * Constructs a tab.
	 *
	 * @param title   the tab title
	 * @param icon    the tab icon
	 * @param widget  the widget contained in the tab
	 * @param tooltip the tab tooltip
	 * @throws IllegalArgumentException if both the title and the icon are null
	 * @throws NullPointerException     if the widget is null
	 */
	private Tab(@Nullable Text title, @Nullable Icon icon, WWidget widget, @Nullable Consumer<TooltipBuilder> tooltip) {
		if (title == null && icon == null) {
			throw new IllegalArgumentException("A tab must have a title or an icon");
		}

		this.title = title;
		this.icon = icon;
		this.widget = Objects.requireNonNull(widget, "widget");
		this.tooltip = tooltip;
	}

	/**
	 * Gets the title of this tab.
	 *
	 * @return the title, or null if there's no title
	 */
	@Nullable
	public Text getTitle() {
		return title;
	}

	/**
	 * Gets the icon of this tab.
	 *
	 * @return the icon, or null if there's no title
	 */
	@Nullable
	public Icon getIcon() {
		return icon;
	}

	/**
	 * Gets the contained widget of this tab.
	 *
	 * @return the contained widget
	 */
	public WWidget getWidget() {
		return widget;
	}

	/**
	 * Adds this widget's tooltip to the {@code tooltip} builder.
	 *
	 * @param tooltip the tooltip builder
	 */
	@Environment(EnvType.CLIENT)
	public void addTooltip(TooltipBuilder tooltip) {
		if (this.tooltip != null) {
			this.tooltip.accept(tooltip);
		}
	}

	/**
	 * A builder for tab data.
	 */
	public static final class Builder {
		@Nullable
		private Text title;
		@Nullable
		private Icon icon;
		private final WWidget widget;
		private final List<Text> tooltip = new ArrayList<>();

		/**
		 * Constructs a new tab data builder.
		 *
		 * @param widget the contained widget
		 * @throws NullPointerException if the widget is null
		 */
		public Builder(WWidget widget) {
			this.widget = Objects.requireNonNull(widget, "widget");
		}

		/**
		 * Sets the tab title.
		 *
		 * @param title the new title
		 * @return this builder
		 * @throws NullPointerException if the title is null
		 */
		public Builder title(Text title) {
			this.title = Objects.requireNonNull(title, "title");
			return this;
		}

		/**
		 * Sets the tab icon.
		 *
		 * @param icon the new icon
		 * @return this builder
		 * @throws NullPointerException if the icon is null
		 */
		public Builder icon(Icon icon) {
			this.icon = Objects.requireNonNull(icon, "icon");
			return this;
		}

		/**
		 * Adds lines to the tab's tooltip.
		 *
		 * @param lines the added lines
		 * @return this builder
		 * @throws NullPointerException if the line array is null
		 */
		public Builder tooltip(Text... lines) {
			Objects.requireNonNull(lines, "lines");
			Collections.addAll(tooltip, lines);

			return this;
		}

		/**
		 * Adds lines to the tab's tooltip.
		 *
		 * @param lines the added lines
		 * @return this builder
		 * @throws NullPointerException if the line collection is null
		 */
		public Builder tooltip(Collection<? extends Text> lines) {
			Objects.requireNonNull(lines, "lines");
			tooltip.addAll(lines);
			return this;
		}

		/**
		 * Builds a tab from this builder.
		 *
		 * @return the built tab
		 */
		public Tab build() {
			Consumer<TooltipBuilder> tooltip = null;

			if (!this.tooltip.isEmpty()) {
				//noinspection Convert2Lambda
				tooltip = new Consumer<TooltipBuilder>() {
					@Environment(EnvType.CLIENT)
					@Override
					public void accept(TooltipBuilder builder) {
						builder.add(Builder.this.tooltip.toArray(new Text[0]));
					}
				};
			}

			return new Tab(title, icon, widget, tooltip);
		}
	}
}
