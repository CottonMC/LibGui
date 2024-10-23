package io.github.cottonmc.test;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public final class TestItems {
	public static final Item CLIENT_GUI = new GuiItem(new Item.Settings().registryKey(Keys.CLIENT_GUI));
	public static final Item GUI = new BlockItem(TestBlocks.GUI, new Item.Settings().registryKey(Keys.GUI).useBlockPrefixedTranslationKey());
	public static final Item NO_BLOCK_INVENTORY = new BlockItem(TestBlocks.NO_BLOCK_INVENTORY, new Item.Settings().registryKey(Keys.NO_BLOCK_INVENTORY).useBlockPrefixedTranslationKey());

	public static void register() {
		Registry.register(Registries.ITEM, Keys.CLIENT_GUI, CLIENT_GUI);
		Registry.register(Registries.ITEM, Keys.GUI, GUI);
		Registry.register(Registries.ITEM, Keys.NO_BLOCK_INVENTORY, NO_BLOCK_INVENTORY);
	}

	public static final class Keys {
		public static final RegistryKey<Item> GUI = of("gui");
		public static final RegistryKey<Item> NO_BLOCK_INVENTORY = of("no_block_inventory");
		public static final RegistryKey<Item> CLIENT_GUI = of("client_gui");

		private static RegistryKey<Item> of(String id) {
			return RegistryKey.of(RegistryKeys.ITEM, LibGuiTest.id(id));
		}
	}
}
