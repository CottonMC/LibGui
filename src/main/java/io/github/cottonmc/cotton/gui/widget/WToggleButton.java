package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class WToggleButton extends WWidget {
	// Default on/off images
	protected final static Identifier DEFAULT_OFF_IMAGE = new Identifier("libgui:textures/widget/toggle_off.png");
	protected final static Identifier DEFAULT_ON_IMAGE  = new Identifier("libgui:textures/widget/toggle_on.png");
	protected final static Identifier DEFAULT_FOCUS_IMAGE = new Identifier("libgui:textures/widget/toggle_focus.png");

	protected Identifier onImage;
	protected Identifier offImage;
	protected Identifier focusImage = DEFAULT_FOCUS_IMAGE;

	@Nullable protected Text label = null;

	protected boolean isOn = false;
	@Nullable protected Consumer<Boolean> onToggle = null;

	protected int color = WLabel.DEFAULT_TEXT_COLOR;
	protected int darkmodeColor = WLabel.DEFAULT_DARKMODE_TEXT_COLOR;

	/**
	 * Constructs a toggle button with default images and no label.
	 */
	public WToggleButton() {
		this(DEFAULT_ON_IMAGE, DEFAULT_OFF_IMAGE);
	}

	/**
	 * Constructs a toggle button with default images.
	 *
	 * @param label the button label
	 */
	public WToggleButton(Text label) {
		this(DEFAULT_ON_IMAGE, DEFAULT_OFF_IMAGE);
		this.label = label;
	}

	/**
	 * Constructs a toggle button with custom images and no label.
	 *
	 * @param onImage  the toggled on image
	 * @param offImage the toggled off image
	 */
	public WToggleButton(Identifier onImage, Identifier offImage) {
		this.onImage = onImage;
		this.offImage = offImage;
	}

	/**
	 * Constructs a toggle button with custom images.
	 *
	 * @param onImage  the toggled on image
	 * @param offImage the toggled off image
	 * @param label    the button label
	 */
	public WToggleButton(Identifier onImage, Identifier offImage, Text label) {
		this.onImage = onImage;
		this.offImage = offImage;
		this.label = label;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		ScreenDrawing.texturedRect(x, y, 18, 18, isOn ? onImage : offImage, 0xFFFFFFFF);
		if (isFocused()) {
			ScreenDrawing.texturedRect(x, y, 18, 18, focusImage, 0xFFFFFFFF);
		}

		if (label!=null) {
			ScreenDrawing.drawString(matrices, label.asOrderedText(), x + 22, y+6, LibGuiClient.config.darkMode ? darkmodeColor : color);
		}
	}
	
	@Override
	public boolean canResize() {
		return true;
	}

	@Override
	public boolean canFocus() {
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

	@Override
	public void onKeyPressed(int ch, int key, int modifiers) {
		if (isActivationKey(ch)) {
			onClick(0, 0, 0);
		}
	}

	protected void onToggle(boolean on) {
		if (this.onToggle != null) {
			this.onToggle.accept(on);
		}
	}

	public boolean getToggle() { return this.isOn; }
	public void setToggle(boolean on) { this.isOn = on; }

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

	public WToggleButton setLabel(@Nullable Text label) {
		this.label = label;
		return this;
	}

	public WToggleButton setColor(int light, int dark) {
		this.color = light;
		this.darkmodeColor = dark;

		return this;
	}

	public Identifier getOnImage() {
		return onImage;
	}

	public WToggleButton setOnImage(Identifier onImage) {
		this.onImage = onImage;
		return this;
	}

	public Identifier getOffImage() {
		return offImage;
	}

	public WToggleButton setOffImage(Identifier offImage) {
		this.offImage = offImage;
		return this;
	}

	public Identifier getFocusImage() {
		return focusImage;
	}

	public WToggleButton setFocusImage(Identifier focusImage) {
		this.focusImage = focusImage;
		return this;
	}
}
