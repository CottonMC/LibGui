package io.github.cottonmc.cotton.gui.client;

import net.minecraft.screen.PropertyDelegate;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;

import javax.annotation.Nullable;

/**
 * A GuiDescription without any associated Minecraft classes
 */
public class LightweightGuiDescription implements GuiDescription {
	protected WPanel rootPanel = new WGridPanel();
	protected int titleColor = WLabel.DEFAULT_TEXT_COLOR;
	protected int darkmodeTitleColor = WLabel.DEFAULT_DARKMODE_TEXT_COLOR;
	protected boolean fullscreen = false;
	protected PropertyDelegate propertyDelegate;
	protected WWidget focus;
	
	@Override
	public WPanel getRootPanel() {
		return rootPanel;
	}

	@Override
	public int getTitleColor() {
		return (LibGuiClient.config.darkMode) ? darkmodeTitleColor : titleColor;
	}

	@Override
	public GuiDescription setRootPanel(WPanel panel) {
		this.rootPanel = panel;
		return this;
	}

	@Override
	public GuiDescription setTitleColor(int color) {
		this.titleColor = color;
		return this;
	}

	@Override
	public void addPainters() {
		if (this.rootPanel!=null && !fullscreen) {
			this.rootPanel.setBackgroundPainter(BackgroundPainter.VANILLA);
		}
	}

	@Override
	public void addSlotPeer(ValidatedSlot slot) {
		//NO-OP
	}

	@Override
	@Nullable
	public PropertyDelegate getPropertyDelegate() {
		return propertyDelegate;
	}

	@Override
	public GuiDescription setPropertyDelegate(PropertyDelegate delegate) {
		this.propertyDelegate = delegate;
		return this;
	}

	@Override
	public boolean isFocused(WWidget widget) {
		return widget == focus;
	}

	@Override
	public WWidget getFocus() {
		return focus;
	}

	@Override
	public void requestFocus(WWidget widget) {
		//TODO: Are there circumstances where focus can't be stolen?
		if (focus==widget) return; //Nothing happens if we're already focused
		if (!widget.canFocus()) return; //This is kind of a gotcha but needs to happen
		if (focus!=null) focus.onFocusLost();
		focus = widget;
		focus.onFocusGained();
	}

	@Override
	public void releaseFocus(WWidget widget) {
		if (focus==widget) {
			focus = null;
			widget.onFocusLost();
		}
	}

	@Override
	public boolean isFullscreen() {
		return fullscreen;
	}

	@Override
	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}
}
