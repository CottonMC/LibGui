package io.github.cottonmc.cotton.gui.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

import io.github.cottonmc.cotton.gui.widget.WWidget;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface CottonScreenImpl {
	default Screen asScreen() {
		return (Screen) this;
	}

	@Nullable
	WWidget getLastResponder();

	void setLastResponder(@Nullable WWidget lastResponder);
}
