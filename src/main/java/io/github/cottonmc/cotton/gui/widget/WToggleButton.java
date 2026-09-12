package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.impl.client.NarrationMessages;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class WToggleButton extends WWidget {
	// Default on/off images
	protected static final Texture DEFAULT_OFF_IMAGE = new Texture(LibGuiCommon.id("textures/widget/toggle_off.png"));
	protected static final Texture DEFAULT_ON_IMAGE  = new Texture(LibGuiCommon.id("textures/widget/toggle_on.png"));
	protected static final Texture DEFAULT_FOCUS_IMAGE = new Texture(LibGuiCommon.id("textures/widget/toggle_focus.png"));

	protected Texture onImage;
	protected Texture offImage;
	protected Texture focusImage = DEFAULT_FOCUS_IMAGE;

	protected @Nullable Component label = null;

	protected boolean isOn = false;
	protected @Nullable Consumer<Boolean> onToggle = null;

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
	public WToggleButton(Component label) {
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
		this(new Texture(onImage), new Texture(offImage));
	}

	/**
	 * Constructs a toggle button with custom images.
	 *
	 * @param onImage  the toggled on image
	 * @param offImage the toggled off image
	 * @param label    the button label
	 */
	public WToggleButton(Identifier onImage, Identifier offImage, Component label) {
		this(new Texture(onImage), new Texture(offImage), label);
	}

	/**
	 * Constructs a toggle button with custom images and no label.
	 *
	 * @param onImage  the toggled on image
	 * @param offImage the toggled off image
	 * @since 3.0.0
	 */
	public WToggleButton(Texture onImage, Texture offImage) {
		this.onImage = onImage;
		this.offImage = offImage;
	}

	/**
	 * Constructs a toggle button with custom images.
	 *
	 * @param onImage  the toggled on image
	 * @param offImage the toggled off image
	 * @param label    the button label
	 * @since 3.0.0
	 */
	public WToggleButton(Texture onImage, Texture offImage, Component label) {
		this.onImage = onImage;
		this.offImage = offImage;
		this.label = label;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(GuiGraphicsExtractor context, int x, int y, int mouseX, int mouseY) {
		ScreenDrawing.texturedRect(context, x, y, 18, 18, isOn ? onImage : offImage, 0xFFFFFFFF);
		if (isFocused()) {
			ScreenDrawing.texturedRect(context, x, y, 18, 18, focusImage, 0xFFFFFFFF);
		}

		if (label!=null) {
			ScreenDrawing.drawString(context, label.getVisualOrderText(), x + 22, y+6, shouldRenderInDarkMode() ? darkmodeColor : color);
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
	public InputResult onClick(MouseButtonEvent click, boolean doubled) {
		onClick();
		return InputResult.PROCESSED;
	}

	@Override
	public InputResult onKeyPressed(KeyEvent input) {
		if (input.isSelection()) {
			onClick();
			return InputResult.PROCESSED;
		}

		return InputResult.IGNORED;
	}

	@Environment(EnvType.CLIENT)
	private void onClick() {
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

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

	public @Nullable Consumer<Boolean> getOnToggle() {
		return this.onToggle;
	}

	public WToggleButton setOnToggle(@Nullable Consumer<Boolean> onToggle) {
		this.onToggle = onToggle;
		return this;
	}

	public @Nullable Component getLabel() {
		return label;
	}

	public WToggleButton setLabel(@Nullable Component label) {
		this.label = label;
		return this;
	}

	public WToggleButton setColor(int light, int dark) {
		this.color = light;
		this.darkmodeColor = dark;

		return this;
	}

	public Texture getOnImage() {
		return onImage;
	}

	public WToggleButton setOnImage(Texture onImage) {
		this.onImage = onImage;
		return this;
	}

	public Texture getOffImage() {
		return offImage;
	}

	public WToggleButton setOffImage(Texture offImage) {
		this.offImage = offImage;
		return this;
	}

	public Texture getFocusImage() {
		return focusImage;
	}

	public WToggleButton setFocusImage(Texture focusImage) {
		this.focusImage = focusImage;
		return this;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void addNarrations(NarrationElementOutput builder) {
		Component onOff = isOn ? NarrationMessages.TOGGLE_BUTTON_ON : NarrationMessages.TOGGLE_BUTTON_OFF;
		Component title;

		if (label != null) {
			title = Component.translatable(NarrationMessages.TOGGLE_BUTTON_NAMED_KEY, label, onOff);
		} else {
			title = Component.translatable(NarrationMessages.TOGGLE_BUTTON_UNNAMED_KEY, onOff);
		}

		builder.add(NarratedElementType.TITLE, title);

		if (isFocused()) {
			builder.add(NarratedElementType.USAGE, NarrationMessages.Vanilla.BUTTON_USAGE_FOCUSED);
		} else if (isHovered()) {
			builder.add(NarratedElementType.USAGE, NarrationMessages.Vanilla.BUTTON_USAGE_HOVERED);
		}
	}
}
