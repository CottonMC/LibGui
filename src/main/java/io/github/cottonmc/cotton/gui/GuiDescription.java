package io.github.cottonmc.cotton.gui;

import javax.annotation.Nullable;

import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.screen.PropertyDelegate;

/**
 * A GUI description represents a GUI without depending on screens.
 *
 * <p>GUI descriptions contain the root panel and the property delegate of the GUI.
 * They also manage the focused widget.
 *
 * @see io.github.cottonmc.cotton.gui.client.LightweightGuiDescription
 * @see CottonInventoryController
 */
public interface GuiDescription {
	public WPanel getRootPanel();
	public int getTitleColor();
	
	public GuiDescription setRootPanel(WPanel panel);
	public GuiDescription setTitleColor(int color);
	
	/** Sets the object which manages the integer properties used by WBars */
	public GuiDescription setPropertyDelegate(PropertyDelegate delegate);
	
	/** Typical users won't call this. This adds a Slot to Container/Controller-based guis, and does nothing on lightweight guis. */
	public void addSlotPeer(ValidatedSlot slot);
	
	/** Guis should use this method to add clientside styles and BackgroundPainters to their controls */
	@Environment(EnvType.CLIENT)
	public void addPainters();
	
	/** Gets the object which manages the integer properties used by WBars and such. */
	@Nullable
	public PropertyDelegate getPropertyDelegate();
	
	/** Tests whether the widget is the currently-focused one. */
	public boolean isFocused(WWidget widget);
	
	/** Gets the currently-focused WWidget. May be null. */
	@Nullable
	public WWidget getFocus();
	
	/** Notifies this gui that the widget waants to acquire focus. */
	public void requestFocus(WWidget widget);
	
	/** Notifies this gui that the widget wants to give up its hold over focus. */
	public void releaseFocus(WWidget widget);
	
}
