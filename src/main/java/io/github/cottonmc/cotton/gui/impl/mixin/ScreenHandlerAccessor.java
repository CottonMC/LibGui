package io.github.cottonmc.cotton.gui.impl.mixin;

import net.minecraft.screen.ScreenHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ScreenHandler.class)
public interface ScreenHandlerAccessor {
	@Accessor("disableSync")
	boolean libgui$getDisableSync();
}
