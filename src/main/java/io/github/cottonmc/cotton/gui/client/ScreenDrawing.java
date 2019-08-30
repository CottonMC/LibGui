package io.github.cottonmc.cotton.gui.client;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.class_4493.class_4534;
import net.minecraft.class_4493.class_4535;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;

public class ScreenDrawing {
	private ScreenDrawing() {}
	
	public static void texturedRect(int left, int top, int width, int height, Identifier texture, int color) {
		texturedRect(left, top, width, height, texture, 0, 0, 1, 1, color);
	}

	public static void texturedRect(int left, int top, int width, int height, Identifier texture, float u1, float v1, float u2, float v2, int color) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
		
		//float scale = 0.00390625F;
		
		if (width <= 0) width = 1;
		if (height <= 0) height = 1;
		
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBufferBuilder();
		RenderSystem.enableBlend();
		//GlStateManager.disableTexture2D();
		RenderSystem.blendFuncSeparate(class_4535.SRC_ALPHA, class_4534.ONE_MINUS_SRC_ALPHA, class_4535.ONE, class_4534.ZERO);
		RenderSystem.color4f(r, g, b, 1.0f);
		buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV); //I thought GL_QUADS was deprecated but okay, sure.
		buffer.vertex(left,         top + height, 0).texture(u1, v2).next();
		buffer.vertex(left + width, top + height, 0).texture(u2, v2).next();
		buffer.vertex(left + width, top,          0).texture(u2, v1).next();
		buffer.vertex(left,         top,          0).texture(u1, v1).next();
		tessellator.draw();
		//GlStateManager.enableTexture2D();
		RenderSystem.disableBlend();
	}
	
	/**
	 * Draws an untextured rectangle of the specified RGB color.
	 */
	public static void coloredRect(int left, int top, int width, int height, int color) {
		if (width <= 0) width = 1;
		if (height <= 0) height = 1;
		
		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBufferBuilder();
		RenderSystem.enableBlend();
		RenderSystem.disableTexture();
		RenderSystem.blendFuncSeparate(class_4535.SRC_ALPHA, class_4534.ONE_MINUS_SRC_ALPHA, class_4535.ONE, class_4534.ZERO);
		RenderSystem.color4f(r, g, b, a);
		buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION); //I thought GL_QUADS was deprecated but okay, sure.
		buffer.vertex(left,         top + height, 0.0D).next();
		buffer.vertex(left + width, top + height, 0.0D).next();
		buffer.vertex(left + width, top,          0.0D).next();
		buffer.vertex(left,         top,          0.0D).next();
		tessellator.draw();
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}
	
	public static void maskedRect(Identifier mask, Identifier texture, int left, int top, int width, int height) {
		
		
		texturedRect(left, top, width, height, mask, 0, 0, 1, 1, 0xFFFFFFFF); //TODO: 7 Z
		
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_EQUAL);
		
		texturedRect(left, top, width, height, texture, 0, 0, 1, 1, 0xFFFFFFFF); //, 7);
		
		RenderSystem.depthFunc(GL11.GL_LESS);
		RenderSystem.disableDepthTest();
	}

	/**
	 * Draws a rectangle for a Fluid, because fluids are tough.
	 */
	/*
	public static void rect(Fluid fluid, int left, int top, int width, int height, float u1, float v1, float u2, float v2, int color) {
		Identifier fluidTexture = fluid.getStill();

		TextureAtlasSprite tas = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidTexture.toString());
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		if (width <= 0) width = 1;
		if (height <= 0) height = 1;

		float a = (color >> 24 & 255) / 255.0F;
		float r = (color >> 16 & 255) / 255.0F;
		float g = (color >> 8 & 255) / 255.0F;
		float b = (color & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBufferBuilder();
		GlStateManager.enableBlend();
		//GlStateManager.disableTexture2D();
		GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color4f(r, g, b, a);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX); //I thought GL_QUADS was deprecated but okay, sure.
		buffer.pos(left,         top + height, 0.0D).tex(tas.getInterpolatedU(u1), tas.getInterpolatedV(v2)).endVertex();
		buffer.pos(left + width, top + height, 0.0D).tex(tas.getInterpolatedU(u2), tas.getInterpolatedV(v2)).endVertex();
		buffer.pos(left + width, top,          0.0D).tex(tas.getInterpolatedU(u2), tas.getInterpolatedV(v1)).endVertex();
		buffer.pos(left,         top,          0.0D).tex(tas.getInterpolatedU(u1), tas.getInterpolatedV(v1)).endVertex();
		tessellator.draw();
		//GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}*/
	
	/*
	public static void rect(Fluid fluid, int left, int top, int width, int height, int color) {
		rect(fluid, left, top, width, height, 0, 0, 16, 16, color);
	}*/
	
	/**
	 * Draws a beveled, round rectangle that is substantially similar to default Minecraft UI panels.
	 */
	public static void drawGuiPanel(int x, int y, int width, int height) {
		if (LibGuiClient.config.darkMode) drawGuiPanel(x, y, width, height, 0xFF0B0B0B, 0xFF2F2F2F, 0xFF414141, 0xFF000000);
		else drawGuiPanel(x, y, width, height, 0xFF555555, 0xFFC6C6C6, 0xFFFFFFFF, 0xFF000000);
	}
	
	public static void drawGuiPanel(int x, int y, int width, int height, int panelColor) {
		int shadowColor = multiplyColor(panelColor, 0.50f);
		int hilightColor = multiplyColor(panelColor, 1.25f);
		
		drawGuiPanel(x, y, width, height, shadowColor, panelColor, hilightColor, 0xFF000000);
	}
	
	public static void drawGuiPanel(int x, int y, int width, int height, int shadow, int panel, int hilight, int outline) {
		coloredRect(x + 3,         y + 3,          width - 6, height - 6, panel); //Main panel area
		
		coloredRect(x + 2,         y + 1,          width - 4, 2,          hilight); //Top hilight
		coloredRect(x + 2,         y + height - 3, width - 4, 2,          shadow); //Bottom shadow
		coloredRect(x + 1,         y + 2,          2,         height - 4, hilight); //Left hilight
		coloredRect(x + width - 3, y + 2,          2,         height - 4, shadow); //Right shadow
		coloredRect(x + width - 3, y + 2,          1,         1,          panel); //Topright non-hilight/non-shadow transition pixel
		coloredRect(x + 2,         y + height - 3, 1,         1,          panel); //Bottomleft non-hilight/non-shadow transition pixel
		coloredRect(x + 3,         y + 3,          1,         1,          hilight); //Topleft round hilight pixel
		coloredRect(x + width - 4, y + height - 4, 1,         1,          shadow); //Bottomright round shadow pixel
		
		coloredRect(x + 2,         y,              width - 4, 1,          outline); //Top outline
		coloredRect(x,             y + 2,          1,         height - 4, outline); //Left outline
		coloredRect(x + width - 1, y + 2,          1,         height - 4, outline); //Right outline
		coloredRect(x + 2,         y + height - 1, width - 4, 1,          outline); //Bottom outline
		coloredRect(x + 1,         y + 1,          1,         1,          outline); //Topleft round pixel
		coloredRect(x + 1,         y + height - 2, 1,         1,          outline); //Bottomleft round pixel
		coloredRect(x + width - 2, y + 1,          1,         1,          outline); //Topright round pixel
		coloredRect(x + width - 2, y + height - 2, 1,         1,          outline); //Bottomright round pixel
	}

	/**
	 * Draws a default-sized recessed itemslot panel
	 */
	public static void drawBeveledPanel(int x, int y) {
		drawBeveledPanel(x, y, 18, 18, 0xFF373737, 0xFF8b8b8b, 0xFFFFFFFF);
	}
	
	/**
	 * Draws a default-color recessed itemslot panel of variable size
	 */
	public static void drawBeveledPanel(int x, int y, int width, int height) {
		drawBeveledPanel(x, y, width, height, 0xFF373737, 0xFF8b8b8b, 0xFFFFFFFF);
	}
	
	/**
	 * Draws a generalized-case beveled panel. Can be inset or outset depending on arguments.
	 * @param x				x coordinate of the topleft corner
	 * @param y				y coordinate of the topleft corner
	 * @param width			width of the panel
	 * @param height		height of the panel
	 * @param topleft		color of the top/left bevel
	 * @param panel			color of the panel area
	 * @param bottomright	color of the bottom/right bevel
	 */
	public static void drawBeveledPanel(int x, int y, int width, int height, int topleft, int panel, int bottomright) {
		coloredRect(x,             y,              width,     height,     panel); //Center panel
		coloredRect(x,             y,              width - 1, 1,          topleft); //Top shadow
		coloredRect(x,             y + 1,          1,         height - 2, topleft); //Left shadow
		coloredRect(x + width - 1, y + 1,          1,         height - 1, bottomright); //Right hilight
		coloredRect(x + 1,         y + height - 1, width - 1, 1,          bottomright); //Bottom hilight
	}
	
	public static void drawString(String s, int x, int y, int color) {
		MinecraftClient.getInstance().textRenderer.draw(s, x, y, color);
		//MinecraftClient.getInstance().getFontManager().getTextRenderer(MinecraftClient.DEFAULT_TEXT_RENDERER_ID).draw(s, x, y, color);
	}
	
	public static void drawCenteredWithShadow(String s, int x, int y, int color) {
		TextRenderer render = MinecraftClient.getInstance().getFontManager().getTextRenderer(MinecraftClient.DEFAULT_TEXT_RENDERER_ID);
		render.drawWithShadow(s, (float)(x - render.getStringWidth(s) / 2), (float)y, color);
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
