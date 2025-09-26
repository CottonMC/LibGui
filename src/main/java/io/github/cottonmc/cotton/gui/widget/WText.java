package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.client.TextAlignment;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;
import org.jetbrains.annotations.Nullable;

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
	protected boolean drawShadows;
	protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
	protected VerticalAlignment verticalAlignment = VerticalAlignment.TOP;
	@Environment(EnvType.CLIENT)
	private List<OrderedText> wrappedLines;
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
		int yOffset = TextAlignment.getTextOffsetY(verticalAlignment, height, wrappedLines.size());
		int lineIndex = (y - yOffset) / font.fontHeight;

		if (lineIndex >= 0 && lineIndex < wrappedLines.size()) {
			OrderedText line = wrappedLines.get(lineIndex);
			int xOffset = TextAlignment.getTextOffsetX(horizontalAlignment, width, line);
			return font.getTextHandler().getStyleAt(line, x - xOffset);
		}

		return null;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
		if (wrappedLines == null || wrappingScheduled) {
			wrapLines();
			wrappingScheduled = false;
		}

		TextRenderer font = MinecraftClient.getInstance().textRenderer;
		int yOffset = TextAlignment.getTextOffsetY(verticalAlignment, height, wrappedLines.size());

		for (int i = 0; i < wrappedLines.size(); i++) {
			OrderedText line = wrappedLines.get(i);
			int c = shouldRenderInDarkMode() ? darkmodeColor : color;

			if (getDrawShadows()) {
				ScreenDrawing.drawStringWithShadow(context, line, horizontalAlignment, x, y + yOffset + i * font.fontHeight, width, c);
			} else {
				ScreenDrawing.drawString(context, line, horizontalAlignment, x, y + yOffset + i * font.fontHeight, width, c);
			}
		}

		Style hoveredTextStyle = getTextStyleAt(mouseX, mouseY);
		ScreenDrawing.drawTextHover(context, hoveredTextStyle, x + mouseX, y + mouseY);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public InputResult onClick(Click click, boolean doubled) {
		if (click.button() != 0) return InputResult.IGNORED; // only left clicks

		Style hoveredTextStyle = getTextStyleAt((int) click.x(), (int) click.y());
		if (hoveredTextStyle != null) {
			boolean processed = MinecraftClient.getInstance().currentScreen.handleTextClick(hoveredTextStyle);
			return InputResult.of(processed);
		}

		return InputResult.IGNORED;
	}

	/**
	 * Gets the text of this text widget.
	 *
	 * @return the text
	 */
	public Text getText() {
		return text;
	}

	/**
	 * Sets the text of this text widget.
	 *
	 * @param text the new text
	 * @return this text widget
	 */
	public WText setText(Text text) {
		Objects.requireNonNull(text, "text is null");
		this.text = text;
		wrappingScheduled = true;

		return this;
	}

	/**
	 * Gets the light mode color of this text widget.
	 *
	 * @return the color
	 */
	public int getColor() {
		return color;
	}

	/**
	 * Sets the light mode color of this text widget.
	 *
	 * @param color the new color
	 * @return this text widget
	 */
	public WText setColor(int color) {
		this.color = color;
		return this;
	}

	/**
	 * Gets the dark mode color of this text widget.
	 *
	 * @return the color
	 * @since 2.0.0
	 */
	public int getDarkmodeColor() {
		return darkmodeColor;
	}

	/**
	 * Sets the dark mode color of this text widget.
	 *
	 * @param darkmodeColor the new color
	 * @return this text widget
	 */
	public WText setDarkmodeColor(int darkmodeColor) {
		this.darkmodeColor = darkmodeColor;
		return this;
	}

	/**
	 * Sets the light and dark mode colors of this text widget.
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
	 * Checks whether shadows should be drawn for this text widget.
	 * 
	 * @return {@code true} shadows should be drawn, {@code false} otherwise
	 * @since 11.1.0
	 */
	public boolean getDrawShadows() {
		return drawShadows;
	}

	/**
	 * Sets whether shadows should be drawn for this text widget.
	 *
	 * @param drawShadows {@code true} if shadows should be drawn, {@code false} otherwise
	 * @return this text widget
	 * @since 11.1.0
	 */
	public WText setDrawShadows(boolean drawShadows) {
		this.drawShadows = drawShadows;
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

	@Environment(EnvType.CLIENT)
	@Override
	public void addNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, text);
	}
}
