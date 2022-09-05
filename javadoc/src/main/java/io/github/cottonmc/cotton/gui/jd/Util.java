package io.github.cottonmc.cotton.gui.jd;

import java.util.function.Function;

public final class Util {
	public static <A, B> Function<A, Pair<A, B>> zip(Function<A, B> transform) {
		return a -> new Pair<>(a, transform.apply(a));
	}

	public record Pair<A, B>(A first, B second) {}
}
