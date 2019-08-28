package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.function.IntConsumer;

/**
 * A slider widget that can be used to select int values.
 *
 * <p>You can set two listeners on a slider:
 * <ul>
 *     <li>
 *         A value change listener that gets all value changes (except direct setValue calls).
 *     </li>
 *     <li>
 *         A focus release listener that gets called when the player stops dragging the slider.
 *         For example, this can be used for sending sync packets to the server
 *         when the player has selected a value.
 *     </li>
 * </ul>
 */
public class WSlider extends WWidget {
	public static final int TRACK_WIDTH = 6;
	public static final int THUMB_SIZE = 8;
	public static final Identifier TEXTURE = new Identifier("libgui", "textures/widget/slider.png");

	private final int min, max;
	private final Axis axis;

	private int value;

	/**
	 * True if the user is currently dragging the thumb.
	 * Used for visuals.
	 */
	private boolean dragging = false;

	/**
	 * A value:coordinate ratio. Used for converting user input into values.
	 */
	private float valueToCoordRatio;

	/**
	 * A coordinate:value ratio. Used for rendering the thumb.
	 */
	private float coordToValueRatio;

	@Nullable private IntConsumer valueChangeListener = null;
	@Nullable private Runnable focusReleaseListener = null;

	@Environment(EnvType.CLIENT)
	@Nullable
	private BackgroundPainter backgroundPainter = null;

	public WSlider(int min, int max, Axis axis) {
		if (max <= min)
			throw new IllegalArgumentException("Minimum value must be smaller than the maximum!");

		this.min = min;
		this.max = max;
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
		valueToCoordRatio = (float) (max - min) / trackHeight;
		coordToValueRatio = 1 / valueToCoordRatio;
	}

	@Override
	public boolean canResize() {
		return true;
	}

	@Override
	public boolean canFocus() {
		return true;
	}

	@Override
	public WWidget onMouseDown(int x, int y, int button) {
		// ao = axis-opposite mouse coordinate, aoCenter = center of ao's axis
		int ao = axis == Axis.HORIZONTAL ? y : x;
		int aoCenter = (axis == Axis.HORIZONTAL ? height : width) / 2;

		// Check if cursor is inside or <=2px away from track
		if (ao >= aoCenter - TRACK_WIDTH / 2 - 2 && ao <= aoCenter + TRACK_WIDTH / 2 + 2) {
			requestFocus();
		}
		return super.onMouseDown(x, y, button);
	}

	@Override
	public void onMouseDrag(int x, int y, int button) {
		if (isFocused()) {
			dragging = true;
			moveSlider(x, y);
		}
	}

	@Override
	public void onClick(int x, int y, int button) {
		moveSlider(x, y);
	}

	private void moveSlider(int x, int y) {
		int pos = (axis == Axis.VERTICAL ? (height - y) : x) - THUMB_SIZE / 2;
		int rawValue = min + Math.round(valueToCoordRatio * pos);
		int previousValue = value;
		value = MathHelper.clamp(rawValue, min, max);
		if (value != previousValue && valueChangeListener != null) valueChangeListener.accept(value);
	}

	@Override
	public WWidget onMouseUp(int x, int y, int button) {
		dragging = false;
		return super.onMouseUp(x, y, button);
	}

