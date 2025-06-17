package io.github.cottonmc.cotton.gui.impl.mixin.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DrawContext.class)
public interface DrawContextAccessor {
	@Invoker("drawTexturedQuad")
	void libgui$callDrawTexturedQuad(RenderPipeline pipeline, Identifier sprite, int x1, int x2, int y1, int y2, float u1, float u2, float v1, float v2, int color);
}
