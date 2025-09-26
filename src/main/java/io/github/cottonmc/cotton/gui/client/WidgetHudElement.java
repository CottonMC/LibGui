package io.github.cottonmc.cotton.gui.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.Window;

import io.github.cottonmc.cotton.gui.widget.WWidget;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * A HUD element wrapping a {@link WWidget}.
 *
 * @param widget     the drawn widget
 * @param positioner an optional positioner that moves the widget
 * @since 14.0.0
 */
public record WidgetHudElement(WWidget widget, @Nullable Positioner positioner) implements HudElement {
	@ApiStatus.Internal
	static final Set<WWidget> tickingWidgets = new HashSet<>();

	static {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			for (WWidget widget : tickingWidgets) {
				widget.tick();
			}
		});
	}

	/**
	 * Constructs a new {@code WidgetHudElement} without a positioner.
	 *
	 * @param widget the drawn widget
	 */
	public WidgetHudElement(WWidget widget) {
		this(widget, null);
	}

	/**
	 * Constructs a new {@code WidgetHudElement}.
	 *
	 * @param widget the drawn widget
	 * @param x      the widget's horizontal offset
	 * @param y      the widget's vertical offset
	 * @see Positioner#of documentation about the offsets
	 */
	public WidgetHudElement(WWidget widget, int x, int y) {
		this(widget, Positioner.of(x, y));
	}


	/**
	 * Constructs a new {@code WidgetHudElement} and resizes the inner widget.
	 *
	 * @param widget     the drawn widget
	 * @param positioner an optional positioner that moves the widget
	 * @param width      the widget's new width
	 * @param height     the widget's new height
	 */
	public WidgetHudElement(WWidget widget, Positioner positioner, int width, int height) {
		this(widget, positioner);
		widget.setSize(width, height);
	}

	/**
	 * Constructs a new {@code WidgetHudElement} and resizes the inner widget.
	 *
	 * @param widget the drawn widget
	 * @param x      the widget's horizontal offset
	 * @param y      the widget's vertical offset
	 * @param width  the widget's new width
	 * @param height the widget's new height
	 * @see Positioner#of documentation about the offsets
	 */
	public WidgetHudElement(WWidget widget, int x, int y, int width, int height) {
		this(widget, Positioner.of(x, y), width, height);
	}

	/**
	 * Enables this HUD element's widget ticking.
	 */
	public void enableTicking() {
		tickingWidgets.add(widget);
	}

	/**
	 * Disables this HUD element's widget ticking.
	 */
	public void disableTicking() {
		tickingWidgets.remove(widget);
	}

	@Override
	public void render(DrawContext context, RenderTickCounter tickCounter) {
		render(context, widget, positioner);
	}

	@ApiStatus.Internal
	static void render(DrawContext context, WWidget widget, @Nullable Positioner positioner) {
		Window window = MinecraftClient.getInstance().getWindow();
		int hudWidth = window.getScaledWidth();
		int hudHeight = window.getScaledHeight();
		if (positioner != null) {
			positioner.reposition(widget, hudWidth, hudHeight);
		}

		widget.paint(context, widget.getX(), widget.getY(), -1, -1);
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