	@Override
	public void onFocusLost() {
		if (focusReleaseListener != null) focusReleaseListener.run();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paintBackground(int x, int y, int mouseX, int mouseY) {
		if (backgroundPainter != null) {
			backgroundPainter.paintBackground(x, y, this);
		} else {
			float px = 1 / 32f;
			// thumbX/Y: thumb position in widget-space
			int thumbX, thumbY;
			// thumbXOffset: thumb texture x offset in pixels
			int thumbXOffset;

			if (axis == Axis.VERTICAL) {
				int trackX = x + width / 2 - TRACK_WIDTH / 2;
				thumbX = width / 2 - THUMB_SIZE / 2;
				thumbY = height - THUMB_SIZE + 1 - (int) (coordToValueRatio * (value - min));
				thumbXOffset = 0;

				ScreenDrawing.rect(TEXTURE, trackX, y + 1, TRACK_WIDTH, 1, 16*px, 0*px, 22*px, 1*px, 0xFFFFFFFF);
				ScreenDrawing.rect(TEXTURE, trackX, y + 2, TRACK_WIDTH, height - 2, 16*px, 1*px, 22*px, 2*px, 0xFFFFFFFF);
				ScreenDrawing.rect(TEXTURE, trackX, y + height, TRACK_WIDTH, 1, 16*px, 2*px, 22*px, 3*px, 0xFFFFFFFF);
			} else {
				int trackY = y + height / 2 - TRACK_WIDTH / 2;
				thumbX = (int) (coordToValueRatio * (value - min));
				thumbY = height / 2 - THUMB_SIZE / 2;
				thumbXOffset = 8;

				ScreenDrawing.rect(TEXTURE, x, trackY, 1, TRACK_WIDTH, 16*px, 3*px, 17*px, 9*px, 0xFFFFFFFF);
				ScreenDrawing.rect(TEXTURE, x + 1, trackY, width - 2, TRACK_WIDTH, 17*px, 3*px, 18*px, 9*px, 0xFFFFFFFF);
				ScreenDrawing.rect(TEXTURE, x + width - 1, trackY, 1, TRACK_WIDTH, 18*px, 3*px, 19*px, 9*px, 0xFFFFFFFF);
			}

			// thumbState values:
			// 0: default, 1: dragging, 2: hovered
			int thumbState = dragging ? 1 : (mouseX >= thumbX && mouseX <= thumbX + THUMB_SIZE && mouseY >= thumbY && mouseY <= thumbY + THUMB_SIZE ? 2 : 0);
			ScreenDrawing.rect(TEXTURE, x + thumbX, y + thumbY, THUMB_SIZE, THUMB_SIZE, thumbXOffset*px, 0*px + thumbState * 8*px, (thumbXOffset + 8)*px, 8*px + thumbState * 8*px, 0xFFFFFFFF);

			if (thumbState == 0 && isFocused()) {
				ScreenDrawing.rect(TEXTURE, x + thumbX, y + thumbY, THUMB_SIZE, THUMB_SIZE, 0*px, 24*px, 8*px, 32*px, 0xFFFFFFFF);
			}
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

	public WSlider setFocusReleaseListener(@Nullable Runnable focusReleaseListener) {
		this.focusReleaseListener = focusReleaseListener;
		return this;
	}

	public int getMinValue() {
		return min;
	}

	public int getMaxValue() {
		return max;
	}

	public Axis getAxis() {
		return axis;
	}

	@Environment(EnvType.CLIENT)
	public void setBackgroundPainter(BackgroundPainter backgroundPainter) {
		this.backgroundPainter = backgroundPainter;
	}

	@Override
	public void onKeyPressed(int ch, int key, int modifiers) {
		boolean valueChanged = false;
		if (modifiers == 0) {
			if ((ch == GLFW.GLFW_KEY_LEFT || ch == GLFW.GLFW_KEY_DOWN) && value > min) {
				value--;
				valueChanged = true;
			} else if ((ch == GLFW.GLFW_KEY_RIGHT || ch == GLFW.GLFW_KEY_UP) && value < max) {
				value++;
				valueChanged = true;
			}
		} else if (modifiers == GLFW.GLFW_MOD_CONTROL) {
			if ((ch == GLFW.GLFW_KEY_LEFT || ch == GLFW.GLFW_KEY_DOWN) && value != min) {
				value = min;
				valueChanged = true;
			} else if ((ch == GLFW.GLFW_KEY_RIGHT || ch == GLFW.GLFW_KEY_UP) && value != max) {
				value = max;
				valueChanged = true;
			}
		}

		if (valueChanged && valueChangeListener != null) valueChangeListener.accept(value);
	}
}
