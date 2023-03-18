package io.github.cottonmc.cotton.gui.widget.data;

/**
 * An immutable, two-dimensional int rectangle consisting of a position and dimensions.
 * This record can be used to represent rectangles on the screen.
 *
 * @param x      the X coordinate
 * @param y      the Y coordinate
 * @param width  the horizontal size
 * @param height the vertical size
 * @since 7.0.0
 */
public record Rect2i(int x, int y, int width, int height) {
}
