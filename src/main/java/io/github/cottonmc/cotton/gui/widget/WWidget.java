package io.github.cottonmc.cotton.gui.widget;

import java.util.ArrayList;
import java.util.List;

import io.github.cottonmc.cotton.gui.GuiDescription;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import javax.annotation.Nullable;

/**
 * The base class for all widgets.
 */
public class WWidget {
	/**
	 * The containing panel of this widget.
	 * Can be null if this widget is the root panel or a HUD widget.
	 */
	@Nullable
	protected WPanel parent;
	protected int x = 0;
	protected int y = 0;
	protected int width = 18;
	protected int height = 18;

	/**
	 * The containing {@link GuiDescription} of this widget.
	 * Can be null if this widget is a {@linkplain io.github.cottonmc.cotton.gui.client.CottonHud HUD} widget.
	 */
	@Nullable
	protected GuiDescription host;

	/**
	 * Sets the location of this widget relative to its parent.
	 *
	 * @param x the new X coordinate
	 * @param y the new Y coordinate
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets the size of this widget.
	 *
	 * <p>Overriding methods may restrict one of the dimensions to be
	 * a constant value, for example {@code super.setSize(x, 20)}.
	 *
	 * @param x the new width
	 * @param y the new height
	 */
	public void setSize(int x, int y) {
		this.width = x;
		this.height = y;
	}

	/**
	 * Gets the X coordinate of this widget relative to its parent.
	 *
	 * @return the X coordinate
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the Y coordinate of this widget relative to its parent.
	 *
	 * @return the Y coordinate
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the absolute X coordinate of this widget.
	 *
	 * @return the absolute X coordinate
	 */
	public int getAbsoluteX() {
		if (parent==null) {
			return getX();
		} else {
			return getX() + parent.getAbsoluteX();
		}
	}

	/**
	 * Gets the absolute Y coordinate of this widget.
	 *
	 * @return the absolute Y coordinate
	 */
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

	/**
	 * Checks whether this widget can be resized using {@link #setSize}.
	 *
	 * @return true if this widget can be resized, false otherwise
	 */
	public boolean canResize() {
		return false;
	}

	/**
	 * Sets the parent panel of this widget.
	 *
	 * @param parent the new parent
	 */
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
	 * <p>The default implementation calls {@link #onMouseDrag(int, int, int)} for backwards compatibility.
	 *
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param button The mouse button that was used. Button numbering is consistent with LWJGL Mouse (0=left, 1=right, 2=mousewheel click)
	 * @param deltaX The amount of dragging on the X axis
	 * @param deltaY The amount of dragging on the Y axis
	 *
	 * @since 1.5.0
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

	/**
	 * Checks whether a location is within this widget's bounds.
	 *
	 * <p>The default implementation checks that X and Y are at least 0 and below the width and height of this widget.
	 *
	 * @param x the X coordinate
	 * @param y the Y coordinate
	 * @return true if the location is within this widget, false otherwise
	 */
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

	/**
	 * Executes a client-side tick for this widget.
	 */
	@Environment(EnvType.CLIENT)
	public void tick() {}
}
