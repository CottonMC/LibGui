package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.util.Identifier;

/**
 * A vanilla-style labeled slider widget.
 *
 * @see WAbstractSlider for more information about listeners
 */
/*
	TODO:
    - Add the labels
    - Better textures for thumbs when dragging
    - The thumb goes 1px outside the track on the right side
 */
public class WLabeledSlider extends WAbstractSlider {
	private static final Identifier TEXTURE = AbstractButtonWidget.WIDGETS_LOCATION;

	public WLabeledSlider(int min, int max) {
		super(min, max, Axis.HORIZONTAL);
	}

	public WLabeledSlider(int max) {
		this(0, max);
	}

	@Override
	public void setSize(int x, int y) {
		super.setSize(x, 20);
	}

	@Override
	protected int getThumbWidth() {
		return 6;
	}

	@Override
	protected boolean isMouseInsideBounds(int x, int y) {
		return x >= 0 && x <= width && y >= 0 && y <= height;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paintBackground(int x, int y, int mouseX, int mouseY) {
		drawButton(x, y, 0, width);

		// 1: regular, 2: hovered, 0: disabled/dragging
		int thumbX = (int) (coordToValueRatio * (value - min));
		int thumbY = 0;
		int thumbWidth = getThumbWidth();
		int thumbHeight = height;
		int thumbState = dragging ? 0 : (mouseX >= thumbX && mouseX <= thumbX + thumbWidth && mouseY >= thumbY && mouseY <= thumbY + thumbHeight ? 2 : 1);

		drawButton(x + thumbX, y + thumbY, thumbState, thumbWidth);

		if (thumbState == 1 && isFocused()) {
			// TODO: draw the focus border
		}
	}

	// state = 1: regular, 2: hovered, 0: disabled/dragging
	@Environment(EnvType.CLIENT)
	private void drawButton(int x, int y, int state, int width) {
		float px = 1 / 256f;
		float buttonLeft = 0 * px;
		float buttonTop = (46 + (state * 20)) * px;
		int halfWidth = width / 2;
		if (halfWidth > 198) halfWidth = 198;
		float buttonWidth = halfWidth * px;
		float buttonHeight = 20 * px;
		float buttonEndLeft = (200 - halfWidth) * px;

		ScreenDrawing.rect(AbstractButtonWidget.WIDGETS_LOCATION, x, y, halfWidth, 20, buttonLeft, buttonTop, buttonLeft + buttonWidth, buttonTop + buttonHeight, 0xFFFFFFFF);
		ScreenDrawing.rect(AbstractButtonWidget.WIDGETS_LOCATION, x + halfWidth, y, halfWidth, 20, buttonEndLeft, buttonTop, 200 * px, buttonTop + buttonHeight, 0xFFFFFFFF);
	}
}
