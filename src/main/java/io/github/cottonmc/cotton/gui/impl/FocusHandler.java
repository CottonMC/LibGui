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
			WPanel parent = focus.getParent();
			result = cycleFocus(host, lookForwards, parent != null ? parent : host.getRootPanel(), focus);
		}

		if (!result) {
			// Try again from the beginning
			cycleFocus(host, lookForwards, host.getRootPanel(), null);
		}
	}

	private static boolean cycleFocus(GuiDescription host, boolean lookForwards, WPanel panel, WWidget pivot) {
		WWidget next = panel.cycleFocus(lookForwards, pivot);
		if (next != null) {
			host.requestFocus(next);
			return true;
		} else {
			WPanel parent = panel.getParent();
			if (parent != null) {
				return cycleFocus(host, lookForwards, parent, panel);
			}
		}

		return false;
	}
}
