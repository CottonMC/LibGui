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
	 * Notifies this widget that the mouse has been pressed while inside its bounds
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param button The mouse button that was used. Button numbering is consistent with LWJGL Mouse (0=left, 1=right, 2=mousewheel click)
	 */
	public WWidget onMouseDown(int x, int y, int button) {
		return this;
	}
	
	/**
	 * Notifies this widget that the mouse has been moved while pressed and inside its bounds.
	 *
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param button The mouse button that was used. Button numbering is consistent with LWJGL Mouse (0=left, 1=right, 2=mousewheel click)
	 * @param deltaX The amount of dragging on the X axis
	 * @param deltaY The amount of dragging on the Y axis
	 *
	 * @since 1.5.0
	 * @implSpec The default implementation calls {@link #onMouseDrag(int, int, int)}.
	 */
	public void onMouseDrag(int x, int y, int button, double deltaX, double deltaY) {
		onMouseDrag(x, y, button);
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
	 * Notifies this widget that the mouse has been scrolled inside its bounds.
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param amount The scrolled amount. Positive values are up and negative values are down.
	 */
	public void onMouseScroll(int x, int y, double amount) {
	}

	/**
	 * Notifies this widget that the mouse has been moved while inside its bounds.
	 *
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @since 1.5.0
	 */
	public void onMouseMove(int x, int y) {
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
	public void paintBackground(int x, int y, int mouseX, int mouseY) {
		this.paintBackground(x, y);
	}
	
	@Environment(EnvType.CLIENT)
	public void paintBackground(int x, int y) {
	}
	
	@Deprecated
	@Environment(EnvType.CLIENT)
	public void paintForeground(int x, int y, int mouseX, int mouseY) {
		//if (mouseX >= x && mouseX < x+getWidth() && mouseY >= y && mouseY < y+getHeight()) {
		//	renderTooltip(mouseX, mouseY);
		//}
	}
	
	public boolean isWithinBounds(int x, int y) {
		return x>=0 && y>=0 && x<this.width && y<this.height;
	}
	
	/**
	 * Internal method to render tooltip data. This requires an overriden {@link #addInformation(List)
	 * addInformation} method to insert data into the tooltip - without this, the method returns early, because no work
	 */
	@Environment(EnvType.CLIENT)
	public void renderTooltip(int x, int y, int tX, int tY) {
		List<String> info = new ArrayList<>();
		addInformation(info);

		if (info.size() == 0)
			return;
		
		Screen screen = MinecraftClient.getInstance().currentScreen;
		screen.renderTooltip(info, tX+x, tY+y);
	}
	
	/**
	 * Creates component peers, lays out children, and initializes animation data for this Widget and all its children.
	 * The host container must clear any heavyweight peers from its records before this method is called.
	 */
	public void validate(GuiDescription host) {
		//valid = true;
	}
	
	/**
	 * Adds information to this widget's tooltip. If information remains empty after this call, no tooltip will be drawn.
	 * @param information List containing all previous tooltip data.
	 */
	public void addInformation(List<String> information) {
	}
	
	/**
	 * Find the most specific child node at this location. For non-panel widgets, returns this widget.
	 */
	public WWidget hit(int x, int y) {
		return this;
	}
	
	public void tick() {}
}
