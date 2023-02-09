package io.github.cottonmc.cotton.gui.impl.client;

import java.util.Objects;

import net.minecraft.client.gl.ShaderProgram;

import org.jetbrains.annotations.Nullable;

public final class LibGuiShaders {
	private static @Nullable ShaderProgram tiled;

	public static ShaderProgram getTiled() {
		return Objects.requireNonNull(tiled, "Shader 'libgui_tiled' not initialised!");
	}

	public static void setTiled(ShaderProgram program) {
		tiled = program;
	}
}
