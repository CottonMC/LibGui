package io.github.cottonmc.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

public class LibGuiTest implements ModInitializer {
	public static final String MODID = "libgui-test";
	
	public static GuiBlock GUI_BLOCK;
	public static BlockItem GUI_BLOCK_ITEM;
	public static BlockEntityType<GuiBlockEntity> GUI_BLOCKENTITY_TYPE;
	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier(MODID, "client_gui"), new GuiItem());
		
		GUI_BLOCK = new GuiBlock();
		Registry.register(Registry.BLOCK, new Identifier(MODID, "gui"), GUI_BLOCK);
		GUI_BLOCK_ITEM = new BlockItem(GUI_BLOCK, new Item.Settings().group(ItemGroup.MISC));
		Registry.register(Registry.ITEM, new Identifier(MODID, "gui"), GUI_BLOCK_ITEM);
		GUI_BLOCKENTITY_TYPE = BlockEntityType.Builder.create(GuiBlockEntity::new, GUI_BLOCK).build(null);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "gui"), GUI_BLOCKENTITY_TYPE);
		
		
		ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier(MODID, "gui"), (int syncId, Identifier identifier, PlayerEntity player, PacketByteBuf buf)->{
			return new TestContainer(syncId, player.inventory, BlockContext.create(player.getEntityWorld(), buf.readBlockPos()));
		});
		
		Optional<ModContainer> containerOpt = FabricLoader.getInstance().getModContainer("jankson");
		if (containerOpt.isPresent()) {
			ModContainer jankson = containerOpt.get();
			System.out.println("Jankson root path: "+jankson.getRootPath());
			try {
				Files.list(jankson.getRootPath()).forEach((path)->{
					path.getFileSystem().getFileStores().forEach((store)->{
						System.out.println("        Filestore: "+store.name());
					});
					System.out.println("    "+path.toAbsolutePath());
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
			Path modJson = jankson.getPath("/fabric.mod.json");
			System.out.println("Jankson fabric.mod.json path: "+modJson);
			System.out.println(Files.exists(modJson) ? "Exists" : "Does Not Exist");
		} else {
			System.out.println("Container isn't present!");
		}
	}

}
