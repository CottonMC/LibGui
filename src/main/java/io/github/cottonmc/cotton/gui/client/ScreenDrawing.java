package io.github.cottonmc.cotton.gui.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

/**
 * {@code ScreenDrawing} contains utility methods for drawing contents on a screen.
 */
public class ScreenDrawing {
	private ScreenDrawing() {}

	/**
	 * Draws a textured rectangle.
	 *
	 * @param context the draw context
	 * @param x       the x coordinate of the box on-screen
	 * @param y       the y coordinate of the box on-screen
	 * @param width   the width of the box on-screen
	 * @param height  the height of the box on-screen
	 * @param texture the Identifier for the texture
	 * @param color   a color to tint the texture. This can be transparent! Use 0xFF_FFFFFF if you don't want a color tint
	 */
	public static void texturedRect(DrawContext context, int x, int y, int width, int height, Identifier texture, int color) {
		texturedRect(context, x, y, width, height, texture, 0, 0, 1, 1, color, 1.0f);
	}

	/**
	 * Draws a textured rectangle.
	 *
	 * @param context the draw context
	 * @param x       the x coordinate of the box on-screen
	 * @param y       the y coordinate of the box on-screen
	 * @param width   the width of the box on-screen
	 * @param height  the height of the box on-screen
	 * @param texture the Identifier for the texture
	 * @param color   a color to tint the texture. This can be transparent! Use 0xFF_FFFFFF if you don't want a color tint
	 * @param opacity opacity of the drawn texture. (0f is fully opaque and 1f is fully visible)
	 * @since 2.0.0
	 */
	public static void texturedRect(DrawContext context, int x, int y, int width, int height, Identifier texture, int color, float opacity) {
		texturedRect(context, x, y, width, height, texture, 0, 0, 1, 1, color, opacity);
	}

	/**
	 * Draws a textured rectangle.
	 *
	 * @param context the draw context
	 * @param x       the x coordinate of the box on-screen
	 * @param y       the y coordinate of the box on-screen
	 * @param width   the width of the box on-screen
	 * @param height  the height of the box on-screen
	 * @param texture the Identifier for the texture
	 * @param u1      the left edge of the texture
	 * @param v1      the top edge of the texture
	 * @param u2      the right edge of the texture
	 * @param v2      the bottom edge of the texture
	 * @param color   a color to tint the texture. This can be transparent! Use 0xFF_FFFFFF if you don't want a color tint
	 */
	public static void texturedRect(DrawContext context, int x, int y, int width, int height, Identifier texture, float u1, float v1, float u2, float v2, int color) {
		texturedRect(context, x, y, width, height, texture, u1, v1, u2, v2, color, 1.0f);
	}

	/**
	 * Draws a textured rectangle.
	 *
	 * @param context the draw context
	 * @param x       the x coordinate of the box on-screen
	 * @param y       the y coordinate of the box on-screen
	 * @param width   the width of the box on-screen
	 * @param height  the height of the box on-screen
	 * @param texture the texture
	 * @param color   a color to tint the texture. This can be transparent! Use 0xFF_FFFFFF if you don't want a color tint
	 * @since 3.0.0
	 */
	public static void texturedRect(DrawContext context, int x, int y, int width, int height, Texture texture, int color) {
		texturedRect(context, x, y, width, height, texture, color, 1.0f);
	}

	/**
	 * Draws a textured rectangle.
	 *
	 * @param context the draw context
	 * @param x       the x coordinate of the box on-screen
	 * @param y       the y coordinate of the box on-screen
	 * @param width   the width of the box on-screen
	 * @param height  the height of the box on-screen
	 * @param texture the texture
	 * @param color   a color to tint the texture. This can be transparent! Use 0xFF_FFFFFF if you don't want a color tint
	 * @param opacity opacity of the drawn texture. (0f is fully opaque and 1f is fully visible)
	 * @since 3.0.0
	 */
	public static void texturedRect(DrawContext context, int x, int y, int width, int height, Texture texture, int color, float opacity) {
		texturedRect(context, x, y, width, height, texture.image(), texture.u1(), texture.v1(), texture.u2(), texture.v2(), color, opacity);
	}

