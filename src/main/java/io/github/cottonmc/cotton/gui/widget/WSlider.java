package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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
 *     <li>A value change listener that gets all value changes (except direct setValue calls)</li>
 *     <li>
 *         A focus release listener that gets called when the player stops dragging the slider.
 *         For example, this can be used for sending sync packets to the server
 *         when the player has selected a value.
 *     </li>
 * </ul>
 */
public class WSlider extends WWidget {
	private static final int TRACK_WIDTH = 6;
	private static final int THUMB_SIZE = 8;
	private static final Identifier TEXTURE = new Identifier("libgui", "textures/widget/slider.png");

	private final int min, max;
	private final int valueRange;
	private final Axis axis;

	private int value;
	private float valueToCoordRatio, coordToValueRatio;
	@Nullable private IntConsumer valueChangeListener = null;
	@Nullable private Runnable focusReleaseListener = null;

	@Environment(EnvType.CLIENT)
	@Nullable
	private BackgroundPainter backgroundPainter = null;

	// Used for visuals and detecting dragging after the user starts dragging
	// on top of the slider, but then moves the mouse out but still within the widget's boundary.
//	private boolean dragging = false;

	public WSlider(int min, int max, Axis axis) {
		if (max <= min)
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
			//dragging = true;
			int pos = (axis == Axis.VERTICAL ? (height - y) : x) - THUMB_SIZE / 2;
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
		releaseFocus();
		return super.onMouseUp(x, y, button);
	}

	@Override
	public void onFocusLost() {
		if (focusReleaseListener != null) focusReleaseListener.run();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		if (backgroundPainter != null) {
			backgroundPainter.paintBackground(x, y, this);
		} else {
			float px = 1 / 32f;

			if (axis == Axis.VERTICAL) {
				int trackX = x + width / 2 - TRACK_WIDTH / 2;

				ScreenDrawing.rect(TEXTURE, trackX, y + 1, TRACK_WIDTH, 1, 16 * px, 0 * px, 22 * px, 1 * px, 0xFFFFFFFF);
				ScreenDrawing.rect(TEXTURE, trackX, y + 2, TRACK_WIDTH, height - 2, 16 * px, 1 * px, 22 * px, 2 * px, 0xFFFFFFFF);
				ScreenDrawing.rect(TEXTURE, trackX, y + height, TRACK_WIDTH, 1, 16 * px, 2 * px, 22 * px, 3 * px, 0xFFFFFFFF);
			} else {
				int trackY = y + height / 2 - TRACK_WIDTH / 2;

				ScreenDrawing.rect(TEXTURE, x, trackY, 1, TRACK_WIDTH, 16 * px, 3 * px, 17 * px, 9 * px, 0xFFFFFFFF);
				ScreenDrawing.rect(TEXTURE, x + 1, trackY, width - 2, TRACK_WIDTH, 17 * px, 3 * px, 18 * px, 9 * px, 0xFFFFFFFF);
				ScreenDrawing.rect(TEXTURE, x + width - 1, trackY, 1, TRACK_WIDTH, 18 * px, 3 * px, 19 * px, 9 * px, 0xFFFFFFFF);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paintForeground(int x, int y, int mouseX, int mouseY) {
		float px = 1 / 32f;
		int thumbX, thumbY, thumbXOffset;

		if (axis == Axis.VERTICAL) {
			thumbX = x + width / 2 - THUMB_SIZE / 2;
			thumbY = y + height - THUMB_SIZE + 1 - (int) (coordToValueRatio * (value - min));
			thumbXOffset = 0;
		} else {
			thumbX = x + (int) (coordToValueRatio * (value - min));
			thumbY = y + height / 2 - THUMB_SIZE / 2;
			thumbXOffset = 8;
		}

		boolean dragging = isFocused();
		// TODO: Replace with proper mouse events
		if (dragging) {
			if (GLFW.glfwGetMouseButton(MinecraftClient.getInstance().window.getHandle(), GLFW.GLFW_MOUSE_BUTTON_1) == GLFW.GLFW_PRESS) {
				onMouseDrag(mouseX - x, mouseY - y, 0);
			} else {
				onMouseUp(mouseX - x, mouseY - y, 0);
			}
		}

		// thumbState values:
		// 0: default, 1: dragging, 2: hovered
		int thumbState = dragging ? 1 : (mouseX >= thumbX && mouseX <= thumbX + THUMB_SIZE && mouseY >= thumbY && mouseY <= thumbY + THUMB_SIZE ? 2 : 0);
		ScreenDrawing.rect(TEXTURE, thumbX, thumbY, THUMB_SIZE, THUMB_SIZE, thumbXOffset*px, 0*px + thumbState * 8*px, (thumbXOffset + 8)*px, 8*px + thumbState * 8*px, 0xFFFFFFFF);

		super.paintForeground(x, y, mouseX, mouseY);
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

	@Environment(EnvType.CLIENT)
	public void setBackgroundPainter(BackgroundPainter backgroundPainter) {
		this.backgroundPainter = backgroundPainter;
	}
}
