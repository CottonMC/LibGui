package io.github.cottonmc.cotton.gui.impl.client;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import juuxel.libninepatch.TextureRenderer;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An implementation of LibNinePatch's {@link TextureRenderer} for identifiers.
 */
public final class NinePatchTextureRendererImpl implements TextureRenderer<Identifier>, AutoCloseable {
	private static final NinePatchTextureRendererImpl INSTANCE = new NinePatchTextureRendererImpl();
	private final Deque<MatrixStack> matrixStackStack = new ArrayDeque<>();

	private NinePatchTextureRendererImpl() {}

	// TODO: Replace this with a context system in LNP
	public static NinePatchTextureRendererImpl bind(MatrixStack matrices) {
		INSTANCE.matrixStackStack.push(matrices);
		return INSTANCE;
	}

	@Override
	public void draw(Identifier texture, int x, int y, int width, int height, float u1, float v1, float u2, float v2) {
		ScreenDrawing.texturedRect(matrixStackStack.peek(), x, y, width, height, texture, u1, v1, u2, v2, 0xFF_FFFFFF);
	}

	@Override
	public void close() {
		matrixStackStack.pop();
	}
}
