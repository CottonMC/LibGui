package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;
import java.util.function.IntConsumer;

public class WSlider extends WWidget {
	private static final int TRACK_WIDTH = 6;
	private static final int THUMB_SIZE = 8;
	// TODO: Horizontal textures
	private static final Identifier TEXTURE = new Identifier("libgui", "textures/widget/slider.png");

	private final int min, max;
	private final int valueRange;
	private final Axis axis;

	private int value;
	private float valueToCoordRatio, coordToValueRatio;
	@Nullable private IntConsumer valueChangeListener = null;
	@Nullable private Runnable mouseReleaseListener = null;

	// Used for detecting dragging after the user starts dragging
	// on top of the slider, but then moves the mouse out but still within the widget's boundary.
	private boolean dragging = false;

	public WSlider(int min, int max, Axis axis) {
		if (max >= min)
			throw new IllegalArgumentException("Minimum value must be smaller than the maximum!");

		this.min = min;
		this.max = max;
		this.valueRange = max - min + 1;
		this.axis = axis;
		this.value = min;
	}

	public WSlider(int max, Axis axis) {
		this(0, max, axis);
	}

	@Override
	public void setSize(int x, int y) {
		super.setSize(x, y);
		int trackHeight = (axis == Axis.HORIZONTAL ? x : y) - THUMB_SIZE + 1;
		valueToCoordRatio = (float) valueRange / trackHeight;
		coordToValueRatio = 1 / valueToCoordRatio;
	}

	@Override
	public boolean canResize() {
		return true;
	}

	@Override
	public void onMouseDrag(int x, int y, int button) {
		// a = mouse coordinate on slider axis, axisWidth = width of slider axis
		// ao = axis-opposite mouse coordinate, aoCenter = center of ao's axis
		int a = axis == Axis.HORIZONTAL ? x : y;
		int axisWidth = axis == Axis.HORIZONTAL ? width : height;
		int ao = axis == Axis.HORIZONTAL ? y : x;
		int aoCenter = (axis == Axis.HORIZONTAL ? height : width) / 2;
		if (dragging || ao >= aoCenter - TRACK_WIDTH / 2 && ao <= aoCenter + TRACK_WIDTH / 2) {
			dragging = true;
			int pos = (axis == Axis.VERTICAL ? (axisWidth - a) : a) - THUMB_SIZE / 2;
			int futureValue = min + (int) (valueToCoordRatio * pos);
			value = MathHelper.clamp(futureValue, min, max);
			if (valueChangeListener != null) valueChangeListener.accept(value);
		}
	}

	@Override
	public void onClick(int x, int y, int button) {
		onMouseDrag(x, y, button);
		onMouseUp(x, y, button);
	}

	@Override
	public WWidget onMouseUp(int x, int y, int button) {
		dragging = false;
		if (mouseReleaseListener != null) mouseReleaseListener.run();

		return super.onMouseUp(x, y, button);
	}

	@Override
	public void paintBackground(int x, int y) {
		float px = 1 / 16f;
		if (axis == Axis.VERTICAL) {
			int trackX = x + width / 2 - TRACK_WIDTH / 2;
			int thumbY = y + height - THUMB_SIZE + 1 - (int) (coordToValueRatio * (value - min));

			ScreenDrawing.rect(TEXTURE, trackX, y + 1,      TRACK_WIDTH, 1,          0*px, 8*px,  6*px, 9*px,  0xFFFFFFFF);
			ScreenDrawing.rect(TEXTURE, trackX, y + 2,      TRACK_WIDTH, height - 2, 0*px, 9*px,  6*px, 10*px, 0xFFFFFFFF);
			ScreenDrawing.rect(TEXTURE, trackX, y + height, TRACK_WIDTH, 1,          0*px, 10*px, 6*px, 11*px, 0xFFFFFFFF);

			ScreenDrawing.rect(TEXTURE, x + width / 2 - THUMB_SIZE / 2, thumbY, THUMB_SIZE, THUMB_SIZE, 0*px, 0*px, 8*px, 8*px, 0xFFFFFFFF);
		} else {
			int trackY = y + height / 2 - TRACK_WIDTH / 2;
			int thumbX = x + (int) (coordToValueRatio * (value - min));

			ScreenDrawing.rect(TEXTURE, x, trackY, 1, TRACK_WIDTH, 8*px, 0*px, 9*px, 6*px, 0xFFFFFFFF);
			ScreenDrawing.rect(TEXTURE, x + 1, trackY, width - 2, TRACK_WIDTH, 9*px, 0*px, 10*px, 6*px, 0xFFFFFFFF);
			ScreenDrawing.rect(TEXTURE, x + width - 1, trackY, 1, TRACK_WIDTH, 10*px, 0*px, 11*px, 6*px, 0xFFFFFFFF);

			ScreenDrawing.rect(TEXTURE, thumbX, y + height / 2 - THUMB_SIZE / 2, THUMB_SIZE, THUMB_SIZE, 8*px, 8*px, 16*px, 16*px, 0xFFFFFFFF);
		}
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public WSlider setValueChangeListener(@Nullable IntConsumer valueChangeListener) {
		this.valueChangeListener = valueChangeListener;
		return this;
	}

	public WSlider setMouseReleaseListener(@Nullable Runnable mouseReleaseListener) {
		this.mouseReleaseListener = mouseReleaseListener;
		return this;
	}
}
