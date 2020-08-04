package io.github.cottonmc.cotton.gui.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import org.lwjgl.opengl.GL11;

public class CottonClientScreen extends Screen implements TextHoverRendererScreen {
	protected GuiDescription description;
	protected int left = 0;
	protected int top = 0;

	/**
	 * The X coordinate of the screen title.
	 *
	 * @since 2.0.0
	 */
	protected int titleX;

	/**
	 * The Y coordinate of the screen title.
	 *
	 * @since 2.0.0
	 */
	protected int titleY;

	protected WWidget lastResponder = null;
	
	public CottonClientScreen(GuiDescription description) {
		super(new LiteralText(""));
		this.description = description;
	}
	
	public CottonClientScreen(Text title, GuiDescription description) {
		super(title);
		this.description = description;
	}
	
	public GuiDescription getDescription() {
		return description;
	}
	
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
	 * Repositions the root panel.
	 *
	 * @param screenWidth  the width of the screen
	 * @param screenHeight the height of the screen
	 */
	protected void reposition(int screenWidth, int screenHeight) {
		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				if (!description.isFullscreen()) {
					this.left = (screenWidth - root.getWidth()) / 2;
					this.top = (screenHeight - root.getHeight()) / 2;
					this.titleX = 0;
					this.titleY = 0;
				} else {
					this.left = 0;
					this.top = 0;

					// Offset the title coordinates a little from the edge
					this.titleX = 10;
					this.titleY = 10;

					root.setSize(screenWidth, screenHeight);
				}

				root.layout();
			}
		}
	}
	
	private void paint(MatrixStack matrices, int mouseX, int mouseY) {
		super.renderBackground(matrices);
		
		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				GL11.glEnable(GL11.GL_SCISSOR_TEST);
				Scissors.refreshScissors();
				root.paint(matrices, left, top, mouseX-left, mouseY-top);
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
				Scissors.checkStackIsEmpty();
			}

			if (getTitle() != null && description.isTitleVisible()) {
				int width = description.getRootPanel().getWidth();
				ScreenDrawing.drawString(matrices, getTitle().asOrderedText(), description.getTitleAlignment(), left + titleX, top + titleY, width, description.getTitleColor());
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
		paint(matrices, mouseX, mouseY);
		
		super.render(matrices, mouseX, mouseY, partialTicks);
		
		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				WWidget hitChild = root.hit(mouseX-left, mouseY-top);
				if (hitChild!=null) hitChild.renderTooltip(matrices, left, top, mouseX-left, mouseY-top);
			}
		}
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
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (description.getRootPanel()==null) return super.mouseClicked(mouseX, mouseY, mouseButton);
		WWidget focus = description.getFocus();
		if (focus!=null) {
			
			int wx = focus.getAbsoluteX();
			int wy = focus.getAbsoluteY();
			
			if (mouseX>=wx && mouseX<wx+focus.getWidth() && mouseY>=wy && mouseY<wy+focus.getHeight()) {
				//Do nothing, focus will get the click soon
			} else {
				//Invalidate the component first
				description.releaseFocus(focus);
			}
		}
		
		boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return result;
		if (lastResponder==null) {
			lastResponder = description.getRootPanel().hit(containerX, containerY);
			if (lastResponder!=null) lastResponder.onMouseDown(containerX-lastResponder.getAbsoluteX(), containerY-lastResponder.getAbsoluteY(), mouseButton);
		} else {
			//This is a drag instead
		}
		return result;
		
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (description.getRootPanel()==null) return super.mouseReleased(mouseX, mouseY, mouseButton);
		boolean result = super.mouseReleased(mouseX, mouseY, mouseButton);
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		
		if (lastResponder!=null) {
			lastResponder.onMouseUp(containerX-lastResponder.getAbsoluteX(), containerY-lastResponder.getAbsoluteY(), mouseButton);
			if (containerX>=0 && containerY>=0 && containerX<width && containerY<height) {
				lastResponder.onClick(containerX-lastResponder.getAbsoluteX(), containerY-lastResponder.getAbsoluteY(), mouseButton);
			}
		} else {
			description.getRootPanel().onMouseUp(containerX, containerY, mouseButton);
		}
		
		lastResponder = null;
		return result;
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
		if (description.getRootPanel()==null) return super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
		boolean result = super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
		
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		
		if (lastResponder!=null) {
			lastResponder.onMouseDrag(containerX-lastResponder.getAbsoluteX(), containerY-lastResponder.getAbsoluteY(), mouseButton, deltaX, deltaY);
			return result;
		} else {
			if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return result;
			description.getRootPanel().onMouseDrag(containerX, containerY, mouseButton, deltaX, deltaY);
		}
		return result;
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (description.getRootPanel()==null) return super.mouseScrolled(mouseX, mouseY, amount);
		
		WPanel root = description.getRootPanel();
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		
		WWidget child = root.hit(containerX, containerY);
		child.onMouseScroll(containerX - child.getAbsoluteX(), containerY - child.getAbsoluteY(), amount);
		return true;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		if (description.getRootPanel()==null) return;

		WPanel root = description.getRootPanel();
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;

		WWidget child = root.hit(containerX, containerY);
		child.onMouseMove(containerX - child.getAbsoluteX(), containerY - child.getAbsoluteY());
	}

	@Override
	public boolean charTyped(char ch, int keyCode) {
		if (description.getFocus()==null) return false;
		description.getFocus().onCharTyped(ch);
		return true;
	}
	
	@Override
	public boolean keyPressed(int ch, int keyCode, int modifiers) {
		if (super.keyPressed(ch, keyCode, modifiers)) return true;
		if (description.getFocus()==null) return false;
		description.getFocus().onKeyPressed(ch, keyCode, modifiers);
		return true;
	}
	
	@Override
	public boolean keyReleased(int ch, int keyCode, int modifiers) {
		if (description.getFocus()==null) return false;
		description.getFocus().onKeyReleased(ch, keyCode, modifiers);
		return true;
	}
	
	//@Override
	//public Element getFocused() {
		//return this;
	//}

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
