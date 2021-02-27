package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LibGui;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

// TODO: Different tab positions

/**
 * A panel that contains creative inventory-style tabs on the top.
 *
 * @since 3.0.0
 */
public class WTabPanel extends WPanel {
	private static final int TAB_PADDING = 4;
	private static final int TAB_WIDTH = 28;
	private static final int TAB_HEIGHT = 30;
	private static final int PANEL_PADDING = 8; // The padding of BackgroundPainter.VANILLA
	private static final int ICON_SIZE = 16;
	private final WBox tabRibbon = new WBox(Axis.HORIZONTAL).setSpacing(1);
	private final List<WTab> tabWidgets = new ArrayList<>();
	private final WCardPanel mainPanel = new WCardPanel();

	/**
	 * Constructs a new tab panel.
	 */
	public WTabPanel() {
		add(tabRibbon, 0, 0);
		add(mainPanel, PANEL_PADDING, TAB_HEIGHT + PANEL_PADDING);
	}

	private void add(WWidget widget, int x, int y) {
		children.add(widget);
		widget.setParent(this);
		widget.setLocation(x, y);
		expandToFit(widget);
	}

	/**
	 * Adds a tab to this panel.
	 *
	 * @param tab the added tab
	 */
	public void add(Tab tab) {
		WTab tabWidget = new WTab(tab);

		if (tabWidgets.isEmpty()) {
			tabWidget.selected = true;
		}

		tabWidgets.add(tabWidget);
		tabRibbon.add(tabWidget, TAB_WIDTH, TAB_HEIGHT + TAB_PADDING);
		mainPanel.add(tab.getWidget());
	}

	/**
	 * Configures and adds a tab to this panel.
	 *
	 * @param widget       the contained widget
	 * @param configurator the tab configurator
	 */
	public void add(WWidget widget, Consumer<Tab.Builder> configurator) {
		Tab.Builder builder = new Tab.Builder(widget);
		configurator.accept(builder);
		add(builder.build());
	}

	@Override
	public void setSize(int x, int y) {
		super.setSize(x, y);
		tabRibbon.setSize(x, TAB_HEIGHT);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void addPainters() {
		super.addPainters();
		mainPanel.setBackgroundPainter(BackgroundPainter.VANILLA);
	}

	/**
	 * The data of a tab.
	 */
	public static class Tab {
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
		 * @deprecated Use {@link Builder} instead.
		 */
		@Deprecated
		public Tab(@Nullable Text title, @Nullable Icon icon, WWidget widget, @Nullable Consumer<TooltipBuilder> tooltip) {
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
							builder.add(Tab.Builder.this.tooltip.toArray(new Text[0]));
						}
					};
				}

				return new Tab(title, icon, widget, tooltip);
			}
		}
	}

	private final class WTab extends WWidget {
		private final Tab data;
		boolean selected = false;

		WTab(Tab data) {
			this.data = data;
		}

		@Override
		public boolean canFocus() {
			return true;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public InputResult onClick(int x, int y, int button) {
			super.onClick(x, y, button);

			MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

			for (WTab tab : tabWidgets) {
				tab.selected = (tab == this);
			}

			mainPanel.setSelectedCard(data.getWidget());
			WTabPanel.this.layout();
			return InputResult.PROCESSED;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void onKeyPressed(int ch, int key, int modifiers) {
			if (isActivationKey(ch)) {
				onClick(0, 0, 0);
			}
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
			TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
			Text title = data.getTitle();
			Icon icon = data.getIcon();

			if (title != null) {
				int width = TAB_WIDTH + renderer.getWidth(title);
				if (icon == null) width = Math.max(TAB_WIDTH, width - ICON_SIZE);

				if (this.width != width) {
					setSize(width, this.height);
					getParent().layout();
				}
			}

			(selected ? Painters.SELECTED_TAB : Painters.UNSELECTED_TAB).paintBackground(matrices, x, y, this);
			if (isFocused()) {
				(selected ? Painters.SELECTED_TAB_FOCUS_BORDER : Painters.UNSELECTED_TAB_FOCUS_BORDER).paintBackground(matrices, x, y, this);
			}

			int iconX = 6;

			if (title != null) {
				int titleX = (icon != null) ? iconX + ICON_SIZE + 1 : 0;
				int titleY = (height - TAB_PADDING - renderer.fontHeight) / 2 + 1;
				int width = (icon != null) ? this.width - iconX - ICON_SIZE : this.width;
				HorizontalAlignment align = (icon != null) ? HorizontalAlignment.LEFT : HorizontalAlignment.CENTER;

				int color;
				if (LibGui.isDarkMode()) {
					color = WLabel.DEFAULT_DARKMODE_TEXT_COLOR;
				} else {
					color = selected ? WLabel.DEFAULT_TEXT_COLOR : 0xEEEEEE;
				}

				ScreenDrawing.drawString(matrices, title.asOrderedText(), align, x + titleX, y + titleY, width, color);
			}

			if (icon != null) {
				icon.paint(matrices, x + iconX, y + (height - TAB_PADDING - ICON_SIZE) / 2, ICON_SIZE);
			}
		}

		@Override
		public void addTooltip(TooltipBuilder tooltip) {
			data.addTooltip(tooltip);
		}
	}

	/**
	 * Internal background painter instances for tabs.
	 */
	@Environment(EnvType.CLIENT)
	final static class Painters {
		static final BackgroundPainter SELECTED_TAB = BackgroundPainter.createLightDarkVariants(
				BackgroundPainter.createNinePatch(new Identifier(LibGuiCommon.MOD_ID, "textures/widget/tab/selected_light.png")).setTopPadding(2),
				BackgroundPainter.createNinePatch(new Identifier(LibGuiCommon.MOD_ID, "textures/widget/tab/selected_dark.png")).setTopPadding(2)
		);

		static final BackgroundPainter UNSELECTED_TAB = BackgroundPainter.createLightDarkVariants(
				BackgroundPainter.createNinePatch(new Identifier(LibGuiCommon.MOD_ID, "textures/widget/tab/unselected_light.png")),
				BackgroundPainter.createNinePatch(new Identifier(LibGuiCommon.MOD_ID, "textures/widget/tab/unselected_dark.png"))
		);

		static final BackgroundPainter SELECTED_TAB_FOCUS_BORDER = BackgroundPainter.createNinePatch(new Identifier(LibGuiCommon.MOD_ID, "textures/widget/tab/focus.png")).setTopPadding(2);
		static final BackgroundPainter UNSELECTED_TAB_FOCUS_BORDER = BackgroundPainter.createNinePatch(new Identifier(LibGuiCommon.MOD_ID, "textures/widget/tab/focus.png"));
	}
}
