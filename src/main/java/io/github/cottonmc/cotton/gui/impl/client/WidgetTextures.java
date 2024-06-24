package io.github.cottonmc.cotton.gui.impl.client;

import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.impl.mixin.client.PressableWidgetAccessor;
import io.github.cottonmc.cotton.gui.impl.mixin.client.SliderWidgetAccessor;

import static io.github.cottonmc.cotton.gui.impl.LibGuiCommon.id;

public final class WidgetTextures {
	private static final ButtonTextures LIGHT_LABELED_SLIDER_HANDLE = new ButtonTextures(
			SliderWidgetAccessor.libgui$getHandleTexture(),
			SliderWidgetAccessor.libgui$getHandleHighlightedTexture()
	);
	private static final ButtonTextures DARK_LABELED_SLIDER_HANDLE = new ButtonTextures(
			id("widget/slider_handle_dark"),
			id("widget/slider_handle_highlighted_dark")
	);
	private static final ButtonTextures DARK_BUTTON = new ButtonTextures(
			id("widget/button_dark"),
			id("widget/button_disabled_dark"),
			id("widget/button_highlighted_dark")
	);
	private static final ScrollBarTextures LIGHT_SCROLL_BAR = new ScrollBarTextures(
			id("widget/scroll_bar/background_light"),
			id("widget/scroll_bar/thumb_light"),
			id("widget/scroll_bar/thumb_pressed_light"),
			id("widget/scroll_bar/thumb_hovered_light")
	);
	private static final ScrollBarTextures DARK_SCROLL_BAR = new ScrollBarTextures(
			id("widget/scroll_bar/background_dark"),
			id("widget/scroll_bar/thumb_dark"),
			id("widget/scroll_bar/thumb_pressed_dark"),
			id("widget/scroll_bar/thumb_hovered_dark")
	);

	public static ButtonTextures getButtonTextures(boolean dark) {
		return dark ? DARK_BUTTON : PressableWidgetAccessor.libgui$getTextures();
	}

	public static ButtonTextures getLabeledSliderHandleTextures(boolean dark) {
		return dark ? DARK_LABELED_SLIDER_HANDLE : LIGHT_LABELED_SLIDER_HANDLE;
	}

	public static ScrollBarTextures getScrollBarTextures(boolean dark) {
		return dark ? DARK_SCROLL_BAR : LIGHT_SCROLL_BAR;
	}

	public record ScrollBarTextures(Identifier background, Identifier thumb, Identifier thumbPressed,
									Identifier thumbHovered) {
	}
}
