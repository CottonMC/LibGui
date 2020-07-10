package io.github.cottonmc.cotton.gui.widget;

import io.github.cottonmc.cotton.gui.GuiDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Similar to the CardLayout in AWT, this panel displays one widget at a time from a list of widgets.
 *
 * @since 2.2.0
 */
public class WCardPanel extends WPanel {
	private final List<WWidget> cards = new ArrayList<>();
	private int selectedIndex = 0;

	/**
	 * Adds a card to this panel without resizing it.
	 *
	 * @param card the added card
	 */
	public void add(WWidget card) {
		add(cards.size(), card);
	}

	/**
	 * Adds a card to this panel without resizing it.
	 *
	 * @param index the index of the card
	 * @param card  the added card
	 */
	public void add(int index, WWidget card) {
		cards.add(index, card);

		card.setParent(this);
		card.setLocation(0, 0);
		expandToFit(card);
	}

	/**
	 * Adds a card to this panel and resizes it.
	 *
	 * @param card   the added card
	 * @param width  the new width
	 * @param height the new height
	 */
	public void add(WWidget card, int width, int height) {
		add(cards.size(), card, width, height);
	}

	/**
	 * Adds a card to this panel and resizes it.
	 *
	 * @param index  the index of the card
	 * @param card   the added card
	 * @param width  the new width
	 * @param height the new height
	 */
	public void add(int index, WWidget card, int width, int height) {
		if (card.canResize()) {
			card.setSize(width, height);
		}

		add(index, card);
	}

	/**
	 * Gets the index of the selected card in this panel.
	 *
	 * @return the selected card's index
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}

	/**
	 * Sets the selected index of this panel.
	 *
	 * @param selectedIndex the new selected index
	 * @return this panel
	 * @throws IndexOutOfBoundsException if this panel does not contain the card index
	 */
	public WCardPanel setSelectedIndex(int selectedIndex) {
		if (selectedIndex < 0 || selectedIndex >= cards.size()) {
			throw new IndexOutOfBoundsException("Card index " + selectedIndex + " out of bounds: 0 <= index <" + cards.size());
		}

		this.selectedIndex = selectedIndex;
		return this;
	}

	/**
	 * Gets the selected card of this panel.
	 *
	 * @return the selected card
	 */
	public WWidget getSelectedCard() {
		return cards.get(getSelectedIndex());
	}

	/**
	 * Sets the selected card of this panel.
	 *
	 * @param selectedCard the new selected card
	 * @return this panel
	 * @throws NoSuchElementException if this panel does not contain the card
	 */
	public WCardPanel setSelectedCard(WWidget selectedCard) {
		if (!cards.contains(selectedCard)) {
			throw new NoSuchElementException("Widget " + selectedCard + " is not a card in this panel!");
		}

		return setSelectedIndex(cards.indexOf(selectedCard));
	}

	@Override
	public void layout() {
		children.clear();
		children.addAll(cards);

		// Layout children
		super.layout();

		for (WWidget child : cards) {
			child.setSize(getWidth(), getHeight());
		}

		children.clear();
		children.add(cards.get(getSelectedIndex()));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param c the host GUI description
	 * @throws IllegalStateException if this panel has no cards
	 */
	@Override
	public void validate(GuiDescription c) {
		if (cards.isEmpty()) {
			throw new IllegalStateException("No children in card panel");
		}

		super.validate(c);
	}
}
