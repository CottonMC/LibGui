package io.github.cottonmc.cotton.gui.impl.client;

import net.minecraft.client.gui.screen.Screen;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * The implementation for mouse inputs.
 */
public final class MouseInputHandler {
	public static void onMouseDown(GuiDescription description, CottonScreenImpl screen, int containerX, int containerY, int mouseButton) {
		if (screen.getLastResponder() == null) {
			WWidget lastResponder = description.getRootPanel().hit(containerX, containerY);
			screen.setLastResponder(lastResponder);
			if (lastResponder != null) {
				runTree(
						lastResponder,
						widget -> widget.onMouseDown(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY(), mouseButton)
				);
			}
		} else {
			// This is a drag instead
		}
	}

	public static <S extends Screen & CottonScreenImpl> void onMouseUp(GuiDescription description, S screen, int containerX, int containerY, int mouseButton) {
		WWidget lastResponder = screen.getLastResponder();

		if (lastResponder != null) {
			int width = screen.width;
			int height = screen.height;

			runTree(
					lastResponder,
					widget -> widget.onMouseUp(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY(), mouseButton)
			);

			if (containerX >= 0 && containerY >= 0 && containerX < width && containerY < height) {
				runTree(
						lastResponder,
						widget -> widget.onClick(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY(), mouseButton)
				);
			}
		} else {
			runTree(
					description.getRootPanel().hit(containerX, containerY),
					widget -> widget.onMouseUp(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY(), mouseButton)
			);
		}

		screen.setLastResponder(null);
	}

	public static <S extends Screen & CottonScreenImpl> void onMouseDrag(GuiDescription description, S screen, int containerX, int containerY, int mouseButton, double deltaX, double deltaY) {
		WWidget lastResponder = screen.getLastResponder();

		if (lastResponder != null) {
			lastResponder.onMouseDrag(containerX - lastResponder.getAbsoluteX(), containerY - lastResponder.getAbsoluteY(), mouseButton, deltaX, deltaY);
		} else {
			int width = screen.width;
			int height = screen.height;

			if (containerX < 0 || containerY < 0 || containerX >= width || containerY >= height) return;

			runTree(
					description.getRootPanel().hit(containerX, containerY),
					widget -> widget.onMouseDrag(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY(), mouseButton, deltaX, deltaY)
			);
		}
	}

	public static void onMouseScroll(GuiDescription description, int containerX, int containerY, double amount) {
		runTree(
				description.getRootPanel().hit(containerX, containerY),
				widget -> widget.onMouseScroll(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY(), amount)
		);
	}

	public static void onMouseMove(GuiDescription description, int containerX, int containerY) {
		runTree(
				description.getRootPanel().hit(containerX, containerY),
				widget -> widget.onMouseMove(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY())
		);
	}

	/**
	 * Traverses the {@code function} up the widget tree until it finds a {@link InputResult#PROCESSED} result.
	 *
	 * @param bottom   the starting point for the traversal
	 * @param function the function to run
	 * @return the first widget to return {@link InputResult#PROCESSED}, or null if none found.
	 */
	@Nullable
	private static WWidget runTree(WWidget bottom, Function<WWidget, InputResult> function) {
		WWidget current = bottom;

		while (current != null) {
			InputResult result = function.apply(current);

			if (result == InputResult.PROCESSED) {
				break;
			} else {
				current = current.getParent();
			}
		}

		return current;
	}
}
