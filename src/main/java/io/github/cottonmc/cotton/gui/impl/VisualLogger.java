package io.github.cottonmc.cotton.gui.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.slf4j.helpers.MessageFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * A "logger" that renders its messages on the screen in dev envs.
 */
public final class VisualLogger {
	private static final List<Component> WARNINGS = new ArrayList<>();

	private final Logger logger;
	private final Class<?> clazz;

	public VisualLogger(Class<?> clazz) {
		logger = LoggerFactory.getLogger(clazz);
		this.clazz = clazz;
	}

	public void error(String message, Object... params) {
		log(message, params, Level.ERROR, ChatFormatting.RED);
	}

	public void warn(String message, Object... params) {
		log(message, params, Level.WARN, ChatFormatting.GOLD);
	}

	private void log(String message, Object[] params, Level level, ChatFormatting formatting) {
		logger.atLevel(level).log(message, params);

		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			var text = Component.literal(clazz.getSimpleName() + '/');
			text.append(Component.literal(level.name()).withStyle(formatting));
			text.append(Component.literal(": " + MessageFormatter.arrayFormat(message, params).getMessage()));

			WARNINGS.add(text);
		}
	}

	@Environment(EnvType.CLIENT)
	public static void render(GuiGraphicsExtractor context) {
		var client = Minecraft.getInstance();
		var textRenderer = client.font;
		int width = client.getWindow().getGuiScaledWidth();
		List<FormattedCharSequence> lines = new ArrayList<>();

		for (Component warning : WARNINGS) {
			lines.addAll(textRenderer.split(warning, width));
		}

		int fontHeight = textRenderer.lineHeight;
		int y = 0;

		for (var line : lines) {
			ScreenDrawing.coloredRect(context, 2, 2 + y, textRenderer.width(line), fontHeight, 0x88_000000);
			ScreenDrawing.drawString(context, line, 2, 2 + y, 0xFF_FFFFFF);
			y += fontHeight;
		}
	}

	public static void reset() {
		WARNINGS.clear();
	}
}
