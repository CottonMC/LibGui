package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WToggleButton extends WWidget {
	Text label = null;
	Identifier off_image = new Identifier("libgui:widget/toggle_off.png");
	Identifier on_image  = new Identifier("libgui:widget/toggle_on.png");
	boolean on = false;
	protected int color = WLabel.DEFAULT_TEXT_COLOR;
	protected int darkmodeColor = WLabel.DEFAULT_DARKMODE_TEXT_COLOR;
	
	public WToggleButton() {
		
	}
	
	public WToggleButton(Text text) {
		this.label = text;
	}
	
	public WToggleButton color(int light, int dark) {
		this.color = light;
		this.darkmodeColor = dark;
		return this;
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		ScreenDrawing.rect(on ? on_image : off_image, x, y, 18, 8, 0xFFFFFFFF);
		
		if (label!=null) {
			ScreenDrawing.drawString(label.asFormattedString(), x+20, y, LibGuiClient.config.darkMode ? darkmodeColor : color);
		}
	}
	
	@Override
	public boolean canResize() {
		return true;
	}
	
	@Override
	public void onClick(int x, int y, int button) {
		super.onClick(x, y, button);
		
		MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		this.on = !this.on;
		
		onToggle(this.on);
	}
	
	public void onToggle(boolean on) {
		
	}

	public void setToggle(boolean on) {
		this.on = on;
	}
}
