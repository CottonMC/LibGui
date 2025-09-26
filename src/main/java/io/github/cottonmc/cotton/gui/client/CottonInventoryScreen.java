package io.github.cottonmc.cotton.gui.client;

import com.mojang.datafixers.util.Unit;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.impl.ScreenNetworkingImpl;
import io.github.cottonmc.cotton.gui.impl.VisualLogger;
import io.github.cottonmc.cotton.gui.impl.client.CottonScreenImpl;
import io.github.cottonmc.cotton.gui.impl.client.FocusElements;
import io.github.cottonmc.cotton.gui.impl.client.MouseInputHandler;
import io.github.cottonmc.cotton.gui.impl.client.NarrationHelper;
import io.github.cottonmc.cotton.gui.impl.mixin.client.ScreenAccessor;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenNetworking;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A screen for a {@link SyncedGuiDescription}.
 *
 * @param <T> the description type
 */
public class CottonInventoryScreen<T extends SyncedGuiDescription> extends HandledScreen<T> implements CottonScreenImpl {
	private static final VisualLogger LOGGER = new VisualLogger(CottonInventoryScreen.class);
	protected SyncedGuiDescription description;
	@Nullable protected WWidget lastResponder = null;
	private final MouseInputHandler<CottonInventoryScreen<T>> mouseInputHandler = new MouseInputHandler<>(this);

	/**
	 * Constructs a new screen without a title.
	 *
	 * @param description the GUI description
	 * @param inventory   the player inventory
	 * @since 5.2.0
	 */
	public CottonInventoryScreen(T description, PlayerInventory inventory) {
		this(description, inventory, ScreenTexts.EMPTY);
	}

	/**
	 * Constructs a new screen.
	 *
	 * @param description the GUI description
	 * @param inventory   the player inventory
	 * @param title       the screen title
	 * @since 5.2.0
	 */
	public CottonInventoryScreen(T description, PlayerInventory inventory, Text title) {
		super(description, inventory, title);
		this.description = description;
		width = 18*9;
		height = 18*9;
		this.backgroundWidth = 18*9;
		this.backgroundHeight = 18*9;
		description.getRootPanel().validate(description);
	}

	/**
	 * Constructs a new screen without a title.
	 *
	 * @param description the GUI description
	 * @param player     the player
	 */
	public CottonInventoryScreen(T description, PlayerEntity player) {
		this(description, player.getInventory());
	}

	/**
	 * Constructs a new screen.
	 *
	 * @param description the GUI description
	 * @param player      the player
	 * @param title       the screen title
	 */
	public CottonInventoryScreen(T description, PlayerEntity player, Text title) {
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
		if (root != null) root.addPainters();
		description.addPainters();
		
		reposition(width, height);

		if (root != null) {
			Element rootPanelElement = FocusElements.ofPanel(root);
			((ScreenAccessor) this).libgui$getChildren().add(rootPanelElement);
			setInitialFocus(rootPanelElement);
		} else {
			LOGGER.warn("No root panel found, keyboard navigation disabled");
		}
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

	@Nullable
	@Override
	public WWidget getLastResponder() {
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
		if (basePanel!=null) {
			clearPeers();
			basePanel.validate(description);

			backgroundWidth = basePanel.getWidth();
			backgroundHeight = basePanel.getHeight();
			
			//DEBUG
			if (backgroundWidth<16) backgroundWidth=300;
			if (backgroundHeight<16) backgroundHeight=300;
		}

		titleX = description.getTitlePos().x();
		titleY = description.getTitlePos().y();

		if (!description.isFullscreen()) {
			x = (screenWidth / 2) - (backgroundWidth / 2);
			y = (screenHeight / 2) - (backgroundHeight / 2);
		} else {
			x = 0;
			y = 0;

			if (basePanel != null) {
				basePanel.setSize(screenWidth, screenHeight);
			}
		}
	}
	
	@Override
	public boolean shouldPause() {
		//...yeah, we're going to go ahead and override that.
		return false;
	}

	@Override
	public boolean mouseClicked(Click click, boolean doubled) {
		super.mouseClicked(click, doubled);

		int containerX = (int) click.x() - x;
		int containerY = (int) click.y() - y;
		mouseInputHandler.checkFocus(containerX, containerY);
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return true;
		mouseInputHandler.onMouseDown(containerX, containerY, click, doubled);

		return true;
	}

	@Override
	public boolean mouseReleased(Click click) {
		super.mouseReleased(click);

		int containerX = (int) click.x() - x;
		int containerY = (int) click.y() - y;
		mouseInputHandler.onMouseUp(containerX, containerY, click);

		return true;
	}

	@Override
	public boolean mouseDragged(Click click, double offsetX, double offsetY) {
		super.mouseDragged(click, offsetX, offsetY);

		int containerX = (int) click.x() - x;
		int containerY = (int) click.y() - y;
		mouseInputHandler.onMouseDrag(containerX, containerY, click, offsetX, offsetY);

		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);

		int containerX = (int)mouseX-x;
		int containerY = (int)mouseY-y;
		mouseInputHandler.onMouseScroll(containerX, containerY, horizontalAmount, verticalAmount);

		return true;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		super.mouseMoved(mouseX, mouseY);

		int containerX = (int)mouseX-x;
		int containerY = (int)mouseY-y;
		mouseInputHandler.onMouseMove(containerX, containerY);
	}

