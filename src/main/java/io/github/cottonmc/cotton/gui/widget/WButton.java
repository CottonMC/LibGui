package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.client.NarrationMessages;
import io.github.cottonmc.cotton.gui.impl.client.WidgetTextures;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public class WButton extends WWidget {
	private static final int ICON_SPACING = 2;

	private @Nullable Component label;
	protected int color = WLabel.DEFAULT_TEXT_COLOR;
	/**
	 * The size (width/height) of this button's icon in pixels.
	 * @since 6.4.0
	 */
	protected int iconSize = 16;
	private boolean enabled = true;
	protected HorizontalAlignment alignment = HorizontalAlignment.CENTER;
	
	private @Nullable Runnable onClick;
	private @Nullable Icon icon = null;

	/**
	 * Constructs a button with no label and no icon.
	 */
	public WButton() {
		
	}

	/**
	 * Constructs a button with an icon.
	 *
	 * @param icon the icon
	 * @since 2.2.0
	 */
	public WButton(@Nullable Icon icon) {
		this.icon = icon;
	}

	/**
	 * Constructs a button with a label.
	 *
	 * @param label the label
	 */
	public WButton(@Nullable Component label) {
		this.label = label;
	}

	/**
	 * Constructs a button with an icon and a label.
	 *
	 * @param icon  the icon
	 * @param label the label
	 * @since 2.2.0
	 */
	public WButton(@Nullable Icon icon, @Nullable Component label) {
		this.icon = icon;
		this.label = label;
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
	public void paint(GuiGraphicsExtractor context, int x, int y, int mouseX, int mouseY) {
		boolean hovered = isWithinBounds(mouseX, mouseY);
		WidgetSprites textures = WidgetTextures.getButtonTextures(shouldRenderInDarkMode());
		context.blitSprite(RenderPipelines.GUI_TEXTURED, textures.get(enabled, hovered || isFocused()), x, y, getWidth(), getHeight());

		if (icon != null) {
			icon.paint(context, x+ICON_SPACING, y+(getHeight()-iconSize)/2, iconSize);
		}
		
		if (label!=null) {
			int color = 0xFF_E0E0E0;
			if (!enabled) {
				color = 0xFF_A0A0A0;
			}

			int xOffset = (icon != null && alignment == HorizontalAlignment.LEFT) ? ICON_SPACING+iconSize+ICON_SPACING : 0;
			ScreenDrawing.drawStringWithShadow(context, label.getVisualOrderText(), alignment, x + xOffset, y + ((getHeight() - 8) / 2), width, color); //LibGuiClient.config.darkMode ? darkmodeColor : color);
		}
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public InputResult onClick(MouseButtonEvent click, boolean doubled) {
		return onClick((int) click.x(), (int) click.y());
	}

	@Environment(EnvType.CLIENT)
	@Override
	public InputResult onKeyPressed(KeyEvent input) {
		if (input.isSelection()) {
			return onClick(0, 0);
		}

		return InputResult.IGNORED;
	}

	@Environment(EnvType.CLIENT)
	private InputResult onClick(int x, int y) {
		if (enabled && isWithinBounds(x, y)) {
			Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));

			if (onClick!=null) onClick.run();
			return InputResult.PROCESSED;
		}

		return InputResult.IGNORED;
	}

	/**
	 * Gets the click handler of this button.
	 *
	 * @return the click handler
	 * @since 2.2.0
	 */
	public @Nullable Runnable getOnClick() {
		return onClick;
	}

	/**
	 * Sets the click handler of this button.
	 *
	 * @param onClick the new click handler
	 * @return this button
	 */
	public WButton setOnClick(@Nullable Runnable onClick) {
		this.onClick = onClick;
		return this;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public WButton setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public @Nullable Component getLabel() {
		return label;
	}

	public WButton setLabel(Component label) {
		this.label = label;
		return this;
	}

	public HorizontalAlignment getAlignment() {
		return alignment;
	}

	public WButton setAlignment(HorizontalAlignment alignment) {
		this.alignment = alignment;
		return this;
	}

	/**
	 * Gets the current height / width of the icon.
	 *
	 * @return the current height / width of the icon
	 * @since 6.4.0
	 */
	public int getIconSize() {
		return iconSize;
	}

	/**
	 * Sets the new size of the icon.
	 *
	 * @param iconSize the new height and width of the icon
	 * @return this button
	 * @since 6.4.0
	 */
	public WButton setIconSize(int iconSize) {
		this.iconSize = iconSize;
		return this;
	}

	/**
	 * Gets the icon of this button.
	 *
	 * @return the icon
	 * @since 2.2.0
	 */
	public @Nullable Icon getIcon() {
		return icon;
	}

	/**
	 * Sets the icon of this button.
	 *
	 * @param icon the new icon
	 * @return this button
	 * @since 2.2.0
	 */
	public WButton setIcon(@Nullable Icon icon) {
		this.icon = icon;
		return this;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void addNarrations(NarrationElementOutput builder) {
		Component label = Objects.requireNonNullElse(getLabel(), CommonComponents.EMPTY);
		builder.add(NarratedElementType.TITLE, AbstractWidget.wrapDefaultNarrationMessage(label));

		if (isEnabled()) {
			if (isFocused()) {
				builder.add(NarratedElementType.USAGE, NarrationMessages.Vanilla.BUTTON_USAGE_FOCUSED);
			} else if (isHovered()) {
				builder.add(NarratedElementType.USAGE, NarrationMessages.Vanilla.BUTTON_USAGE_HOVERED);
			}
		}
	}
}
