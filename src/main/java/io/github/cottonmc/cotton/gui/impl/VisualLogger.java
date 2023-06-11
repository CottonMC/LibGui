package io.github.cottonmc.cotton.gui.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * A "logger" that renders its messages on the screen in dev envs.
 */
public final class VisualLogger {
	private static final List<Text> WARNINGS = new ArrayList<>();

	private final Logger logger;
	private final Class<?> clazz;

	public VisualLogger(Class<?> clazz) {
		logger = LogManager.getLogger(clazz);
		this.clazz = clazz;
	}

	public void error(String message, Object... params) {
		log(message, params, Level.ERROR, Formatting.RED);
	}

	public void warn(String message, Object... params) {
		log(message, params, Level.WARN, Formatting.GOLD);
	}

	private void log(String message, Object[] params, Level level, Formatting formatting) {
		logger.log(level, message, params);

		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			var text = Text.literal(clazz.getSimpleName() + '/');
			text.append(Text.literal(level.name()).formatted(formatting));
			text.append(Text.literal(": " + ParameterizedMessage.format(message, params)));

			WARNINGS.add(text);
		}
	}

	@Environment(EnvType.CLIENT)
	public static void render(DrawContext context) {
		var client = MinecraftClient.getInstance();
		var textRenderer = client.textRenderer;
		int width = client.getWindow().getScaledWidth();
		List<OrderedText> lines = new ArrayList<>();

		for (Text warning : WARNINGS) {
			lines.addAll(textRenderer.wrapLines(warning, width));
		}

		int fontHeight = textRenderer.fontHeight;
		int y = 0;

		for (var line : lines) {
			ScreenDrawing.coloredRect(context, 2, 2 + y, textRenderer.getWidth(line), fontHeight, 0x88_000000);
			ScreenDrawing.drawString(context, line, 2, 2 + y, 0xFF_FFFFFF);
			y += fontHeight;
		}
	}

	public static void reset() {
		WARNINGS.clear();
	}
}
