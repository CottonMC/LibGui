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

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class WToggleButton extends WWidget {
	/** Default On / Off Images */
	protected final static Identifier DEFAULT_OFF_IMAGE = new Identifier("libgui:textures/widget/toggle_off.png");
	protected final static Identifier DEFAULT_ON_IMAGE  = new Identifier("libgui:textures/widget/toggle_on.png");

	protected Identifier onImage;
	protected Identifier offImage;

	@Nullable protected Text label = null;

	protected boolean isOn = false;
	@Nullable protected Consumer<Boolean> onToggle = null;

	protected int color = WLabel.DEFAULT_TEXT_COLOR;
	protected int darkmodeColor = WLabel.DEFAULT_DARKMODE_TEXT_COLOR;

	/** All default values, no text */
	public WToggleButton() {
		this(DEFAULT_ON_IMAGE, DEFAULT_OFF_IMAGE);
	}

	/** Defaults with text */
	public WToggleButton(Text text) {
		this(DEFAULT_ON_IMAGE, DEFAULT_OFF_IMAGE);
		this.label = text;
	}

	/** Custom images */
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

	/**
	 * @deprecated Use {@link #WToggleButton(Identifier, Identifier)} instead.
	 */
	@Deprecated
	public WToggleButton(Identifier onImage, Identifier offImage, int width, int height) {
		this(onImage, offImage);
	}

	/**
	 * @deprecated Use {@link #WToggleButton(Identifier, Identifier, Text)} instead.
	 */
	@Deprecated
	public WToggleButton(Text label, Identifier onImage, Identifier offImage, int width, int height) {
		this(onImage, offImage, label);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		ScreenDrawing.texturedRect(x, y, 18, 18, isOn ? onImage : offImage, 0xFFFFFFFF);
		
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
			this.onToggle.accept(on);
		}
	}

	public boolean getToggle() { return this.isOn; }
	public void setToggle(boolean on) { this.isOn = on; }

	/**
	 * Set on toggle handler
	 *
	 * @deprecated Use {@link #setOnToggle(Consumer)}
	 */
	@Deprecated
	public void setOnToggle(Runnable r) {
		this.onToggle = on -> r.run();
	}

	@Nullable
	public Consumer<Boolean> getOnToggle() {
		return this.onToggle;
	}

	public WToggleButton setOnToggle(@Nullable Consumer<Boolean> onToggle) {
		this.onToggle = onToggle;
		return this;
	}

	@Nullable
	public Text getLabel() {
		return label;
	}

	public void setLabel(@Nullable Text label) {
		this.label = label;
	}

	/**
	 * @deprecated Use {@link #setColor} instead.
	 */
	@Deprecated
	public WToggleButton color(int light, int dark) {
		return setColor(light, dark);
	}

	public WToggleButton setColor(int light, int dark) {
		this.color = light;
		this.darkmodeColor = dark;

		return this;
	}
}
