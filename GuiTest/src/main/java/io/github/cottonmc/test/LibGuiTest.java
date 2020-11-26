package io.github.cottonmc.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LibGuiTest implements ModInitializer {
	public static final String MODID = "libgui-test";
	
	public static GuiBlock GUI_BLOCK;
	public static BlockItem GUI_BLOCK_ITEM;
	public static BlockEntityType<GuiBlockEntity> GUI_BLOCKENTITY_TYPE;
	public static ScreenHandlerType<TestDescription> GUI_SCREEN_HANDLER_TYPE;

	@Override
	public void onInitialize() {
		Registry.register(Registry.ITEM, new Identifier(MODID, "client_gui"), new GuiItem());
		
		GUI_BLOCK = new GuiBlock();
		Registry.register(Registry.BLOCK, new Identifier(MODID, "gui"), GUI_BLOCK);
		GUI_BLOCK_ITEM = new BlockItem(GUI_BLOCK, new Item.Settings().group(ItemGroup.MISC));
		Registry.register(Registry.ITEM, new Identifier(MODID, "gui"), GUI_BLOCK_ITEM);
		GUI_BLOCKENTITY_TYPE = FabricBlockEntityTypeBuilder.create(GuiBlockEntity::new, GUI_BLOCK).build(null);
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "gui"), GUI_BLOCKENTITY_TYPE);
		
		GUI_SCREEN_HANDLER_TYPE = ScreenHandlerRegistry.registerSimple(new Identifier(MODID, "gui"), (int syncId, PlayerInventory inventory) -> {
			return new TestDescription(GUI_SCREEN_HANDLER_TYPE, syncId, inventory, ScreenHandlerContext.EMPTY);
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
