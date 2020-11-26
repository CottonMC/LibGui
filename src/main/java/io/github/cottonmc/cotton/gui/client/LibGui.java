package io.github.cottonmc.cotton.gui.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import io.github.cottonmc.cotton.gui.impl.client.LibGuiClient;

/**
 * Provides access to global LibGui data such as the dark mode state.
 *
 * @since 4.0.0
 */
@Environment(EnvType.CLIENT)
public final class LibGui {
	public static boolean isDarkMode() {
		return LibGuiClient.config.darkMode;
	}
}
