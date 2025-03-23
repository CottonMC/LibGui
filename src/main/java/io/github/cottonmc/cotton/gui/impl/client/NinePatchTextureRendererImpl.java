package io.github.cottonmc.cotton.gui.impl.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import juuxel.libninepatch.ContextualTextureRenderer;

/**
 * An implementation of LibNinePatch's {@link ContextualTextureRenderer} for identifiers.
 */
public enum NinePatchTextureRendererImpl implements ContextualTextureRenderer<Identifier, DrawContext> {
	INSTANCE;

	@Override
	public void draw(Identifier texture, DrawContext context, int x, int y, int width, int height, float u1, float v1, float u2, float v2) {
		ScreenDrawing.texturedRect(context, x, y, width, height, texture, u1, v1, u2, v2, 0xFF_FFFFFF);
	}

	/*@Override
	public void drawTiled(Identifier texture, DrawContext context, int x, int y, int regionWidth, int regionHeight, int tileWidth, int tileHeight, float u1, float v1, float u2, float v2) {
		var framebuffer = MinecraftClient.getInstance().getFramebuffer();
		var colorAttachment = framebuffer.getColorAttachment();
		var depthAttachment = framebuffer.getDepthAttachment();

		try (var renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(colorAttachment, OptionalInt.empty(), depthAttachment, OptionalDouble.empty())) {
			Matrix4f positionMatrix = context.getMatrices().peek().getPositionMatrix();
			renderPass.setUniform("LibGuiRectanglePos", (float) x, y);
			renderPass.setUniform("LibGuiTileDimensions", (float) tileWidth, tileHeight);
			renderPass.setUniform("LibGuiTileUvs", u1, v1, u2, v2);
			renderPass.setUniform("LibGuiPositionMatrix", positionMatrix);

			// TODO: The code below should be replaced with a manually built buffer
			//  that connects to the render pass above, see VertexConsumerProvider.Immediate.draw(RL, BB)
			RenderSystem.setShaderColor(1, 1, 1, 1);
			var renderLayer = LibGuiShaders.TILED_RECTANGLE_LAYER.apply(texture);
			var buffer = ((DrawContextAccessor) context).libgui$getVertexConsumers().getBuffer(renderLayer);
			buffer.vertex(positionMatrix, x, y, 0);
			buffer.vertex(positionMatrix, x, y + regionHeight, 0);
			buffer.vertex(positionMatrix, x + regionWidth, y + regionHeight, 0);
			buffer.vertex(positionMatrix, x + regionWidth, y, 0);
			context.draw();
		}
	}*/
}
