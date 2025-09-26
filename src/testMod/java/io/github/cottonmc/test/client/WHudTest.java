package io.github.cottonmc.test.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WWidget;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class WHudTest extends WWidget {
	private final Random random = new Random();
	private int offsetX;
	private int offsetY;

	@Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
		ScreenDrawing.coloredRect(context, x + offsetX, y + offsetY, width, height, 0xFF_00FF00);
	}

	@Override
	public void tick() {
		if (random.nextFloat() < 0.1f) {
			offsetX += random.nextInt(-1, 2);
			offsetY += random.nextInt(-1, 2);
		}
	}
}
