package io.github.cottonmc.test.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WSlider;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.WTiledSprite;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Color;
import io.github.cottonmc.cotton.gui.widget.data.Insets;

public class TestClientGui extends LightweightGuiDescription {
	//private static final Identifier PORTAL1 = new Identifier("libgui-test:portal.png");
	//private static final Identifier PORTAL2 = new Identifier("libgui-test:portal2.png");
	
	private int r = 0;
	private int g = 0;
	private int b = 0;
	
	public TestClientGui() {
		WGridPanel root = new WGridPanel(22);
		root.setInsets(Insets.ROOT_PANEL);
		this.setRootPanel(root);
		WLabel title = new WLabel(new LiteralText("Client Test Gui"), WLabel.DEFAULT_TEXT_COLOR) {
			@Environment(EnvType.CLIENT)
			@Override
			public void addTooltip(TooltipBuilder tooltip) {
				tooltip.add(new LiteralText("Radical!"));
			}
		};
		WTiledSprite wood = new WTiledSprite(
			8, 8, // tile width and height
			500, // animation speed
			new Identifier("minecraft:textures/block/birch_planks.png"),
			new Identifier("minecraft:textures/block/dark_oak_planks.png"),
			new Identifier("minecraft:textures/block/jungle_planks.png")
		);
		root.add(wood, 3, 3, 2, 2);
		root.add(title, 0, 0);
		
		WTextField text = new WTextField();
		text.setSuggestion("Search");
		root.add(text, 0, 1, 8, 1);
		text.setSize(7*18, 20);
		/*
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
		list.setBackgroundPainter(PANEL);
		root.add(list, 0, 2, 7, 6);
		
		root.add(new WButton(new LiteralText("Teleport")), 3,8,4,1);*/
		WColorBox col = new WColorBox();
		root.add(col, 3,2,1,3);
		
		WSlider r = new WSlider(0, 100, Axis.VERTICAL);
		root.add(r, 0, 2, 1, 3);
		r.setValueChangeListener((i)->{
			this.r = i;
			updateCol(col);
			System.out.println("h: "+this.r+" s: "+this.g+ " l: "+this.b);
			System.out.println("col is now "+Integer.toHexString(col.color));
		});
		WSlider g = new WSlider(0, 100, Axis.VERTICAL);
		root.add(g, 1, 2, 1, 3);
		g.setValueChangeListener((i)->{
			this.g = i;
			updateCol(col);
		});
		WSlider b = new WSlider(0, 100, Axis.VERTICAL);
		root.add(b, 2, 2, 1, 3);
		b.setValueChangeListener((i)->{
			this.b = i;
			updateCol(col);
		});

		WButton openOther = new WButton(new LiteralText("Go to scrolling"));
		openOther.setOnClick(() -> {
			MinecraftClient.getInstance().openScreen(new CottonClientScreen(new ScrollingTestGui()));
		});
		root.add(openOther, 0, 7, 4, 1);
		
		root.validate(this);
	}
	/*
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
	}*/
	
	private void updateCol(WColorBox col) {
		Color.HSL hsl = new Color.HSL(r/100f, g/100f, b/100f);
		col.setColor(hsl.toRgb());
	}
	
	public static class WColorBox extends WWidget {
		protected int color = 0xFF_FFFFFF;
		public WColorBox() {}
		
		public void setColor(int col) {
			this.color = col;
		}
		
		@Override
		public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
			ScreenDrawing.coloredRect(matrices, x, y, this.getWidth(), this.getHeight(), color);
		}
	}
}
