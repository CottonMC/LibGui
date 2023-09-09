package io.github.cottonmc.cotton.gui.impl.client;

import io.github.cottonmc.cotton.gui.impl.mixin.client.SliderWidgetAccessor;

import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.impl.mixin.client.PressableWidgetAccessor;

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

	public static ButtonTextures getButtonTextures(boolean dark) {
		return dark ? DARK_BUTTON : PressableWidgetAccessor.libgui$getTextures();
	}

	public static ButtonTextures getLabeledSliderHandleTextures(boolean dark) {
		return dark ? DARK_LABELED_SLIDER_HANDLE : LIGHT_LABELED_SLIDER_HANDLE;
	}

	private static Identifier id(String path) {
		return new Identifier(LibGuiCommon.MOD_ID, path);
	}
}
