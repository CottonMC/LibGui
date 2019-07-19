package io.github.cottonmc.cotton.gui.client;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import io.github.cottonmc.jankson.JanksonFactory;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;

public class LibGuiClient implements ClientModInitializer {
	public static final Logger logger = LogManager.getLogger();

	public static LibGuiConfig config;

	public static final Jankson jankson = JanksonFactory.createJankson();

	@Override
	public void onInitializeClient() {
		config = loadConfig();
	}

	public LibGuiConfig loadConfig() {
		try {
			File file = FabricLoader.getInstance().getConfigDirectory().toPath().resolve("libgui.json5").toFile();
			if (!file.exists()) saveConfig(new LibGuiConfig());
			JsonObject json = jankson.load(file);
			LibGuiConfig result =  jankson.fromJson(json, LibGuiConfig.class);
			JsonElement jsonElementNew = jankson.toJson(new LibGuiConfig());
			if(jsonElementNew instanceof JsonObject){
				JsonObject jsonNew = (JsonObject) jsonElementNew;
				if(json.getDelta(jsonNew).size()>= 0){
					saveConfig(result);
				}
			}
		} catch (Exception e) {
			logger.error("[LibGui] Error loading config: {}", e.getMessage());
		}
		return new LibGuiConfig();
	}

	public void saveConfig(LibGuiConfig config) {
		try {
			File file = FabricLoader.getInstance().getConfigDirectory().toPath().resolve("libgui.json5").toFile();
			JsonElement json = jankson.toJson(config);
			String result = json.toJson(true, true);
			if (!file.exists()) file.createNewFile();
			FileOutputStream out = new FileOutputStream(file,false);
			out.write(result.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.error("[LibGui] Error saving config: {}", e.getMessage());
		}
	}

}
