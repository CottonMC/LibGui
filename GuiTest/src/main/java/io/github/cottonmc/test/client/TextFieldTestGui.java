package io.github.cottonmc.test.client;

import net.minecraft.item.Items;
import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;

public class TextFieldTestGui extends LightweightGuiDescription {
	public TextFieldTestGui() {
		WGridPanel grid = (WGridPanel) rootPanel;
		WTextField textField = new WTextField(Text.of("Type something")).setMaxLength(Integer.MAX_VALUE);
		grid.add(textField, 0, 0, 6, 1);
		grid.add(new WButton(new ItemIcon(Items.BARRIER), Text.of("Clear")).setOnClick(() -> textField.setText("")), 0, 2, 6, 1);
		rootPanel.validate(this);
	}
}
