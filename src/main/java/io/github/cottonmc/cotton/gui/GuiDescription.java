package io.github.cottonmc.cotton.gui;

import javax.annotation.Nullable;

import io.github.cottonmc.cotton.gui.widget.WPanel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.PropertyDelegate;

public interface GuiDescription {
	public WPanel getRootPanel();
	public int getTitleColor();
	
	public GuiDescription setRootPanel(WPanel panel);
	public GuiDescription setTitleColor(int color);
	public GuiDescription setPropertyDelegate(PropertyDelegate delegate);
	public void addSlotPeer(ValidatedSlot slot);
	
	@Environment(EnvType.CLIENT)
	public void addPainters();
	

	
	
	@Nullable
	public PropertyDelegate getPropertyDelegate();
	
}
