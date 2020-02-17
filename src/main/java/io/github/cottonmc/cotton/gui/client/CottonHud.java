package io.github.cottonmc.cotton.gui.client;

import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;

import java.util.*;

/**
 * Manages widgets that are painted on the in-game HUD.
 */
@Environment(EnvType.CLIENT)
public enum CottonHud implements HudRenderCallback {
	INSTANCE;

	static {
		HudRenderCallback.EVENT.register(INSTANCE);
	}

	private final Map<WWidget, Position> widgets = new HashMap<>();

	/**
	 * Adds a new widget to the HUD at the specified offsets.
	 *
	 * <p>The offsets wrap around the screen. Use negative values for bottom/right edges.
	 *
	 * @param widget the widget
	 * @param x the x offset
	 * @param y the y offset
	 */
	public void add(WWidget widget, int x, int y) {
		widgets.put(widget, new Position(x, y));
	}

	/**
	 * Adds a new widget to the HUD at the specified offsets and resizes it.
	 *
	 * <p>The offsets wrap around the screen. Use negative values for bottom/right edges.
	 *
	 * @param widget the widget
	 * @param x the x offset
	 * @param y the y offset
	 * @param width the width of the widget
	 * @param height the height of the widget
	 */
	public void add(WWidget widget, int x, int y, int width, int height) {
		add(widget, x, y);
		widget.setSize(width, height);
	}

	/**
	 * Sets the positioner of the widget.
	 *
	 * @param widget the widget
	 */
	public void setPosition(WWidget widget, int x, int y) {
		Position pos = widgets.get(widget);
		pos.x = x;
		pos.y = y;
	}

	/**
	 * Removes the widget from the HUD.
	 *
	 * @param widget the widget
	 */
	public void remove(WWidget widget) {
		widgets.remove(widget);
	}

	@Override
	public void onHudRender(float tickDelta) {
		Window window = MinecraftClient.getInstance().getWindow();
		int hudWidth = window.getScaledWidth();
		int hudHeight = window.getScaledHeight();
		for (Map.Entry<WWidget, Position> entry : widgets.entrySet()) {
			WWidget widget = entry.getKey();
			Position pos = entry.getValue();
			widget.setLocation((hudWidth + pos.x) % hudWidth, (hudHeight + pos.y) % hudHeight);
			widget.paintBackground(widget.getX(), widget.getY(), -1, -1);
		}
	}

	private static final class Position {
		int x;
		int y;

		Position(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
