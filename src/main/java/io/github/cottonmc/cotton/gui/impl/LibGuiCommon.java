package io.github.cottonmc.cotton.gui.impl;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public final class LibGuiCommon implements ModInitializer {
	public static final String MOD_ID = "libgui";

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ScreenNetworkingImpl.init();
	}
}
