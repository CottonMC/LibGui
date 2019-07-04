package io.github.cottonmc.cotton.gui.widget;

import java.util.List;

import com.google.common.collect.Lists;

import io.github.cottonmc.cotton.gui.CottonScreenController;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class WPanel extends WWidget {
	protected final List<WWidget> children = Lists.newArrayList();
	@Environment(EnvType.CLIENT)
	private BackgroundPainter backgroundPainter = null;
	
	@Override
	public void createPeers(CottonScreenController c) {
		//System.out.println("Creating peers - before: "+c.slotList.size());
		for(WWidget child : children) {
			child.createPeers(c);
		}
		//System.out.println("Peers created - after: "+c.slotList.size());
	}
	
	public void remove(WWidget w) {
		children.remove(w);
	}
	
	@Override
	public boolean canResize() {
		return true;
	}
	
	@Environment(EnvType.CLIENT)
	public WPanel setBackgroundPainter(BackgroundPainter painter) {
		this.backgroundPainter = painter;
		return this;
	}
	
	/**
	 * Uses this Panel's layout rules to reposition and resize components to fit nicely in the panel.
	 */
	public void layout() {
		for(WWidget child : children) {
			if (child instanceof WPanel) ((WPanel) child).layout();
			expandToFit(child);
		}
	}
	
	protected void expandToFit(WWidget w) {
		int pushRight = w.getX()+w.getWidth();
		int pushDown =  w.getY()+w.getHeight();
		this.setSize(Math.max(this.getWidth(), pushRight), Math.max(this.getHeight(), pushDown));
	}
	
	@Override
	public WWidget onMouseUp(int x, int y, int button) {
		if (children.isEmpty()) return super.onMouseUp(x, y, button);
		for(int i=children.size()-1; i>=0; i--) { //Backwards so topmost widgets get priority
			WWidget child = children.get(i);
			if (    x>=child.getX() &&
					y>=child.getY() &&
					x<child.getX()+child.getWidth() &&
					y<child.getY()+child.getHeight()) {
				return child.onMouseUp(x-child.getX(), y-child.getY(), button);
			}
		}
		return super.onMouseUp(x, y, button);
	}
	
	@Override
	public WWidget onMouseDown(int x, int y, int button) {
		if (children.isEmpty()) return super.onMouseDown(x, y, button);
		for(int i=children.size()-1; i>=0; i--) { //Backwards so topmost widgets get priority
			WWidget child = children.get(i);
			if (    x>=child.getX() &&
					y>=child.getY() &&
					x<child.getX()+child.getWidth() &&
					y<child.getY()+child.getHeight()) {
				return child.onMouseDown(x-child.getX(), y-child.getY(), button);
			}
		}
		return super.onMouseDown(x, y, button);
	}
	
	@Override
	public void onMouseDrag(int x, int y, int button) {
		if (children.isEmpty()) return;
		for(int i=children.size()-1; i>=0; i--) { //Backwards so topmost widgets get priority
			WWidget child = children.get(i);
			if (    x>=child.getX() &&
					y>=child.getY() &&
					x<child.getX()+child.getWidth() &&
					y<child.getY()+child.getHeight()) {
				child.onMouseDrag(x-child.getX(), y-child.getY(), button);
				return; //Only send the message to the first valid recipient
			}
		}
		super.onMouseDrag(x, y, button);
	}
	
	@Override
	public void onClick(int x, int y, int button) {
		if (children.isEmpty()) return;
		for(int i=children.size()-1; i>=0; i--) { //Backwards so topmost widgets get priority
			WWidget child = children.get(i);
			if (    x>=child.getX() &&
					y>=child.getY() &&
					x<child.getX()+child.getWidth() &&
					y<child.getY()+child.getHeight()) {
				child.onClick(x-child.getX(), y-child.getY(), button);
				return; //Only send the message to the first valid recipient
			}
		}
	}
	
	@Override
	public void validate(CottonScreenController c) {
		layout();
		createPeers(c);
	}
	
	@Environment(EnvType.CLIENT)
	@Override
	public void paintBackground(int x, int y) {
		if (backgroundPainter!=null) backgroundPainter.paintBackground(x, y, this);
		
		for(WWidget child : children) {
			child.paintBackground(x + child.getX(), y + child.getY());
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paintForeground(int x, int y, int mouseX, int mouseY) {
		for(WWidget child : children) {
			child.paintForeground(x + child.getX(), y + child.getY(), mouseX, mouseY);
		}
	}
}
