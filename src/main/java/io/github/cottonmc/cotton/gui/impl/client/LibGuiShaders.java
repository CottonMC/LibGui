package io.github.cottonmc.cotton.gui.impl.client;

import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import org.jetbrains.annotations.Nullable;

public final class LibGuiShaders {
	private static @Nullable ShaderProgram tiledRectangle;

	static void register() {
		CoreShaderRegistrationCallback.EVENT.register(context -> {
			// Register our core shaders.
			// The tiled rectangle shader is used for performant tiled texture rendering.
			context.register(new Identifier(LibGuiCommon.MOD_ID, "tiled_rectangle"), VertexFormats.POSITION, program -> tiledRectangle = program);
		});
	}

	private static ShaderProgram assertPresent(ShaderProgram program, String name) {
		if (program == null) {
			throw new NullPointerException("Shader libgui:" + name + " not initialised!");
		}

		return program;
	}

	public static ShaderProgram getTiledRectangle() {
		return assertPresent(tiledRectangle, "tiled_rectangle");
	}
}
