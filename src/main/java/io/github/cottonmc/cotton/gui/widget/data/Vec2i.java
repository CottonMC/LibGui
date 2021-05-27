package io.github.cottonmc.cotton.gui.widget.data;

/**
 * An immutable, two-dimensional int vector.
 * This record can be used to represent positions on the screen.
 *
 * @param x the horizontal component
 * @param y the vertical component
 * @since 4.0.0
 */
public record Vec2i(int x, int y) {
}
