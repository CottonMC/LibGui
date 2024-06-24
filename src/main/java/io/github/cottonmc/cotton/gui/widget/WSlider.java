package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import org.jetbrains.annotations.Nullable;

/**
 * A simple slider widget that can be used to select int values.
 *
 * @see WAbstractSlider for supported listeners
 */
public class WSlider extends WAbstractSlider {
	public static final int TRACK_WIDTH = 6;
	public static final int THUMB_SIZE = 8;
	public static final Identifier LIGHT_TEXTURE = LibGuiCommon.id("textures/widget/slider_light.png");
	public static final Identifier DARK_TEXTURE = LibGuiCommon.id("textures/widget/slider_dark.png");

	@Environment(EnvType.CLIENT)
	@Nullable
	private BackgroundPainter backgroundPainter;

	public WSlider(int min, int max, Axis axis) {
		super(min, max, axis);
	}

	@Override
	protected int getThumbWidth() {
		return THUMB_SIZE;
	}

	@Override
	protected boolean isMouseInsideBounds(int x, int y) {
		// ao = axis-opposite mouse coordinate, aoCenter = center of ao's axis
		int ao = axis == Axis.HORIZONTAL ? y : x;
		int aoCenter = (axis == Axis.HORIZONTAL ? height : width) / 2;

		// Check if cursor is inside or <=2px away from track
		return ao >= aoCenter - TRACK_WIDTH / 2 - 2 && ao <= aoCenter + TRACK_WIDTH / 2 + 2;
	}

	@SuppressWarnings("SuspiciousNameCombination")
	@Environment(EnvType.CLIENT)
	@Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
		if (backgroundPainter != null) {
			backgroundPainter.paintBackground(context, x, y, this);
		} else {
			float px = 1 / 32f;
			// thumbX/Y: thumb position in widget-space
			int thumbX, thumbY;
			// thumbXOffset: thumb texture x offset in pixels
			int thumbXOffset;
			Identifier texture = shouldRenderInDarkMode() ? DARK_TEXTURE : LIGHT_TEXTURE;

			if (axis == Axis.VERTICAL) {
				int trackX = x + width / 2 - TRACK_WIDTH / 2;
				thumbX = width / 2 - THUMB_SIZE / 2;
				thumbY = direction == Direction.UP
						? (height - THUMB_SIZE) + 1 - (int) (coordToValueRatio * (value - min))
						: Math.round(coordToValueRatio * (value - min));
				thumbXOffset = 0;

				ScreenDrawing.texturedRect(context, trackX, y + 1, TRACK_WIDTH, 1, texture, 16*px, 0*px, 22*px, 1*px, 0xFFFFFFFF);
				ScreenDrawing.texturedRect(context, trackX, y + 2, TRACK_WIDTH, height - 2, texture, 16*px, 1*px, 22*px, 2*px, 0xFFFFFFFF);
				ScreenDrawing.texturedRect(context, trackX, y + height, TRACK_WIDTH, 1, texture, 16*px, 2*px, 22*px, 3*px, 0xFFFFFFFF);
			} else {
				int trackY = y + height / 2 - TRACK_WIDTH / 2;
				thumbX = direction == Direction.LEFT
						? (width - THUMB_SIZE) - (int) (coordToValueRatio * (value - min))
						: Math.round(coordToValueRatio * (value - min));
				thumbY = height / 2 - THUMB_SIZE / 2;
				thumbXOffset = 8;

				ScreenDrawing.texturedRect(context, x, trackY, 1, TRACK_WIDTH, texture, 16*px, 3*px, 17*px, 9*px, 0xFFFFFFFF);
				ScreenDrawing.texturedRect(context, x + 1, trackY, width - 2, TRACK_WIDTH, texture, 17*px, 3*px, 18*px, 9*px, 0xFFFFFFFF);
				ScreenDrawing.texturedRect(context, x + width - 1, trackY, 1, TRACK_WIDTH, texture, 18*px, 3*px, 19*px, 9*px, 0xFFFFFFFF);
			}

			// thumbState values:
			// 0: default, 1: dragging, 2: hovered
			int thumbState = dragging ? 1 : (mouseX >= thumbX && mouseX <= thumbX + THUMB_SIZE && mouseY >= thumbY && mouseY <= thumbY + THUMB_SIZE ? 2 : 0);
			ScreenDrawing.texturedRect(context, x + thumbX, y + thumbY, THUMB_SIZE, THUMB_SIZE, texture, thumbXOffset*px, 0*px + thumbState * 8*px, (thumbXOffset + 8)*px, 8*px + thumbState * 8*px, 0xFFFFFFFF);

			if (thumbState == 0 && isFocused()) {
				ScreenDrawing.texturedRect(context, x + thumbX, y + thumbY, THUMB_SIZE, THUMB_SIZE, texture, 0*px, 24*px, 8*px, 32*px, 0xFFFFFFFF);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Nullable
	public BackgroundPainter getBackgroundPainter() {
		return backgroundPainter;
	}

	@Environment(EnvType.CLIENT)
	public void setBackgroundPainter(@Nullable BackgroundPainter backgroundPainter) {
		this.backgroundPainter = backgroundPainter;
	}
}
