package io.github.cottonmc.cotton.gui.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.impl.client.NinePatchTextureRendererImpl;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import juuxel.libninepatch.NinePatch;

import java.util.function.Consumer;

/**
 * Nine-patch background painters paint rectangles using a special nine-patch texture.
 * The texture is divided into nine sections: four corners, four edges and a center part.
 * The edges and the center are either tiled or stretched, depending on the mode of the painter,
 * to fill the area between the corners. By default, the texture is tiled.
 *
 * <p>Nine-patch background painters can be created using {@link BackgroundPainter#createNinePatch(Identifier)},
 * {@link #createNinePatch(Texture, Consumer)}, or with the constructor directly. The latter two let you customise
 * the look of the background more finely.
 *
 * <p>{@code NinePatchBackgroundPainter} has a customizable padding that can be applied.
 * By default there is no padding, but you can set it using {@link NinePatchBackgroundPainter#setPadding(int)}.
 *
 * @since 4.0.0
 */
@Environment(EnvType.CLIENT)
public final class NinePatchBackgroundPainter implements BackgroundPainter {
	private final NinePatch<Identifier> ninePatch;
	private int topPadding = 0;
	private int leftPadding = 0;
	private int bottomPadding = 0;
	private int rightPadding = 0;

	public NinePatchBackgroundPainter(NinePatch<Identifier> ninePatch) {
		this.ninePatch = ninePatch;
	}

	public int getTopPadding() {
		return topPadding;
	}

	public NinePatchBackgroundPainter setTopPadding(int topPadding) {
		this.topPadding = topPadding;
		return this;
	}

	public int getLeftPadding() {
		return leftPadding;
	}

	public NinePatchBackgroundPainter setLeftPadding(int leftPadding) {
		this.leftPadding = leftPadding;
		return this;
	}

	public int getBottomPadding() {
		return bottomPadding;
	}

	public NinePatchBackgroundPainter setBottomPadding(int bottomPadding) {
		this.bottomPadding = bottomPadding;
		return this;
	}

	public int getRightPadding() {
		return rightPadding;
	}

	public NinePatchBackgroundPainter setRightPadding(int rightPadding) {
		this.rightPadding = rightPadding;
		return this;
	}

	public NinePatchBackgroundPainter setPadding(int padding) {
		this.topPadding = this.leftPadding = this.bottomPadding = this.rightPadding = padding;
		return this;
	}

	public NinePatchBackgroundPainter setPadding(int vertical, int horizontal) {
		this.topPadding = this.bottomPadding = vertical;
		this.leftPadding = this.rightPadding = horizontal;
		return this;
	}

	public NinePatchBackgroundPainter setPadding(int topPadding, int leftPadding, int bottomPadding, int rightPadding) {
		this.topPadding = topPadding;
		this.leftPadding = leftPadding;
		this.bottomPadding = bottomPadding;
		this.rightPadding = rightPadding;

		return this;
	}

	@Override
	public void paintBackground(DrawContext context, int left, int top, WWidget panel) {
		var matrices = context.getMatrices();
		matrices.pushMatrix();
		matrices.translate(left - leftPadding, top - topPadding);
		ninePatch.draw(NinePatchTextureRendererImpl.INSTANCE, context, panel.getWidth() + leftPadding + rightPadding, panel.getHeight() + topPadding + bottomPadding);

		matrices.popMatrix();
	}
}
