package io.github.cottonmc.cotton.gui.widget;

import java.util.List;

import com.google.common.collect.Lists;

import io.github.cottonmc.cotton.gui.CottonScreenController;
import io.github.cottonmc.cotton.gui.ValidatedSlot;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;

public class WItemSlot extends WWidget {
	private final List<Slot> peers = Lists.newArrayList();
	private BackgroundPainter backgroundPainter;
	private Inventory inventory;
	//private CottonScreenController container;
	private int startIndex = 0;
	private int slotsWide = 1;
	private int slotsHigh = 1;
	private boolean big = false;
	//private boolean ltr = true;
	private float opacity = 0.2f;
	
	public WItemSlot(Inventory inventory, int startIndex, int slotsWide, int slotsHigh, boolean big, boolean ltr) {
		this.inventory = inventory;
		this.startIndex = startIndex;
		this.slotsWide = slotsWide;
		this.slotsHigh = slotsHigh;
		this.big = big;
		//this.ltr = ltr;
	}
	
	private WItemSlot() {}
	
	public static WItemSlot of(Inventory inventory, int index) {
		WItemSlot w = new WItemSlot();
		w.inventory = inventory;
		w.startIndex = index;
		
		return w;
	}
	
	public static WItemSlot of(Inventory inventory, int startIndex, int slotsWide, int slotsHigh) {
		WItemSlot w = new WItemSlot();
		w.inventory = inventory;
		w.startIndex = startIndex;
		w.slotsWide = slotsWide;
		w.slotsHigh = slotsHigh;
		
		return w;
	}
	
	public static WItemSlot outputOf(Inventory inventory, int index) {
		WItemSlot w = new WItemSlot();
		w.inventory = inventory;
		w.startIndex = index;
		w.big = true;
		
		return w;
	}
	
	public static WItemSlot ofPlayerStorage(Inventory inventory) {
		WItemSlot w = new WItemSlot();
		w.inventory = inventory;
		w.startIndex = 9;
		w.slotsWide = 9;
		w.slotsHigh = 3;
		//w.ltr = false;
		
		return w;
	}
	
	@Override
	public int getWidth() {
		return slotsWide * 18;
	}
	
	@Override
	public int getHeight() {
		return slotsHigh * 18;
	}
	
	@Override
	public void createPeers(CottonScreenController c) {
		//this.container = c;
		peers.clear();
		int index = startIndex;
		
		/*if (ltr) {
			for (int x = 0; x < slotsWide; x++) {
				for (int y = 0; y < slotsHigh; y++) {
					ValidatedSlot slot = new ValidatedSlot(inventory, index, this.getAbsoluteX() + (x * 18), this.getAbsoluteY() + (y * 18));
					peers.add(slot);
					c.addSlotPeer(slot);
					index++;
				}
			}
		} else {*/
			for (int y = 0; y < slotsHigh; y++) {
				for (int x = 0; x < slotsWide; x++) {
					ValidatedSlot slot = new ValidatedSlot(inventory, index, this.getAbsoluteX() + (x * 18), this.getAbsoluteY() + (y * 18));
					peers.add(slot);
					c.addSlotPeer(slot);
					index++;
				}
			}
		//}
	}
	
	@Environment(EnvType.CLIENT)
	public void setBackgroundPainter(BackgroundPainter painter) {
		this.backgroundPainter = painter;
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		if (backgroundPainter!=null) {
			backgroundPainter.paintBackground(x, y, this);
		} else {
			for (int xi = 0; xi < slotsWide; xi++) {
				for (int yi = 0; yi < slotsHigh; yi++) {
					//int lo = ScreenDrawing.colorAtOpacity(0x000000, 0.72f);
					//int bg = ScreenDrawing.colorAtOpacity(0x000000, 0.29f);
					//int hi = ScreenDrawing.colorAtOpacity(0xFFFFFF, 1.0f);
					//if (container!=null) {
						int lo = ScreenDrawing.colorAtOpacity(0x000000, opacity);
						int bg = ScreenDrawing.colorAtOpacity(0x000000, opacity/2.4f);
						int hi = ScreenDrawing.colorAtOpacity(0xFFFFFF, opacity);
					//}
					
					if (big) {
						ScreenDrawing.drawBeveledPanel((xi * 18) + x - 4, (yi * 18) + y - 4, 24, 24,
								lo, bg, hi);
					} else {
						ScreenDrawing.drawBeveledPanel((xi * 18) + x - 1, (yi * 18) + y - 1, 18, 18,
								lo, bg, hi);
					}
				}
			}
		}
	}
}