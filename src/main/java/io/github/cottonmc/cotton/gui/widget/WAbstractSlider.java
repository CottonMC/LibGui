package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
 *         A value change listener that gets all value changes.
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
	/**
	 * The minimum time between two draggingFinished events caused by scrolling ({@link #onMouseScroll}).
	 */
	private static final int DRAGGING_FINISHED_RATE_LIMIT_FOR_SCROLLING = 10;

	protected int min, max;
	protected final Axis axis;
	protected Direction direction;

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
	private boolean pendingDraggingFinishedFromKeyboard = false;
	private int draggingFinishedFromScrollingTimer = 0;
	private boolean pendingDraggingFinishedFromScrolling = false;

	@Nullable private IntConsumer valueChangeListener = null;
	@Nullable private IntConsumer draggingFinishedListener = null;

	protected WAbstractSlider(int min, int max, Axis axis) {
		if (max <= min)
			throw new IllegalArgumentException("Minimum value must be smaller than the maximum!");

		this.min = min;
		this.max = max;
		this.axis = axis;
		this.value = min;
		this.direction = (axis == Axis.HORIZONTAL) ? Direction.LEFT : Direction.UP;
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

	@Environment(EnvType.CLIENT)
	@Override
	public WWidget onMouseDown(int x, int y, int button) {
		// Check if cursor is inside or <=2px away from track
		if (isMouseInsideBounds(x, y)) {
			requestFocus();
		}
		return super.onMouseDown(x, y, button);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onMouseDrag(int x, int y, int button) {
		if (isFocused()) {
			dragging = true;
			moveSlider(x, y);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onClick(int x, int y, int button) {
		moveSlider(x, y);
		if (draggingFinishedListener != null) draggingFinishedListener.accept(value);
	}

	private void moveSlider(int x, int y) {
		int axisPos;

		switch (direction) {
			case UP:
				axisPos = height - y;
				break;
			case DOWN:
				axisPos = y;
				break;
			case LEFT:
				axisPos = width - x;
				break;
			case RIGHT:
			default:
				axisPos = x;
				break;
		}

		int pos = axisPos - getThumbWidth() / 2;
		int rawValue = min + Math.round(valueToCoordRatio * pos);
		int previousValue = value;
		value = MathHelper.clamp(rawValue, min, max);
		if (value != previousValue) onValueChanged(value);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public WWidget onMouseUp(int x, int y, int button) {
		dragging = false;
		if (draggingFinishedListener != null) draggingFinishedListener.accept(value);
		return super.onMouseUp(x, y, button);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onMouseScroll(int x, int y, double amount) {
		if (direction == Direction.LEFT || direction == Direction.DOWN) {
			amount = -amount;
		}

		int previous = value;
		value = MathHelper.clamp(value + (int) Math.signum(amount) * MathHelper.ceil(valueToCoordRatio * Math.abs(amount) * 2), min, max);

		if (previous != value) {
			onValueChanged(value);
			pendingDraggingFinishedFromScrolling = true;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void tick() {
		if (draggingFinishedFromScrollingTimer > 0) {
			draggingFinishedFromScrollingTimer--;
		}

		if (pendingDraggingFinishedFromScrolling && draggingFinishedFromScrollingTimer <= 0) {
			if (draggingFinishedListener != null) draggingFinishedListener.accept(value);
			pendingDraggingFinishedFromScrolling = false;
			draggingFinishedFromScrollingTimer = DRAGGING_FINISHED_RATE_LIMIT_FOR_SCROLLING;
		}
	}

	public int getValue() {
		return value;
	}

	/**
	 * Sets the slider value without calling listeners.
	 * @param value the new value
	 */
	public void setValue(int value) {
		setValue(value, false);
	}

	/**
	 * Sets the slider value.
	 *
	 * @param value the new value
	 * @param callListeners if true, call all slider listeners
	 */
	public void setValue(int value, boolean callListeners) {
		int previous = this.value;
		this.value = MathHelper.clamp(value, min, max);
		if (callListeners && previous != this.value) {
			onValueChanged(this.value);
			if (draggingFinishedListener != null) draggingFinishedListener.accept(value);
		}
	}

	@Nullable
	public IntConsumer getValueChangeListener() {
		return valueChangeListener;
	}

	public void setValueChangeListener(@Nullable IntConsumer valueChangeListener) {
		this.valueChangeListener = valueChangeListener;
	}

	@Nullable
	public IntConsumer getDraggingFinishedListener() {
		return draggingFinishedListener;
	}

	public void setDraggingFinishedListener(@Nullable IntConsumer draggingFinishedListener) {
		this.draggingFinishedListener = draggingFinishedListener;
	}

	public int getMinValue() {
		return min;
	}

	public int getMaxValue() {
		return max;
	}

	public void setMinValue(int min) {
		this.min = min;
		if (this.value < min) {
			this.value = min;
			onValueChanged(this.value);
		}
	}

	public void setMaxValue(int max) {
		this.max = max;
		if (this.value > max) {
			this.value = max;
			onValueChanged(this.value);
		}
	}

	public Axis getAxis() {
		return axis;
	}

	/**
	 * Gets the direction of this slider.
	 *
	 * @return the direction
	 * @since 2.0.0
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Sets the direction of this slider.
	 *
	 * @param direction the new direction
	 * @throws IllegalArgumentException if the {@linkplain Direction#getAxis() direction axis} is not equal to {@link #axis}.
	 * @since 2.0.0
	 */
	public void setDirection(Direction direction) {
		if (direction.getAxis() != axis) {
			throw new IllegalArgumentException("Incorrect axis: " + axis);
		}

		this.direction = direction;
	}

	protected void onValueChanged(int value) {
		if (valueChangeListener != null) valueChangeListener.accept(value);
	}

	@Environment(EnvType.CLIENT)
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
			pendingDraggingFinishedFromKeyboard = true;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onKeyReleased(int ch, int key, int modifiers) {
		if (pendingDraggingFinishedFromKeyboard && (isDecreasingKey(ch) || isIncreasingKey(ch))) {
			if (draggingFinishedListener != null) draggingFinishedListener.accept(value);
			pendingDraggingFinishedFromKeyboard = false;
		}
	}

	private boolean isDecreasingKey(int ch) {
		return direction.isInverted()
				? (ch == GLFW.GLFW_KEY_RIGHT || ch == GLFW.GLFW_KEY_UP)
				: (ch == GLFW.GLFW_KEY_LEFT || ch == GLFW.GLFW_KEY_DOWN);
	}

	private boolean isIncreasingKey(int ch) {
		return direction.isInverted()
				? (ch == GLFW.GLFW_KEY_LEFT || ch == GLFW.GLFW_KEY_DOWN)
				: (ch == GLFW.GLFW_KEY_RIGHT || ch == GLFW.GLFW_KEY_UP);
	}

	/**
	 * The direction enum represents all four directions a slider can face.
	 *
	 * <p>For example, a slider whose value grows towards the right faces right.
	 *
	 * <p>The default direction for vertical sliders is {@link #UP} and
	 * the one for horizontal sliders is {@link #RIGHT}.
	 *
	 * @since 2.0.0
	 */
	public enum Direction {
		UP(Axis.VERTICAL, false),
		DOWN(Axis.VERTICAL, true),
		LEFT(Axis.HORIZONTAL, true),
		RIGHT(Axis.HORIZONTAL, false);

		private final Axis axis;
		private final boolean inverted;

		Direction(Axis axis, boolean inverted) {
			this.axis = axis;
			this.inverted = inverted;
		}

		/**
		 * Gets the direction's axis.
		 *
		 * @return the axis
		 */
		public Axis getAxis() {
			return axis;
		}

		/**
		 * Returns whether this slider is inverted.
		 *
		 * <p>An inverted slider will have reversed keyboard control.
		 *
		 * @return whether this slider is inverted
		 */
		public boolean isInverted() {
			return inverted;
		}
	}
}
