package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

import com.google.common.annotations.Beta;
import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

/**
 * The base class for all widgets.
 */
public class WWidget {
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * The containing panel of this widget.
	 * Can be null if this widget is the root panel or a HUD widget.
	 */
	@Nullable
	protected WPanel parent;

	/** The X coordinate of this widget relative to its parent. */
	protected int x = 0;
	/** The Y coordinate of this widget relative to its parent. */
	protected int y = 0;
	/** The width of this widget, defaults to 18 pixels. */
	protected int width = 18;
	/** The height of this widget, defaults to 18 pixels. */
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
	 * Gets the parent panel of this widget.
	 *
	 * @return the parent, or null if this widget has no parent
	 * @since 2.0.0
	 */
	@Nullable
	public WPanel getParent() {
		return parent;
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
	 *
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param button The mouse button that was used. Button numbering is consistent with LWJGL Mouse (0=left, 1=right, 2=mousewheel click)
	 * @return {@link InputResult#PROCESSED} if the event is handled, {@link InputResult#IGNORED} otherwise.
	 */
	@Environment(EnvType.CLIENT)
	public InputResult onMouseDown(int x, int y, int button) {
		return InputResult.IGNORED;
	}

	/**
	 * Notifies this widget that the mouse has been moved while pressed and inside its bounds.
	 *
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param button The mouse button that was used. Button numbering is consistent with LWJGL Mouse (0=left, 1=right, 2=mousewheel click)
	 * @param deltaX The amount of dragging on the X axis
	 * @param deltaY The amount of dragging on the Y axis
	 * @return {@link InputResult#PROCESSED} if the event is handled, {@link InputResult#IGNORED} otherwise.
	 * @since 1.5.0
	 */
	@Environment(EnvType.CLIENT)
	public InputResult onMouseDrag(int x, int y, int button, double deltaX, double deltaY) {
		return InputResult.IGNORED;
	}

	/**
	 * Notifies this widget that the mouse has been released while inside its bounds
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param button The mouse button that was used. Button numbering is consistent with LWJGL Mouse (0=left, 1=right, 2=mousewheel click)
	 * @return {@link InputResult#PROCESSED} if the event is handled, {@link InputResult#IGNORED} otherwise.
	 */
	@Environment(EnvType.CLIENT)
	public InputResult onMouseUp(int x, int y, int button) {
		return InputResult.IGNORED;
	}

	/**
	 * Notifies this widget that the mouse has been pressed and released, both while inside its bounds.
	 *
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param button The mouse button that was used. Button numbering is consistent with LWJGL Mouse (0=left, 1=right, 2=mousewheel click)
	 * @return {@link InputResult#PROCESSED} if the event is handled, {@link InputResult#IGNORED} otherwise.
	 */
	@Environment(EnvType.CLIENT)
	public InputResult onClick(int x, int y, int button) {
		return InputResult.IGNORED;
	}

	/**
	 * Notifies this widget that the mouse has been scrolled inside its bounds.
	 *
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param amount The scrolled amount. Positive values are up and negative values are down.
	 * @return {@link InputResult#PROCESSED} if the event is handled, {@link InputResult#IGNORED} otherwise.
	 */
	@Environment(EnvType.CLIENT)
	public InputResult onMouseScroll(int x, int y, double amount) {
		return InputResult.IGNORED;
	}

	/**
	 * Notifies this widget that the mouse has been moved while inside its bounds.
	 *
	 * @param x The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @return {@link InputResult#PROCESSED} if the event is handled, {@link InputResult#IGNORED} otherwise.
	 * @since 1.5.0
	 */
	@Environment(EnvType.CLIENT)
	public InputResult onMouseMove(int x, int y) {
		return InputResult.IGNORED;
	}

	/**
	 * Notifies this widget that a character has been typed. This method is subject to key repeat,
	 * and may be called for characters that do not directly have a corresponding keyboard key.
	 * @param ch the character typed
	 */
	@Environment(EnvType.CLIENT)
	public void onCharTyped(char ch) {
	}

	/**
	 * Notifies this widget that a key has been pressed.
	 * @param key the GLFW scancode of the key
	 */
	@Environment(EnvType.CLIENT)
	public void onKeyPressed(int ch, int key, int modifiers) {
	}

	/**
	 * Notifies this widget that a key has been released
	 * @param key the GLFW scancode of the key
	 */
	@Environment(EnvType.CLIENT)
	public void onKeyReleased(int ch, int key, int modifiers) {
	}

	/** Notifies this widget that it has gained focus */
	public void onFocusGained() {
	}

	/** Notifies this widget that it has lost focus */
	public void onFocusLost() {
	}

	/**
	 * Tests whether this widget has focus.
	 *
	 * @return true if this widget widget has focus, false otherwise
	 * @see GuiDescription#isFocused(WWidget)
	 */
	public boolean isFocused() {
		if (host==null) return false;
		return host.isFocused(this);
	}

	/**
	 * If this widget has a host, requests the focus from the host.
	 *
	 * @see GuiDescription#requestFocus(WWidget)
	 */
	public void requestFocus() {
		if (host!=null) {
			host.requestFocus(this);
		} else {
			LOGGER.warn("Requesting focus for {}, but the host is null", this);
		}
	}

	/**
	 * If this widget has a host, releases this widget's focus.
	 *
	 * @see GuiDescription#releaseFocus(WWidget)
	 */
	public void releaseFocus() {
		if (host!=null) host.releaseFocus(this);
	}

	/**
	 * Tests whether this widget can have the focus in the GUI.
	 *
	 * @return true if this widget can be focused, false otherwise
	 */
	public boolean canFocus() {
		return false;
	}

	/**
	 * Creates "heavyweight" component peers
	 * @param c the top-level Container that will hold the peers
	 * @deprecated All widget peers should be added in {@link #validate(GuiDescription)}.
	 */
	@Deprecated
	public void createPeers(GuiDescription c) {
	}

	/**
	 * Paints this widget.
	 *
	 * @param matrices the rendering matrix stack
	 * @param x        this widget's X coordinate on the screen
	 * @param y        this widget's Y coordinate on the screen
	 * @param mouseX   the X coordinate of the cursor
	 * @param mouseY   the X coordinate of the cursor
	 * @since 2.0.0
	 */
	@Environment(EnvType.CLIENT)
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
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
	 * Internal method to render tooltip data. This requires an overriden {@link #addTooltip(TooltipBuilder)
	 * addTooltip} method to insert data into the tooltip - without this, the method returns early, because no work
	 *
	 * @param x  the X coordinate of this widget on screen
	 * @param y  the Y coordinate of this widget on screen
	 * @param tX the X coordinate of the tooltip
	 * @param tY the Y coordinate of the tooltip
	 */
	@Environment(EnvType.CLIENT)
	public void renderTooltip(MatrixStack matrices, int x, int y, int tX, int tY) {
		TooltipBuilder builder = new TooltipBuilder();
		addTooltip(builder);

		if (builder.size() == 0) return;

		Screen screen = MinecraftClient.getInstance().currentScreen;
		screen.renderOrderedTooltip(matrices, builder.lines, tX+x, tY+y);
	}

	/**
	 * Creates component peers, lays out children, and initializes animation data for this Widget and all its children.
	 * The host container must clear any heavyweight peers from its records before this method is called.
	 *
	 * @param host the host GUI description
	 */
	public void validate(GuiDescription host) {
		if (host != null) {
			this.host = host;
		} else {
			LOGGER.warn("Validating {} with a null host", this);
		}
	}

	/**
	 * Gets the host of this widget.
	 *
	 * @return the host
	 * @see #host
	 * @since 2.1.0
	 */
	@Nullable
	public final GuiDescription getHost() {
		return host;
	}

	/**
	 * Sets the host of this widget without creating peers.
	 *
	 * @param host the new host
	 * @see #host
	 * @since 2.1.0
	 */
	public void setHost(@Nullable GuiDescription host) {
		this.host = host;
	}

	/**
	 * Adds lines to this widget's tooltip. If the lines remain empty after this call, no tooltip will be drawn.
	 *
	 * @param tooltip the builder to add tooltip lines to
	 */
	@Environment(EnvType.CLIENT)
	public void addTooltip(TooltipBuilder tooltip) {
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

	/**
	 * Cycles the focus inside this widget.
	 *
	 * <p>If this widget is not focusable, returns null.
	 *
	 * @param lookForwards whether this should cycle forwards (true) or backwards (false)
	 * @return the next focused widget, or null if should exit to the parent panel
	 * @since 2.0.0
	 */
	@Nullable
	public WWidget cycleFocus(boolean lookForwards) {
		return canFocus() ? (isFocused() ? null : this) : null;
	}

	/**
	 * Notifies this widget that it is visible and
	 * shows any hidden peers of itself and its children.
	 *
	 * @since 3.0.0
	 */
	@Beta
	public void onShown() {
	}

	/**
	 * Notifies this widget that it won't be drawn and
	 * hides any visible peers of itself and its children.
	 *
	 * <p>The default implementation releases this widget's
	 * focus if it is focused. Overriding implementations
	 * might want to do this as well.
	 *
	 * @since 3.0.0
	 */
	@Beta
	public void onHidden() {
		releaseFocus();
	}

	/**
	 * Adds the default background painters to this widget and all children.
	 *
	 * <p>Always called before {@link GuiDescription#addPainters()} to allow users to modify painters.
	 *
	 * @since 3.0.0
	 */
	@Environment(EnvType.CLIENT)
	public void addPainters() {
	}

	/**
	 * Tests if the provided key code is an activation key for widgets.
	 *
	 * <p>The activation keys are Enter, keypad Enter, and Space.
	 *
	 * @param ch the key code
	 * @return whether the key is an activation key
	 * @since 2.0.0
	 */
	@Environment(EnvType.CLIENT)
	public static boolean isActivationKey(int ch) {
		return ch == GLFW.GLFW_KEY_ENTER || ch == GLFW.GLFW_KEY_KP_ENTER || ch == GLFW.GLFW_KEY_SPACE;
	}
}
