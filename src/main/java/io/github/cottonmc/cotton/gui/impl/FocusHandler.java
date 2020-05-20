package io.github.cottonmc.cotton.gui.impl;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WWidget;

/**
 * The implementation for focus cycling.
 */
public final class FocusHandler {
	public static void cycleFocus(GuiDescription host, boolean lookForwards) {
		boolean result;
		WWidget focus = host.getFocus();
		if (focus == null) {
			result = cycleFocus(host, lookForwards, host.getRootPanel(), null);
		} else {
			result = cycleFocus(host, lookForwards, focus, null);
		}

		if (!result) {
			// Try again from the beginning
			cycleFocus(host, lookForwards, host.getRootPanel(), null);
		}
	}

	private static boolean cycleFocus(GuiDescription host, boolean lookForwards, WWidget widget, WWidget pivot) {
		WWidget next = widget instanceof WPanel
				? ((WPanel) widget).cycleFocus(lookForwards, pivot)
				: widget.cycleFocus(lookForwards);

		if (next != null) {
			host.requestFocus(next);
			return true;
		} else {
			WPanel parent = widget.getParent();
			if (parent != null) {
				return cycleFocus(host, lookForwards, parent, widget);
			}
		}

		return false;
	}
}
