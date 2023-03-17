package io.github.cottonmc.cotton.gui.impl.client;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class LibGuiShaders {
	private static @Nullable ShaderProgram tiled;

	static void register() {
		CoreShaderRegistrationCallback.EVENT.register(context -> {
			// Register our core shaders.
			// The tiled shaders is used for performant tiled texture rendering.
			context.register(new Identifier(LibGuiCommon.MOD_ID, "tiled"), VertexFormats.POSITION, program -> tiled = program);
		});
	}

	public static ShaderProgram getTiled() {
		return Objects.requireNonNull(tiled, "Shader 'libgui_tiled' not initialised!");
	}
}
