package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import org.jetbrains.annotations.Nullable;

/**
 * A bar that displays int values from a {@link PropertyDelegate}.
 *
 * <p>Bars can be used for all kinds of bars including
 * progress bars (and progress arrows) and energy bars.
 */
public class WBar extends WWidget {
	/**
	 * The background texture. If not null, it will be
	 * drawn behind the bar contents.
	 */
	@Nullable
	protected final Texture bg;

	/**
	 * The bar texture. If not null, it will be
	 * drawn to represent the current field.
	 */
	@Nullable
	protected final Texture bar;

	/**
	 * The ID of the displayed property in the {@link #properties}.
	 */
	protected final int field;

	/**
	 * The ID of the property representing the maximum value of the {@link #field}.
	 *
	 * <p>If {@code max} is 0, the {@link #maxValue} constant will be used instead.
	 */
	protected final int max;

	/**
	 * The constant maximum value of the {@link #field}.
	 *
	 * <p>This constant will only be used if {@link #max} is 0.
	 *
	 * @see #withConstantMaximum(Identifier, Identifier, int, int, Direction)
	 */
	protected int maxValue;
	protected PropertyDelegate properties;
	protected final Direction direction;
	protected String tooltipLabel;
	protected Text tooltipTextComponent;
	
	public WBar(Texture bg, Texture bar, int field, int maxfield) {
		this(bg, bar, field, maxfield, Direction.UP);
	}

	public WBar(Texture bg, Texture bar, int field, int maxfield, Direction dir) {
		this.bg = bg;
		this.bar = bar;
		this.field = field;
		this.max = maxfield;
		this.maxValue = 0;
		this.direction = dir;
	}

	public WBar(Identifier bg, Identifier bar, int field, int maxfield) {
		this(bg, bar, field, maxfield, Direction.UP);
	}

	public WBar(Identifier bg, Identifier bar, int field, int maxfield, Direction dir) {
		this(new Texture(bg), new Texture(bar), field, maxfield, dir);
	}

	/**
	 * Adds a tooltip to the WBar.
	 *
	 * Formatting Guide: The tooltip label is passed into {@code String.format} and can receive two integers
	 * (%d) - the first is the current value of the bar's focused field, and the second is the
	 * bar's focused maximum.
	 *
	 * @param label Translation key of the string to render on the tooltip.
	 * @return WBar with tooltip enabled and set.
	 */
	public WBar withTooltip(String label) {
		this.tooltipLabel = label;
		return this;
	}
	
	
	
	public WBar withTooltip(Text label) {
		this.tooltipTextComponent = label;
		return this;
	}
	
