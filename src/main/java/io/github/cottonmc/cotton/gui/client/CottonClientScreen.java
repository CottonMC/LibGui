package io.github.cottonmc.cotton.gui.client;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class CottonClientScreen extends Screen implements TextHoverRendererScreen {
	protected GuiDescription description;
	protected int left = 0;
	protected int top = 0;
	protected int containerWidth = 0;
	protected int containerHeight = 0;
	
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
		
		description.addPainters();
		reposition(screenWidth, screenHeight);
	}
	
	public void reposition(int screenWidth, int screenHeight) {
		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				this.containerWidth = root.getWidth();
				this.containerHeight = root.getHeight();
				
				this.left = (screenWidth - root.getWidth()) / 2;
				this.top = (screenHeight - root.getHeight()) / 2;
			}
		}
	}
	
	public void paint(int mouseX, int mouseY) {
		super.renderBackground();
		
		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				root.paintBackground(left, top, mouseX-left, mouseY-top);
			}
		}
		
		if (getTitle() != null) {
			font.draw(getTitle().asFormattedString(), left, top, description.getTitleColor());
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		paint(mouseX, mouseY);
		
		super.render(mouseX, mouseY, partialTicks);
		
		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				root.paintForeground(left, top, mouseX, mouseY);
				
				WWidget hitChild = root.hit(mouseX-left, mouseY-top);
				if (hitChild!=null) hitChild.renderTooltip(left, top, mouseX-left, mouseY-top);
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
	public void renderTextHover(Text text, int x, int y) {
		renderComponentHoverEffect(text, x, y);
	}
}
