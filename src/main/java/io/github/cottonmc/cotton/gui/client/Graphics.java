package io.github.cottonmc.cotton.gui.client;

import io.github.cottonmc.cotton.gui.impl.client.GraphicsImpl;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Texture;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public interface Graphics {
	MatrixStack getMatrices();
	VertexConsumerProvider getVertexConsumers();

	default void texture(Texture texture, int x, int y, int width, int height) {
		texture(texture, x, y, width, height, 0xFF_FFFFFF);
	}
	default void texture(Texture texture, int x, int y, int width, int height, int color) {
		texture(texture.image, x, y, texture.u1, texture.v1, texture.u2, texture.v2, width, height, color);
	}
	default void texture(Identifier texture, int x, int y, int width, int height) {
		texture(texture, x, y, 0, 0, 1, 1, width, height);
	}
	default void texture(Identifier texture, int x, int y, int width, int height, int color) {
		texture(texture, x, y, 0, 0, 1, 1, width, height, color);
	}
	default void texture(Identifier texture, int x, int y, float u1, float v1, float u2, float v2, int width, int height) {
		texture(texture, x, y, u1, v1, u2, v2, width, height, 0xFF_FFFFFF);
	}
	void texture(Identifier texture, int x, int y, float u1, float v1, float u2, float v2, int width, int height, int color);

	void rect(int x, int y, int width, int height, int color);

	void text(String text, int x, int y, int color);
	void text(OrderedText text, int x, int y, int color);
	void text(Text text, int x, int y, int color);
	default void text(String text, HorizontalAlignment align, int x, int y, int width, int color) {
		text(text, align, x, y, width, color, false);
	}
	default void text(OrderedText text, HorizontalAlignment align, int x, int y, int width, int color) {
		text(text, align, x, y, width, color, false);
	}
	default void text(Text text, HorizontalAlignment align, int x, int y, int width, int color) {
		text(text, align, x, y, width, color, false);
	}
	void text(String text, HorizontalAlignment align, int x, int y, int width, int color, boolean shadow);
	void text(OrderedText text, HorizontalAlignment align, int x, int y, int width, int color, boolean shadow);
	void text(Text text, HorizontalAlignment align, int x, int y, int width, int color, boolean shadow);

	default void translate(double x, double y, double z) {
		getMatrices().translate(x, y, z);
	}

	default void rotate(float degrees) {
		getMatrices().multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(degrees));
	}

	default void push() {
		getMatrices().push();
	}

	default void pop() {
		getMatrices().pop();
	}

	static Graphics of(MatrixStack matrices) {
		return new GraphicsImpl(matrices);
	}

	static void draw(MatrixStack matrices, Consumer<Graphics> drawer) {
		GraphicsImpl g = new GraphicsImpl(matrices);
		g.push();
		drawer.accept(g);
		g.pop();
		g.draw();
	}
}
