package io.github.cottonmc.cotton.gui.impl.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
abstract class HandledScreenMixin {
	@Inject(
			method = "renderMain",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/gui/DrawContext;IIF)V",
					shift = At.Shift.AFTER
			),
			allow = 1
	)
	private void onSuperRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
		if ((Object) this instanceof CottonInventoryScreen<?> cottonInventoryScreen) {
			cottonInventoryScreen.paintDescription(context, mouseX, mouseY, delta);
		}
	}
}
