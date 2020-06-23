package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Style;

import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.client.TextHoverRendererScreen;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * A multiline label widget.
 *
 * @since 1.8.0
 */
public class WText extends WWidget {
	protected StringRenderable text;
	protected int color;
	protected int darkmodeColor;
	protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
	protected VerticalAlignment verticalAlignment = VerticalAlignment.TOP;
	private List<StringRenderable> wrappedLines;
	private boolean wrappingScheduled = false;

	public WText(StringRenderable text) {
		this(text, WLabel.DEFAULT_TEXT_COLOR);
	}

	public WText(StringRenderable text, int color) {
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
		wrappedLines = font.wrapLines(text, width);
	}

	/**
	 * Gets the text style at the specific widget-space coordinates.
	 *
	 * @param x the X coordinate in widget space
	 * @param y the Y coordinate in widget space
	 * @return the text style at the position, or null if not found
	 */
	@Environment(EnvType.CLIENT)
	@Nullable
	public Style getTextStyleAt(int x, int y) {
		TextRenderer font = MinecraftClient.getInstance().textRenderer;
		int lineIndex = y / font.fontHeight;

		if (lineIndex >= 0 && lineIndex < wrappedLines.size()) {
			StringRenderable line = wrappedLines.get(lineIndex);
			return font.getTextHandler().trimToWidth(line, x);
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

		int yOffset;
		switch (verticalAlignment) {
			case CENTER:
				yOffset = height / 2 - font.fontHeight * wrappedLines.size() / 2;
				break;
			case BOTTOM:
				yOffset = height - font.fontHeight * wrappedLines.size();
				break;
			case TOP:
			default:
				yOffset = 0;
				break;
		}

		for (int i = 0; i < wrappedLines.size(); i++) {
			StringRenderable line = wrappedLines.get(i);
			int c = LibGuiClient.config.darkMode ? darkmodeColor : color;

			ScreenDrawing.drawString(matrices, line, horizontalAlignment, x, y + yOffset + i * font.fontHeight, width, c);
		}

		Style hoveredTextStyle = getTextStyleAt(mouseX, mouseY);
		if (hoveredTextStyle != null) {
			Screen screen = MinecraftClient.getInstance().currentScreen;
			if (screen instanceof TextHoverRendererScreen) {
				((TextHoverRendererScreen) screen).renderTextHover(matrices, hoveredTextStyle, x + mouseX, y + mouseY);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onClick(int x, int y, int button) {
		if (button != 0) return; // only left clicks

		Style hoveredTextStyle = getTextStyleAt(x, y);
		if (hoveredTextStyle != null) {
			MinecraftClient.getInstance().currentScreen.handleTextClick(hoveredTextStyle);
		}
	}

	/**
	 * Gets the text of this label.
	 *
	 * @return the text
	 */
	public StringRenderable getText() {
		return text;
	}

	/**
	 * Sets the text of this label.
	 *
	 * @param text the new text
	 * @return this label
	 */
	public WText setText(StringRenderable text) {
		Objects.requireNonNull(text, "text is null");
		this.text = text;
		wrappingScheduled = true;

		return this;
	}

	/**
	 * Gets the light mode color of this label.
	 *
	 * @return the color
	 */
	public int getColor() {
		return color;
	}

	/**
	 * Sets the light mode color of this label.
	 *
	 * @param color the new color
	 * @return this text widget
	 */
	public WText setColor(int color) {
		this.color = color;
		return this;
	}

	/**
	 * Gets the dark mode color of this label.
	 *
	 * @return the color
	 * @since 2.0.0
	 */
	public int getDarkmodeColor() {
		return darkmodeColor;
	}

	/**
	 * Sets the dark mode color of this label.
	 *
	 * @param darkmodeColor the new color
	 * @return this text widget
	 */
	public WText setDarkmodeColor(int darkmodeColor) {
		this.darkmodeColor = darkmodeColor;
		return this;
	}

	/**
	 * Sets the light and dark mode colors of this label.
	 *
	 * @param color         the new light color
	 * @param darkmodeColor the new dark color
	 * @return this text widget
	 */
	public WText setColor(int color, int darkmodeColor) {
		setColor(color);
		setDarkmodeColor(darkmodeColor);
		return this;
	}

	/**
	 * Disables separate dark mode coloring by copying the dark color to be the light color.
	 *
	 * @return this text widget
	 */
	public WText disableDarkmode() {
		this.darkmodeColor = this.color;
		return this;
	}

	/**
	 * Gets the horizontal alignment of this text widget.
	 *
	 * @return the alignment
	 * @since 1.9.0
	 */
	public HorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}

	/**
	 * Sets the horizontal alignment of this text widget.
	 *
	 * @param horizontalAlignment the new alignment
	 * @return this widget
	 * @since 1.9.0
	 */
	public WText setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
		return this;
	}

	/**
	 * Gets the vertical alignment of this text widget.
	 *
	 * @return the alignment
	 * @since 2.0.0
	 */
	public VerticalAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	/**
	 * Sets the vertical alignment of this text widget.
	 *
	 * @param verticalAlignment the new alignment
	 * @return this widget
	 * @since 2.0.0
	 */
	public WText setVerticalAlignment(VerticalAlignment verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
		return this;
	}
}
