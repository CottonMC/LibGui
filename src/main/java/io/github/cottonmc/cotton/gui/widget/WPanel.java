package io.github.cottonmc.cotton.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.data.Insets;

import java.util.AbstractList;
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
	protected final List<WWidget> children = new WidgetList(this, new ArrayList<>());
	@Environment(EnvType.CLIENT)
	private BackgroundPainter backgroundPainter;

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
			if (child instanceof WPanel panel) panel.layout();
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
		int pushRight = w.getX() + w.getWidth() + insets.right();
		int pushDown =  w.getY() + w.getHeight() + insets.bottom();
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
			int wx = x - child.getX();
			int wy = y - child.getY();
			if (child.isWithinBounds(wx, wy)) {
				return child.hit(wx, wy);
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
	}

	@Override
	public void setHost(GuiDescription host) {
		super.setHost(host);
		for (WWidget child : children) {
			child.setHost(host);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
		if (backgroundPainter!=null) backgroundPainter.paintBackground(context, x, y, this);

		for(WWidget child : children) {
			child.paint(context, x + child.getX(), y + child.getY(), mouseX-child.getX(), mouseY-child.getY());
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

	/**
	 * {@return a stream of all visible top-level widgets in this panel}
	 *
	 * @since 4.2.0
	 */
	public final Stream<WWidget> streamChildren() {
		return children.stream();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " {\n" + children.stream().map(Object::toString).map(x -> x + ",").flatMap(x -> Stream.of(x.split("\n")).filter(y -> !y.isEmpty()).map(y -> "\t" + y)).collect(Collectors.joining("\n")) + "\n}";
	}

	private static final class WidgetList extends AbstractList<WWidget> {
		private final WPanel owner;
		private final List<WWidget> backing;

		private WidgetList(WPanel owner, List<WWidget> backing) {
			this.owner = owner;
			this.backing = backing;
		}

		@Override
		public WWidget get(int index) {
			return backing.get(index);
		}

		private void checkWidget(WWidget widget) {
			if (widget == null) {
				throw new NullPointerException("Adding null widget to " + owner);
			}

			int n = 0;
			WWidget parent = owner;
			while (parent != null) {
				if (widget == parent) {
					if (n == 0) {
						throw new IllegalArgumentException("Adding panel to itself: " + widget);
					} else {
						throw new IllegalArgumentException("Adding level " + n + " parent recursively to " + owner + ": " + widget);
					}
				}

				parent = parent.getParent();
				n++;
			}
		}

		@Override
		public WWidget set(int index, WWidget element) {
			checkWidget(element);
			return backing.set(index, element);
		}

		@Override
		public void add(int index, WWidget element) {
			checkWidget(element);
			backing.add(index, element);
		}

		@Override
		public WWidget remove(int index) {
			return backing.remove(index);
		}

		@Override
		public int size() {
			return backing.size();
		}
	}
}
