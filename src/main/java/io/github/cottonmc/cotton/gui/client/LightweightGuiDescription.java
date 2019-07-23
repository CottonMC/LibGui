package io.github.cottonmc.cotton.gui.client;

import javax.annotation.Nullable;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import net.minecraft.container.PropertyDelegate;

/**
 * A GuiDescription without any associated Minecraft classes
 */
public class LightweightGuiDescription implements GuiDescription {
	protected WPanel rootPanel = new WGridPanel();
	protected int titleColor = WLabel.DEFAULT_TEXT_COLOR;
	protected int darkmodeTitleColor = WLabel.DEFAULT_DARKMODE_TEXT_COLOR;
	protected PropertyDelegate propertyDelegate;
	
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
		if (this.rootPanel!=null) {
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

}
