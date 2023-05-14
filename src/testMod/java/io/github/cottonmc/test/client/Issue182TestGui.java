package io.github.cottonmc.test.client;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WBox;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.Axis;

// Used for testing issue #182 (https://github.com/CottonMC/LibGui/issues/182):
// Highlighting/selecting text in the top field makes the bottom one blue.
public final class Issue182TestGui extends LightweightGuiDescription {
	public Issue182TestGui() {
		WBox root = new WBox(Axis.VERTICAL);
		root.add(new WTextField(), 100, 20);
		root.add(new WTextField(), 100, 20);
		setRootPanel(root);
		root.validate(this);
	}
}
