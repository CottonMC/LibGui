package io.github.cottonmc.cotton.gui.impl;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.networking.DataSlot;
import io.github.cottonmc.cotton.gui.networking.NetworkDirection;
import io.github.cottonmc.cotton.gui.networking.ScreenMessageKey;

import java.util.Objects;

public final class DataSlotImpl<T> implements DataSlot<T> {
	private final SyncedGuiDescription owner;
	private final ScreenMessageKey<T> key;
	private T value;
	private final NetworkDirection networkDirection;
	private final Event<ChangeListener<T>> valueChangedEvent;
	private boolean dirty = false;

	public DataSlotImpl(SyncedGuiDescription owner, ScreenMessageKey<T> key, T initialValue, NetworkDirection networkDirection) {
		this.owner = owner;
		this.key = key;
		this.value = initialValue;
		this.networkDirection = networkDirection;
		this.valueChangedEvent = EventFactory.createArrayBacked(ChangeListener.class, listeners -> (dataSlot, value) -> {
			for (ChangeListener<T> listener : listeners) {
				listener.onValueChanged(dataSlot, value);
			}
		});
	}

	public T get() {
		return value;
	}

	public void set(T value) {
		if (!Objects.equals(this.value, value)) {
			this.value = value;
			dirty = true;
			valueChangedEvent.invoker().onValueChanged(this, value);
		}
	}

	public void checkAndSendUpdate() {
		if (dirty) {
			owner.getNetworking(networkDirection.from()).send(key, value);
			dirty = false;
		}
	}

	@Override
	public Event<ChangeListener<T>> getValueChangedEvent() {
		return valueChangedEvent;
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
