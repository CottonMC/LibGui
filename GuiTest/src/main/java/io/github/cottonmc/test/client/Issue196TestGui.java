package io.github.cottonmc.test.client;

import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WTextField;

public final class Issue196TestGui extends LightweightGuiDescription {
	public Issue196TestGui() {
		WTextField textField = new WTextField(Text.literal("Select with tab and type text"));
		textField.setText("");
		((WGridPanel) rootPanel).add(textField, 0, 0, 4, 1);
		rootPanel.validate(this);
	}
}
