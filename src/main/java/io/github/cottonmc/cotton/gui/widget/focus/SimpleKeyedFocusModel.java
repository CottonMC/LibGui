package io.github.cottonmc.cotton.gui.widget.focus;

import io.github.cottonmc.cotton.gui.widget.data.Rect2i;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A {@link FocusModel} implementation that wraps a {@link List} of foci.
 * New instances can be created with the constructor or the {@linkplain Builder builder}.
 *
 * @param <K> the focus key type
 * @since 7.0.0
 */
public final class SimpleKeyedFocusModel<K> implements FocusModel<K> {
	private final List<Focus<K>> foci;
	private @Nullable K focused;

	/**
	 * Constructs a keyed focus model.
	 *
	 * @param foci the foci
	 * @throws IllegalArgumentException if there are duplicate keys
	 */
	public SimpleKeyedFocusModel(List<Focus<K>> foci) {
		this.foci = Objects.requireNonNull(foci, "foci");

		Set<K> keys = new HashSet<>();
		for (Focus<K> focus : foci) {
			if (!keys.add(focus.key())) {
				throw new IllegalArgumentException("Duplicate focus key: " + focus.key());
			}
		}
	}

	/**
	 * {@return the focused key, or {@code null} if not available}
	 */
	public @Nullable K getFocusedKey() {
		return focused;
	}

	@Override
	public boolean isFocused(Focus<K> focus) {
		return Objects.equals(focus.key(), focused);
	}

	@Override
	public void setFocused(Focus<K> focus) {
		focused = focus.key();
	}

	@Override
	public Stream<Focus<K>> foci() {
		return foci.stream();
	}

	/**
	 * Creates a builder for {@code SimpleKeyedFocusModel}.
	 *
	 * @param <K> the focus key type
	 * @return the builder
	 */
	@Contract("-> new")
	public static <K> Builder<K> builder() {
		return new Builder<>();
	}

	/**
	 * A builder for {@code SimpleKeyedFocusModel}.
	 * New instances can be obtained using {@link #builder()}.
	 *
	 * <p>This builder class is reusable &ndash; created instances are not
	 * affected by data added to this builder afterwards.
	 *
	 * @param <K> the focus key type
	 */
	public static final class Builder<K> {
		private final List<Focus<K>> foci = new ArrayList<>();

		private Builder() {
		}

		/**
		 * Adds a focus to this builder.
		 *
		 * @param focus the focus to add
		 * @return this builder
		 */
		@Contract("null -> fail; _ -> this")
		public Builder<K> add(Focus<K> focus) {
			Objects.requireNonNull(focus, "Focus cannot be null");
			foci.add(focus);
			return this;
		}

		/**
		 * Adds a focus to this builder.
		 *
		 * @param key  the focus key
		 * @param area the focus area
		 * @return this builder
		 */
		@Contract("_, null -> fail; _, _ -> this")
		public Builder<K> add(K key, Rect2i area) {
			Objects.requireNonNull(area, "Focus area cannot be null");
			return add(new Focus<>(key, area));
		}

		/**
		 * Adds foci from a collection to this builder.
		 *
		 * @param foci the foci to add
		 * @return this builder
		 */
		@Contract("null -> fail; _ -> this")
		public Builder<K> addAll(Collection<Focus<K>> foci) {
			Objects.requireNonNull(foci, "Foci cannot be null");
			this.foci.addAll(foci);
			return this;
		}

		/**
		 * Builds a new {@code SimpleKeyedFocusModel} from the data in this builder.
		 *
		 * @return the created model
		 */
		@Contract("-> new")
		public SimpleKeyedFocusModel<K> build() {
			return new SimpleKeyedFocusModel<>(List.copyOf(foci));
		}
	}
}
