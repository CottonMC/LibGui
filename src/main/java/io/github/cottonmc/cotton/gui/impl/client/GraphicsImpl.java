package io.github.cottonmc.cotton.gui.impl.client;

import io.github.cottonmc.cotton.gui.client.Graphics;

import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public class GraphicsImpl implements Graphics {
	private final MatrixStack matrices;
	private final TextRenderer textRenderer;
	private final VertexConsumerProvider.Immediate vertexConsumers;

	public GraphicsImpl(MatrixStack matrices) {
		this.matrices = matrices;
		MinecraftClient client = MinecraftClient.getInstance();
		this.vertexConsumers = client.getBufferBuilders().getEntityVertexConsumers();
		this.textRenderer = client.textRenderer;
	}

	@Override
	public MatrixStack getMatrices() {
		return matrices;
	}

	@Override
	public VertexConsumerProvider getVertexConsumers() {
		return vertexConsumers;
	}

	@Override
	public void texture(Identifier texture, int x, int y, float u1, float v1, float u2, float v2, int width, int height, int color) {
		VertexConsumer buffer = vertexConsumers.getBuffer(Layers.getTextured(texture));
		Matrix4f matrix = matrices.peek().getModel();

		int alpha = (color & 0xFF_000000) >> 24;
		int red = (color & 0x00_FF0000) >> 16;
		int green = (color & 0x00_00FF00) >> 8;
		int blue = (color & 0x00_0000FF);

		buffer.vertex(matrix, x, y + height, 0).color(red, green, blue, alpha).texture(u1, v2).next();
		buffer.vertex(matrix, x + width, y + height, 0).color(red, green, blue, alpha).texture(u2, v2).next();
		buffer.vertex(matrix, x + width, y, 0).color(red, green, blue, alpha).texture(u2, v1).next();
		buffer.vertex(matrix, x, y, 0).color(red, green, blue, alpha).texture(u1, v1).next();
	}

	@Override
	public void rect(int x, int y, int width, int height, int color) {
		VertexConsumer buffer = vertexConsumers.getBuffer(Layers.COLORED);
		Matrix4f matrix = matrices.peek().getModel();

		int alpha = (color & 0xFF_000000) >> 24;
		int red = (color & 0x00_FF0000) >> 16;
		int green = (color & 0x00_00FF00) >> 8;
		int blue = (color & 0x00_0000FF);

		buffer.vertex(matrix, x, y + height, 0).color(red, green, blue, alpha).next();
		buffer.vertex(matrix, x + width, y + height, 0).color(red, green, blue, alpha).next();
		buffer.vertex(matrix, x + width, y, 0).color(red, green, blue, alpha).next();
		buffer.vertex(matrix, x, y, 0).color(red, green, blue, alpha).next();
	}

	@Override
	public void text(String text, int x, int y, int color) {
		textRenderer.draw(matrices, text, x, y, color);
	}

	@Override
	public void text(OrderedText text, int x, int y, int color) {
		textRenderer.draw(matrices, text, x, y, color);
	}

	@Override
	public void text(Text text, int x, int y, int color) {
		textRenderer.draw(matrices, text, x, y, color);
	}

	@Override
	public void text(String text, HorizontalAlignment align, int x, int y, int width, int color, boolean shadow) {
		switch(align) {
			case LEFT: {
				if (shadow) {
					textRenderer.drawWithShadow(matrices, text, x, y, color);
				} else {
					textRenderer.draw(matrices, text, x, y, color);
				}
			}
			break;
			case CENTER: {
				int wid = textRenderer.getWidth(text);
				int l = (width / 2) - (wid / 2);
				if (shadow) {
					textRenderer.drawWithShadow(matrices, text, x + l, y, color);
				} else {
					textRenderer.draw(matrices, text, x + l, y, color);
				}
			}
			break;
			case RIGHT: {
				int wid = textRenderer.getWidth(text);
				int l = width - wid;
				if (shadow) {
					textRenderer.drawWithShadow(matrices, text, x + l, y, color);
				} else {
					textRenderer.draw(matrices, text, x + l, y, color);
				}
			}
			break;
		}
	}

	@Override
	public void text(OrderedText text, HorizontalAlignment align, int x, int y, int width, int color, boolean shadow) {
		switch(align) {
			case LEFT: {
				if (shadow) {
					textRenderer.drawWithShadow(matrices, text, x, y, color);
				} else {
					textRenderer.draw(matrices, text, x, y, color);
				}
			}
			break;
			case CENTER: {
				int wid = textRenderer.getWidth(text);
				int l = (width / 2) - (wid / 2);
				if (shadow) {
					textRenderer.drawWithShadow(matrices, text, x + l, y, color);
				} else {
					textRenderer.draw(matrices, text, x + l, y, color);
				}
			}
			break;
			case RIGHT: {
				int wid = textRenderer.getWidth(text);
				int l = width - wid;
				if (shadow) {
					textRenderer.drawWithShadow(matrices, text, x + l, y, color);
				} else {
					textRenderer.draw(matrices, text, x + l, y, color);
				}
			}
			break;
		}
	}

	@Override
	public void text(Text text, HorizontalAlignment align, int x, int y, int width, int color, boolean shadow) {
		switch(align) {
			case LEFT: {
				if (shadow) {
					textRenderer.drawWithShadow(matrices, text, x, y, color);
				} else {
					textRenderer.draw(matrices, text, x, y, color);
				}
			}
			break;
			case CENTER: {
				int wid = textRenderer.getWidth(text);
				int l = (width / 2) - (wid / 2);
				if (shadow) {
					textRenderer.drawWithShadow(matrices, text, x + l, y, color);
				} else {
					textRenderer.draw(matrices, text, x + l, y, color);
				}
			}
			break;
			case RIGHT: {
				int wid = textRenderer.getWidth(text);
				int l = width - wid;
				if (shadow) {
					textRenderer.drawWithShadow(matrices, text, x + l, y, color);
				} else {
					textRenderer.draw(matrices, text, x + l, y, color);
				}
			}
			break;
		}
	}

	public void draw() {
		vertexConsumers.draw();
	}

	private static class Layers extends RenderPhase {
		static final RenderLayer COLORED = createColored();

		private Layers(String name, Runnable beginAction, Runnable endAction) {
			super(name, beginAction, endAction);
		}

		private static RenderLayer getTextured(Identifier texture) {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(texture, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).cull(DISABLE_CULLING).lightmap(DISABLE_LIGHTMAP).overlay(DISABLE_OVERLAY_COLOR).build(false);
			return RenderLayer.of("libgui_textured", VertexFormats.POSITION_COLOR_TEXTURE, VertexFormat.DrawMode.QUADS, 256, true, true, multiPhaseParameters);
		}

		private static RenderLayer createColored() {
			RenderLayer.MultiPhaseParameters multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder().transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(ENABLE_DIFFUSE_LIGHTING).alpha(ONE_TENTH_ALPHA).cull(DISABLE_CULLING).lightmap(DISABLE_LIGHTMAP).overlay(DISABLE_OVERLAY_COLOR).build(false);
			return RenderLayer.of("libgui_colored", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS, 256, true, true, multiPhaseParameters);
		}
	}
}
