package io.github.cottonmc.test;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.networking.DataSlot;
import io.github.cottonmc.cotton.gui.networking.NetworkDirection;
import io.github.cottonmc.cotton.gui.networking.NetworkSide;
import io.github.cottonmc.cotton.gui.networking.ScreenMessageKey;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import io.github.cottonmc.cotton.gui.widget.icon.TextureIcon;

public class TestDescription extends SyncedGuiDescription {
	private static final Identifier TEST_MESSAGE = LibGuiCommon.id("test");
	private static final Identifier TEST_REGISTRY_MESSAGE = LibGuiCommon.id("test_with_registry");
	private static final Identifier UNREGISTERED_ON_SERVER = LibGuiCommon.id("unregistered_on_server");
	private static final ScreenMessageKey<BlockPos> ON_SERVER_READY_MESSAGE = new ScreenMessageKey<>(
		LibGuiCommon.id("on_server_ready"),
		BlockPos.CODEC
	);
	private static final ScreenMessageKey<Component> BUTTON_LABEL_DATA_SLOT = new ScreenMessageKey<>(
		LibGuiCommon.id("button_label"),
		ComponentSerialization.CODEC
	);
	private static final ScreenMessageKey<Integer> BUTTON_COLOR_DATA_SLOT = new ScreenMessageKey<>(
		LibGuiCommon.id("button_color"),
		Codec.INT
	);

	private static final int[] BUTTON_TEXT_COLORS = {
		0xFF_FFFFFF,
		0xFF_FF0000,
		0xFF_00FF00,
		0xFF_0000FF,
		0xFF_222222,
	};

	private int messagesSent;
	private DataSlot<Component> buttonLabel;
	private DataSlot<Integer> buttonColor;

	public TestDescription(MenuType<?> type, int syncId, Inventory playerInventory, ContainerLevelAccess context) {
		super(type, syncId, playerInventory, getBlockInventory(context, GuiBlockEntity.INVENTORY_SIZE), null);

		WGridPanel root = (WGridPanel)this.getRootPanel();

		WItemSlot slot = WItemSlot.of(blockInventory, 0, 4, 1);
		root.add(slot, 0, 1);

		WButton buttonA = new WButton();

		buttonA.setOnClick(() -> {
			getNetworking(NetworkSide.CLIENT).send(TEST_MESSAGE, Codec.INT, ++messagesSent);
			getNetworking(NetworkSide.CLIENT).send(UNREGISTERED_ON_SERVER, MapCodec.unitCodec(Unit.INSTANCE), Unit.INSTANCE);
			buttonColor.set(BUTTON_TEXT_COLORS[messagesSent % BUTTON_TEXT_COLORS.length]);
		});

		root.add(buttonA, 0, 3, 4, 1);

		WButton buttonB = new WButton(Component.literal("Show Warnings"));
		buttonB.setOnClick(() -> slot.setIcon(new TextureIcon(LibGuiTest.id("saddle.png"))));

		root.add(buttonB, 5, 3, 4, 1);
		TextureIcon testIcon = new TextureIcon(new Texture(LibGuiTest.id("icon.png")));
		root.add(new WButton(testIcon, Component.literal("Button C")), 0, 5, 4, 1);
		root.add(new WButton(Component.literal("Button D")), 5, 5, 4, 1);
		root.add(new WTextField(Component.literal("Type something...")).setMaxLength(64), 0, 7, 5, 1);

		root.add(new WLabel(Component.literal("Large Glass-only output:")), 0, 9);
		WItemSlot glassOutputSlot = WItemSlot.outputOf(blockInventory, 0).setOutputFilter(stack -> stack.is(Items.GLASS));
		glassOutputSlot.setIcon(new TextureIcon(Identifier.withDefaultNamespace("textures/block/glass.png")));
		root.add(glassOutputSlot, 4, 9);
		WToggleButton glassIconToggle = new WToggleButton(Component.literal("Show glass icon only when empty?"));
		glassIconToggle.setOnToggle(glassOutputSlot::setIconOnlyPaintedForEmptySlots);
		root.add(glassIconToggle, 0, 10);

		root.add(WItemSlot.of(blockInventory, 7).setIcon(new TextureIcon(LibGuiTest.id("saddle.png"))).setInputFilter(stack -> stack.is(Items.SADDLE)), 7, 10);

		root.add(createPlayerInventoryPanel(), 0, 11);
		System.out.println(root.toString());

		this.getRootPanel().validate(this);

		buttonLabel = registerDataSlot(BUTTON_LABEL_DATA_SLOT, Component.empty());
		// You can set values outside a ready event listener.
		if (!getLevel().isClientSide()) buttonLabel.set(Component.literal("Send Message"));
		// The button will never be yellow! Initial values won't be synced.
		buttonColor = registerDataSlot(BUTTON_COLOR_DATA_SLOT, 0xFF_FFFF00, NetworkDirection.CLIENT_TO_SERVER);

		buttonLabel.addChangeListener((dataSlot, from, to) -> buttonA.setLabel(to));
		buttonColor.addChangeListener((dataSlot, from, to) -> {
			buttonB.setLabel(buttonB.getLabel().copy().withColor(to));
		});

		getNetworking(NetworkSide.SERVER).receive(TEST_MESSAGE, Codec.INT, value -> {
			System.out.println("Received on the server " + value + " times!");
		});

		getNetworking(NetworkSide.SERVER).receive(TEST_REGISTRY_MESSAGE, Biome.CODEC, value -> {
			System.out.println("Received registry entry on the server: " + value);
		});

		getNetworking(NetworkSide.SERVER).getReadyEvent().register(networking -> {
			System.out.println("Ready to receive and send on the server!");
			var pos = playerInventory.player.blockPosition();
			networking.send(ON_SERVER_READY_MESSAGE, pos);
		});

		getNetworking(NetworkSide.CLIENT).receive(ON_SERVER_READY_MESSAGE, pos -> {
			System.out.println("Server was ready to send at " + pos);
		});

		getNetworking(NetworkSide.CLIENT).getReadyEvent().register(networking -> {
			System.out.println("Ready to receive and send on the client!");
			var biome = getLevel().getBiome(playerInventory.player.blockPosition());
			networking.send(TEST_REGISTRY_MESSAGE, Biome.CODEC, biome);
		});

		try {
			slot.onHidden();
			slot.onShown();
		} catch (Throwable t) {
			throw new AssertionError("ValidatedSlot.setVisible crashed", t);
		}
	}
}
