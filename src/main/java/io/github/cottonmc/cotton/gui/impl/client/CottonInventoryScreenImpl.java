package io.github.cottonmc.cotton.gui.impl.client;

import net.minecraft.client.gui.DrawContext;

public interface CottonInventoryScreenImpl extends CottonScreenImpl {
	void paintDescription(DrawContext context, int mouseX, int mouseY, float delta);
}
