package io.github.cottonmc.cotton.gui.impl.mixin.client;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SliderWidget.class)
public interface SliderWidgetAccessor {
	@Accessor("TEXTURE")
	static Identifier libgui$getTexture() {
		throw new AssertionError();
	}

	@Accessor("HANDLE_TEXTURE")
	static Identifier libgui$getHandleTexture() {
		throw new AssertionError();
	}

	@Accessor("HANDLE_HIGHLIGHTED_TEXTURE")
	static Identifier libgui$getHandleHighlightedTexture() {
		throw new AssertionError();
	}
}
