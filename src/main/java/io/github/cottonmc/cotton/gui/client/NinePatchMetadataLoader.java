package io.github.cottonmc.cotton.gui.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.io.InputStream;
import java.util.*;

@Environment(EnvType.CLIENT)
public class NinePatchMetadataLoader extends SinglePreparationResourceReloadListener<Map<Identifier, Properties>> implements IdentifiableResourceReloadListener {
	public static final NinePatchMetadataLoader INSTANCE = new NinePatchMetadataLoader();

	private static final Identifier ID = new Identifier("libgui", "9patch_metadata");
	private static final String SUFFIX = ".9patch";

	private Map<Identifier, TextureProperties> properties = Collections.emptyMap();

	public TextureProperties getProperties(Identifier texture) {
		return properties.getOrDefault(texture, TextureProperties.DEFAULT);
	}

	@Override
	public Identifier getFabricId() {
		return ID;
	}

	@Override
	protected Map<Identifier, Properties> prepare(ResourceManager manager, Profiler profiler) {
		Collection<Identifier> ids = manager.findResources("textures", s -> s.endsWith(SUFFIX));
		Map<Identifier, Properties> result = new HashMap<>();

		for (Identifier input : ids) {
			try (Resource resource = manager.getResource(input);
				 InputStream stream = resource.getInputStream()) {
				Properties props = new Properties();
				props.load(stream);
				Identifier textureId = new Identifier(input.getNamespace(), input.getPath().substring(0, input.getPath().length() - SUFFIX.length()));
				result.put(textureId, props);
			} catch (Exception e) {
				LibGuiClient.logger.error("Error while loading metadata file {}, skipping...", input, e);
			}
		}

		return result;
	}

	@Override
	protected void apply(Map<Identifier, Properties> meta, ResourceManager manager, Profiler profiler) {
		properties = new HashMap<>();
		for (Map.Entry<Identifier, Properties> entry : meta.entrySet()) {
			Identifier id = entry.getKey();
			Properties props = entry.getValue();

			if (!props.containsKey("mode")) {
				LibGuiClient.logger.error("Metadata file for nine-patch texture {} is missing required key 'mode'", id);
				continue;
			}

			String modeStr = props.getProperty("mode");
			BackgroundPainter.NinePatch.Mode mode = BackgroundPainter.NinePatch.Mode.fromString(modeStr);

			if (mode == null) {
				LibGuiClient.logger.error("Invalid mode '{}' in nine-patch metadata file for texture {}", modeStr, id);
				continue;
			}

			TextureProperties texProperties = new TextureProperties(mode);
			properties.put(id, texProperties);
		}
	}

	public static class TextureProperties {
		public static final TextureProperties DEFAULT = new TextureProperties(BackgroundPainter.NinePatch.Mode.STRETCHING);

		private final BackgroundPainter.NinePatch.Mode mode;

		public TextureProperties(BackgroundPainter.NinePatch.Mode mode) {
			this.mode = mode;
		}

		public BackgroundPainter.NinePatch.Mode getMode() {
			return mode;
		}
	}
}
