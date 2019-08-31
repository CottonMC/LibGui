package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.text.Text;

import javax.annotation.Nullable;

/**
 * A vanilla-style labeled slider widget.
 *
 * <p>In addition to the standard slider listeners,
 * labeled sliders also support "label updaters" that can update the label
 * when the value is changed.
 *
 * @see WAbstractSlider for more information about listeners
 */
public class WLabeledSlider extends WAbstractSlider {
	@Nullable private Text label = null;
	@Nullable private LabelUpdater labelUpdater = null;

	public WLabeledSlider(int min, int max) {
		super(min, max, Axis.HORIZONTAL);
	}

	public WLabeledSlider(int min, int max, Text label) {
		this(min, max);
		this.label = label;
	}


	@Override
	public void setSize(int x, int y) {
		super.setSize(x, 20);
	}

	@Nullable
	public Text getLabel() {
		return label;
	}

	public void setLabel(@Nullable Text label) {
		this.label = label;
	}

	@Override
	protected void onValueChanged(int value) {
		super.onValueChanged(value);
		if (labelUpdater != null) {
			label = labelUpdater.updateLabel(value);
		}
	}

	public void setLabelUpdater(@Nullable LabelUpdater labelUpdater) {
		this.labelUpdater = labelUpdater;
	}

	@Override
	protected int getThumbWidth() {
		return 8;
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
		int thumbX = Math.round(coordToValueRatio * (value - min));
		int thumbY = 0;
		int thumbWidth = getThumbWidth();
		int thumbHeight = height;
		boolean hovering = mouseX >= thumbX && mouseX <= thumbX + thumbWidth && mouseY >= thumbY && mouseY <= thumbY + thumbHeight;
		int thumbState = dragging || hovering ? 2 : 1;

		drawButton(x + thumbX, y + thumbY, thumbState, thumbWidth);

		if (thumbState == 1 && isFocused()) {
			float px = 1 / 32f;
			ScreenDrawing.texturedRect(x + thumbX, y + thumbY, thumbWidth, thumbHeight, WSlider.TEXTURE, 24*px, 0*px, 32*px, 20*px, 0xFFFFFFFF);
		}

		if (label != null) {
			int color = isMouseInsideBounds(mouseX, mouseY) ? 0xFFFFA0 : 0xE0E0E0;
			ScreenDrawing.drawCenteredWithShadow(label.asFormattedString(), x + width / 2, y + height / 2 - 4, color);
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

		ScreenDrawing.texturedRect(x, y, halfWidth, 20, AbstractButtonWidget.WIDGETS_LOCATION, buttonLeft, buttonTop, buttonLeft + buttonWidth, buttonTop + buttonHeight, 0xFFFFFFFF);
		ScreenDrawing.texturedRect(x + halfWidth, y, halfWidth, 20, AbstractButtonWidget.WIDGETS_LOCATION, buttonEndLeft, buttonTop, 200 * px, buttonTop + buttonHeight, 0xFFFFFFFF);
	}

	@FunctionalInterface
	public interface LabelUpdater {
		Text updateLabel(int value);
	}
}
