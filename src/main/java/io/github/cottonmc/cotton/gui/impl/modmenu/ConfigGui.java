package io.github.cottonmc.cotton.gui.impl.modmenu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.impl.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.cottonmc.cotton.gui.widget.data.Insets;

public class ConfigGui extends LightweightGuiDescription {

	public ConfigGui(Screen previous) {
		WGridPanel root = new WGridPanel();
		root.setInsets(Insets.ROOT_PANEL);
		setRootPanel(root);

		WToggleButton darkmodeButton = new WToggleButton(new TranslatableText("option.libgui.darkmode")) {
			@Override
			public void onToggle(boolean on) {
				LibGuiClient.config.darkMode = on;
				LibGuiClient.saveConfig(LibGuiClient.config);
			}
		};
		darkmodeButton.setToggle(LibGuiClient.config.darkMode);
		root.add(darkmodeButton, 0, 2, 6, 1);
		
		WTextField testField = new WTextField();
		testField.setSuggestion("test");
		root.add(testField, 0, 3, 4, 1);

		/*
		WSlider verticalSlider = new WSlider(-100, 100, Axis.VERTICAL);
		verticalSlider.setDraggingFinishedListener(() -> System.out.println("Mouse released"));
		verticalSlider.setValueChangeListener(System.out::println);

		WLabeledSlider horizontalSlider = new WLabeledSlider(0, 500);
		horizontalSlider.setLabelUpdater(value -> new LiteralText(value + "!"));
		horizontalSlider.setDraggingFinishedListener(() -> System.out.println("Mouse released"));
		horizontalSlider.setValue(250);

		root.add(verticalSlider, 6, 0, 1, 3);
		root.add(horizontalSlider, 1, 4, 4, 1);
		*/

		root.add(new WKirbSprite(), 5, 4);
		
		WButton doneButton = new WButton(new TranslatableText("gui.done"));
		doneButton.setOnClick(()->{
			MinecraftClient.getInstance().openScreen(previous);
		});
		root.add(doneButton, 0, 5, 3, 1);
		
		root.setBackgroundPainter(BackgroundPainter.VANILLA);
		root.setSize(7*18, 6*18);
		
		root.validate(this);
	}
}
