package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.client.TextHoverRendererScreen;
import io.github.cottonmc.cotton.gui.widget.data.Alignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import javax.annotation.Nullable;

/**
 * A single-line label widget.
 */
public class WLabel extends WWidget {
	protected Text text;
	protected Alignment alignment = Alignment.LEFT;
	protected int color;
	protected int darkmodeColor;

	/**
	 * The default text color for light mode labels.
	 */
	public static final int DEFAULT_TEXT_COLOR = 0x404040;

	/**
	 * The default text color for {@linkplain io.github.cottonmc.cotton.gui.client.LibGuiConfig#darkMode dark mode} labels.
	 */
	public static final int DEFAULT_DARKMODE_TEXT_COLOR = 0xbcbcbc;

	/**
	 * Constructs a new label.
	 *
	 * @param text the text of the label
	 * @param color the color of the label
	 */
	public WLabel(String text, int color) {
		this(new LiteralText(text), color);
	}

	/**
	 * Constructs a new label.
	 *
	 * @param text the text of the label
	 * @param color the color of the label
	 */
	public WLabel(Text text, int color) {
		this.text = text;
		this.color = color;
		this.darkmodeColor = (color==DEFAULT_TEXT_COLOR) ? DEFAULT_DARKMODE_TEXT_COLOR : color;
	}

	/**
	 * Constructs a new label with the {@linkplain #DEFAULT_TEXT_COLOR default text color}.
	 *
	 * @param text the text of the label
	 */
	public WLabel(String text) {
		this(text, DEFAULT_TEXT_COLOR);
	}

	/**
	 * Constructs a new label with the {@linkplain #DEFAULT_TEXT_COLOR default text color}.
	 *
	 * @param text the text of the label
	 * @since 1.8.0
	 */
	public WLabel(Text text) {
		this(text, DEFAULT_TEXT_COLOR);
	}

	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		ScreenDrawing.drawString(matrices, text, alignment, x, y, this.getWidth(), LibGuiClient.config.darkMode ? darkmodeColor : color);

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
		Text hoveredText = getTextAt(x, y);
		if (hoveredText != null) {
			Screen screen = MinecraftClient.getInstance().currentScreen;
			if (screen != null) {
				screen.handleTextClick(hoveredText);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Nullable
	public Text getTextAt(int x, int y) {
		if (isWithinBounds(x, y)) {
			return MinecraftClient.getInstance().textRenderer.method_27527().method_27489(text, x);
		}
		return null;
	}

	@Override
	public boolean canResize() {
		return true;
	}
	
	@Override
	public void setSize(int x, int y) {
		super.setSize(x, 20);
	}
	
	public WLabel setDarkmodeColor(int color) {
		darkmodeColor = color;
		return this;
	}
	
	public WLabel disableDarkmode() {
		this.darkmodeColor = this.color;
		return this;
	}
	
	public WLabel setColor(int color, int darkmodeColor) {
		this.color = color;
		this.darkmodeColor = darkmodeColor;
		return this;
	}
	
	public WLabel setText(Text text) {
		this.text = text;
		return this;
	}
	
	public WLabel setAlignment(Alignment align) {
		this.alignment = align;
		return this;
	}
}