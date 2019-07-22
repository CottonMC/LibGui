package io.github.cottonmc.cotton.gui.client;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ClientCottonScreen extends Screen {
	protected GuiDescription description;
	protected int left = 0;
	protected int top = 0;
	protected int containerWidth = 0;
	protected int containerHeight = 0;
	
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
		
		
		reposition(screenWidth, screenHeight);
	}
	
	@Override
	public void resize(MinecraftClient client, int screenWidth, int screenHeight) {
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
	public void render(int int_1, int int_2, float float_1) {
		renderBackground();
		
		super.render(int_1, int_2, float_1);
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
	}
	
	
	@Override
	public void tick() {
		super.tick();
	}
}
