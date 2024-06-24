package io.github.cottonmc.test.client;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabeledSlider;
import io.github.cottonmc.cotton.gui.widget.WPanel;
import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.WTabPanel;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;

import java.util.function.IntConsumer;

public class TextureTestGui extends LightweightGuiDescription {
	public TextureTestGui() {
		WTabPanel root = new WTabPanel();

		var panelSprite = new Texture(LibGuiCommon.id("widget/panel_light"), Texture.Type.GUI_SPRITE);
		var panelTexture = new Texture(LibGuiCommon.id("textures/gui/sprites/widget/panel_light.png"), Texture.Type.STANDALONE);
		var simpleSprite = new Texture(Identifier.ofVanilla("icon/video_link"), Texture.Type.GUI_SPRITE);

		root.add(createPanel(panelSprite), tab -> tab.icon(new TextureIcon(panelSprite)).tooltip(Text.literal("Nine-slice sprite")));
		root.add(createPanel(simpleSprite), tab -> tab.icon(new TextureIcon(simpleSprite)).tooltip(Text.literal("Simple sprite")));
		root.add(createPanel(panelTexture), tab -> tab.icon(new TextureIcon(panelTexture)).tooltip(Text.literal("Standalone")));
		setRootPanel(root);
		root.validate(this);
	}

	@Override
	public void addPainters() {
		// Remove tab panel background
	}

	private WPanel createPanel(Texture texture) {
		WSprite sprite = new WSprite(texture);

		WLabeledSlider red = new WLabeledSlider(0, 255, Axis.HORIZONTAL, Text.literal("Red"));
		WLabeledSlider green = new WLabeledSlider(0, 255, Axis.HORIZONTAL, Text.literal("Green"));
		WLabeledSlider blue = new WLabeledSlider(0, 255, Axis.HORIZONTAL, Text.literal("Blue"));
		WLabeledSlider alpha = new WLabeledSlider(0, 255, Axis.HORIZONTAL, Text.literal("Alpha"));

		red.setValue(255);
		green.setValue(255);
		blue.setValue(255);
		alpha.setValue(255);

		WSlider u1 = new WSlider(0, 100, Axis.HORIZONTAL);
		WSlider u2 = new WSlider(0, 100, Axis.HORIZONTAL);
		WSlider v1 = new WSlider(0, 100, Axis.VERTICAL);
		WSlider v2 = new WSlider(0, 100, Axis.VERTICAL);

		u2.setValue(100);
		v2.setValue(100);

		IntConsumer tintListener = unused -> {
			sprite.setTint(blue.getValue() | (green.getValue() << 8) | (red.getValue() << 16) | (alpha.getValue() << 24));
		};
		red.setValueChangeListener(tintListener);
		green.setValueChangeListener(tintListener);
		blue.setValueChangeListener(tintListener);
		alpha.setValueChangeListener(tintListener);

		IntConsumer uvListener = unused -> {
			sprite.setUv(u1.getValue() * 0.01f, v1.getValue() * 0.01f, u2.getValue() * 0.01f, v2.getValue() * 0.01f);
		};
		u1.setValueChangeListener(uvListener);
		u2.setValueChangeListener(uvListener);
		v1.setValueChangeListener(uvListener);
		v2.setValueChangeListener(uvListener);

		WGridPanel panel = new WGridPanel(20);
		panel.setInsets(Insets.ROOT_PANEL);
		panel.setGaps(3, 3);

		panel.add(red, 0, 0, 3, 1);
		panel.add(green, 3, 0, 3, 1);
		panel.add(blue, 0, 1, 3, 1);
		panel.add(alpha, 3, 1, 3, 1);

		panel.add(u1, 2, 2, 4, 1);
		panel.add(u2, 2, 3, 4, 1);
		panel.add(v1, 0, 4, 1, 4);
		panel.add(v2, 1, 4, 1, 4);

		panel.add(sprite, 2, 4, 4, 4);
		return panel;
	}
}
