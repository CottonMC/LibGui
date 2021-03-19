package io.github.cottonmc.cotton.gui.client;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.impl.client.CottonScreenImpl;
import io.github.cottonmc.cotton.gui.impl.client.MouseInputHandler;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL11;

public class CottonClientScreen extends Screen implements CottonScreenImpl {
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

	@Nullable
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
	public void init() {
		super.init();
		client.keyboard.setRepeatEvents(true);
		
		WPanel root = description.getRootPanel();
		if (root != null) root.addPainters();
		description.addPainters();
		reposition(width, height);
	}

	@Override
	public void removed() {
		super.removed();
		this.client.keyboard.setRepeatEvents(false);
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
			}
		}
	}
	
	private void paint(MatrixStack matrices, int mouseX, int mouseY) {
		renderBackground(matrices);
		
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
		
		super.mouseClicked(mouseX, mouseY, mouseButton);
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return true;
		MouseInputHandler.onMouseDown(description, this, containerX, containerY, mouseButton);

		return true;
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (description.getRootPanel()==null) return super.mouseReleased(mouseX, mouseY, mouseButton);
		super.mouseReleased(mouseX, mouseY, mouseButton);
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		MouseInputHandler.onMouseUp(description, this, containerX, containerY, mouseButton);
		
		return true;
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double deltaX, double deltaY) {
		if (description.getRootPanel()==null) return super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
		super.mouseDragged(mouseX, mouseY, mouseButton, deltaX, deltaY);
		
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		MouseInputHandler.onMouseDrag(description, this, containerX, containerY, mouseButton, deltaX, deltaY);

		return true;
	}
	
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (description.getRootPanel()==null) return super.mouseScrolled(mouseX, mouseY, amount);
		
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		MouseInputHandler.onMouseScroll(description, containerX, containerY, amount);

		return true;
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY) {
		if (description.getRootPanel()==null) return;

		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		MouseInputHandler.onMouseMove(description, containerX, containerY);
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
	public void renderTextHover(MatrixStack matrices, @Nullable Style textStyle, int x, int y) {
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
