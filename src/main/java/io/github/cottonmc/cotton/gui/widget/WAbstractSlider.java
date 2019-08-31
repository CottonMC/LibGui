package io.github.cottonmc.cotton.gui.widget;

import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import io.github.cottonmc.cotton.gui.widget.data.Axis;

import javax.annotation.Nullable;
import java.util.function.IntConsumer;

/**
 * A base class for slider widgets that can be used to select int values.
 *
 * <p>You can set two listeners on a slider:
 * <ul>
 *     <li>
 *         A value change listener that gets all value changes (including direct setValue calls).
 *     </li>
 *     <li>
 *         A dragging finished listener that gets called when the player stops dragging the slider
 *         or modifies the value with the keyboard.
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

	/**
	 * True if there is a pending dragging finished event caused by the keyboard.
	 */
	private boolean valueChangedWithKeys = false;

	@Nullable private IntConsumer valueChangeListener = null;
	@Nullable private Runnable draggingFinishedListener = null;

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
		int trackHeight = (axis == Axis.HORIZONTAL ? x : y) - getThumbWidth();
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
		if (draggingFinishedListener != null) draggingFinishedListener.run();
	}

	private void moveSlider(int x, int y) {
		int pos = (axis == Axis.VERTICAL ? (height - y) : x) - getThumbWidth() / 2;
		int rawValue = min + Math.round(valueToCoordRatio * pos);
		int previousValue = value;
		value = MathHelper.clamp(rawValue, min, max);
		if (value != previousValue) onValueChanged(value);
	}

	@Override
	public WWidget onMouseUp(int x, int y, int button) {
		dragging = false;
		if (draggingFinishedListener != null) draggingFinishedListener.run();
		return super.onMouseUp(x, y, button);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
		onValueChanged(value);
	}

	@Nullable
	public IntConsumer getValueChangeListener() {
		return valueChangeListener;
	}

	public void setValueChangeListener(@Nullable IntConsumer valueChangeListener) {
		this.valueChangeListener = valueChangeListener;
	}

	@Nullable
	public Runnable getDraggingFinishedListener() {
		return draggingFinishedListener;
	}

	public void setDraggingFinishedListener(@Nullable Runnable draggingFinishedListener) {
		this.draggingFinishedListener = draggingFinishedListener;
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

	protected void onValueChanged(int value) {
		if (valueChangeListener != null) valueChangeListener.accept(value);
	}

	@Override
	public void onKeyPressed(int ch, int key, int modifiers) {
		boolean valueChanged = false;
		if (modifiers == 0) {
			if (isDecreasingKey(ch) && value > min) {
				value--;
				valueChanged = true;
			} else if (isIncreasingKey(ch) && value < max) {
				value++;
				valueChanged = true;
			}
		} else if (modifiers == GLFW.GLFW_MOD_CONTROL) {
			if (isDecreasingKey(ch) && value != min) {
				value = min;
				valueChanged = true;
			} else if (isIncreasingKey(ch) && value != max) {
				value = max;
				valueChanged = true;
			}
		}

		if (valueChanged) {
			onValueChanged(value);
			valueChangedWithKeys = true;
		}
	}

	@Override
	public void onKeyReleased(int ch, int key, int modifiers) {
		if (valueChangedWithKeys && (isDecreasingKey(ch) || isIncreasingKey(ch))) {
			if (draggingFinishedListener != null) draggingFinishedListener.run();
			valueChangedWithKeys = false;
		}
	}

	private static boolean isDecreasingKey(int ch) {
		return ch == GLFW.GLFW_KEY_LEFT || ch == GLFW.GLFW_KEY_DOWN;
	}

	private static boolean isIncreasingKey(int ch) {
		return ch == GLFW.GLFW_KEY_RIGHT || ch == GLFW.GLFW_KEY_UP;
	}
}
