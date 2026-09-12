package io.github.cottonmc.cotton.gui.client;

import com.mojang.datafixers.util.Unit;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.PreeditEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.impl.ScreenNetworkingImpl;
import io.github.cottonmc.cotton.gui.impl.VisualLogger;
import io.github.cottonmc.cotton.gui.impl.client.CottonScreenImpl;
import io.github.cottonmc.cotton.gui.impl.client.FocusElements;
import io.github.cottonmc.cotton.gui.impl.client.MouseInputHandler;
import io.github.cottonmc.cotton.gui.impl.client.NarrationHelper;
import io.github.cottonmc.cotton.gui.impl.mixin.client.AbstractContainerScreenAccessor;
import io.github.cottonmc.cotton.gui.impl.mixin.client.ScreenAccessor;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

/**
 * A screen for a {@link SyncedGuiDescription}.
 *
 * @param <T> the description type
 */
public class CottonInventoryScreen<T extends SyncedGuiDescription> extends AbstractContainerScreen<T> implements CottonScreenImpl {
	protected final SyncedGuiDescription description;
	protected @Nullable WWidget lastResponder = null;
	private final MouseInputHandler<CottonInventoryScreen<T>> mouseInputHandler = new MouseInputHandler<>(this);

	/**
	 * Constructs a new screen without a title.
	 *
	 * @param description the GUI description
	 * @param inventory   the player inventory
	 * @since 5.2.0
	 */
	public CottonInventoryScreen(T description, Inventory inventory) {
		this(description, inventory, CommonComponents.EMPTY);
	}

	/**
	 * Constructs a new screen.
	 *
	 * @param description the GUI description
	 * @param inventory   the player inventory
	 * @param title       the screen title
	 * @since 5.2.0
	 */
	public CottonInventoryScreen(T description, Inventory inventory, Component title) {
		super(description, inventory, title);
		this.description = description;
		width = 18*9;
		height = 18*9;
		((AbstractContainerScreenAccessor) this).setImageWidth(18 * 9);
		((AbstractContainerScreenAccessor) this).setImageHeight(18 * 9);
		description.getRootPanel().validate(description);
		description.addFocusChangeListener((from, to) -> {
			boolean fromTextInput = from != null && from.canFocusForTextInput();
			boolean toTextInput = to != null && to.canFocusForTextInput();

			if (from != to && (fromTextInput || toTextInput)) {
				minecraft.onTextInputFocusChange(this, toTextInput);
			}
		});
	}

	/**
	 * Constructs a new screen without a title.
	 *
	 * @param description the GUI description
	 * @param player     the player
	 */
	public CottonInventoryScreen(T description, Player player) {
		this(description, player.getInventory());
	}

	/**
	 * Constructs a new screen.
	 *
	 * @param description the GUI description
	 * @param player      the player
	 * @param title       the screen title
	 */
	public CottonInventoryScreen(T description, Player player, Component title) {
		this(description, player.getInventory(), title);
	}
	
	/*
	 * RENDERING NOTES:
	 * 
	 * * "width" and "height" are the width and height of the overall screen
	 * * "backgroundWidth" and "backgroundHeight" are the width and height of the panel to render
	 * * ~~"left" and "top" are *actually* self-explanatory~~
	 *   * "left" and "top" are now (1.15) "x" and "y". A bit less self-explanatory, I guess.
	 * * coordinates start at 0,0 at the topleft of the screen.
	 */

	@Override
	public void init() {
		super.init();

		WPanel root = description.getRootPanel();
		root.addPainters();
		description.addPainters();
		
		reposition(width, height);

		GuiEventListener rootPanelElement = FocusElements.ofPanel(root);
		((ScreenAccessor) this).libgui$getChildren().add(rootPanelElement);
		setInitialFocus(rootPanelElement);
	}

	@Override
	public void removed() {
		super.removed();
		VisualLogger.reset();
	}

	@ApiStatus.Internal
	@Override
	public GuiDescription getDescription() {
		return description;
	}

	@Override
	public @Nullable WWidget getLastResponder() {
		return lastResponder;
	}

	@Override
	public void setLastResponder(@Nullable WWidget lastResponder) {
		this.lastResponder = lastResponder;
	}

	/**
	 * Clears the heavyweight peers of this screen's GUI description.
	 */
	private void clearPeers() {
		description.slots.clear();
	}

	/**
	 * Repositions the root panel.
	 *
	 * @param screenWidth  the width of the screen
	 * @param screenHeight the height of the screen
	 */
	protected void reposition(int screenWidth, int screenHeight) {
		WPanel basePanel = description.getRootPanel();
		clearPeers();
		basePanel.validate(description);

		((AbstractContainerScreenAccessor) this).setImageWidth(basePanel.getWidth());
		((AbstractContainerScreenAccessor) this).setImageHeight(basePanel.getHeight());

		titleLabelX = description.getTitlePos().x();
		titleLabelY = description.getTitlePos().y();

		if (!description.isFullscreen()) {
			leftPos = (screenWidth / 2) - (imageWidth / 2);
			topPos = (screenHeight / 2) - (imageHeight / 2);
		} else {
			leftPos = 0;
			topPos = 0;

			basePanel.setSize(screenWidth, screenHeight);
		}
	}
	
