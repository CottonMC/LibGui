package io.github.cottonmc.cotton.gui.client;

import io.github.cottonmc.cotton.gui.CottonScreenController;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;

public class CottonScreen<T extends CottonScreenController> extends AbstractContainerScreen<T> {
	protected CottonScreenController container;
	public static final int PADDING = 8;
	protected WWidget lastResponder = null;
	protected WWidget focus = null;
	
	public CottonScreen(T container, PlayerEntity player) {
		super(container, player.inventory, new LiteralText(""));
		this.container = container;
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
	 * * "left" and "top" are *actually* self-explanatory
	 * * coordinates start at 0,0 at the topleft of the screen.
	 */
	
	@Override
	public void init(MinecraftClient minecraftClient_1, int screenWidth, int screenHeight) {
		super.init(minecraftClient_1, screenWidth, screenHeight);
		
		container.addPainters();
		
		reposition();
	}
	
	public void reposition() {
		WPanel basePanel = container.getRootPanel();
		if (basePanel!=null) {
			basePanel.validate(container);
			
			containerWidth = basePanel.getWidth();
			containerHeight = basePanel.getHeight();
			
			//DEBUG
			if (containerWidth<16) containerWidth=300;
			if (containerHeight<16) containerHeight=300;
			//if (left<0 || left>300) left = 10;
			//if (top<0 || top>300) top = 10;
		}
		left = (width / 2) - (containerWidth / 2);
		top =  (height / 2) - (containerHeight / 2);
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
		if (container.getFocus()==null) return false;
		container.getFocus().onCharTyped(ch);
		return true;
	}
	
	@Override
	public boolean keyPressed(int ch, int keyCode, int modifiers) {
		if (container.getFocus()==null) return false;
		container.getFocus().onKeyPressed(keyCode, modifiers);
		return true;
	}
	
	@Override
	public boolean keyReleased(int ch, int keyCode, int modifiers) {
		if (container.getFocus()==null) return false;
		container.getFocus().onKeyReleased(keyCode, modifiers);
		return true;
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		boolean result = super.mouseClicked(mouseX, mouseY, mouseButton);
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return result;
		lastResponder = container.doMouseDown(containerX, containerY, mouseButton);
		return result;
	}
	
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) { //Testing shows that STATE IS ACTUALLY BUTTON
		boolean result = super.mouseReleased(mouseX, mouseY, mouseButton);
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return result;
		
		WWidget responder = container.doMouseUp(containerX, containerY, mouseButton);
		if (responder!=null && responder==lastResponder) container.doClick(containerX, containerY, mouseButton);
		lastResponder = null;
		return result;
	}
	
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double unknown_1, double unknown_2) {
		boolean result = super.mouseDragged(mouseX, mouseY, mouseButton, unknown_1, unknown_2);
		
		int containerX = (int)mouseX-left;
		int containerY = (int)mouseY-top;
		if (containerX<0 || containerY<0 || containerX>=width || containerY>=height) return result;
		container.doMouseDrag(containerX, containerY, mouseButton);
		return result;
	}
	
	//Zapping this method may fix some positioning bugs - but may cause some backgroundPainter bugs. Will need to monitor.
	/*
	@Override
	public void resize(MinecraftClient minecraftClient_1, int int_1, int int_2) {
		//super.onScaleChanged(minecraftClient_1, int_1, int_2);
		this.width = int_1;
		this.height = int_2;
		reposition();
	}*/
	
	/*
	 * SPECIAL FUNCTIONS: Where possible, we want to draw everything based on *actual GUI state and composition* rather
	 * than relying on pre-baked textures that the programmer then needs to carefully match up their GUI to.
	 */
	
	private int multiplyColor(int color, float amount) {
		int a = color & 0xFF000000;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8  & 255) / 255.0F;
		float b = (color       & 255) / 255.0F;
		
		r = Math.min(r*amount, 1.0f);
		g = Math.min(g*amount, 1.0f);
		b = Math.min(b*amount, 1.0f);
		
		int ir = (int)(r*255);
		int ig = (int)(g*255);
		int ib = (int)(b*255);
		
		return    a |
				(ir << 16) |
				(ig <<  8) |
				 ib;
	}
	
	@Override
	protected void drawBackground(float partialTicks, int mouseX, int mouseY) {
		if (this.container==null) {
			return;
		}
		WPanel root = this.container.getRootPanel();
		if (root==null) return;
		
		root.paintBackground(left, top);
		
		//TODO: Change this to a label that lives in the rootPanel instead?
		if (container instanceof Nameable) {
			Text name = ((Nameable)container).getDisplayName();
			font.draw(name.asFormattedString(), left, top, container.getTitleColor());
		} else if (getTitle() != null) {
			font.draw(getTitle().asFormattedString(), left, top, container.getTitleColor());
		}
	}
	
	@Override
	protected void drawForeground(int mouseX, int mouseY) {
		if (this.container==null) {
			System.out.println("CONTAINER IS NULL.");
			return;
		}
		
		if (this.container.getRootPanel()!=null) {
			this.container.getRootPanel().paintForeground(0, 0, mouseX+left, mouseY+top);
		}
	}
	
	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		// Render the background shadow
		this.renderBackground();

		this.drawBackground(partialTicks, mouseX, mouseY);
		
		super.render(mouseX, mouseY, partialTicks);
		drawMouseoverTooltip(mouseX, mouseY);
	}
	
}
