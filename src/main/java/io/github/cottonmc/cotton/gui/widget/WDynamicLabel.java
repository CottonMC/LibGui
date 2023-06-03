package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;

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
	protected HorizontalAlignment alignment = HorizontalAlignment.LEFT;
	protected int color;
	protected int darkmodeColor;

	public static final int DEFAULT_TEXT_COLOR = 0x404040;
	public static final int DEFAULT_DARKMODE_TEXT_COLOR = 0xbcbcbc;

	public WDynamicLabel(Supplier<String> text, int color) {
		this.text = text;
		this.color = color;
		this.darkmodeColor = (color==DEFAULT_TEXT_COLOR) ? DEFAULT_DARKMODE_TEXT_COLOR : color;
	}

	public WDynamicLabel(Supplier<String> text) {
		this(text, DEFAULT_TEXT_COLOR);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
		String tr = text.get();
		ScreenDrawing.drawString(context, tr, alignment, x, y, this.getWidth(), shouldRenderInDarkMode() ? darkmodeColor : color);
	}

	@Override
	public boolean canResize() {
		return true;
	}
	
	@Override
	public void setSize(int x, int y) {
		super.setSize(x, 20);
	}
	
	public WDynamicLabel setDarkmodeColor(int color) {
		darkmodeColor = color;
		return this;
	}
	
	public WDynamicLabel disableDarkmode() {
		this.darkmodeColor = this.color;
		return this;
	}
	
	public WDynamicLabel setColor(int color, int darkmodeColor) {
		this.color = color;
		this.darkmodeColor = darkmodeColor;
		return this;
	}
	
	public WDynamicLabel setText(Supplier<String> text) {
		this.text = text;
		return this;
	}
	
	public WDynamicLabel setAlignment(HorizontalAlignment align) {
		this.alignment = align;
		return this;
	}
}