	@Override
	public boolean isPauseScreen() {
		//...yeah, we're going to go ahead and override that.
		return false;
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
		super.mouseClicked(click, doubled);

		int containerX = (int) click.x() - leftPos;
		int containerY = (int) click.y() - topPos;
		mouseInputHandler.checkFocus(containerX, containerY);
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return true;
		mouseInputHandler.onMouseDown(containerX, containerY, click, doubled);

		return true;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent click) {
		super.mouseReleased(click);

		int containerX = (int) click.x() - leftPos;
		int containerY = (int) click.y() - topPos;
		mouseInputHandler.onMouseUp(containerX, containerY, click);

		return true;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent click, double offsetX, double offsetY) {
		super.mouseDragged(click, offsetX, offsetY);

		int containerX = (int) click.x() - leftPos;
		int containerY = (int) click.y() - topPos;
		mouseInputHandler.onMouseDrag(containerX, containerY, click, offsetX, offsetY);

		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);

		int containerX = (int)mouseX- leftPos;
		int containerY = (int)mouseY- topPos;
		mouseInputHandler.onMouseScroll(containerX, containerY, horizontalAmount, verticalAmount);

		return true;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		super.mouseMoved(mouseX, mouseY);

		int containerX = (int)mouseX- leftPos;
		int containerY = (int)mouseY- topPos;
		mouseInputHandler.onMouseMove(containerX, containerY);
	}

	@Override
	public boolean charTyped(CharacterEvent input) {
		WWidget focus = description.getFocus();
		if (focus != null && focus.onCharTyped(input) == InputResult.PROCESSED) {
			return true;
		}

		return super.charTyped(input);
	}

	@Override
	public boolean keyPressed(KeyEvent input) {
		WWidget focus = description.getFocus();
		if (focus != null && focus.onKeyPressed(input) == InputResult.PROCESSED) {
			return true;
		}

		return super.keyPressed(input);
	}

	@Override
	public boolean keyReleased(KeyEvent input) {
		WWidget focus = description.getFocus();
		if (focus != null && focus.onKeyReleased(input) == InputResult.PROCESSED) {
			return true;
		}

		return super.keyReleased(input);
	}

	@Override
	public boolean preeditUpdated(@Nullable PreeditEvent event) {
		WWidget focus = description.getFocus();
		if (focus != null && focus.onPreeditUpdated(event) == InputResult.PROCESSED) {
			return true;
		}

		return super.preeditUpdated(event);
	}

	/**
	 * Paints the GUI description of this screen.
	 *
	 * @param context the draw context
	 * @param mouseX  the absolute X coordinate of the mouse cursor
	 * @param mouseY  the absolute Y coordinate of the mouse cursor
	 * @param delta   the tick delta
	 * @since 9.2.0
	 */
	public void paintDescription(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
		WPanel root = description.getRootPanel();
		root.paint(context, leftPos, topPos, mouseX- leftPos, mouseY- topPos);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);
		VisualLogger.render(graphics);
	}

	@Override
	protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		super.extractTooltip(graphics, mouseX, mouseY);
		WPanel root = description.getRootPanel();
		WWidget hitChild = root.hit(mouseX- leftPos, mouseY- topPos);
		if (hitChild!=null) hitChild.renderTooltip(graphics, leftPos, topPos, mouseX- leftPos, mouseY- topPos);
	}

	@Override
	protected void extractLabels(GuiGraphicsExtractor context, int mouseX, int mouseY) {
		if (description.isTitleVisible()) {
			int width = description.getRootPanel().getWidth();
			ScreenDrawing.drawString(context, getTitle().getVisualOrderText(), description.getTitleAlignment(), titleLabelX, titleLabelY, width - 2 * titleLabelX, description.getTitleColor());
		}

		// Don't draw the player inventory label as it's drawn by the widget itself
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		WPanel root = description.getRootPanel();
		root.tick();

		description.sendDataSlotUpdates();
	}

	@Override
	protected void updateNarratedWidget(NarrationElementOutput builder) {
		NarrationHelper.addNarrations(description.getRootPanel(), builder);
	}

	@Override
	public void added() {
		ScreenNetworking networking = description.getNetworking(NetworkSide.CLIENT);
		((ScreenNetworkingImpl) networking).markReady();
		networking.send(ScreenNetworkingImpl.CLIENT_READY_MESSAGE_KEY, Unit.INSTANCE);
	}
}
