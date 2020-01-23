package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.data.Alignment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;

/**
 * A panel title widget with a 3D effect.
 *
 * <img src="https://github.com/CottonMC/docs/images/libgui_title.png" alt="Screenshot of a title widget">
 *
 * <p>Titles should be added to their panels at (0, 0) and they should be as wide as the panel.
 * The default title painter assumes that the containing panel uses {@link BackgroundPainter#VANILLA} or
 * a {@linkplain BackgroundPainter.NinePatch nine-patch background painter} with a padding of 8.
 */
public class WTitle extends WWidget {
	public static final BackgroundPainter DEFAULT_BACKGROUND_PAINTER =
			BackgroundPainter.createLightDarkVariants(
					BackgroundPainter.createNinePatch(new Identifier("libgui", "textures/widget/title_light.png"), 8).setBottomPadding(0),
					BackgroundPainter.createNinePatch(new Identifier("libgui", "textures/widget/title_dark.png"), 8).setBottomPadding(0)
			);

	private Text label;
	private Alignment alignment = Alignment.CENTER;
	private int color = 0xFFFFFF;

	@Environment(EnvType.CLIENT)
	@Nullable
	private BackgroundPainter backgroundPainter = DEFAULT_BACKGROUND_PAINTER;

	public WTitle(Text label) {
		this.label = label;
	}

	@Override
	public boolean canResize() {
		return true;
	}

	@Override
	public void setSize(int x, int y) {
		super.setSize(x, 16);
	}

	@Environment(EnvType.CLIENT)
	@Nullable
	public BackgroundPainter getBackgroundPainter() {
		return backgroundPainter;
	}

	@Environment(EnvType.CLIENT)
	public WTitle setBackgroundPainter(@Nullable BackgroundPainter backgroundPainter) {
		this.backgroundPainter = backgroundPainter;
		return this;
	}

	public Text getLabel() {
		return label;
	}

	public WTitle setLabel(Text label) {
		this.label = label;
		return this;
	}

	public int getColor() {
		return color;
	}

	public WTitle setColor(int color) {
		this.color = color;
		return this;
	}

	public Alignment getAlignment() {
		return alignment;
	}

	public WTitle setAlignment(Alignment alignment) {
		this.alignment = alignment;
		return this;
	}

	@Override
	public void paintBackground(int x, int y, int mouseX, int mouseY) {
		if (backgroundPainter != null) backgroundPainter.paintBackground(x, y, this);

		ScreenDrawing.drawStringWithShadow(label.asFormattedString(), alignment, x, y, getWidth(), color);
	}
}
