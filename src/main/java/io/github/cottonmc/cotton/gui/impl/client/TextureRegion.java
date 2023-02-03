package io.github.cottonmc.cotton.gui.impl.client;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class TextureRegion extends ResourceTexture {
	@NotNull
	final ImageRegion imageRegion;

	@Nullable
	private NativeImage image = null;

	public TextureRegion(@NotNull Identifier textureIdentifier, float startXFraction, float startYFraction, float widthFraction, float heightFraction) {
		super(textureIdentifier);
		imageRegion = new FractionRegion(startXFraction, startYFraction, widthFraction, heightFraction);
		registerAndLoad();
	}

	public TextureRegion(@NotNull Identifier textureIdentifier, int startXPixel, int startYPixel, int widthPixel, int heightPixel) {
		super(textureIdentifier);
		imageRegion = new PixelRegion(startXPixel, startYPixel, widthPixel, heightPixel);
		registerAndLoad();
	}

	private void registerAndLoad() {
		String regionID = this.location.getPath() + "-" + imageRegion.getIdString();
		MinecraftClient.getInstance().getTextureManager().registerTexture(new Identifier("tex-region", regionID), this);
	}

	@Override
	public void load(ResourceManager manager) throws IOException {
		TextureData textureData = this.loadTextureData(manager);
		textureData.checkException();

		NativeImage nativeImage = textureData.getImage();
		if (RenderSystem.isOnRenderThreadOrInit()) {
			imageRegion.upload(this, nativeImage);
		} else {
			RenderSystem.recordRenderCall(() -> imageRegion.upload(this, nativeImage));
		}
	}

	@Override
	public void close() {
		if (this.image == null) {
			return;
		}

		this.image.close();
		this.clearGlId();
		this.image = null;
	}

	private interface ImageRegion {
		void upload(AbstractTexture texture, NativeImage image);

		String getIdString();
	}

	private static record PixelRegion(int x, int y, int width, int height) implements ImageRegion {
		@Override
		public void upload(AbstractTexture texture, NativeImage image) {
			TextureUtil.prepareImage(texture.getGlId(), 0, this.width, this.height);
			texture.bindTexture();
			image.upload(0, this.x, this.y, 0, 0, this.width, this.height, false, false, false, false);
		}

		@Override
		public String getIdString() {
			return "" + x + "-" + y + "-" + width + "-" + height;
		}
	}

	private static record FractionRegion(float x, float y, float width, float height) implements ImageRegion {
		@Override
		public void upload(AbstractTexture texture, NativeImage image) {
			int xPx = (int) (this.x * image.getWidth());
			int yPx = (int) (this.y * image.getHeight());
			int widthPx = (int) (this.width * image.getWidth());
			int heightPx = (int) (this.height * image.getHeight());

			TextureUtil.prepareImage(texture.getGlId(), 0, widthPx, heightPx);
			image.upload(0, 0, 0, xPx, yPx, widthPx, heightPx, false, false, false, false);
		}

		@Override
		public String getIdString() {
			return "" + x + "-" + y + "-" + width + "-" + height;
		}
	}
}
