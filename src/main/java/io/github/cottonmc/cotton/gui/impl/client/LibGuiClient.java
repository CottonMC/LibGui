package io.github.cottonmc.cotton.gui.impl.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import io.github.cottonmc.cotton.gui.impl.Proxy;
import io.github.cottonmc.cotton.gui.impl.ScreenNetworkingImpl;
import io.github.cottonmc.jankson.JanksonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LibGuiClient implements ClientModInitializer {
	public static final Logger logger = LogManager.getLogger();
	public static volatile LibGuiConfig config;

	public static final Jankson jankson = JanksonFactory.createJankson();

	@Override
	public void onInitializeClient() {
		config = loadConfig();

		ClientPlayNetworking.registerGlobalReceiver(ScreenNetworkingImpl.ScreenMessage.ID, (payload, context) -> {
			ScreenNetworkingImpl.handle(context.client(), context.player(), payload);
		});

		Proxy.proxy = new ClientProxy();
	}

	public static LibGuiConfig loadConfig() {
		try {
			Path file = FabricLoader.getInstance().getConfigDir().resolve("libgui.json5");
			
			if (Files.notExists(file)) saveConfig(new LibGuiConfig());
			
			JsonObject json;
			try (InputStream in = Files.newInputStream(file)) {
				json = jankson.load(in);
			}

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
			Path file = FabricLoader.getInstance().getConfigDir().resolve("libgui.json5");
			
			JsonElement json = jankson.toJson(config);
			String result = json.toJson(true, true);
			Files.write(file, result.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			logger.error("[LibGui] Error saving config: {}", e.getMessage());
		}
	}
}
