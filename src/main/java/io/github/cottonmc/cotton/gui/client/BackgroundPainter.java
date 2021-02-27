package io.github.cottonmc.cotton.gui.client;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WWidget;

/**
 * Background painters are used to paint the background of a widget.
 * The background painter instance of a widget can be changed to customize the look of a widget.
 */
@FunctionalInterface
public interface BackgroundPainter {
	/**
	 * Paint the specified panel to the screen.
	 * @param left The absolute position of the left of the panel, in gui-screen coordinates
	 * @param top The absolute position of the top of the panel, in gui-screen coordinates
	 * @param panel The panel being painted
	 */
	public void paintBackground(MatrixStack matrices, int left, int top, WWidget panel);

	/**
	 * The {@code VANILLA} background painter draws a vanilla-like gui panel using {@linkplain NinePatch nine-patch textures}.
	 *
	 * <p>This background painter uses {@code libgui:textures/widget/panel_light.png} as the light texture and
	 * {@code libgui:textures/widget/panel_dark.png} as the dark texture.
	 *
	 * <p>This background painter is the default painter for root panels.
	 * 	 * You can override {@link io.github.cottonmc.cotton.gui.GuiDescription#addPainters()} to customize the painter yourself.
	 *
	 * @since 1.5.0
	 */
	public static BackgroundPainter VANILLA = createLightDarkVariants(
			createNinePatch(new Identifier(LibGuiCommon.MOD_ID, "textures/widget/panel_light.png")),
			createNinePatch(new Identifier(LibGuiCommon.MOD_ID, "textures/widget/panel_dark.png"))
	);

	/**
	 * The {@code SLOT} background painter draws item slots or slot-like widgets.
	 */
	public static BackgroundPainter SLOT = (matrices, left, top, panel) -> {
		if (!(panel instanceof WItemSlot)) {
			ScreenDrawing.drawBeveledPanel(matrices, left-1, top-1, panel.getWidth()+2, panel.getHeight()+2, 0xB8000000, 0x4C000000, 0xB8FFFFFF);
		} else {
			WItemSlot slot = (WItemSlot)panel;
			for(int x = 0; x < slot.getWidth()/18; ++x) {
				for(int y = 0; y < slot.getHeight()/18; ++y) {
					int index = x + y * (slot.getWidth() / 18);
					int lo = 0xB8000000;
					int bg = 0x4C000000;
					//this will cause a slightly discolored bottom border on vanilla backgrounds but it's necessary for color support, it shouldn't be *too* visible unless you're looking for it
					int hi = 0xB8FFFFFF;
					if (slot.isBigSlot()) {
						ScreenDrawing.drawBeveledPanel(matrices, (x * 18) + left - 4, (y * 18) + top - 4, 26, 26,
								lo, bg, hi);
						if (slot.getFocusedSlot() == index) {
							int sx = (x * 18) + left - 4;
							int sy = (y * 18) + top - 4;
							ScreenDrawing.coloredRect(matrices, sx,          sy,          26,     1,      0xFF_FFFFA0);
							ScreenDrawing.coloredRect(matrices, sx,          sy + 1,      1,      26 - 1, 0xFF_FFFFA0);
							ScreenDrawing.coloredRect(matrices, sx + 26 - 1, sy + 1,      1,      26 - 1, 0xFF_FFFFA0);
							ScreenDrawing.coloredRect(matrices, sx + 1,      sy + 26 - 1, 26 - 1, 1,      0xFF_FFFFA0);
						}
					} else {
						ScreenDrawing.drawBeveledPanel(matrices, (x * 18) + left, (y * 18) + top, 16+2, 16+2,
								lo, bg, hi);
						if (slot.getFocusedSlot() == index) {
							int sx = (x * 18) + left;
							int sy = (y * 18) + top;
							ScreenDrawing.coloredRect(matrices, sx,          sy,          18,     1,      0xFF_FFFFA0);
							ScreenDrawing.coloredRect(matrices, sx,          sy + 1,      1,      18 - 1, 0xFF_FFFFA0);
							ScreenDrawing.coloredRect(matrices, sx + 18 - 1, sy + 1,      1,      18 - 1, 0xFF_FFFFA0);
							ScreenDrawing.coloredRect(matrices, sx + 1,      sy + 18 - 1, 18 - 1, 1,      0xFF_FFFFA0);
						}
					}
				}
			}
		}
	};

	/**
	 * Creates a colorful gui panel painter. This painter paints the panel using the specified color.
	 *
	 * @param panelColor the panel background color
	 * @return a colorful gui panel painter
	 * @see ScreenDrawing#drawGuiPanel(MatrixStack, int, int, int, int, int)
	 */
	public static BackgroundPainter createColorful(int panelColor) {
		return (matrices, left, top, panel) -> {
			ScreenDrawing.drawGuiPanel(matrices, left, top, panel.getWidth(), panel.getHeight(), panelColor);
		};
	}

	/**
	 * Creates a colorful gui panel painter that has a custom contrast between the shadows and highlights.
	 *
	 * @param panelColor the panel background color
	 * @param contrast the contrast between the shadows and highlights
	 * @return a colorful gui panel painter
	 */
	public static BackgroundPainter createColorful(int panelColor, float contrast) {
		return (matrices, left, top, panel) -> {
			int shadowColor = ScreenDrawing.multiplyColor(panelColor, 1.0f - contrast);
			int hilightColor = ScreenDrawing.multiplyColor(panelColor, 1.0f + contrast);
			
			ScreenDrawing.drawGuiPanel(matrices, left, top, panel.getWidth(), panel.getHeight(), shadowColor, panelColor, hilightColor, 0xFF000000);
		};
	}

	/**
	 * Creates a new nine-patch background painter.
	 *
	 * <p>This method is equivalent to {@code new NinePatch(texture)}.
	 *
	 * @param texture the background painter texture
	 * @return a new nine-patch background painter
	 * @since 1.5.0
	 */
	public static NinePatch createNinePatch(Identifier texture) {
		return new NinePatch(texture);
	}

	/**
	 * Creates a new nine-patch background painter with a custom padding.
	 *
	 * <p>This method is equivalent to {@code new NinePatch(texture).setPadding(padding)}.
	 *
	 * @param texture the background painter texture
	 * @param padding the padding of the painter
	 * @return a new nine-patch background painter
	 * @since 1.5.0
	 */
	public static NinePatch createNinePatch(Identifier texture, int padding) {
		return new NinePatch(texture).setPadding(padding);
	}

	/**
	 * Creates a background painter that uses either the {@code light} or the {@code dark} background painter
	 * depending on the current setting.
	 *
	 * @param light the light mode background painter
	 * @param dark the dark mode background painter
	 * @return a new background painter that chooses between the two inputs
	 * @since 1.5.0
	 */
	public static BackgroundPainter createLightDarkVariants(BackgroundPainter light, BackgroundPainter dark) {
		return (matrices, left, top, panel) -> {
			if (LibGui.isDarkMode()) dark.paintBackground(matrices, left, top, panel);
			else light.paintBackground(matrices, left, top, panel);
		};
	}
}
