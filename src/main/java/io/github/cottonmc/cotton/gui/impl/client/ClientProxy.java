package io.github.cottonmc.cotton.gui.impl.client;

import io.github.cottonmc.cotton.gui.impl.Proxy;
import io.github.cottonmc.cotton.gui.widget.WWidget;

public final class ClientProxy extends Proxy {
	@Override
	public void addPainters(WWidget widget) {
		widget.addPainters();
	}
}
