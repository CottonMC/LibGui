package io.github.cottonmc.test;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public final class TestBlocks {
	public static final GuiBlock GUI = new GuiBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).registryKey(Keys.GUI));
	public static final Block NO_BLOCK_INVENTORY = new NoBlockInventoryBlock(AbstractBlock.Settings.copy(Blocks.STONE).registryKey(Keys.NO_BLOCK_INVENTORY));

	public static void register() {
		Registry.register(Registries.BLOCK, Keys.GUI, GUI);
		Registry.register(Registries.BLOCK, Keys.NO_BLOCK_INVENTORY, NO_BLOCK_INVENTORY);
	}

	public static final class Keys {
		public static final RegistryKey<Block> GUI = of("gui");
		public static final RegistryKey<Block> NO_BLOCK_INVENTORY = of("no_block_inventory");

		private static RegistryKey<Block> of(String id) {
			return RegistryKey.of(RegistryKeys.BLOCK, LibGuiTest.id(id));
		}
	}
}
