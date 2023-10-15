package io.github.cottonmc.cotton.gui.impl.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;

import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;

public final class TextAlignment {
	public static int getTextOffsetX(HorizontalAlignment alignment, int width, OrderedText text) {
		return switch (alignment) {
			case LEFT -> 0;

			case CENTER -> {
				TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
				int textWidth = renderer.getWidth(text);
				yield width / 2 - textWidth / 2;
			}

			case RIGHT -> {
				TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
				int textWidth = renderer.getWidth(text);
				yield width - textWidth;
			}
		};
	}

	public static int getTextOffsetY(VerticalAlignment alignment, int height, int lines) {
		return switch (alignment) {
			case TOP -> 0;

			case CENTER -> {
				TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
				int textHeight = renderer.fontHeight * lines;
				yield height / 2 - textHeight / 2;
			}

			case BOTTOM -> {
				TextRenderer renderer = MinecraftClient.getInstance().textRenderer;
				int textHeight = renderer.fontHeight * lines;
				yield height - textHeight;
			}
		};
	}
}
