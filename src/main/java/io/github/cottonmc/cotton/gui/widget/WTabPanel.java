package io.github.cottonmc.cotton.gui.widget;

import com.google.common.collect.ImmutableList;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.StringRenderable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A panel that contains creative inventory-style tabs on the top.
 *
 * @since 2.3.0
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
	 * Adds a {@linkplain WBox#addFiller() filler} to the tab ribbon.
	 */
	public void addTabRibbonFiller() {
		tabRibbon.addFiller();
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
		private final Icon icon;
		private final WWidget widget;
		private final List<StringRenderable> tooltip;

		/**
		 * Constructs a tab with no tooltip.
		 *
		 * @param icon   the tab icon
		 * @param widget the widget contained in the tab
		 * @throws NullPointerException if any parameter is null
		 */
		public Tab(Icon icon, WWidget widget) {
			this(icon, widget, ImmutableList.of());
		}

		/**
		 * Constructs a tab.
		 *
		 * @param icon    the tab icon
		 * @param widget  the widget contained in the tab
		 * @param tooltip the tab tooltip
		 * @throws NullPointerException if any parameter is null
		 */
		public Tab(Icon icon, WWidget widget, StringRenderable tooltip) {
			this(icon, widget, ImmutableList.of(Objects.requireNonNull(tooltip, "tooltip")));
		}

		/**
		 * Constructs a tab.
		 *
		 * @param icon    the tab icon
		 * @param widget  the widget contained in the tab
		 * @param tooltip the tab tooltip lines
		 * @throws NullPointerException if any parameter is null
		 */
		public Tab(Icon icon, WWidget widget, List<StringRenderable> tooltip) {
			this.icon = Objects.requireNonNull(icon, "icon");
			this.widget = Objects.requireNonNull(widget, "widget");
			this.tooltip = ImmutableList.copyOf(Objects.requireNonNull(tooltip, "tooltip"));
		}

		/**
		 * Gets the icon of this tab.
		 *
		 * @return the icon
		 */
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
		 * Adds this widget's tooltip to the {@code tooltip} list.
		 *
		 * @param tooltip the tooltip line list
		 */
		public void addTooltip(List<StringRenderable> tooltip) {
			tooltip.addAll(this.tooltip);
		}
	}

	private final class WTab extends WWidget {
		private final Tab data;
		boolean selected = false;

		public WTab(Tab data) {
			this.data = data;
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void onClick(int x, int y, int button) {
			super.onClick(x, y, button);

			MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

			for (WTab tab : tabWidgets) {
				tab.selected = (tab == this);
			}

			mainPanel.setSelectedCard(data.getWidget());
			WTabPanel.this.layout();
		}

		@Environment(EnvType.CLIENT)
		@Override
		public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
			if (selected) {
				BackgroundPainter.SELECTED_TAB.paintBackground(x, y, this);
			} else {
				BackgroundPainter.UNSELECTED_TAB.paintBackground(x, y, this);
			}

			data.getIcon().paint(matrices, x + (width - ICON_SIZE) / 2, y + (height - TAB_PADDING - ICON_SIZE) / 2, ICON_SIZE);
		}

		@Override
		public void addTooltip(List<StringRenderable> tooltip) {
			data.addTooltip(tooltip);
		}
	}
}
