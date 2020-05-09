package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.client.TextHoverRendererScreen;
import io.github.cottonmc.cotton.gui.widget.data.Alignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
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
	private boolean wrappingScheduled = false;

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
		wrappingScheduled = true;
	}

	@Override
	public boolean canResize() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	private void wrapLines() {
		TextRenderer font = MinecraftClient.getInstance().textRenderer;
		wrappedLines = font.method_27527().method_27491(text, width, Style.field_24360, false);
	}

	@Environment(EnvType.CLIENT)
	@Nullable
	public Text getTextAt(int x, int y) {
		TextRenderer font = MinecraftClient.getInstance().textRenderer;
		int lineIndex = y / font.fontHeight;

		if (lineIndex >= 0 && lineIndex < wrappedLines.size()) {
			Text line = wrappedLines.get(lineIndex);
			return font.method_27527().method_27489(line, x);
		}

		return null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		if (wrappedLines == null || wrappingScheduled) {
			wrapLines();
			wrappingScheduled = false;
		}

		TextRenderer font = MinecraftClient.getInstance().textRenderer;
		for (int i = 0; i < wrappedLines.size(); i++) {
			Text line = wrappedLines.get(i);
			int c = LibGuiClient.config.darkMode ? darkmodeColor : color;

			ScreenDrawing.drawString(matrices, line, alignment, x, y + i * font.fontHeight, width, c);
		}

		Text hoveredText = getTextAt(mouseX, mouseY);
		if (hoveredText != null) {
			Screen screen = MinecraftClient.getInstance().currentScreen;
			if (screen instanceof TextHoverRendererScreen) {
				((TextHoverRendererScreen) screen).renderTextHover(matrices, hoveredText, x + mouseX, y + mouseY);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onClick(int x, int y, int button) {
		if (button != 0) return; // only left clicks

		Text hoveredText = getTextAt(x, y);
		if (hoveredText != null) {
			MinecraftClient.getInstance().currentScreen.handleTextClick(hoveredText);
		}
	}

	public Text getText() {
		return text;
	}

	public WText setText(Text text) {
		Objects.requireNonNull(text, "text is null");
		this.text = text;
		wrappingScheduled = true;

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

	/**
	 * Gets the alignment of this text widget.
	 *
	 * @return the alignment
	 * @since 1.9.0
	 */
	public Alignment getAlignment() {
		return alignment;
	}

	/**
	 * Sets the alignment of this text widget.
	 *
	 * @param alignment the new alignment
	 * @return this widget
	 * @since 1.9.0
	 */
	public WText setAlignment(Alignment alignment) {
		this.alignment = alignment;
		return this;
	}
}
