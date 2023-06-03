package io.github.cottonmc.cotton.gui.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

import io.github.cottonmc.cotton.gui.widget.WWidget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages widgets that are painted on the in-game HUD.
 */
@Environment(EnvType.CLIENT)
public final class CottonHud {
	private static final Set<WWidget> widgets = new HashSet<>();
	private static final Map<WWidget, Positioner> positioners = new HashMap<>();

	static {
		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
			Window window = MinecraftClient.getInstance().getWindow();
			int hudWidth = window.getScaledWidth();
			int hudHeight = window.getScaledHeight();
			for (WWidget widget : widgets) {
				Positioner positioner = positioners.get(widget);
				if (positioner != null) {
					positioner.reposition(widget, hudWidth, hudHeight);
				}

				widget.paint(drawContext, widget.getX(), widget.getY(), -1, -1);
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			for (WWidget widget : widgets) {
				widget.tick();
			}
		});
	}

	/**
	 * Adds a new widget to the HUD.
	 *
	 * @param widget the widget
	 */
	public static void add(WWidget widget) {
		widgets.add(widget);
	}

	/**
	 * Adds a new widget to the HUD at the specified offsets.
	 *
	 * @param widget the widget
	 * @param x the x offset
	 * @param y the y offset
	 * @see Positioner#of documentation about the offsets
	 */
	public static void add(WWidget widget, int x, int y) {
		add(widget, Positioner.of(x, y));
	}

	/**
	 * Adds a new widget to the HUD at the specified offsets and resizes it.
	 *
	 * @param widget the widget
	 * @param x the x offset
	 * @param y the y offset
	 * @param width the width of the widget
	 * @param height the height of the widget
	 * @see Positioner#of documentation about the offsets
	 */
	public static void add(WWidget widget, int x, int y, int width, int height) {
		add(widget, Positioner.of(x, y));
		widget.setSize(width, height);
	}

	/**
	 * Adds a new widget to the HUD with a custom positioner.
	 *
	 * @param widget the widget
	 * @param positioner the positioner
	 */
	public static void add(WWidget widget, Positioner positioner) {
		widgets.add(widget);
		setPositioner(widget, positioner);
	}

	/**
	 * Adds a new widget to the HUD with a custom positioner and resizes it.
	 *
	 * @param widget the widget
	 * @param positioner the positioner
	 * @param width the width of the widget
	 * @param height the height of the widget
	 */
	public static void add(WWidget widget, Positioner positioner, int width, int height) {
		widgets.add(widget);
		widget.setSize(width, height);
		setPositioner(widget, positioner);
	}

	/**
	 * Sets the positioner of the widget.
	 *
	 * @param widget the widget
	 * @param positioner the positioner
	 */
	public static void setPositioner(WWidget widget, Positioner positioner) {
		positioners.put(widget, positioner);
	}

	/**
	 * Removes the widget from the HUD.
	 *
	 * @param widget the widget
	 */
	public static void remove(WWidget widget) {
		widgets.remove(widget);
	}

	/**
	 * Positioners can be used to change the position of a widget based on the window dimensions.
	 */
	@FunctionalInterface
	public interface Positioner {
		/**
		 * Repositions the widget according to the HUD dimensions.
		 *
		 * @param widget the widget
		 * @param hudWidth the width of the HUD
		 * @param hudHeight the height of the HUD
		 */
		void reposition(WWidget widget, int hudWidth, int hudHeight);

		/**
		 * Creates a new positioner that offsets widgets.
		 *
		 * <p>If an offset is negative, the offset is subtracted from the HUD dimension on that axis.
		 *
		 * @param x the x offset
		 * @param y the y offset
		 * @return an offsetting positioner
		 */
		static Positioner of(int x, int y) {
			return (widget, hudWidth, hudHeight) -> {
				widget.setLocation((hudWidth + x) % hudWidth, (hudHeight + y) % hudHeight);
			};
		}

		/**
		 * Creates a new positioner that centers widgets on the X axis and offsets them on the Y axis.
		 *
		 * <p>If the Y offset is negative, the offset is subtracted from the HUD height.
		 *
		 * @param y the y offset
		 * @return a centering positioner
		 */
		static Positioner horizontallyCentered(int y) {
			return (widget, hudWidth, hudHeight) -> {
				widget.setLocation((hudWidth - widget.getWidth()) / 2, (hudHeight + y) % hudHeight);
			};
		}
	}
}
