package io.github.cottonmc.cotton.gui.client;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import io.github.cottonmc.jankson.JanksonFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.resource.ResourceType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class LibGuiClient implements ClientModInitializer {
	public static final Logger logger = LogManager.getLogger();
	public static final String MODID = "libgui";
	public static volatile LibGuiConfig config;

	public static final Jankson jankson = JanksonFactory.createJankson();

	@Override
	public void onInitializeClient() {
		config = loadConfig();

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(NinePatch.MetadataLoader.INSTANCE);
	}

	public static LibGuiConfig loadConfig() {
		try {
			File file = new File(FabricLoader.getInstance().getConfigDirectory(),"libgui.json5");
			
			if (!file.exists()) saveConfig(new LibGuiConfig());
			
			JsonObject json = jankson.load(file);
			config =  jankson.fromJson(json, LibGuiConfig.class);
			
			/*
			JsonElement jsonElementNew = jankson.toJson(new LibGuiConfig());
			if(jsonElementNew instanceof JsonObject) {
				JsonObject jsonNew = (JsonObject) jsonElementNew;
				if(json.getDelta(jsonNew).size()>= 0) { //TODO: Insert new keys as defaults into `json` IR object instead of writing the config out, so comments are preserved
					saveConfig(config);
				}
			}*/
		} catch (Exception e) {
			logger.error("[LibGui] Error loading config: {}", e.getMessage());
		}
		return config;
	}

	public static void saveConfig(LibGuiConfig config) {
		try {
			File file = new File(FabricLoader.getInstance().getConfigDirectory(),"libgui.json5");
			
			JsonElement json = jankson.toJson(config);
			String result = json.toJson(true, true);
			try (FileOutputStream out = new FileOutputStream(file, false)) {
				out.write(result.getBytes(StandardCharsets.UTF_8));
			}
		} catch (Exception e) {
			logger.error("[LibGui] Error saving config: {}", e.getMessage());
		}
	}
}
