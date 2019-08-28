package io.github.cottonmc.cotton.gui.widget;

import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.function.IntConsumer;

/**
 * A base class for slider widgets that can be used to select int values.
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
public abstract class WAbstractSlider extends WWidget {
	protected final int min, max;
	protected final Axis axis;

	protected int value;

	/**
	 * True if the user is currently dragging the thumb.
	 * Used for visuals.
	 */
	protected boolean dragging = false;

	/**
	 * A value:coordinate ratio. Used for converting user input into values.
	 */
	protected float valueToCoordRatio;

	/**
	 * A coordinate:value ratio. Used for rendering the thumb.
	 */
	protected float coordToValueRatio;

	@Nullable private IntConsumer valueChangeListener = null;
	@Nullable private Runnable focusReleaseListener = null;

	protected WAbstractSlider(int min, int max, Axis axis) {
		if (max <= min)
			throw new IllegalArgumentException("Minimum value must be smaller than the maximum!");

		this.min = min;
		this.max = max;
		this.axis = axis;
		this.value = min;
	}

	/**
	 * @return the thumb size along the slider axis
	 */
	protected abstract int getThumbWidth();

	/**
	 * Checks if the mouse cursor is close enough to the slider that the user can start dragging.
	 *
	 * @param x the mouse x position
	 * @param y the mouse y position
	 * @return if the cursor is inside dragging bounds
	 */
	protected abstract boolean isMouseInsideBounds(int x, int y);

	@Override
	public void setSize(int x, int y) {
		super.setSize(x, y);
		int trackHeight = (axis == Axis.HORIZONTAL ? x : y) - getThumbWidth() + 1;
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
		// Check if cursor is inside or <=2px away from track
		if (isMouseInsideBounds(x, y)) {
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
		int pos = (axis == Axis.VERTICAL ? (height - y) : x) - getThumbWidth() / 2;
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

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public WAbstractSlider setValueChangeListener(@Nullable IntConsumer valueChangeListener) {
		this.valueChangeListener = valueChangeListener;
		return this;
	}

	public WAbstractSlider setFocusReleaseListener(@Nullable Runnable focusReleaseListener) {
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
