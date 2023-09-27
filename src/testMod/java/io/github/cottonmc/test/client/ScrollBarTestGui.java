package io.github.cottonmc.test.client;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.VerticalAlignment;

public class ScrollBarTestGui extends LightweightGuiDescription {
	private boolean darkMode = false;

	public ScrollBarTestGui() {
		WPlainPanel root = new WPlainPanel();
		setRootPanel(root);
		root.setSize(256, 240);
		root.setInsets(Insets.ROOT_PANEL);

		WScrollBar scrollBarTop = new WScrollBar(Axis.HORIZONTAL);
		root.add(scrollBarTop, 0, 0, 256, 16);

		WScrollBar scrollBarDown = new WScrollBar(Axis.HORIZONTAL);
		root.add(scrollBarDown, 0, 240 - scrollBarDown.getHeight(), 256, 8);

		WScrollBar scrollBarLeft = new WScrollBar(Axis.VERTICAL);
		root.add(scrollBarLeft, 0, 18, 16, 202);

		WScrollBar scrollBarRight = new WScrollBar(Axis.VERTICAL);
		root.add(scrollBarRight, 248, 18, 8, 202);

		WLabel label = new WLabel(Text.of("Scrolling Speed: 4"));
		label.setHorizontalAlignment(HorizontalAlignment.CENTER);
		label.setVerticalAlignment(VerticalAlignment.CENTER);
		root.add(label, 32, 112, 192, 16);

		WSlider slider = new WSlider(1, 100, Axis.HORIZONTAL);
		slider.setDraggingFinishedListener(i -> {
			label.setText(Text.of("Scrolling Speed: " + i));

			scrollBarTop.setScrollingSpeed(i);
			scrollBarDown.setScrollingSpeed(i);
			scrollBarLeft.setScrollingSpeed(i);
			scrollBarRight.setScrollingSpeed(i);
		});
		root.add(slider, 78, 128, 100, 16);

		WToggleButton toggleButton = new WToggleButton();
		toggleButton.setOnToggle(on -> darkMode = on);
		root.add(toggleButton, 128 - (toggleButton.getWidth() / 2), 104 - (toggleButton.getHeight() / 2));

		root.validate(this);
	}

	@Override
	public TriState isDarkMode() {
		return TriState.of(darkMode);
	}
}
