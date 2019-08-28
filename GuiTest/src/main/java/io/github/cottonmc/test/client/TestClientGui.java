package io.github.cottonmc.test.client;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WListPanel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.minecraft.text.LiteralText;

public class TestClientGui extends LightweightGuiDescription {

	public TestClientGui() {
		WGridPanel root = new WGridPanel(24);
		this.setRootPanel(root);
		
		WLabel title = new WLabel(new LiteralText("Client Test Gui"), WLabel.DEFAULT_TEXT_COLOR);
		root.add(title, 0, 0);
		
		WTextField text = new WTextField();
		text.setSuggestion("Test Suggestion");
		root.add(text, 0, 1, 8, 1);
		text.setSize(8*18, 20);
		
		ArrayList<String> data = new ArrayList<>();
		for(int i=0; i<100; i++) {
			data.add(""+i);
		}
		
		BiConsumer<String, WLabel> configurator = (String s, WLabel label) -> {
			label.setText(new LiteralText(s));
		};
		WListPanel<String, WLabel> list = new WListPanel<String, WLabel>(data, WLabel.class, ()->new WLabel(""), configurator);
		root.add(list, 0, 2, 7, 5);
		
		root.validate(this);
	}
}
