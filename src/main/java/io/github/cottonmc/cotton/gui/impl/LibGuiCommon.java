package io.github.cottonmc.cotton.gui.impl;

import net.fabricmc.api.ModInitializer;

public final class LibGuiCommon implements ModInitializer {
	@Override
	public void onInitialize() {
		ScreenNetworkingImpl.init();
	}
}
