package io.github.cottonmc.cotton.gui.impl;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.networking.DataSlot;
import io.github.cottonmc.cotton.gui.networking.NetworkDirection;
import io.github.cottonmc.cotton.gui.networking.ScreenMessageKey;
import io.github.cottonmc.cotton.gui.widget.data.ObservableProperty;

public final class DataSlotImpl<T> implements DataSlot<T> {
	private final SyncedGuiDescription owner;
	private final ScreenMessageKey<T> key;
	private final ObservableProperty<T> value;
	private final NetworkDirection networkDirection;
	private boolean dirty = false;

	public DataSlotImpl(SyncedGuiDescription owner, ScreenMessageKey<T> key, T initialValue, NetworkDirection networkDirection) {
		this.owner = owner;
		this.key = key;
		this.value = ObservableProperty.of(initialValue).name("value").build();
		this.value.addListener((property, from, to) -> dirty = true);
		this.networkDirection = networkDirection;
	}

	@Override
	public ObservableProperty<T> valueProperty() {
		return value;
	}

	public void checkAndSendUpdate() {
		if (dirty) {
			owner.getNetworking(networkDirection.from()).send(key, value.get());
			dirty = false;
		}
	}

	@Override
	public ScreenMessageKey<T> getKey() {
		return key;
	}

	@Override
	public NetworkDirection getNetworkDirection() {
		return networkDirection;
	}
}
