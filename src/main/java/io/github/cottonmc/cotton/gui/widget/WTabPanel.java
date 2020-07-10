package io.github.cottonmc.cotton.gui.widget;

import com.google.common.collect.ImmutableList;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Padding;
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

public class WTabPanel extends WPanel {
	private static final Padding DEFAULT_PADDING = new Padding(0);
	private static final Padding PANEL_PADDING = new Padding(8);
	private final WBox tabRibbon = new WBox(Axis.HORIZONTAL).setSpacing(0);
	private final List<WTab> tabWidgets = new ArrayList<>();
	private final WCardPanel mainPanel = new WCardPanel();

	public WTabPanel() {
		add(tabRibbon, 0, 0);
		add(mainPanel, 0, 30);
	}

	private void add(WWidget widget, int x, int y) {
		children.add(widget);
		widget.setParent(this);
		widget.setLocation(x, y);
		expandToFit(widget);
	}

	public void add(Tab tab) {
		WTab tabWidget = new WTab(tab);

		if (tabWidgets.isEmpty()) {
			tabWidget.selected = true;
		}

		tabWidgets.add(tabWidget);
		tabRibbon.add(tabWidget, 28, 30 + 2);
		mainPanel.add(tab.getWidget());
	}

	protected Padding getPadding(WWidget widget) {
		return widget instanceof WPanel ? PANEL_PADDING : DEFAULT_PADDING;
	}

	@Override
	public void setSize(int x, int y) {
		super.setSize(x, y);
		tabRibbon.setSize(x, 30);
	}

	@Override
	public void layout() {
		super.layout();
		Padding padding = getPadding(mainPanel.getSelectedCard());
		mainPanel.setLocation(padding.left, 30 + padding.top);
		mainPanel.setSize(x - padding.left - padding.right, y - 30 - padding.top - padding.bottom);
	}

	public static class Tab {
		private final Icon icon;
		private final WWidget widget;
		private final List<StringRenderable> tooltip;

		public Tab(Icon icon, WWidget widget) {
			this(icon, widget, ImmutableList.of());
		}

		public Tab(Icon icon, WWidget widget, StringRenderable tooltip) {
			this(icon, widget, ImmutableList.of(Objects.requireNonNull(tooltip, "tooltip")));
		}

		public Tab(Icon icon, WWidget widget, List<StringRenderable> tooltip) {
			this.icon = Objects.requireNonNull(icon, "icon");
			this.widget = Objects.requireNonNull(widget, "widget");
			this.tooltip = ImmutableList.copyOf(Objects.requireNonNull(tooltip, "tooltip"));
		}

		public Icon getIcon() {
			return icon;
		}

		public WWidget getWidget() {
			return widget;
		}

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

			data.getIcon().paint(matrices, x + (width - 16) / 2, y + (height - 16) / 2, 16);
		}

		@Override
		public void addTooltip(List<StringRenderable> tooltip) {
			data.addTooltip(tooltip);
		}
	}
}
