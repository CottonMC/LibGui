package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class WButton extends WWidget {
	private Text label;
	protected int color = WLabel.DEFAULT_TEXT_COLOR;
	protected int darkmodeColor = WLabel.DEFAULT_TEXT_COLOR;
	private boolean enabled = true;
	
	private Runnable onClick;
	
	public WButton() {
		
	}
	
	public WButton(Text text) {
		this.label = text;
	}
	
	@Override
	public boolean canResize() {
		return true;
	}
	
	
	@Override
	public void paintForeground(int x, int y, int mouseX, int mouseY) {
		//System.out.println("Mouse: { "+mouseX+", "+mouseY+" }");
		
		boolean hovered = (mouseX>=x && mouseY>=y && mouseX<x+getWidth() && mouseY<y+getHeight());
		int state = 1; //1=regular. 2=hovered. 0=disabled.
		if (!enabled) state = 0;
		else if (hovered) state = 2;
		
		float px = 1/256f;
		float buttonLeft = 0 * px;
		float buttonTop = (46 + (state*20)) * px;
		int halfWidth = getWidth()/2;
		if (halfWidth>198) halfWidth=198;
		float buttonWidth = halfWidth*px;
		float buttonHeight = 20*px;
		
		float buttonEndLeft = (200-(getWidth()/2)) * px;
		
		ScreenDrawing.texturedRect(x, y, getWidth()/2, 20, AbstractButtonWidget.WIDGETS_LOCATION, buttonLeft, buttonTop, buttonLeft+buttonWidth, buttonTop+buttonHeight, 0xFFFFFFFF);
		ScreenDrawing.texturedRect(x+(getWidth()/2), y, getWidth()/2, 20, AbstractButtonWidget.WIDGETS_LOCATION, buttonEndLeft, buttonTop, 200*px, buttonTop+buttonHeight, 0xFFFFFFFF);
		
		if (label!=null) {
			int color = 0xE0E0E0;
			if (!enabled) {
				color = 0xA0A0A0;
			} else if (hovered) {
				color = 0xFFFFA0;
			}
			
			ScreenDrawing.drawCenteredWithShadow(label.asFormattedString(), x+(getWidth()/2), y + ((20 - 8) / 2), color); //LibGuiClient.config.darkMode ? darkmodeColor : color);
		}
		
		
		super.paintForeground(x, y, mouseX, mouseY);
	}
	
	@Override
	public void setSize(int x, int y) {
		super.setSize(x, 20);
	}
	
	@Override
	public void onClick(int x, int y, int button) {
		super.onClick(x, y, button);
		
		if (enabled && isWithinBounds(x, y)) {
			MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

			if (onClick!=null) onClick.run();
		}
	}

	public void setOnClick(Runnable r) {
		this.onClick = r;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
