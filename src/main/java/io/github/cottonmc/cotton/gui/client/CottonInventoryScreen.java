package io.github.cottonmc.cotton.gui.client;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import org.lwjgl.opengl.GL11;

/**
 * A screen for a {@link SyncedGuiDescription}.
 *
 * @param <T> the description type
 */
public class CottonInventoryScreen<T extends SyncedGuiDescription> extends HandledScreen<T> implements TextHoverRendererScreen {
	protected SyncedGuiDescription description;
	protected WWidget lastResponder = null;

	/**
	 * Constructs a new screen without a title.
	 *
	 * @param description the GUI description
	 * @param player     the player
	 */
	public CottonInventoryScreen(T description, PlayerEntity player) {
		this(description, player, new LiteralText(""));
	}

	/**
	 * Constructs a new screen.
	 *
	 * @param description the GUI description
	 * @param player      the player
	 * @param title       the screen title
	 */
	public CottonInventoryScreen(T description, PlayerEntity player, Text title) {
		super(description, player.inventory, title);
		this.description = description;
		width = 18*9;
		height = 18*9;
		this.backgroundWidth = 18*9;
		this.backgroundHeight = 18*9;
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
	public void init(MinecraftClient client, int screenWidth, int screenHeight) {
		super.init(client, screenWidth, screenHeight);
		client.keyboard.enableRepeatEvents(true);
		
		description.addPainters();
		
		reposition(screenWidth, screenHeight);
	}

	@Override
	public void removed() {
		super.removed();
		this.client.keyboard.enableRepeatEvents(false);
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

		if (!description.isFullscreen()) {
			x = (width / 2) - (backgroundWidth / 2);
			y = (height / 2) - (backgroundHeight / 2);
			titleX = 0;
			titleY = 0;
		} else {
			x = 0;
			y = 0;

			// Offset the title coordinates a little from the edge
			titleX = 10;
			titleY = 10;

			if (basePanel != null) {
				basePanel.setSize(screenWidth, screenHeight);
			}
		}
	}
	
	@Override
	public boolean isPauseScreen() {
		//...yeah, we're going to go ahead and override that.
		return false;
	}
	
	@Override
	public boolean charTyped(char ch, int keyCode) {
		if (description.getFocus()==null) return false;
		description.getFocus().onCharTyped(ch);
		return true;
	}
	
	@Override
	public boolean keyPressed(int ch, int keyCode, int modifiers) {
		//System.out.println("Key " + Integer.toHexString(ch)+" "+Integer.toHexString(keyCode));
		if (ch==GLFW.GLFW_KEY_ESCAPE) {
			this.client.player.closeHandledScreen();
			return true;
		} else if (ch==GLFW.GLFW_KEY_TAB) {
			changeFocus(!hasShiftDown());
			return true;
		} else {
			//if (super.keyPressed(ch, keyCode, modifiers)) return true;
			if (description.getFocus()==null) {
				if (MinecraftClient.getInstance().options.keyInventory.matchesKey(ch, keyCode)) {
					this.client.player.closeHandledScreen();
					return true;
				}
				return false;
			} else {
				description.getFocus().onKeyPressed(ch, keyCode, modifiers);
				return true;
			}
		}
	}
	
	@Override
	public boolean keyReleased(int ch, int keyCode, int modifiers) {
		if (description.getFocus()==null) return false;
		description.getFocus().onKeyReleased(ch, keyCode, modifiers);
		return true;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);
		int containerX = (int)mouseX-x;
		int containerY = (int)mouseY-y;
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return result;
		if (lastResponder==null) {
			lastResponder = description.doMouseDown(containerX, containerY, mouseButton);
		} else {
			//This is a drag instead
		}
		return result;
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) { //Testing shows that STATE IS ACTUALLY BUTTON
		boolean result = super.mouseReleased(mouseX, mouseY, mouseButton);
		int containerX = (int)mouseX-x;
		int containerY = (int)mouseY-y;
		
		if (lastResponder!=null) {
			lastResponder.onMouseUp(containerX-lastResponder.getAbsoluteX(), containerY-lastResponder.getAbsoluteY(), mouseButton);
			if (containerX>=0 && containerY>=0 && containerX<width && containerY<height) {
				lastResponder.onClick(containerX-lastResponder.getAbsoluteX(), containerY-lastResponder.getAbsoluteY(), mouseButton);
			}
		} else {
			description.doMouseUp(containerX, containerY, mouseButton);
		}
		
		lastResponder = null;
		return result;
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
		boolean result = super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
		
		int containerX = (int)mouseX-x;
		int containerY = (int)mouseY-y;
		
		if (lastResponder!=null) {
			lastResponder.onMouseDrag(containerX-lastResponder.getAbsoluteX(), containerY-lastResponder.getAbsoluteY(), mouseButton, deltaX, deltaY);
			return result;
		} else {
			if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return result;
			description.doMouseDrag(containerX, containerY, mouseButton, deltaX, deltaY);
		}
		return result;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (description.getRootPanel()==null) return super.mouseScrolled(mouseX, mouseY, amount);
		
		WPanel root = description.getRootPanel();
		int containerX = (int)mouseX-x;
		int containerY = (int)mouseY-y;
		
		WWidget child = root.hit(containerX, containerY);
		child.onMouseScroll(containerX - child.getAbsoluteX(), containerY - child.getAbsoluteY(), amount);
		return true;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		if (description.getRootPanel()==null) return;

		WPanel root = description.getRootPanel();
		int containerX = (int)mouseX-x;
		int containerY = (int)mouseY-y;

		WWidget child = root.hit(containerX, containerY);
		child.onMouseMove(containerX - child.getAbsoluteX(), containerY - child.getAbsoluteY());
	}

	@Override
	protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {} //This is just an AbstractContainerScreen thing; most Screens don't work this way.
	
	private void paint(MatrixStack matrices, int mouseX, int mouseY) {
		super.renderBackground(matrices);
		
		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				GL11.glEnable(GL11.GL_SCISSOR_TEST);
				Scissors.refreshScissors();
				root.paint(matrices, x, y, mouseX-x, mouseY-y);
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				Scissors.checkStackIsEmpty();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
		paint(matrices, mouseX, mouseY);
		
		super.render(matrices, mouseX, mouseY, partialTicks);
		DiffuseLighting.disable(); //Needed because super.render leaves dirty state
		
		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				WWidget hitChild = root.hit(mouseX-x, mouseY-y);
				if (hitChild!=null) hitChild.renderTooltip(matrices, x, y, mouseX-x, mouseY-y);
			}
		}
		
		drawMouseoverTooltip(matrices, mouseX, mouseY); //Draws the itemstack tooltips
	}

	@Override
	protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
		if (description != null && description.isTitleVisible()) {
			int width = description.getRootPanel().getWidth();
			ScreenDrawing.drawString(matrices, getTitle().asOrderedText(), description.getTitleAlignment(), titleX, titleY, width, description.getTitleColor());
		}

		// Don't draw the player inventory label as it's drawn by the widget itself
	}

	@Override
	public void tick() {
		super.tick();
		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				root.tick();
			}
		}
	}

	@Override
	public void renderTextHover(MatrixStack matrices, Style textStyle, int x, int y) {
		renderTextHoverEffect(matrices, textStyle, x, y);
	}

	@Override
	public boolean changeFocus(boolean lookForwards) {
		if (description != null) {
			description.cycleFocus(lookForwards);
		}

		return true;
	}
}
