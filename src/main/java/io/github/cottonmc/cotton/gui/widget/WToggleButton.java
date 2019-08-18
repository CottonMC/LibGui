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


	/** All default values, no text */
	public WToggleButton() {
		this(DEFAULT_ON_IMAGE, DEFAULT_OFF_IMAGE, 18, 8);
	}

	/** Defaults with text */
	public WToggleButton(Text text) {
		this(DEFAULT_ON_IMAGE, DEFAULT_OFF_IMAGE, 18, 8);
		this.label = text;
	}

	/** Custom images,  with default sizes */
	public WToggleButton(Identifier onImage, Identifier offImage) {

		this.onImage = onImage;
		this.offImage = offImage;
	}

	/** Custom images,  with default sizes and a label */
	public WToggleButton(Identifier onImage, Identifier offImage, Text label) {

		this.onImage = onImage;
		this.offImage = offImage;
		this.label = label;
	}

	/** Custom images,  with custom sizes */
	public WToggleButton(Identifier onImage, Identifier offImage, int width, int height) {

		this.onImage = onImage;
		this.offImage = offImage;
		this.width = width;
		this.height = height;
	}

	/** Fully customized,  custom images,  sizes and label */
	public WToggleButton(Text label, Identifier onImage, Identifier offImage, int width, int height) {
		this(onImage, offImage, width, height);

		this.label = label;
	}

	public WToggleButton color(int light, int dark) {

		this.color = light;
		this.darkmodeColor = dark;

		return this;
	}

	Text label = null;

	/** Default On / Off Images */
	protected final static Identifier DEFAULT_OFF_IMAGE = new Identifier("libgui:textures/widget/toggle_off.png");
	protected final static Identifier DEFAULT_ON_IMAGE  = new Identifier("libgui:textures/widget/toggle_on.png");

	protected Identifier onImage;
	protected Identifier offImage;

	/** Default size values */
	protected int width = 18;
	protected int height = 18;

	protected boolean isOn = false;
	protected Runnable onToggle;

	protected int color = WLabel.DEFAULT_TEXT_COLOR;
	protected int darkmodeColor = WLabel.DEFAULT_DARKMODE_TEXT_COLOR;


	@Environment(EnvType.CLIENT)
	@Override
	public void paintBackground(int x, int y) {

		ScreenDrawing.rect(isOn ? DEFAULT_ON_IMAGE : DEFAULT_OFF_IMAGE, x, y, 18, 18, 0xFFFFFFFF);
		
		if (label!=null) {

			ScreenDrawing.drawString(label.asFormattedString(), x + 22, y+6, LibGuiClient.config.darkMode ? darkmodeColor : color);
		}
	}
	
	@Override
	public boolean canResize() {

		return true;
	}


	@Environment(EnvType.CLIENT)
	@Override
	public void onClick(int x, int y, int button) {
		super.onClick(x, y, button);
		
		MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));

		this.isOn = !this.isOn;
		onToggle(this.isOn);
	}
	
	protected void onToggle(boolean on) {

		if (this.onToggle != null) {

			this.onToggle.run();
		}
	}

	public boolean getToggle() { return this.isOn; }
	public void setToggle(boolean on) { this.isOn = on; }

	/** Set on toggle handler */
	public void setOnToggle(Runnable r) {

		this.onToggle = r;
	}
}
