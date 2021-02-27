package io.github.cottonmc.cotton.gui.impl.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;

import io.github.cottonmc.cotton.gui.widget.WWidget;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface CottonScreenImpl {
	@Nullable
	WWidget getLastResponder();

	void setLastResponder(@Nullable WWidget lastResponder);

	void renderTextHover(MatrixStack matrices, @Nullable Style textStyle, int x, int y);
}
