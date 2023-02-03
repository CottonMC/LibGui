package io.github.cottonmc.cotton.gui.impl.client;

import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import juuxel.libninepatch.ContextualTextureRenderer;
import org.lwjgl.opengl.GL11;

/**
 * An implementation of LibNinePatch's {@link ContextualTextureRenderer} for identifiers.
 */
public enum NinePatchTextureRendererImpl implements ContextualTextureRenderer<AbstractTexture, MatrixStack> {
	INSTANCE;

	@Override
	public void draw(AbstractTexture texture, MatrixStack matrices, int x, int y, int width, int height, float u1, float v1, float u2, float v2) {
		ScreenDrawing.texturedRectTiled(matrices, x, y, width, height, texture, u1, v1, u2, v2, 0xFF_FFFFFF, 1.0f, GL11.GL_REPEAT);
	}

	@Override
	public void drawTiled(AbstractTexture texture, MatrixStack matrices, int x, int y, int width, int height, int tileWidth, int tileHeight) {
		float numHorizontalTiles = width / ((float) tileWidth);
		float numVerticalTiles = height / ((float) tileHeight);
		ScreenDrawing.texturedRectTiled(matrices, x, y, width, height, texture, 0, 0, numHorizontalTiles, numVerticalTiles, 0xFF_FFFFFF, 1.0f, GL11.GL_REPEAT);
	}


}
