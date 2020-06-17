package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.StringRenderable;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.data.Alignment;

public class WButton extends WWidget {
	private StringRenderable label;
	protected int color = WLabel.DEFAULT_TEXT_COLOR;
	protected int darkmodeColor = WLabel.DEFAULT_TEXT_COLOR;
	private boolean enabled = true;
	protected Alignment alignment = Alignment.CENTER;
	
	private Runnable onClick;
	
	public WButton() {
		
	}
	
	public WButton(StringRenderable text) {
		this.label = text;
	}
	
	@Override
	public boolean canResize() {
		return true;
	}

	@Override
	public boolean canFocus() {
		return true;
	}

	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		boolean hovered = (mouseX>=0 && mouseY>=0 && mouseX<getWidth() && mouseY<getHeight());
		int state = 1; //1=regular. 2=hovered. 0=disabled.
		if (!enabled) state = 0;
		else if (hovered || isFocused()) state = 2;
		
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
			} /*else if (hovered) {
				color = 0xFFFFA0;
			}*/
			
			ScreenDrawing.drawStringWithShadow(matrices, label, alignment, x, y + ((20 - 8) / 2), width, color); //LibGuiClient.config.darkMode ? darkmodeColor : color);
		}
	}
	
	@Override
	public void setSize(int x, int y) {
		super.setSize(x, 20);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onClick(int x, int y, int button) {
		super.onClick(x, y, button);
		
		if (enabled && isWithinBounds(x, y)) {
			MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

			if (onClick!=null) onClick.run();
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onKeyPressed(int ch, int key, int modifiers) {
		if (isActivationKey(ch)) {
			onClick(0, 0, 0);
		}
	}

	public WButton setOnClick(Runnable r) {
		this.onClick = r;
		return this;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public WButton setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public StringRenderable getLabel() {
		return label;
	}

	public WButton setLabel(StringRenderable label) {
		this.label = label;
		return this;
	}

	public Alignment getAlignment() {
		return alignment;
	}

	public WButton setAlignment(Alignment alignment) {
		this.alignment = alignment;
		return this;
	}
}
