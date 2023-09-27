package io.github.cottonmc.cotton.gui.impl.mixin.client;

import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.PressableWidget;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PressableWidget.class)
public interface PressableWidgetAccessor {
	@Accessor("TEXTURES")
	static ButtonTextures libgui$getTextures() {
		throw new AssertionError();
	}
}