	/**
	 * Draws a textured rectangle.
	 *
	 * @param context the draw context
	 * @param x       the x coordinate of the box on-screen
	 * @param y       the y coordinate of the box on-screen
	 * @param width   the width of the box on-screen
	 * @param height  the height of the box on-screen
	 * @param texture the Identifier for the texture
	 * @param u1      the left edge of the texture
	 * @param v1      the top edge of the texture
	 * @param u2      the right edge of the texture
	 * @param v2      the bottom edge of the texture
	 * @param color   a color to tint the texture. This can be transparent! Use 0xFF_FFFFFF if you don't want a color tint
	 * @param opacity opacity of the drawn texture. (0f is fully opaque and 1f is fully visible)
	 * @since 2.0.0
	 */
	public static void texturedRect(DrawContext context, int x, int y, int width, int height, Identifier texture, float u1, float v1, float u2, float v2, int color, float opacity) {
		if (width <= 0) width = 1;
		if (height <= 0) height = 1;

		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		Matrix4f model = context.getMatrices().peek().getPositionMatrix();
		RenderSystem.enableBlend();
		RenderSystem.setShaderTexture(0, texture);
		RenderSystem.setShaderColor(r, g, b, opacity);
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		buffer.vertex(model, x,         y + height, 0).texture(u1, v2).next();
		buffer.vertex(model, x + width, y + height, 0).texture(u2, v2).next();
		buffer.vertex(model, x + width, y,          0).texture(u2, v1).next();
		buffer.vertex(model, x,         y,          0).texture(u1, v1).next();
		BufferRenderer.drawWithGlobalProgram(buffer.end());
		RenderSystem.disableBlend();
	}

	/**
	 * Draws a textured rectangle with UV values based on the width and height.
	 *
	 * <p>If the texture is 256x256, this draws the texture at one pixel per texel.
	 *
	 * @param context  the draw context
	 * @param x        the x coordinate of the box on-screen
	 * @param y        the y coordinate of the box on-screen
	 * @param width    the width of the box on-screen
	 * @param height   the height of the box on-screen
	 * @param texture  the Identifier for the texture
	 * @param textureX the x offset into the texture
	 * @param textureY the y offset into the texture
	 * @param color    a color to tint the texture. This can be transparent! Use 0xFF_FFFFFF if you don't want a color tint
	 */
	public static void texturedGuiRect(DrawContext context, int x, int y, int width, int height, Identifier texture, int textureX, int textureY, int color) {
		float px = 1/256f;
		texturedRect(context, x, y, width, height, texture, textureX*px, textureY*px, (textureX+width)*px, (textureY+height)*px, color);
	}

	/**
	 * Draws a textured rectangle with UV values based on the width and height.
	 *
	 * <p>If the texture is 256x256, this draws the texture at one pixel per texel.
	 *
	 * @param context the draw context
	 * @param left    the x coordinate of the box on-screen
	 * @param top     the y coordinate of the box on-screen
	 * @param width   the width of the box on-screen
	 * @param height  the height of the box on-screen
	 * @param texture the Identifier for the texture
	 * @param color   a color to tint the texture. This can be transparent! Use 0xFF_FFFFFF if you don't want a color tint
	 */
	public static void texturedGuiRect(DrawContext context, int left, int top, int width, int height, Identifier texture, int color) {
		texturedGuiRect(context, left, top, width, height, texture, 0, 0, color);
	}

	/**
	 * Draws an untextured rectangle of the specified RGB color.
	 */
	public static void coloredRect(DrawContext context, int left, int top, int width, int height, int color) {
		if (width <= 0) width = 1;
		if (height <= 0) height = 1;

		context.fill(left, top, left + width, top + height, color);
	}

