package io.github.cottonmc.cotton.gui.impl.mixin.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;

import io.github.cottonmc.cotton.gui.impl.client.ItemUseChecker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Prevents LibGui screens from being opened in a dev environment
// using Item.use/useOnBlock/useOnEntity.
@Mixin(Gui.class)
abstract class GuiMixin {
	@Inject(method = "setScreen", at = @At("HEAD"))
	private void onSetScreen(Screen screen, CallbackInfo info) {
		ItemUseChecker.checkSetScreen(screen);
	}
}
