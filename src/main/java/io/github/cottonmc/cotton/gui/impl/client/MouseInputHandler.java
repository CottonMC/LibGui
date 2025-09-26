package io.github.cottonmc.cotton.gui.impl.client;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;

import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.data.ObservableProperty;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * The implementation for mouse inputs.
 */
public final class MouseInputHandler<S extends Screen & CottonScreenImpl> {
	private final S screen;
	private final ObservableProperty<@Nullable WWidget> hovered = ObservableProperty.<WWidget>of(null).build();
	private boolean clickDoubled;

	public MouseInputHandler(S screen) {
		this.screen = screen;
		hovered.addListener((property, from, to) -> {
			if (from != null) from.setHovered(false);
			if (to != null) to.setHovered(true);
		});
	}

	public void onMouseDown(int containerX, int containerY, Click click, boolean doubled) {
		this.clickDoubled = doubled;

		if (screen.getLastResponder() == null) {
			WWidget lastResponder = screen.getDescription().getRootPanel().hit(containerX, containerY);
			screen.setLastResponder(lastResponder);
			if (lastResponder != null) {
				runTree(
						lastResponder,
						widget -> widget.onMouseDown(clickAt(click, containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY()), doubled)
				);
			}
		} else {
			// This is a drag instead
		}
	}

	public void onMouseUp(int containerX, int containerY, Click click) {
		WWidget lastResponder = screen.getLastResponder();

		if (lastResponder != null) {
			int width = screen.width;
			int height = screen.height;

			runTree(
					lastResponder,
					widget -> widget.onMouseUp(clickAt(click, containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY()))
			);

			if (containerX >= 0 && containerY >= 0 && containerX < width && containerY < height) {
				runTree(
						lastResponder,
						widget -> widget.onClick(clickAt(click, containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY()), clickDoubled)
				);
			}
		} else {
			runTree(
					screen.getDescription().getRootPanel().hit(containerX, containerY),
					widget -> widget.onMouseUp(clickAt(click, containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY()))
			);
		}

		screen.setLastResponder(null);
	}

	public void onMouseDrag(int containerX, int containerY, Click click, double offsetX, double offsetY) {
		WWidget lastResponder = screen.getLastResponder();

		if (lastResponder != null) {
			lastResponder.onMouseDrag(clickAt(click, containerX - lastResponder.getAbsoluteX(), containerY - lastResponder.getAbsoluteY()), offsetX, offsetY);
		} else {
			int width = screen.width;
			int height = screen.height;

			if (containerX < 0 || containerY < 0 || containerX >= width || containerY >= height) return;

			runTree(
					screen.getDescription().getRootPanel().hit(containerX, containerY),
					widget -> widget.onMouseDrag(clickAt(click, containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY()), offsetX, offsetY)
			);
		}
	}

	public void onMouseScroll(int containerX, int containerY, double horizontalAmount, double verticalAmount) {
		runTree(
				screen.getDescription().getRootPanel().hit(containerX, containerY),
				widget -> widget.onMouseScroll(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY(), horizontalAmount, verticalAmount)
		);
	}

	public void onMouseMove(int containerX, int containerY) {
		WWidget hit = screen.getDescription().getRootPanel().hit(containerX, containerY);

		runTree(
				hit,
				widget -> widget.onMouseMove(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY())
		);

		@Nullable
		WWidget hoveredWidget = runTree(
				hit,
				widget -> InputResult.of(widget.canHover() && widget.isWithinBounds(containerX - widget.getAbsoluteX(), containerY - widget.getAbsoluteY()))
		);
		hovered.set(hoveredWidget);
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

	/**
	 * Checks if the focus is not being hovered and releases it in that case.
	 *
	 * @param mouseX the mouse cursor's X coordinate in widget space
	 * @param mouseY the mouse cursor's Y coordinate in widget space
	 */
	public void checkFocus(int mouseX, int mouseY) {
		WWidget focus = screen.getDescription().getFocus();
		if (focus != null) {
			int wx = focus.getAbsoluteX();
			int wy = focus.getAbsoluteY();

			if (!focus.isWithinBounds(mouseX - wx, mouseY - wy)) {
				screen.getDescription().releaseFocus(focus);
			}
		}
	}

	public static Click clickAt(Click click, double x, double y) {
		return new Click(x, y, click.buttonInfo());
	}
}
