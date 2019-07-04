package io.github.cottonmc.cotton.gui.widget;

import java.util.List;

import io.github.cottonmc.cotton.gui.CottonScreenController;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class WBar extends WWidget {
	protected final Identifier bg;
	protected final Identifier bar;
	protected final int field;
	protected final int max;
	protected int maxValue;
	protected PropertyDelegate properties;
	protected final Direction direction;
	protected String tooltipLabel;
	protected Text tooltipTextComponent;
	
	public WBar(Identifier bg, Identifier bar, int field, int maxfield) {
		this(bg, bar, field, maxfield, Direction.UP);
	}

	public WBar(Identifier bg, Identifier bar, int field, int maxfield, Direction dir) {
		this.bg = bg;
		this.bar = bar;
		this.field = field;
		this.max = maxfield;
		this.maxValue = 0;
		this.direction = dir;
	}

	/**
	 * Adds a tooltip to the WBar.
	 *
	 * Formatting Guide: The tooltip label is passed into String.Format and can recieve two integers
	 * (%d) - the first is the current value of the bar's focused field, and the second is the
	 * bar's focused maximum.
	 * @param label String to render on the tooltip.
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
	public void paintBackground(int x, int y) {
		if (bg!=null) {
			ScreenDrawing.rect(bg, x, y, getWidth(), getHeight(), 0xFFFFFFFF);
		} else {
			ScreenDrawing.rect(x, y, getWidth(), getHeight(), ScreenDrawing.colorAtOpacity(0x000000, 0.25f));
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
					ScreenDrawing.rect(bar, left, top, getWidth(), barSize, 0, 1 - percent, 1, 1, 0xFFFFFFFF);
				} else {
					ScreenDrawing.rect(left, top, getWidth(), barSize,  ScreenDrawing.colorAtOpacity(0xFFFFFF, 0.5f));
				}
				break;
			}
			case RIGHT: {
				if (bar!=null) {
					ScreenDrawing.rect(bar, x, y, barSize, getHeight(), 0, 0, percent, 1, 0xFFFFFFFF);
				} else {
					ScreenDrawing.rect(x, y, barSize, getHeight(), ScreenDrawing.colorAtOpacity(0xFFFFFF, 0.5f));
				}
				break;
			}
			case DOWN: {
				if (bar!=null) {
					ScreenDrawing.rect(bar, x, y, getWidth(), barSize, 0, 0, 1, percent, 0xFFFFFFFF);
				} else {
					ScreenDrawing.rect(x, y, getWidth(), barSize, ScreenDrawing.colorAtOpacity(0xFFFFFF, 0.5f));
				}
				break;
			}
			case LEFT: {
				int left = x + getWidth();
				int top = y;
				left -= barSize;
				if (bar!=null) {
					ScreenDrawing.rect(bar, left, top, barSize, getHeight(), 1 - percent, 0, 1, 1, 0xFFFFFFFF);
				} else {
					ScreenDrawing.rect(left, top, barSize, getHeight(), ScreenDrawing.colorAtOpacity(0xFFFFFF, 0.5f));
				}
				break;
			}
		}
	}

	@Override
	public void addInformation(List<String> information) {
		if (tooltipLabel!=null) {
			int value = (field>=0) ? properties.get(field) : 0;
			int valMax = (max>=0) ? properties.get(max) : maxValue;
			String formatted = tooltipLabel;
			try {
				formatted = new TranslatableText(tooltipLabel, Integer.valueOf(value), Integer.valueOf(valMax)).asFormattedString();
			} catch (Throwable t) {
				formatted = t.getLocalizedMessage();
			} //Fallback to raw tooltipLabel
			information.add(formatted);
		}
		if (tooltipTextComponent!=null) {
			try {
				information.add(tooltipTextComponent.asFormattedString());
			} catch (Throwable t) {
				information.add(t.getLocalizedMessage());
			}
		}
	}
	
	@Override
	public void createPeers(CottonScreenController c) {
		if (properties==null) properties = c.getPropertyDelegate();
	}
	
	/**
	 * Creates a WBar that has a constant maximum-value instead of getting the maximum from a field.
	 * @param bg         the background image to use for the bar
	 * @param bar        the foreground image that represents the filled bar
	 * @param properties the PropertyDelegate to pull bar values from
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