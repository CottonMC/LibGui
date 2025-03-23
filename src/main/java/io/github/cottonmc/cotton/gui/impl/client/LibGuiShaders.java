package io.github.cottonmc.cotton.gui.impl.client;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;

import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;

import java.util.function.Function;

public final class LibGuiShaders {
	public static final RenderPipeline TILED_RECTANGLE = RenderPipeline.builder(RenderPipelines.MATRICES_COLOR_SNIPPET)
		.withVertexFormat(VertexFormats.POSITION, VertexFormat.DrawMode.QUADS)
		.withLocation(LibGuiCommon.id("pipeline/tiled_rectangle"))
		.withVertexShader(LibGuiCommon.id("core/tiled_rectangle"))
		.withFragmentShader(LibGuiCommon.id("core/tiled_rectangle"))
		.withBlend(BlendFunction.TRANSLUCENT)
		.withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
		.withUniform("LibGuiRectanglePos", UniformType.VEC2)
		.withUniform("LibGuiTileDimensions", UniformType.VEC2)
		.withUniform("LibGuiTileUvs", UniformType.VEC4)
		.withUniform("LibGuiPositionMatrix", UniformType.MATRIX4X4)
		.build();

	public static final Function<Identifier, RenderLayer> TILED_RECTANGLE_LAYER = Util.memoize(texture -> RenderLayer.of(
		"libgui:tiled_gui_rectangle",
		786432,
		TILED_RECTANGLE,
		RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(texture, TriState.FALSE, false))
			.build(false)
	));

	static void register() {
		// Register our pipelines.
		// The tiled rectangle shader is used for performant tiled texture rendering.
		RenderPipelines.register(TILED_RECTANGLE);
	}
}
