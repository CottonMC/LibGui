package io.github.cottonmc.cotton.gui.client;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ClientCottonScreen extends Screen {
	protected GuiDescription description;
	protected int left = 0;
	protected int top = 0;
	protected int containerWidth = 0;
	protected int containerHeight = 0;
	
	protected WWidget lastResponder = null;
	
	public ClientCottonScreen(GuiDescription description) {
		super(new LiteralText(""));
		this.description = description;
	}
	
	public ClientCottonScreen(Text title, GuiDescription description) {
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
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		renderBackground();
		
		super.render(mouseX, mouseY, partialTicks);
		
		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				root.paintForeground(left, top, mouseX, mouseY);
			}
		}
	}
	
	@Override
	public void renderBackground() {
		super.renderBackground();
		
		if (description!=null) {
			WPanel root = description.getRootPanel();
			if (root!=null) {
				root.paintBackground(left, top);
			}
		}
		
		if (getTitle() != null) {
			font.draw(getTitle().asFormattedString(), left, top, description.getTitleColor());
		}
	}
	
	
	@Override
	public void tick() {
		super.tick();
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
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
		
		if (description.getRootPanel()==null) return super.mouseClicked(mouseX, mouseY, mouseButton);
		
		boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return result;
		lastResponder = description.getRootPanel().onMouseDown(containerX, containerY, mouseButton);
		
		return result;
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
		if (description.getRootPanel()==null) return super.mouseReleased(mouseX, mouseY, mouseButton);
		
		boolean result = super.mouseReleased(mouseX, mouseY, mouseButton);
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return result;
		
		WWidget responder = description.getRootPanel().onMouseUp(containerX, containerY, mouseButton);
		if (responder!=null && responder==lastResponder) description.getRootPanel().onClick(containerX, containerY, mouseButton);
		lastResponder = null;
		return result;
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double unknown_1, double unknown_2) {
		if (description.getRootPanel()==null) return super.mouseDragged(mouseX, mouseY, mouseButton, unknown_1, unknown_2);
		
		boolean result = super.mouseDragged(mouseX, mouseY, mouseButton, unknown_1, unknown_2);
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return result;
		description.getRootPanel().onMouseDrag(containerX, containerY, mouseButton);
		return result;
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
	
	@Override
	public Element getFocused() {
		return this;
	}
}
