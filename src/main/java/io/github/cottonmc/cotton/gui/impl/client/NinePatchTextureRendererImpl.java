package io.github.cottonmc.cotton.gui.impl.client;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import juuxel.libninepatch.ContextualTextureRenderer;

/**
 * An implementation of LibNinePatch's {@link ContextualTextureRenderer} for identifiers.
 */
public enum NinePatchTextureRendererImpl implements ContextualTextureRenderer<Identifier, MatrixStack> {
	INSTANCE;

	@Override
	public void draw(Identifier texture, MatrixStack matrices, int x, int y, int width, int height, float u1, float v1, float u2, float v2) {
		ScreenDrawing.texturedRect(matrices, x, y, width, height, texture, u1, v1, u2, v2, 0xFF_FFFFFF);
	}
}
