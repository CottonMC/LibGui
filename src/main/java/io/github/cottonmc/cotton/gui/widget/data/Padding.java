package io.github.cottonmc.cotton.gui.widget.data;

import java.util.Objects;

public final class Padding {
	public final int top;
	public final int left;
	public final int bottom;
	public final int right;

	public Padding(int all) {
		this(all, all, all, all);
	}

	public Padding(int horizontal, int vertical) {
		this(vertical, horizontal, vertical, horizontal);
	}

	public Padding(int top, int left, int bottom, int right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Padding padding = (Padding) o;
		return top == padding.top &&
				left == padding.left &&
				bottom == padding.bottom &&
				right == padding.right;
	}

	@Override
	public int hashCode() {
		return Objects.hash(top, left, bottom, right);
	}
}