	/**
	 * Draws a beveled, round rectangle that is substantially similar to default Minecraft UI panels.
	 *
	 * @param context the draw context
	 * @param x       the X position of the panel
	 * @param y       the Y position of the panel
	 * @param width   the width of the panel
	 * @param height  the height of the panel
	 */
	public static void drawGuiPanel(DrawContext context, int x, int y, int width, int height) {
		if (LibGui.isDarkMode()) drawGuiPanel(context, x, y, width, height, 0xFF0B0B0B, 0xFF2F2F2F, 0xFF414141, 0xFF000000);
		else drawGuiPanel(context, x, y, width, height, 0xFF555555, 0xFFC6C6C6, 0xFFFFFFFF, 0xFF000000);
	}

	/**
	 * Draws a beveled, round, and colored rectangle that is substantially similar to default Minecraft UI panels.
	 *
	 * @param context    the draw context
	 * @param x          the X position of the panel
	 * @param y          the Y position of the panel
	 * @param width      the width of the panel
	 * @param height     the height of the panel
	 * @param panelColor the panel ARGB color
	 */
	public static void drawGuiPanel(DrawContext context, int x, int y, int width, int height, int panelColor) {
		int shadowColor = multiplyColor(panelColor, 0.50f);
		int hilightColor = multiplyColor(panelColor, 1.25f);

		drawGuiPanel(context, x, y, width, height, shadowColor, panelColor, hilightColor, 0xFF000000);
	}

	/**
	 * Draws a beveled, round rectangle with custom edge colors that is substantially similar to default Minecraft UI panels.
	 *
	 * @param context the draw context
	 * @param x       the X position of the panel
	 * @param y       the Y position of the panel
	 * @param width   the width of the panel
	 * @param height  the height of the panel
	 * @param shadow  the bottom/right shadow ARGB color
	 * @param panel   the center ARGB color
	 * @param hilight the top/left hilight ARGB color
	 * @param outline the outline ARGB color
	 */
	public static void drawGuiPanel(DrawContext context, int x, int y, int width, int height, int shadow, int panel, int hilight, int outline) {
		coloredRect(context, x + 3,         y + 3,          width - 6, height - 6, panel); //Main panel area

		coloredRect(context, x + 2,         y + 1,          width - 4, 2,          hilight); //Top hilight
		coloredRect(context, x + 2,         y + height - 3, width - 4, 2,          shadow); //Bottom shadow
		coloredRect(context, x + 1,         y + 2,          2,         height - 4, hilight); //Left hilight
		coloredRect(context, x + width - 3, y + 2,          2,         height - 4, shadow); //Right shadow
		coloredRect(context, x + width - 3, y + 2,          1,         1,          panel); //Topright non-hilight/non-shadow transition pixel
		coloredRect(context, x + 2,         y + height - 3, 1,         1,          panel); //Bottomleft non-hilight/non-shadow transition pixel
		coloredRect(context, x + 3,         y + 3,          1,         1,          hilight); //Topleft round hilight pixel
		coloredRect(context, x + width - 4, y + height - 4, 1,         1,          shadow); //Bottomright round shadow pixel

		coloredRect(context, x + 2,         y,              width - 4, 1,          outline); //Top outline
		coloredRect(context, x,             y + 2,          1,         height - 4, outline); //Left outline
		coloredRect(context, x + width - 1, y + 2,          1,         height - 4, outline); //Right outline
		coloredRect(context, x + 2,         y + height - 1, width - 4, 1,          outline); //Bottom outline
		coloredRect(context, x + 1,         y + 1,          1,         1,          outline); //Topleft round pixel
		coloredRect(context, x + 1,         y + height - 2, 1,         1,          outline); //Bottomleft round pixel
		coloredRect(context, x + width - 2, y + 1,          1,         1,          outline); //Topright round pixel
		coloredRect(context, x + width - 2, y + height - 2, 1,         1,          outline); //Bottomright round pixel
	}

