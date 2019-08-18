package io.github.cottonmc.cotton.gui.widget;

import java.util.ArrayList;
import java.util.List;

import io.github.cottonmc.cotton.gui.GuiDescription;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class WWidget {
	protected WPanel parent;
	protected int x = 0;
	protected int y = 0;
	protected int width = 18;
	protected int height = 18;
	protected GuiDescription host;
	
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setSize(int x, int y) {
		this.width = x;
		this.height = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getAbsoluteX() {
		if (parent==null) {
			return getX();
		} else {
			return getX() + parent.getAbsoluteX();
		}
	}
	
	public int getAbsoluteY() {
		if (parent==null) {
			return getY();
		} else {
			return getY() + parent.getAbsoluteY();
		}
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public boolean canResize() {
		return false;
	}
	
	public void setParent(WPanel parent) {
		this.parent = parent;
	}
	
	/**
	 * Draw this Widget at the specified coordinates. The coordinates provided are the top-level device coordinates of
	 * this widget's topleft corner, so don't translate by the widget X/Y! That's already been done. Your "valid"
	 * drawing space is from (x, y) to (x + width - 1, y + height - 1) inclusive. However, no scissor or depth masking
	 * is done, so please take care to respect your boundaries.
	 * @param x The X coordinate of the leftmost pixels of this widget in device (opengl) coordinates
	 * @param y The Y coordinate of the topmost pixels of this widget in device (opengl) coordinates
	 */
	public void paint(int x, int y) {
		
	}
	
	/**
	 * Notifies this widget that the mouse has been pressed while inside its bounds
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param button The mouse button that was used. Button numbering is consistent with LWJGL Mouse (0=left, 1=right, 2=mousewheel click)
	 */
	public WWidget onMouseDown(int x, int y, int button) {
		return this;
	}
	
	/**
	 * Notifies this widget that the mouse has been moved while pressed and inside its bounds
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param button The mouse button that was used. Button numbering is consistent with LWJGL Mouse (0=left, 1=right, 2=mousewheel click)
	 */
	public void onMouseDrag(int x, int y, int button) {
	}
	
	/**
	 * Notifies this widget that the mouse has been released while inside its bounds
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param button The mouse button that was used. Button numbering is consistent with LWJGL Mouse (0=left, 1=right, 2=mousewheel click)
	 */
	public WWidget onMouseUp(int x, int y, int button) {
		return this;
	}
	
	/**
	 * Notifies this widget that the mouse has been pressed and released, both while inside its bounds.
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param button The mouse button that was used. Button numbering is consistent with LWJGL Mouse (0=left, 1=right, 2=mousewheel click)
	 */
	public void onClick(int x, int y, int button) {
	}
	
	/**
	 * Notifies this widget that a character has been typed. This method is subject to key repeat,
	 * and may be called for characters that do not directly have a corresponding keyboard key.
	 * @param ch the character typed
	 */
	public void onCharTyped(char ch) {
	}
	
	/**
	 * Notifies this widget that a key has been pressed.
	 * @param key the GLFW scancode of the key
	 */
	public void onKeyPressed(int ch, int key, int modifiers) {
	}
	
	/**
	 * Notifies this widget that a key has been released
	 * @param key the GLFW scancode of the key
	 */
	public void onKeyReleased(int ch, int key, int modifiers) {
	}
	
	/** Notifies this widget that it has gained focus */
	public void onFocusGained() {
	}
	
	/** Notifies this widget that it has lost focus */
	public void onFocusLost() {
	}
	
	public boolean isFocused() {
		if (host==null) return false;
		return host.isFocused(this);
	}
	
	public void requestFocus() {
		if (host!=null) {
			host.requestFocus(this);
		} else {
			System.out.println("host is null");
		}
	}
	
	public void releaseFocus() {
		if (host!=null) host.releaseFocus(this);
	}
	
	public boolean canFocus() {
		return false;
	}
	
	/**
	 * Creates "heavyweight" component peers
	 * @param c the top-level Container that will hold the peers
	 */
	public void createPeers(GuiDescription c) {
		host=c;
	}
	
	@Environment(EnvType.CLIENT)
	public void paintBackground(int x, int y) {
	}
	
	@Environment(EnvType.CLIENT)
	public void paintForeground(int x, int y, int mouseX, int mouseY) {
		if (mouseX >= x && mouseX < x+getWidth() && mouseY >= y && mouseY < y+getHeight()) {
			renderTooltip(mouseX, mouseY);
		}
	}
	
	/**
	 * Internal method to conditionally render tooltip data. This requires an overriden {@link #addInformation(List)
	 * addInformation} method to insert data into the tooltip - without this, the method returns early, because no work
	 * is needing to be done on an empty list.
	 * @param tX The adjusted X coordinate at which to render the tooltip.
	 * @param tY The adjusted X coordinate at which to render the tooltip.
	 */
	@Environment(EnvType.CLIENT)
	protected void renderTooltip(int tX, int tY) {
		List<String> info = new ArrayList<>();
		addInformation(info);

		if (info.size() == 0)
			return;
		
		Screen screen = MinecraftClient.getInstance().currentScreen;
		screen.renderTooltip(info, tX, tY);
	}
	
	/**
	 * Creates component peers, lays out children, and initializes animation data for this Widget and all its children.
	 * The host container must clear any heavyweight peers from its records before this method is called.
	 */
	public void validate(GuiDescription host) {
		//valid = true;
	}
	
	/**
	 * Adds information to this widget's tooltip. This requires a call to {@link #setRenderTooltip(boolean)
	 * setRenderTooltip} (obviously passing in {@code true}), in order to enable the rendering of your tooltip.
	 * @param information List containing all previous tooltip data.
	 */
	public void addInformation(List<String> information) {
}
}
