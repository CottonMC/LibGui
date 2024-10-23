package io.github.cottonmc.cotton.gui.impl.client;

import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.TriState;
import net.minecraft.util.Util;

import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;

import java.util.function.Function;

public final class LibGuiShaders {
	public static final ShaderProgramKey TILED_RECTANGLE = new ShaderProgramKey(
		LibGuiCommon.id("core/tiled_rectangle"),
		VertexFormats.POSITION, Defines.EMPTY
	);

	public static final Function<Identifier, RenderLayer> TILED_RECTANGLE_LAYER = Util.memoize(texture -> RenderLayer.of(
		"libgui:tiled_gui_rectangle",
		VertexFormats.POSITION,
		VertexFormat.DrawMode.QUADS,
		RenderLayer.CUTOUT_BUFFER_SIZE,
		RenderLayer.MultiPhaseParameters.builder()
			.texture(new RenderPhase.Texture(texture, TriState.FALSE, false))
			.program(new RenderPhase.ShaderProgram(TILED_RECTANGLE))
			.transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
			.depthTest(RenderPhase.LEQUAL_DEPTH_TEST)
			.build(false)
	));

	static void register() {
		// Register our core shaders.
		// The tiled rectangle shader is used for performant tiled texture rendering.
		ShaderProgramKeys.getAll().add(TILED_RECTANGLE);
	}
}
