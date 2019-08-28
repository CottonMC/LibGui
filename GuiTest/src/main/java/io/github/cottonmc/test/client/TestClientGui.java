package io.github.cottonmc.test.client;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WListPanel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

public class TestClientGui extends LightweightGuiDescription {

	@Environment(EnvType.CLIENT)
	public static final BackgroundPainter PANEL = (x, y, panel)->{
		ScreenDrawing.drawBeveledPanel(x, y, panel.getWidth(), panel.getHeight());
	};
	
	private static final Identifier PORTAL1 = new Identifier("libgui-test:portal.png");
	private static final Identifier PORTAL2 = new Identifier("libgui-test:portal2.png");
	
	public TestClientGui() {
		WGridPanel root = new WGridPanel(22);
		this.setRootPanel(root);
		
		WLabel title = new WLabel(new LiteralText("Client Test Gui"), WLabel.DEFAULT_TEXT_COLOR);
		root.add(title, 0, 0);
		
		WTextField text = new WTextField();
		text.setSuggestion("Search");
		root.add(text, 0, 1, 8, 1);
		text.setSize(7*18, 20);
		
		ArrayList<String> data = new ArrayList<>();
		data.add("Wolfram Alpha");
		data.add("Strange Home");
		data.add("Nether Base");
		data.add("Death");
		data.add("Cake");
		data.add("Mushroom Island");
		data.add("A List Item");
		data.add("Notes");
		data.add("Slime Island");
		
		BiConsumer<String, PortalDestination> configurator = (String s, PortalDestination destination) -> {
			destination.label.setText(new LiteralText(s));
			
			int hash = s.hashCode();
			Identifier sprite = ((hash & 0x01) == 0) ? PORTAL1 : PORTAL2;
			destination.sprite.setImage(sprite);
			
			int cost = (hash >> 1) & 0x2FF;
			destination.cost.setText(new LiteralText(""+cost+" XP"));
		};
		WListPanel<String, PortalDestination> list = new WListPanel<String, PortalDestination>(data, PortalDestination.class, PortalDestination::new, configurator);
		list.setListItemHeight(2*18);
		root.add(list, 0, 2, 7, 6);
		
		root.add(new WButton(new LiteralText("Teleport")), 3,8,4,1);
		
		root.validate(this);
	}
	
	public static class PortalDestination extends WPlainPanel {
		WSprite sprite;
		WLabel label;
		WLabel cost;
		
		public PortalDestination() {
			sprite = new WSprite(new Identifier("libgui-test:portal"));
			this.add(sprite, 2, 2, 18, 18);
			label = new WLabel("Foo");
			this.add(label, 18+ 4, 2, 5*18, 18);
			cost = new WLabel("1000 Xp");
			this.add(cost, 2, 20, 6*18, 18);
			
			this.setSize(7*18, 2*18);
			
			this.setBackgroundPainter(PANEL); //Would fail on a serverside gui
		}
	}
}

