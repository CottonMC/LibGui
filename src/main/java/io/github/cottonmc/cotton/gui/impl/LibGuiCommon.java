package io.github.cottonmc.cotton.gui.impl;

import net.fabricmc.api.ModInitializer;

public final class LibGuiCommon implements ModInitializer {
	public static final String MOD_ID = "libgui";

	@Override
	public void onInitialize() {
		ScreenNetworkingImpl.init();
	}
}
