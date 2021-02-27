package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Panels are widgets that contain other widgets.
 */
public abstract class WPanel extends WWidget {
	/**
	 * The widgets contained within this panel.
	 *
	 * <p>The list is mutable.
	 */
	protected final List<WWidget> children = new ArrayList<>();
	@Environment(EnvType.CLIENT)
	private BackgroundPainter backgroundPainter = null;

	@SuppressWarnings("deprecation")
	@Override
	public void createPeers(GuiDescription c) {
		super.createPeers(c);
		for(WWidget child : children) {
			child.createPeers(c);
		}
	}

	/**
	 * Removes the widget from this panel.
	 *
	 * @param w the removed widget
	 */
	public void remove(WWidget w) {
		children.remove(w);
	}

	@Override
	public boolean canResize() {
		return true;
	}

	/**
	 * Sets the {@link BackgroundPainter} of this panel.
	 *
	 * @param painter the new painter
	 * @return this panel
	 */
	@Environment(EnvType.CLIENT)
	public WPanel setBackgroundPainter(BackgroundPainter painter) {
		this.backgroundPainter = painter;
		return this;
	}

	/**
	 * Gets the current {@link BackgroundPainter} of this panel.
	 *
	 * @return the painter
	 */
	@Environment(EnvType.CLIENT)
	public BackgroundPainter getBackgroundPainter() {
		return this.backgroundPainter;
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

	/**
	 * Expands this panel be at least as large as the widget.
	 *
	 * @param w the widget
	 */
	protected void expandToFit(WWidget w) {
		expandToFit(w, Insets.NONE);
	}

	/**
	 * Expands this panel be at least as large as the widget.
	 *
	 * @param w      the widget
	 * @param insets the layout insets
	 * @since 4.0.0
	 */
	protected void expandToFit(WWidget w, Insets insets) {
		int pushRight = w.getX()+w.getWidth()+insets.right;
		int pushDown =  w.getY()+w.getHeight()+insets.bottom;
		this.setSize(Math.max(this.getWidth(), pushRight), Math.max(this.getHeight(), pushDown));
	}

	/**
	 * Finds the most specific child node at this location.
	 */
	@Override
	public WWidget hit(int x, int y) {
		if (children.isEmpty()) return this;
		for(int i=children.size()-1; i>=0; i--) { //Backwards so topmost widgets get priority
			WWidget child = children.get(i);
			if (    x>=child.getX() &&
					y>=child.getY() &&
					x<child.getX()+child.getWidth() &&
					y<child.getY()+child.getHeight()) {
				return child.hit(x-child.getX(), y-child.getY());
			}
		}
		return this;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Subclasses should call {@code super.validate(c)} to ensure that children are validated.
	 *
	 * @param c the host GUI description
	 */
	@Override
	public void validate(GuiDescription c) {
		super.validate(c);
		layout();
		for (WWidget child : children) {
			child.validate(c);
		}
		if (c!=null) createPeers(c);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		if (backgroundPainter!=null) backgroundPainter.paintBackground(matrices, x, y, this);

		for(WWidget child : children) {
			child.paint(matrices, x + child.getX(), y + child.getY(), mouseX-child.getX(), mouseY-child.getY());
		}
	}

	/**
	 * Ticks all children of this panel.
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void tick() {
		for(WWidget child : children) child.tick();
	}

	@Nullable
	@Override
	public WWidget cycleFocus(boolean lookForwards) {
		return cycleFocus(lookForwards, null);
	}

	/**
	 * Cycles the focus inside this panel.
	 *
	 * @param lookForwards whether this should cycle forwards (true) or backwards (false)
	 * @param pivot        the widget that should be cycled around (can be null for beginning / end)
	 * @return the next focused widget, or null if should exit to the parent panel
	 * @since 2.0.0
	 */
	@Nullable
	public WWidget cycleFocus(boolean lookForwards, @Nullable WWidget pivot) {
		if (pivot == null) {
			if (lookForwards) {
				for (WWidget child : children) {
					WWidget result = checkFocusCycling(lookForwards, child);
					if (result != null) return result;
				}
			} else if (!children.isEmpty()) {
				for (int i = children.size() - 1; i >= 0; i--) {
					WWidget child = children.get(i);
					WWidget result = checkFocusCycling(lookForwards, child);
					if (result != null) return result;
				}
			}
		} else {
			int currentIndex = children.indexOf(pivot);

			if (currentIndex == -1) { // outside widget
				currentIndex = lookForwards ? 0 : children.size() - 1;
			}

			if (lookForwards) {
				if (currentIndex < children.size() - 1) {
					for (int i = currentIndex + 1; i < children.size(); i++) {
						WWidget child = children.get(i);
						WWidget result = checkFocusCycling(lookForwards, child);
						if (result != null) return result;
					}
				}
			} else { // look forwards = false
				if (currentIndex > 0) {
					for (int i = currentIndex - 1; i >= 0; i--) {
						WWidget child = children.get(i);
						WWidget result = checkFocusCycling(lookForwards, child);
						if (result != null) return result;
					}
				}
			}
		}

		return null;
	}

	@Nullable
	private WWidget checkFocusCycling(boolean lookForwards, WWidget child) {
		if (child.canFocus() || child instanceof WPanel) {
			return child.cycleFocus(lookForwards);
		}

		return null;
	}

	@Override
	public void onShown() {
		for (WWidget child : children) {
			child.onShown();
		}
	}

	@Override
	public void onHidden() {
		super.onHidden();

		for (WWidget child : children) {
			child.onHidden();
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Subclasses should call {@code super.addPainters()} to ensure that children have proper default painters.
	 *
	 * @since 3.0.0
	 */
	@Environment(EnvType.CLIENT)
	@Override
	public void addPainters() {
		for (WWidget child : children) {
			child.addPainters();
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " {\n" + children.stream().map(Object::toString).map(x -> x + ",").flatMap(x -> Stream.of(x.split("\n")).filter(y -> !y.isEmpty()).map(y -> "\t" + y)).collect(Collectors.joining("\n")) + "\n}";
	}
}
