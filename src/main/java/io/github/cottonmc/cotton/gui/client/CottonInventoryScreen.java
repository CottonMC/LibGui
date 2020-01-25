package io.github.cottonmc.cotton.gui.client;

import net.minecraft.client.render.DiffuseLighting;
import org.lwjgl.glfw.GLFW;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class CottonInventoryScreen<T extends CottonCraftingController> extends ContainerScreen<T> implements TextHoverRendererScreen {
	protected CottonCraftingController description;
	public static final int PADDING = 8;
	protected WWidget lastResponder = null;
	protected WWidget focus = null;
	
	public CottonInventoryScreen(T container, PlayerEntity player) {
		super(container, player.inventory, new LiteralText(""));
		this.description = container;
		width = 18*9;
		height = 18*9;
		this.containerWidth = 18*9;
		this.containerHeight = 18*9;
	}
	
	/*
	 * RENDERING NOTES:
	 * 
	 * * "width" and "height" are the width and height of the overall screen
	 * * "containerWidth" and "containerHeight" are the width and height of the panel to render
	 * * ~~"left" and "top" are *actually* self-explanatory~~
	 *   * "left" and "top" are now (1.15) "x" and "y". A bit less self-explanatory, I guess.
	 * * coordinates start at 0,0 at the topleft of the screen.
	 */
	
	@Override
	public void init(MinecraftClient minecraftClient_1, int screenWidth, int screenHeight) {
		super.init(minecraftClient_1, screenWidth, screenHeight);
		
		description.addPainters();
		
		reposition();
	}
	
	public void reposition() {
		WPanel basePanel = description.getRootPanel();
		if (basePanel!=null) {
			basePanel.validate(description);
			
			containerWidth = basePanel.getWidth();
			containerHeight = basePanel.getHeight();
			
			//DEBUG
			if (containerWidth<16) containerWidth=300;
			if (containerHeight<16) containerHeight=300;
		}
		x = (width / 2) - (containerWidth / 2);
		y =  (height / 2) - (containerHeight / 2);
	}
	
	@Override
	public void onClose() {
		super.onClose();
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
			this.minecraft.player.closeContainer();
			return true;
		} else {
			//if (super.keyPressed(ch, keyCode, modifiers)) return true;
			if (description.getFocus()==null) {
				if (MinecraftClient.getInstance().options.keyInventory.matchesKey(ch, keyCode)) {
					this.minecraft.player.closeContainer();
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
	protected void drawBackground(float partialTicks, int mouseX, int mouseY) {} //This is just an AbstractContainerScreen thing; most Screens don't work this way.
	
	public void paint(int mouseX, int mouseY) {
		super.renderBackground();
		
		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				root.paintBackground(x, y, mouseX-x, mouseY-y);
			}
		}
		
		if (getTitle() != null) {
			font.draw(getTitle().asFormattedString(), x, y, description.getTitleColor());
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		paint(mouseX, mouseY);
		
		super.render(mouseX, mouseY, partialTicks);
		DiffuseLighting.disable(); //Needed because super.render leaves dirty state
		
		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				root.paintForeground(x, y, mouseX, mouseY);
				
				WWidget hitChild = root.hit(mouseX-x, mouseY-y);
				if (hitChild!=null) hitChild.renderTooltip(x, y, mouseX-x, mouseY-y);
			}
		}
		
		drawMouseoverTooltip(mouseX, mouseY); //Draws the itemstack tooltips
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
	public void renderTextHover(Text text, int x, int y) {
		renderComponentHoverEffect(text, x, y);
	}
}
