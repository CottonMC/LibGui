package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.client.TextHoverRendererScreen;
import io.github.cottonmc.cotton.gui.widget.data.Alignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Texts;
import net.minecraft.text.Text;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * A multiline label widget.
 *
 * @since 1.8.0
 */
public class WText extends WWidget {
	protected Text text;
	protected int color;
	protected int darkmodeColor;
	protected Alignment alignment = Alignment.LEFT;
	private List<Text> wrappedLines;

	public WText(Text text) {
		this(text, WLabel.DEFAULT_TEXT_COLOR);
	}

	public WText(Text text, int color) {
		this.text = Objects.requireNonNull(text, "text must not be null");
		this.color = color;
		this.darkmodeColor = (color == WLabel.DEFAULT_TEXT_COLOR) ? WLabel.DEFAULT_DARKMODE_TEXT_COLOR : color;
	}

	@Override
	public void setSize(int x, int y) {
		super.setSize(x, y);
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			wrapLines();
		}
	}

	@Environment(EnvType.CLIENT)
	private void wrapLines() {
		TextRenderer font = MinecraftClient.getInstance().textRenderer;
		wrappedLines = Texts.wrapLines(text, width, font, true, true);
	}

	@Environment(EnvType.CLIENT)
	@Nullable
	protected Text getTextAt(int x, int y) {
		TextRenderer font = MinecraftClient.getInstance().textRenderer;
		int lineIndex = y / font.fontHeight;

		if (lineIndex >= 0 && lineIndex < wrappedLines.size()) {
			Text line = wrappedLines.get(lineIndex);
			int xi = 0;
			for (Text part : line) {
				xi += font.getStringWidth(part.asFormattedString());
				if (xi > x) return part;
			}
		}

		return null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paintBackground(int x, int y, int mouseX, int mouseY) {
		TextRenderer font = MinecraftClient.getInstance().textRenderer;
		for (int i = 0; i < wrappedLines.size(); i++) {
			Text line = wrappedLines.get(i);
			int c = LibGuiClient.config.darkMode ? darkmodeColor : color;
			String str = line.asFormattedString();

			ScreenDrawing.drawString(str, alignment, x, y + i * font.fontHeight, width, c);
		}

		Text hoveredText = getTextAt(mouseX, mouseY);
		if (hoveredText != null) {
			Screen screen = MinecraftClient.getInstance().currentScreen;
			if (screen instanceof TextHoverRendererScreen) {
				((TextHoverRendererScreen) screen).renderTextHover(hoveredText, x + mouseX, y + mouseY);
			}
		}
	}

	@Override
	public void onClick(int x, int y, int button) {
		if (button != 0) return; // only left clicks

		Text hoveredText = getTextAt(x, y);
		if (hoveredText != null) {
			MinecraftClient.getInstance().currentScreen.handleComponentClicked(hoveredText);
		}
	}

	public Text getText() {
		return text;
	}

	public WText setText(Text text) {
		this.text = text;

		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			wrapLines();
		}

		return this;
	}

	public int getColor() {
		return color;
	}

	public WText setColor(int color) {
		this.color = color;
		return this;
	}

	public WText setDarkmodeColor(int darkmodeColor) {
		this.darkmodeColor = darkmodeColor;
		return this;
	}

	public WText setColor(int color, int darkmodeColor) {
		setColor(color);
		setDarkmodeColor(darkmodeColor);
		return this;
	}

	public WText disableDarkmode() {
		this.darkmodeColor = this.color;
		return this;
	}
}
