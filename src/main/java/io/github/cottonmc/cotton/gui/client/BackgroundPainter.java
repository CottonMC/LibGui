package io.github.cottonmc.cotton.gui.client;

import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.util.Identifier;

public interface BackgroundPainter {
	/**
	 * Paint the specified panel to the screen.
	 * @param left The absolute position of the left of the panel, in gui-screen coordinates
	 * @param top The absolute position of the top of the panel, in gui-screen coordinates
	 * @param panel The panel being painted
	 */
	public void paintBackground(int left, int top, WWidget panel);
	
	
	
	public static BackgroundPainter VANILLA = (left, top, panel) -> {
		ScreenDrawing.drawGuiPanel(left-8, top-8, panel.getWidth()+16, panel.getHeight()+16);
	};

	public static BackgroundPainter SLOT = (left, top, panel) -> {
		if (!(panel instanceof WItemSlot)) {
			ScreenDrawing.drawBeveledPanel(left-1, top-1, panel.getWidth()+2, panel.getHeight()+2, 0xB8000000, 0x4C000000, 0xB8FFFFFF);
		} else {
			WItemSlot slot = (WItemSlot)panel;
			for(int x = 0; x < slot.getWidth()/18; ++x) {
				for(int y = 0; y < slot.getHeight()/18; ++y) {
					int lo = 0xB8000000;
					int bg = 0x4C000000;
					//this will cause a slightly discolored bottom border on vanilla backgrounds but it's necessary for color support, it shouldn't be *too* visible unless you're looking for it
					int hi = 0xB8FFFFFF;
					if (slot.isBigSlot()) {
						ScreenDrawing.drawBeveledPanel((x * 18) + left - 4, (y * 18) + top - 4, 16+8, 16+8,
								lo, bg, hi);
					} else {
						ScreenDrawing.drawBeveledPanel((x * 18) + left - 1, (y * 18) + top - 1, 16+2, 16+2,
								lo, bg, hi);
					}
				}
			}
		}
	};
	
	public static BackgroundPainter createColorful(int panelColor) {
		return (left, top, panel) -> {
			ScreenDrawing.drawGuiPanel(left-8, top-8, panel.getWidth()+16, panel.getHeight()+16, panelColor);
		};
	}
	
	public static BackgroundPainter createColorful(int panelColor, float contrast) {
		return (left, top, panel) -> {
			int shadowColor = ScreenDrawing.multiplyColor(panelColor, 1.0f - contrast);
			int hilightColor = ScreenDrawing.multiplyColor(panelColor, 1.0f + contrast);
			
			ScreenDrawing.drawGuiPanel(left-8, top-8, panel.getWidth()+16, panel.getHeight()+16, shadowColor, panelColor, hilightColor, 0xFF000000);
		};
	}

	/**
	 * Utility method to call {@link NinePatch#NinePatch(Identifier)}.
	 *
	 * @param texture the background painter texture
	 * @return a new nine-patch background painter
	 */
	public static BackgroundPainter.NinePatch createNinePatch(Identifier texture) {
		return new NinePatch(texture);
	}

	/**
	 * Utility method to call {@link NinePatch#NinePatch(Identifier)}
	 * and set the padding of the nine-patch painter.
	 *
	 * @param texture the background painter texture
	 * @param padding the padding of the painter
	 * @return a new nine-patch background painter
	 */
	public static BackgroundPainter.NinePatch createNinePatch(Identifier texture, int padding) {
		return new NinePatch(texture).setPadding(padding);
	}

	/**
	 * The nine-patch background painter paints rectangles using a nine-patch texture.
	 *
	 * <p>Nine-patch textures are separated into nine sections: four corners, four edges and a center part.
	 * The edges and the center are stretched to fill the area between the corners.
	 *
	 * <p>{@code NinePatch} painters have a customizable padding that can be applied.
	 * For example, a GUI panel for a container block might have a padding of 8 pixels, like {@link BackgroundPainter#VANILLA}.
	 * You can set the padding using {@link NinePatch#setPadding(int)}.
	 */
	public static class NinePatch implements BackgroundPainter {
		private final Identifier texture;
		private final int cornerSize;
		private final float cornerUv;
		private int padding = 0;

		/**
		 * Creates a nine-patch background painter with 4 px corners and a 0.25 cornerUv (corner fraction of whole texture).
		 *
		 * @param texture the texture ID
		 */
		public NinePatch(Identifier texture) {
			this(texture, 4, 0.25f);
		}

		/**
		 * Creates a nine-patch background painter.
		 *
		 * @param texture the texture ID
		 * @param cornerSize the size of the corners on the screen
		 * @param cornerUv the fraction of the corners of the whole texture
		 */
		public NinePatch(Identifier texture, int cornerSize, float cornerUv) {
			this.texture = texture;
			this.cornerSize = cornerSize;
			this.cornerUv = cornerUv;
		}

		public int getPadding() {
			return padding;
		}

		public NinePatch setPadding(int padding) {
			this.padding = padding;
			return this;
		}

		public Identifier getTexture() {
			return texture;
		}

		public int getCornerSize() {
			return cornerSize;
		}

		public float getCornerUv() {
			return cornerUv;
		}

		@Override
		public void paintBackground(int left, int top, WWidget panel) {
			int width = panel.getWidth() + 2 * padding;
			int height = panel.getHeight() + 2 * padding;
			left = left - padding;
			top = top - padding;
			int x1 = left + cornerSize;
			int x2 = left + width - cornerSize;
			int y1 = top + cornerSize;
			int y2 = top + height - cornerSize;
			float uv1 = cornerUv;
			float uv2 = 1.0f - cornerUv;

			ScreenDrawing.texturedRect(left, top, cornerSize, cornerSize, texture, 0, 0, uv1, uv1, 0xFF_FFFFFF);
			ScreenDrawing.texturedRect(x2, top, cornerSize, cornerSize, texture, uv2, 0, 1, uv1, 0xFF_FFFFFF);
			ScreenDrawing.texturedRect(left, y2, cornerSize, cornerSize, texture, 0, uv2, uv1, 1, 0xFF_FFFFFF);
			ScreenDrawing.texturedRect(x2, y2, cornerSize, cornerSize, texture, uv2, uv2, 1, 1, 0xFF_FFFFFF);

			ScreenDrawing.texturedRect(x1, top, width - 2 * cornerSize, cornerSize, texture, uv1, 0, uv2, uv1, 0xFF_FFFFFF);
			ScreenDrawing.texturedRect(left, y1, cornerSize, height - 2 * cornerSize, texture, 0, uv1, uv1, uv2, 0xFF_FFFFFF);
			ScreenDrawing.texturedRect(x1, y2, width - 2 * cornerSize, cornerSize, texture, uv1, uv2, uv2, 1, 0xFF_FFFFFF);
			ScreenDrawing.texturedRect(x2, y1, cornerSize, height - 2 * cornerSize, texture, uv2, uv1, 1, uv2, 0xFF_FFFFFF);

			ScreenDrawing.texturedRect(x1, y1, width - 2 * cornerSize, height - 2 * cornerSize, texture, uv1, uv1, uv2, uv2, 0xFF_FFFFFF);
		}
	}
}
