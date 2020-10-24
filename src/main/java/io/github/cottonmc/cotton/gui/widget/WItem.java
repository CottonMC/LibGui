package io.github.cottonmc.cotton.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;

import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A widget that displays an item or a list of items.
 *
 * @since 1.8.0
 */
public class WItem extends WWidget {
	private List<ItemStack> items;
	private int duration = 25;
	private int ticks = 0;
	private int current = 0;

	public WItem(List<ItemStack> items) {
		setItems(items);
	}

	public WItem(Tag<? extends ItemConvertible> tag) {
		this(getRenderStacks(tag));
	}

	public WItem(ItemStack stack) {
		this(Collections.singletonList(stack));
	}

	@Override
	public boolean canResize() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void tick() {
		if (ticks++ >= duration) {
			ticks = 0;
			current = (current + 1) % items.size();
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void paint(MatrixStack matrices, int x, int y, int mouseX, int mouseY) {
		RenderSystem.enableDepthTest();

		MinecraftClient mc = MinecraftClient.getInstance();
		ItemRenderer renderer = mc.getItemRenderer();
		renderer.zOffset = 100f;
		renderer.renderInGui(items.get(current), x + getWidth() / 2 - 9, y + getHeight() / 2 - 9);
		renderer.zOffset = 0f;
	}

	/**
	 * Returns the animation duration of this {@code WItem}.
	 *
	 * <p>Defaults to 25 screen ticks.
	 */
	public int getDuration() {
		return duration;
	}

	public WItem setDuration(int duration) {
		this.duration = duration;
		return this;
	}

	public List<ItemStack> getItems() {
		return items;
	}

	/**
	 * Sets the item list of this {@code WItem} and resets the animation state.
	 *
	 * @param items the new item list
	 * @return this instance
	 */
	public WItem setItems(List<ItemStack> items) {
		Objects.requireNonNull(items, "stacks == null!");
		if (items.isEmpty()) throw new IllegalArgumentException("The stack list is empty!");

		this.items = items;

		// Reset the state
		current = 0;
		ticks = 0;

		return this;
	}

	/**
	 * Gets the render stacks ({@link Item#getStackForRender()}) of each item in a tag.
	 */
	private static List<ItemStack> getRenderStacks(Tag<? extends ItemConvertible> tag) {
		ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();

		for (ItemConvertible item : tag.values()) {
			builder.add(new ItemStack(item));
		}

		return builder.build();
	}
}