	@Override
	public boolean canResize() {
		return true;
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		if (bg!=null) {
			ScreenDrawing.texturedRect(matrices, x, y, getWidth(), getHeight(), bg, 0xFFFFFFFF);
		} else {
			ScreenDrawing.coloredRect(matrices, x, y, getWidth(), getHeight(), ScreenDrawing.colorAtOpacity(0x000000, 0.25f));
		}
		
		float percent = properties.get(field) / (float) properties.get(max);
		if (percent < 0) percent = 0f;
		if (percent > 1) percent = 1f;
		
		int barMax = getWidth();
		if (direction == Direction.DOWN || direction == Direction.UP) barMax = getHeight();
		percent = ((int) (percent * barMax)) / (float) barMax; //Quantize to bar size
		
		int barSize = (int) (barMax * percent);
		if (barSize <= 0) return;
		
		switch(direction) { //anonymous blocks in this switch statement are to sandbox variables
			case UP: {
				int left = x;
				int top = y + getHeight();
				top -= barSize;
				if (bar!=null) {
					ScreenDrawing.texturedRect(matrices, left, top, getWidth(), barSize, bar.image, bar.u1, MathHelper.lerp(percent, bar.v2, bar.v1), bar.u2, bar.v2, 0xFFFFFFFF);
				} else {
					ScreenDrawing.coloredRect(matrices, left, top, getWidth(), barSize,  ScreenDrawing.colorAtOpacity(0xFFFFFF, 0.5f));
				}
				break;
			}
			case RIGHT: {
				if (bar!=null) {
					ScreenDrawing.texturedRect(matrices, x, y, barSize, getHeight(), bar.image, bar.u1, bar.v1, MathHelper.lerp(percent, bar.u1, bar.u2), bar.v2, 0xFFFFFFFF);
				} else {
					ScreenDrawing.coloredRect(matrices, x, y, barSize, getHeight(), ScreenDrawing.colorAtOpacity(0xFFFFFF, 0.5f));
				}
				break;
			}
			case DOWN: {
				if (bar!=null) {
					ScreenDrawing.texturedRect(matrices, x, y, getWidth(), barSize, bar.image, bar.u1, bar.v1, bar.u2, MathHelper.lerp(percent, bar.v1, bar.v2), 0xFFFFFFFF);
				} else {
					ScreenDrawing.coloredRect(matrices, x, y, getWidth(), barSize, ScreenDrawing.colorAtOpacity(0xFFFFFF, 0.5f));
				}
				break;
			}
			case LEFT: {
				int left = x + getWidth();
				int top = y;
				left -= barSize;
				if (bar!=null) {
					ScreenDrawing.texturedRect(matrices, left, top, barSize, getHeight(), bar.image, MathHelper.lerp(percent, bar.u2, bar.u1), bar.v1, bar.u2, bar.v2, 0xFFFFFFFF);
				} else {
					ScreenDrawing.coloredRect(matrices, left, top, barSize, getHeight(), ScreenDrawing.colorAtOpacity(0xFFFFFF, 0.5f));
				}
				break;
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void addTooltip(TooltipBuilder information) {
		if (tooltipLabel!=null) {
			int value = (field>=0) ? properties.get(field) : 0;
			int valMax = (max>=0) ? properties.get(max) : maxValue;
			Text formatted;
			try {
				formatted = new TranslatableText(tooltipLabel, Integer.valueOf(value), Integer.valueOf(valMax));
			} catch (Throwable t) {
				formatted = new LiteralText(t.getLocalizedMessage());
			} //Fallback to raw tooltipLabel
			information.add(formatted);
		}
		if (tooltipTextComponent!=null) {
			try {
				information.add(tooltipTextComponent);
			} catch (Throwable t) {
				information.add(new LiteralText(t.getLocalizedMessage()));
			}
		}
	}
	
	@Override
	public void validate(GuiDescription host) {
		super.validate(host);
		if (properties==null) properties = host.getPropertyDelegate();
	}

	/**
	 * Gets the current properties of this bar.
	 *
	 * @return the current property delegate, or null if not initialized yet
	 */
	@Nullable
	public PropertyDelegate getProperties() {
		return properties;
	}

	/**
	 * Sets the current properties of this bar.
	 *
	 * <p>This method is meant for situations when a GUI description is unavailable (such as HUDs).
	 * {@link GuiDescription#getPropertyDelegate()} should be preferred over this if available.
	 *
	 * @param properties the properties
	 * @return this bar
	 */
	public WBar setProperties(PropertyDelegate properties) {
		this.properties = properties;
		return this;
	}

	/**
	 * Creates a WBar that has a constant maximum-value instead of getting the maximum from a field.
	 * @param bg         the background image to use for the bar
	 * @param bar        the foreground image that represents the filled bar
	 * @param field      the field index for bar values
	 * @param maxValue   the constant maximum value for the bar
	 * @param dir        the direction the bar should grow towards
	 * @return           a new WBar with a constant maximum value.
	 */
	public static WBar withConstantMaximum(Identifier bg, Identifier bar, int field, int maxValue, Direction dir) {
		WBar result = new WBar(bg, bar, field, -1, dir);
		result.maxValue = maxValue;
		return result;
	}

	public static enum Direction {
		UP,
		RIGHT,
		DOWN,
		LEFT;
	}
}
