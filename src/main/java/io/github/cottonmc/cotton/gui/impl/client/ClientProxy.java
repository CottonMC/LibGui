package io.github.cottonmc.cotton.gui.impl.client;

import net.minecraft.client.Minecraft;

import io.github.cottonmc.cotton.gui.impl.Proxy;
import io.github.cottonmc.cotton.gui.impl.VisualLogger;
import io.github.cottonmc.cotton.gui.widget.WWidget;

public final class ClientProxy extends Proxy {
	private static final VisualLogger LOGGER = new VisualLogger(ClientProxy.class);

	@Override
	public void addPainters(WWidget widget) {
		widget.addPainters();
	}

	@Override
	public void setTextInputFocused(boolean focused) {
		var mc = Minecraft.getInstance();
		var screen = mc.gui.screen();

		if (screen instanceof CottonScreenImpl) {
			Minecraft.getInstance().onTextInputFocusChange(screen, focused);
		} else {
			LOGGER.warn("Tried to focus LibGui text input widget outside of LibGui screen");
		}
	}
}