	@Override
	public boolean charTyped(CharInput input) {
		WWidget focus = description.getFocus();
		if (focus != null && focus.onCharTyped(input) == InputResult.PROCESSED) {
			return true;
		}

		return super.charTyped(input);
	}

	@Override
	public boolean keyPressed(KeyInput input) {
		WWidget focus = description.getFocus();
		if (focus != null && focus.onKeyPressed(input) == InputResult.PROCESSED) {
			return true;
		}

		return super.keyPressed(input);
	}

	@Override
	public boolean keyReleased(KeyInput input) {
		WWidget focus = description.getFocus();
		if (focus != null && focus.onKeyReleased(input) == InputResult.PROCESSED) {
			return true;
		}

		return super.keyReleased(input);
	}

	@Override
	protected void drawBackground(DrawContext context, float partialTicks, int mouseX, int mouseY) {} //This is just an AbstractContainerScreen thing; most Screens don't work this way.

	/**
	 * Paints the GUI description of this screen.
	 *
	 * @param context the draw context
	 * @param mouseX  the absolute X coordinate of the mouse cursor
	 * @param mouseY  the absolute Y coordinate of the mouse cursor
	 * @param delta   the tick delta
	 * @since 9.2.0
	 */
	public void paintDescription(DrawContext context, int mouseX, int mouseY, float delta) {
		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				root.paint(context, x, y, mouseX-x, mouseY-y);
			}
		}
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float partialTicks) {
		super.render(context, mouseX, mouseY, partialTicks);

		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				WWidget hitChild = root.hit(mouseX-x, mouseY-y);
				if (hitChild!=null) hitChild.renderTooltip(context, x, y, mouseX-x, mouseY-y);
			}
		}
		
		drawMouseoverTooltip(context, mouseX, mouseY); //Draws the itemstack tooltips
		VisualLogger.render(context);
	}

	@Override
	protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
		if (description != null && description.isTitleVisible()) {
			int width = description.getRootPanel().getWidth();
			ScreenDrawing.drawString(context, getTitle().asOrderedText(), description.getTitleAlignment(), titleX, titleY, width - 2 * titleX, description.getTitleColor());
		}

		// Don't draw the player inventory label as it's drawn by the widget itself
	}

	@Override
	protected void handledScreenTick() {
		super.handledScreenTick();
		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				root.tick();
			}

			description.sendDataSlotUpdates();
		}
	}

	@Override
	protected void addElementNarrations(NarrationMessageBuilder builder) {
		if (description != null) NarrationHelper.addNarrations(description.getRootPanel(), builder);
	}

	@Override
	public void onDisplayed() {
		if (description != null) {
			ScreenNetworking networking = description.getNetworking(NetworkSide.CLIENT);
			((ScreenNetworkingImpl) networking).markReady();
			networking.send(ScreenNetworkingImpl.CLIENT_READY_MESSAGE_KEY, Unit.INSTANCE);
		}
	}
}
