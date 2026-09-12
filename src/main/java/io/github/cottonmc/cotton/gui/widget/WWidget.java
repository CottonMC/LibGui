package io.github.cottonmc.cotton.gui.widget;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.LibGui;
import io.github.cottonmc.cotton.gui.impl.VisualLogger;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.data.ObservableProperty;
import io.github.cottonmc.cotton.gui.widget.focus.FocusModel;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

/**
 * The base class for all widgets.
 *
 * @properties
 */
public class WWidget {
	private static final VisualLogger LOGGER = new VisualLogger(WWidget.class);

	/**
	 * The containing panel of this widget.
	 * Can be null if this widget is the root panel or a HUD widget.
	 */
	protected @Nullable WPanel parent;

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
	 * Can be null if this widget is a {@linkplain io.github.cottonmc.cotton.gui.client.WidgetHudElement HUD} widget.
	 */
	protected @Nullable GuiDescription host;

	private final ObservableProperty<Boolean> hovered = ObservableProperty.of(false).nonnull().name("WWidget.hovered").build();

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
	public @Nullable WPanel getParent() {
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
	 * @param click   the click data whose coordinates are in widget space ((0, 0) is the top-left point of this widget)
	 * @param doubled {@code true} if this is a mouse down event of a double click, {@code false} otherwise
	 * @return {@link InputResult#PROCESSED} if the event is handled, {@link InputResult#IGNORED} otherwise.
	 */
	@Environment(EnvType.CLIENT)
	public InputResult onMouseDown(MouseButtonEvent click, boolean doubled) {
		return InputResult.IGNORED;
	}

	/**
	 * Notifies this widget that the mouse has been moved while pressed and inside its bounds.
	 *
	 * @param click   the click data whose coordinates are in widget space ((0, 0) is the top-left point of this widget)
	 * @param offsetX the amount of dragging on the X axis
	 * @param offsetY the amount of dragging on the Y axis
	 * @return {@link InputResult#PROCESSED} if the event is handled, {@link InputResult#IGNORED} otherwise.
	 * @since 1.5.0
	 */
	@Environment(EnvType.CLIENT)
	public InputResult onMouseDrag(MouseButtonEvent click, double offsetX, double offsetY) {
		return InputResult.IGNORED;
	}

	/**
	 * Notifies this widget that the mouse has been released while inside its bounds
	 *
	 * @param click the click data whose coordinates are in widget space ((0, 0) is the top-left point of this widget)
	 * @return {@link InputResult#PROCESSED} if the event is handled, {@link InputResult#IGNORED} otherwise.
	 */
	@Environment(EnvType.CLIENT)
	public InputResult onMouseUp(MouseButtonEvent click) {
		return InputResult.IGNORED;
	}

	/**
	 * Notifies this widget that the mouse has been pressed and released, both while inside its bounds.
	 *
	 * @param click   the click data whose coordinates are in widget space ((0, 0) is the top-left point of this widget)
	 * @param doubled {@code true} if this is a double click, {@code false} otherwise
	 * @return {@link InputResult#PROCESSED} if the event is handled, {@link InputResult#IGNORED} otherwise.
	 */
	@Environment(EnvType.CLIENT)
	public InputResult onClick(MouseButtonEvent click, boolean doubled) {
		return InputResult.IGNORED;
	}

	/**
	 * Notifies this widget that the mouse has been scrolled inside its bounds.
	 *
	 * @param x                The X coordinate of the event, in widget-space (0 is the left edge of this widget)
	 * @param y                The Y coordinate of the event, in widget-space (0 is the top edge of this widget)
	 * @param horizontalAmount The scrolled horizontal amount. Positive values are right and negative values are left.
	 * @param verticalAmount   The scrolled vertical amount. Positive values are up and negative values are down.
	 * @return {@link InputResult#PROCESSED} if the event is handled, {@link InputResult#IGNORED} otherwise.
	 */
	@Environment(EnvType.CLIENT)
	public InputResult onMouseScroll(int x, int y, double horizontalAmount, double verticalAmount) {
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
	 * @param input the character typed
	 * @return {@link InputResult#PROCESSED} if the event is handled, {@link InputResult#IGNORED} otherwise.
	 */
	@Environment(EnvType.CLIENT)
	public InputResult onCharTyped(CharacterEvent input) {
		return InputResult.IGNORED;
	}

	/**
	 * Notifies this widget that a key has been pressed.
	 * @param input the key input
	 * @return {@link InputResult#PROCESSED} if the event is handled, {@link InputResult#IGNORED} otherwise.
	 */
	@Environment(EnvType.CLIENT)
	public InputResult onKeyPressed(KeyEvent input) {
		return InputResult.IGNORED;
	}

	/**
	 * Notifies this widget that a key has been released
	 * @param input the key input
	 * @return {@link InputResult#PROCESSED} if the event is handled, {@link InputResult#IGNORED} otherwise.
	 */
	@Environment(EnvType.CLIENT)
	public InputResult onKeyReleased(KeyEvent input) {
		return InputResult.IGNORED;
	}

	/**
	 * Notifies this widget the preedit overlay has been updated.
	 *
	 * @param event the preedit event
	 * @return {@link InputResult#PROCESSED} if the event was handled, {@link InputResult#IGNORED} otherwise.
	 * @since 16.0.0
	 */
	@Environment(EnvType.CLIENT)
	public InputResult onPreeditUpdated(@Nullable PreeditEvent event) {
		return InputResult.IGNORED;
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
	 * Tests whether this widget can have the focus in the GUI for text input.
	 *
	 * <p>Text input widgets <strong>must</strong> override this to provide proper input method editor support.
	 *
	 * @return true if this widget can be focused for text input, false otherwise
	 */
	public boolean canFocusForTextInput() {
		return false;
	}

	/**
	 * Paints this widget.
	 *
	 * @param context the draw context
	 * @param x       this widget's X coordinate on the screen
	 * @param y       this widget's Y coordinate on the screen
	 * @param mouseX  the X coordinate of the cursor
	 * @param mouseY  the X coordinate of the cursor
	 * @since 2.0.0
	 */
	@Environment(EnvType.CLIENT)
	public void paint(GuiGraphicsExtractor context, int x, int y, int mouseX, int mouseY) {
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
	 * Internal method to render tooltip data. This requires an overridden {@link #addTooltip(TooltipBuilder)
	 * addTooltip} method to insert data into the tooltip - without this, the method returns early because of no work.
	 *
	 * @param context the draw context
	 * @param x       the X coordinate of this widget on screen
	 * @param y       the Y coordinate of this widget on screen
	 * @param tX      the X coordinate of the tooltip
	 * @param tY      the Y coordinate of the tooltip
	 */
	@Environment(EnvType.CLIENT)
	public void renderTooltip(GuiGraphicsExtractor context, int x, int y, int tX, int tY) {
		TooltipBuilder builder = new TooltipBuilder();
		addTooltip(builder);

		if (builder.size() == 0) return;

		var client = Minecraft.getInstance();
		context.setTooltipForNextFrame(client.font, builder.lines, DefaultTooltipPositioner.INSTANCE, tX + x, tY + y, false);
	}

	/**
	 * Creates component peers and initializes animation data for this Widget and all its children.
	 * The host {@linkplain net.minecraft.world.inventory.AbstractContainerMenu screen handler} must clear any heavyweight peers
	 * from its records before this method is called.
	 *
	 * <p>This method must be called on the root panel of any screen once the widgets have been initialized.
	 *
	 * @param host the host GUI description
	 */
	public void validate(GuiDescription host) {
		if (host != null) {
			this.host = host;
		} else {
			LOGGER.warn("Validating {} with a null host", this);
		}
	}

	/**
	 * Gets the host of this widget.
	 *
	 * @return the host
	 * @see #host
	 * @since 2.1.0
	 */
	public final @Nullable GuiDescription getHost() {
		return host;
	}

	/**
	 * Sets the host of this widget and all its children without creating peers.
	 *
	 * @param host the new host
	 * @see #host
	 * @since 2.1.0
	 */
	public void setHost(@Nullable GuiDescription host) {
		if (host != null) {
			this.host = host;
		} else {
			LOGGER.warn("Setting null host for {}", this);
		}
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
	 * Returns the focus model of this widget. The focus
	 * model provides the focusable areas of this widget,
	 * and handles switching through them.
	 *
	 * <p>If this widget {@linkplain #canFocus() can focus}, it should return
	 * a nonnull focus model. The default implementation returns
	 * {@link FocusModel#simple FocusModel.simple(this)} when the widget can be focused.
	 *
	 * @return the focus model, or {@code null} if not available
	 * @since 7.0.0
	 */
	public @Nullable FocusModel<?> getFocusModel() {
		return canFocus() ? FocusModel.simple(this) : null;
	}

	/**
	 * Notifies this widget that it is visible and
	 * shows any hidden peers of itself and its children.
	 *
	 * @since 3.0.0
	 */
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
	 * Tests whether this widget receives {@linkplain #hoveredProperty() mouse hovering status}.
	 *
	 * @return true if this widget receives hovering status, false otherwise
	 * @since 4.2.0
	 */
	public boolean canHover() {
		return true;
	}

	/**
	 * Returns whether the user is hovering over this widget.
	 * The result is an <em>observable property</em> that can be modified and listened to.
	 *
	 * <p>This property takes into account {@link #isWithinBounds(int, int)} to check
	 * if the cursor is within the bounds, as well as {@link #canHover()} to enable hovering at all.
	 *
	 * <p>Hovering is used by LibGui itself mostly for narration support.
	 * For rendering, it might be preferable that you check the mouse coordinates in
	 * {@link #paint(GuiGraphicsExtractor, int, int, int, int) paint()} directly.
	 * That lets you react to different parts of the widget being hovered over.
	 *
	 * @return the {@code hovered} property
	 * @since 4.2.0
	 * @see #canHover()
	 * @see #isHovered()
	 * @see #setHovered(boolean)
	 */
	public ObservableProperty<Boolean> hoveredProperty() {
		return hovered;
	}

	/**
	 * Returns whether the user is hovering over this widget.
	 * This is equivalent to calling <code>{@link #hoveredProperty()}.get()</code>.
	 *
	 * @return true if this widget is hovered, false otherwise
	 * @since 4.2.0
	 */
	public final boolean isHovered() {
		return hoveredProperty().get();
	}

	/**
	 * Sets the {@link #hoveredProperty() hovered} property.
	 * This is equivalent to calling <code>{@link #hoveredProperty()}.set(<i>hovered</i>)</code>.
	 *
	 * @param hovered the new value; true if hovered, false otherwise
	 * @since 4.2.0
	 */
	public final void setHovered(boolean hovered) {
		hoveredProperty().set(hovered);
	}

	/**
	 * {@return whether this widget can be narrated}
	 *
	 * @see #addNarrations(NarrationElementOutput)
	 * @since 4.2.0
	 */
	public boolean isNarratable() {
		return true;
	}

	/**
	 * Adds the narrations of this widget to a narration builder.
	 * Narrations will only apply if this widget {@linkplain #isNarratable() is narratable}.
	 *
	 * <p>The widget needs to be {@linkplain #canFocus() focusable} or {@linkplain #canHover() hoverable},
	 * and also be focused/hovered for narrations to be added.
	 *
	 * @param builder the narration builder, cannot be null
	 * @since 4.2.0
	 */
	@Environment(EnvType.CLIENT)
	public void addNarrations(NarrationElementOutput builder) {
	}

	/**
	 * Tests if the provided key code is an activation key for widgets.
	 *
	 * <p>The activation keys are Enter, keypad Enter, and Space.
	 *
	 * @param ch the SDL scancode of the key, e.g. {@link KeyEvent#input()}
	 * @return whether the key is an activation key
	 * @since 2.0.0
	 * @deprecated Use {@link net.minecraft.client.input.InputWithModifiers#isSelection()} instead.
	 */
	@Environment(EnvType.CLIENT)
	@Deprecated(forRemoval = true)
	@ApiStatus.ScheduledForRemoval(inVersion = "19.0.0")
	public static boolean isActivationKey(int ch) {
		return switch (ch) {
			case InputConstants.KEY_SPACE, InputConstants.KEY_RETURN, InputConstants.KEY_NUMPADENTER -> true;
			default -> false;
		};
	}

	/**
	 * Checks if this widget should be rendered in dark mode.
	 *
	 * <p>If the widget has a host that {@linkplain GuiDescription#isDarkMode() forces dark mode},
	 * the forced value is used. Otherwise, this method returns {@link LibGui#isDarkMode()}.
	 *
	 * <p>{@linkplain #paint Painting} should respect this value for general-purpose widgets
	 * intended for use in multiple different GUIs.
	 *
	 * @return {@code true} if this widget should be rendered in dark mode, {@code false} otherwise
	 * @since 7.1.0
	 */
	@Environment(EnvType.CLIENT)
	public boolean shouldRenderInDarkMode() {
		var globalDarkMode = LibGui.isDarkMode();

		if (host != null) {
			return host.isDarkMode().orElse(globalDarkMode);
		}

		return globalDarkMode;
	}
}
