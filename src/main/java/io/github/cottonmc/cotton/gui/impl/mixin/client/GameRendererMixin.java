package io.github.cottonmc.cotton.gui.impl.mixin.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceFactory;

import io.github.cottonmc.cotton.gui.impl.client.LibGuiShaders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
abstract class GameRendererMixin {
	@Inject(
			method = "loadPrograms",
			at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"),
			slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=rendertype_crumbling")),
			allow = 1,
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void libgui_onLoadPrograms(ResourceFactory factory, CallbackInfo info, List<?> shaderStages, List<Pair<ShaderProgram, Consumer<ShaderProgram>>> programs) throws IOException {
		programs.add(Pair.of(new ShaderProgram(factory, "libgui_tiled", VertexFormats.POSITION), LibGuiShaders::setTiled));
	}
}