	/**
	 * Draws a default-sized recessed itemslot panel
	 */
	public static void drawBeveledPanel(DrawContext context, int x, int y) {
		drawBeveledPanel(context, x, y, 18, 18, 0xFF373737, 0xFF8b8b8b, 0xFFFFFFFF);
	}

	/**
	 * Draws a default-color recessed itemslot panel of variable size
	 */
	public static void drawBeveledPanel(DrawContext context, int x, int y, int width, int height) {
		drawBeveledPanel(context, x, y, width, height, 0xFF373737, 0xFF8b8b8b, 0xFFFFFFFF);
	}

	/**
	 * Draws a generalized-case beveled panel. Can be inset or outset depending on arguments.
	 *
	 * @param context     the draw context
	 * @param x           x coordinate of the topleft corner
	 * @param y           y coordinate of the topleft corner
	 * @param width       width of the panel
	 * @param height      height of the panel
	 * @param topleft     color of the top/left bevel
	 * @param panel       color of the panel area
	 * @param bottomright color of the bottom/right bevel
	 */
	public static void drawBeveledPanel(DrawContext context, int x, int y, int width, int height, int topleft, int panel, int bottomright) {
		coloredRect(context, x,             y,              width,     height,     panel); //Center panel
		coloredRect(context, x,             y,              width - 1, 1,          topleft); //Top shadow
		coloredRect(context, x,             y + 1,          1,         height - 2, topleft); //Left shadow
		coloredRect(context, x + width - 1, y + 1,          1,         height - 1, bottomright); //Right hilight
		coloredRect(context, x + 1,         y + height - 1, width - 1, 1,          bottomright); //Bottom hilight
	}

	/**
	 * Draws a string with a custom alignment.
	 *
	 * @param context the draw context
	 * @param s       the string
	 * @param align   the alignment of the string
	 * @param x       the X position
	 * @param y       the Y position
	 * @param width   the width of the string, used for aligning
	 * @param color   the text color
	 */
	public static void drawString(DrawContext context, String s, HorizontalAlignment align, int x, int y, int width, int color) {
		var textRenderer = MinecraftClient.getInstance().textRenderer;
		switch (align) {
			case LEFT -> {
				context.drawText(textRenderer, s, x, y, color, false);
			}

			case CENTER -> {
				int wid = textRenderer.getWidth(s);
				int l = (width / 2) - (wid / 2);
				context.drawText(textRenderer, s, x + l, y, color, false);
			}

			case RIGHT -> {
				int wid = textRenderer.getWidth(s);
				int l = width - wid;
				context.drawText(textRenderer, s, x + l, y, color, false);
			}
		}
	}

	/**
	 * Draws a text component with a custom alignment.
	 *
	 * @param context the draw context
	 * @param text    the text
	 * @param align   the alignment of the string
	 * @param x       the X position
	 * @param y       the Y position
	 * @param width   the width of the string, used for aligning
	 * @param color   the text color
	 * @since 1.9.0
	 */
	public static void drawString(DrawContext context, OrderedText text, HorizontalAlignment align, int x, int y, int width, int color) {
		var textRenderer = MinecraftClient.getInstance().textRenderer;
		switch (align) {
			case LEFT -> {
				context.drawText(textRenderer, text, x, y, color, false);
			}

			case CENTER -> {
				int wid = textRenderer.getWidth(text);
				int l = (width / 2) - (wid / 2);
				context.drawText(textRenderer, text, x + l, y, color, false);
			}

			case RIGHT -> {
				int wid = textRenderer.getWidth(text);
				int l = width - wid;
				context.drawText(textRenderer, text, x + l, y, color, false);
			}
		}
	}

