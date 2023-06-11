package io.github.cottonmc.cotton.gui.impl.client;

import com.mojang.blaze3d.systems.RenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import juuxel.libninepatch.ContextualTextureRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

/**
 * An implementation of LibNinePatch's {@link ContextualTextureRenderer} for identifiers.
 */
public enum NinePatchTextureRendererImpl implements ContextualTextureRenderer<Identifier, DrawContext> {
	INSTANCE;

	@Override
	public void draw(Identifier texture, DrawContext context, int x, int y, int width, int height, float u1, float v1, float u2, float v2) {
		ScreenDrawing.texturedRect(context, x, y, width, height, texture, u1, v1, u2, v2, 0xFF_FFFFFF);
	}

	@Override
	public void drawTiled(Identifier texture, DrawContext context, int x, int y, int regionWidth, int regionHeight, int tileWidth, int tileHeight, float u1, float v1, float u2, float v2) {
		RenderSystem.setShader(LibGuiShaders::getTiledRectangle);
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		Matrix4f positionMatrix = context.getMatrices().peek().getPositionMatrix();
		onRenderThread(() -> {
			@Nullable ShaderProgram program = RenderSystem.getShader();
			if (program != null) {
				program.getUniformOrDefault("LibGuiRectanglePos").set((float) x, (float) y);
				program.getUniformOrDefault("LibGuiTileDimensions").set((float) tileWidth, (float) tileHeight);
				program.getUniformOrDefault("LibGuiTileUvs").set(u1, v1, u2, v2);
				program.getUniformOrDefault("LibGuiPositionMatrix").set(positionMatrix);
			}
		});

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		RenderSystem.enableBlend();
		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
		buffer.vertex(positionMatrix, x, y, 0).next();
		buffer.vertex(positionMatrix, x, y + regionHeight, 0).next();
		buffer.vertex(positionMatrix, x + regionWidth, y + regionHeight, 0).next();
		buffer.vertex(positionMatrix, x + regionWidth, y, 0).next();
		BufferRenderer.drawWithGlobalProgram(buffer.end());
		RenderSystem.disableBlend();
	}

	private static void onRenderThread(RenderCall renderCall) {
		if (RenderSystem.isOnRenderThread()) {
			renderCall.execute();
		} else {
			RenderSystem.recordRenderCall(renderCall);
		}
	}
}
