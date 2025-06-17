package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;

import io.github.cottonmc.cotton.gui.client.LibGui;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.client.TextAlignment;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;

import java.util.function.Supplier;

/**
 * Dynamic labels are labels that pull their text from a {@code Supplier<String>}.
 * They can be used for automatically getting data from a block entity or another data source.
 *
 * <p>Translating strings in dynamic labels should be done using
 * {@link net.minecraft.client.resource.language.I18n#translate(String, Object...)}.
 */
public class WDynamicLabel extends WWidget {
	protected Supplier<String> text;
	protected HorizontalAlignment horizontalAlignment = HorizontalAlignment.LEFT;
	protected VerticalAlignment verticalAlignment = VerticalAlignment.TOP;
	protected int color;
	protected int darkmodeColor;
	protected boolean drawShadows;

	/**
	 * The default text color for light mode labels.
	 */
	public static final int DEFAULT_TEXT_COLOR = WLabel.DEFAULT_TEXT_COLOR;

	/**
	 * The default text color for {@linkplain LibGui#isDarkMode() dark mode} labels.
	 */
	public static final int DEFAULT_DARKMODE_TEXT_COLOR = WLabel.DEFAULT_DARKMODE_TEXT_COLOR;

	/**
	 * Constructs a new dynamic label.
	 *
	 * @param text the text of the label
	 * @param color the color of the label
	 */
	public WDynamicLabel(Supplier<String> text, int color) {
		this.text = text;
		this.color = color;
		this.darkmodeColor = (color==DEFAULT_TEXT_COLOR) ? DEFAULT_DARKMODE_TEXT_COLOR : color;
	}

	/**
	 * Constructs a new dynamic label with the {@linkplain #DEFAULT_TEXT_COLOR default text color}.
	 *
	 * @param text the text of the label
	 */
	public WDynamicLabel(Supplier<String> text) {
		this(text, DEFAULT_TEXT_COLOR);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
		int yOffset = TextAlignment.getTextOffsetY(verticalAlignment, height, 1);

		String tr = text.get();

		if (getDrawShadows()) {
			ScreenDrawing.drawStringWithShadow(context, tr, horizontalAlignment, x, y + yOffset, this.getWidth(), shouldRenderInDarkMode() ? darkmodeColor : color);
		} else {
			ScreenDrawing.drawString(context, tr, horizontalAlignment, x, y + yOffset, this.getWidth(), shouldRenderInDarkMode() ? darkmodeColor : color);
		}
	}

	@Override
	public boolean canResize() {
		return true;
	}
	
	@Override
	public void setSize(int x, int y) {
		super.setSize(x, 20);
	}
	
	/**
	 * Sets the dark mode color of this label.
	 *
	 * @param color the new color
	 * @return this label
	 */
	public WDynamicLabel setDarkmodeColor(int color) {
		darkmodeColor = color;
		return this;
	}
	
	/**
	 * Disables separate dark mode coloring by copying the dark color to be the light color.
	 *
	 * @return this label
	 */
	public WDynamicLabel disableDarkmode() {
		this.darkmodeColor = this.color;
		return this;
	}
	
	/**
	 * Sets the light and dark mode colors of this label.
	 *
	 * @param color         the new light color
	 * @param darkmodeColor the new dark color
	 * @return this label
	 */
	public WDynamicLabel setColor(int color, int darkmodeColor) {
		this.color = color;
		this.darkmodeColor = darkmodeColor;
		return this;
	}

	/**
	 * Checks whether shadows should be drawn for this label.
	 * 
	 * @return {@code true} shadows should be drawn, {@code false} otherwise
	 * @since 11.1.0
	 */
	public boolean getDrawShadows() {
		return drawShadows;
	}

	/**
	 * Sets whether shadows should be drawn for this label.
	 *
	 * @param drawShadows {@code true} if shadows should be drawn, {@code false} otherwise
	 * @return this label
	 * @since 11.1.0
	 */
	public WDynamicLabel setDrawShadows(boolean drawShadows) {
		this.drawShadows = drawShadows;
		return this;
	}
	
	/**
	 * Sets the text of this label.
	 *
	 * @param text the new text
	 * @return this label
	 */
	public WDynamicLabel setText(Supplier<String> text) {
		this.text = text;
		return this;
	}

	/**
	 * Gets the horizontal text alignment of this label.
	 *
	 * @return the alignment
	 */
	public HorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}
	
	/**
	 * Sets the horizontal text alignment of this label.
	 *
	 * @param align the new text alignment
	 * @return this label
	 */
	public WDynamicLabel setHorizontalAlignment(HorizontalAlignment align) {
		this.horizontalAlignment = align;
		return this;
	}

	/**
	 * Gets the vertical text alignment of this label.
	 *
	 * @return the alignment
	 * @since 11.1.0
	 */
	public VerticalAlignment getVerticalAlignment() {
		return verticalAlignment;
	}

	/**
	 * Sets the vertical text alignment of this label.
	 *
	 * @param align the new text alignment
	 * @return this label
	 * @since 11.1.0
	 */
	public WDynamicLabel setVerticalAlignment(VerticalAlignment align) {
		this.verticalAlignment = align;
		return this;
	}
}