	/**
	 * Draws a shadowed string.
	 *
	 * @param context the draw context
	 * @param s       the string
	 * @param align   the alignment of the string
	 * @param x       the X position
	 * @param y       the Y position
	 * @param width   the width of the string, used for aligning
	 * @param color   the text color
	 */
	public static void drawStringWithShadow(DrawContext context, String s, HorizontalAlignment align, int x, int y, int width, int color) {
		var textRenderer = MinecraftClient.getInstance().textRenderer;
		switch (align) {
			case LEFT -> {
				context.drawText(textRenderer, s, x, y, color, true);
			}

			case CENTER -> {
				int wid = textRenderer.getWidth(s);
				int l = (width / 2) - (wid / 2);
				context.drawText(textRenderer, s, x + l, y, color, true);
			}

			case RIGHT -> {
				int wid = textRenderer.getWidth(s);
				int l = width - wid;
				context.drawText(textRenderer, s, x + l, y, color, true);
			}
		}
	}

	/**
	 * Draws a shadowed text component.
	 *
	 * @param context the draw context
	 * @param text    the text component
	 * @param align   the alignment of the string
	 * @param x       the X position
	 * @param y       the Y position
	 * @param width   the width of the string, used for aligning
	 * @param color   the text color
	 */
	public static void drawStringWithShadow(DrawContext context, OrderedText text, HorizontalAlignment align, int x, int y, int width, int color) {
		var textRenderer = MinecraftClient.getInstance().textRenderer;
		switch (align) {
			case LEFT -> {
				context.drawText(textRenderer, text, x, y, color, true);
			}

			case CENTER -> {
				int wid = textRenderer.getWidth(text);
				int l = (width / 2) - (wid / 2);
				context.drawText(textRenderer, text, x + l, y, color, true);
			}

			case RIGHT -> {
				int wid = textRenderer.getWidth(text);
				int l = width - wid;
				context.drawText(textRenderer, text, x + l, y, color, true);
			}
		}
	}

	/**
	 * Draws a left-aligned string.
	 *
	 * @param context the draw context
	 * @param s       the string
	 * @param x       the X position
	 * @param y       the Y position
	 * @param color   the text color
	 */
	public static void drawString(DrawContext context, String s, int x, int y, int color) {
		context.drawText(MinecraftClient.getInstance().textRenderer, s, x, y, color, false);
	}

	/**
	 * Draws a left-aligned text component.
	 *
	 * @param context the draw context
	 * @param text    the text component
	 * @param x       the X position
	 * @param y       the Y position
	 * @param color   the text color
	 */
	public static void drawString(DrawContext context, OrderedText text, int x, int y, int color) {
		context.drawText(MinecraftClient.getInstance().textRenderer, text, x, y, color, false);
	}

	/**
	 * Draws the text hover effects for a text style.
	 *
	 * <p>This method has no effect when the caller is not in a LibGui screen.
	 * For example, there will be nothing drawn in HUDs.
	 *
	 * @param context   the draw context
	 * @param textStyle the text style
	 * @param x         the X position
	 * @param y         the Y position
	 * @since 4.0.0
	 */
	public static void drawTextHover(DrawContext context, @Nullable Style textStyle, int x, int y) {
		context.drawHoverEvent(MinecraftClient.getInstance().textRenderer, textStyle, x, y);
	}

	public static int colorAtOpacity(int opaque, float opacity) {
		if (opacity<0.0f) opacity=0.0f;
		if (opacity>1.0f) opacity=1.0f;

		int a = (int)(opacity * 255.0f);

		return (opaque & 0xFFFFFF) | (a << 24);
	}

	public static int multiplyColor(int color, float amount) {
		int a = color & 0xFF000000;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8  & 255) / 255.0F;
		float b = (color       & 255) / 255.0F;

		r = Math.min(r*amount, 1.0f);
		g = Math.min(g*amount, 1.0f);
		b = Math.min(b*amount, 1.0f);

		int ir = (int)(r*255);
		int ig = (int)(g*255);
		int ib = (int)(b*255);

		return
				 a |
				(ir << 16) |
				(ig <<  8) |
				 ib;
	}
}
