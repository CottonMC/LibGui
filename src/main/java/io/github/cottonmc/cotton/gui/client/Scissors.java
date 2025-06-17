package io.github.cottonmc.cotton.gui.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.stream.Collectors;

/**
 * Contains a stack for GL scissors for restricting the drawn area of a widget.
 *
 * @since 2.0.0
 * @deprecated Use scissor methods in {@link DrawContext} instead.
 */
@Deprecated(forRemoval = true)
@Environment(EnvType.CLIENT)
public final class Scissors {
	private static final ArrayDeque<Frame> STACK = new ArrayDeque<>();

	private Scissors() {
	}

	/**
	 * Pushes a new scissor frame onto the stack and refreshes the scissored area.
	 *
	 * @param context the associated draw context
	 * @param x the frame's X coordinate
	 * @param y the frame's Y coordinate
	 * @param width the frame's width in pixels
	 * @param height the frame's height in pixels
	 * @return the pushed frame
	 */
	public static Frame push(DrawContext context, int x, int y, int width, int height) {
		Frame frame = new Frame(x, y, width, height, context);
		STACK.push(frame);
		context.scissorStack.push(new ScreenRect(x, y, width, height));

		return frame;
	}

	/**
	 * Pops the topmost scissor frame and refreshes the scissored area.
	 *
	 * @throws IllegalStateException if there are no scissor frames on the stack
	 */
	public static void pop() {
		if (STACK.isEmpty()) {
			throw new IllegalStateException("No scissors on the stack!");
		}

		STACK.pop().context.disableScissor();
	}

	/**
	 * Internal method. Throws an {@link IllegalStateException} if the scissor stack is not empty.
	 */
	static void checkStackIsEmpty() {
		if (!STACK.isEmpty()) {
			throw new IllegalStateException("Unpopped scissor frames: " + STACK.stream().map(Frame::toString).collect(Collectors.joining(", ")));
		}
	}

	/**
	 * A single scissor frame in the stack.
	 */
	public static final class Frame implements AutoCloseable {
		private final int x;
		private final int y;
		private final int width;
		private final int height;
		private final @Nullable DrawContext context;

		private Frame(int x, int y, int width, int height, @Nullable DrawContext context) {
			if (width < 0) throw new IllegalArgumentException("Negative width for a stack frame");
			if (height < 0) throw new IllegalArgumentException("Negative height for a stack frame");

			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.context = context;
		}

		/**
		 * Pops this frame from the stack.
		 *
		 * @throws IllegalStateException if: <ul>
		 *                               <li>this frame is not on the stack, or</li>
		 *                               <li>this frame is not the topmost element on the stack</li>
		 *                               </ul>
		 * @see Scissors#pop()
		 */
		@Override
		public void close() {
			if (STACK.peekLast() != this) {
				if (STACK.contains(this)) {
					throw new IllegalStateException(this + " is not on top of the stack!");
				} else {
					throw new IllegalStateException(this + " is not on the stack!");
				}
			}

			pop();
		}

		@Override
		public String toString() {
			return "Frame{ at = (" + x + ", " + y + "), size = (" + width + ", " + height + ") }";
		}
	}
}
