package io.github.cottonmc.test;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LibGuiTest implements ModInitializer {
	public static final String MODID = "libgui-test";
	
	@Override
	public void onInitialize() {
		//TODO: Register an item that spawns a clientside gui, and a block that spawns a serverside one
		
		Registry.register(Registry.ITEM, new Identifier(MODID, "client_gui"), new GuiItem());
	}

}
