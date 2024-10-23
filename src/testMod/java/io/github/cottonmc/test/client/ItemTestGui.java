package io.github.cottonmc.test.client;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WItem;
import io.github.cottonmc.cotton.gui.widget.WLabel;

import java.util.List;

public final class ItemTestGui extends LightweightGuiDescription {
	public ItemTestGui() {
		WGridPanel root = (WGridPanel) rootPanel;

		root.add(new WLabel(Text.literal("Single stack")), 0, 0, 3, 1);
		root.add(new WItem(new ItemStack(Items.APPLE)), 0, 1);

		root.add(new WLabel(Text.literal("Stack list")), 3, 0, 3, 1);
		root.add(new WItem(List.of(new ItemStack(Items.DIAMOND), new ItemStack(Items.EMERALD))), 3, 1);

		root.add(new WLabel(Text.literal("Tag")), 0, 2, 3, 1);
		root.add(new WItem(ItemTags.HOES), 0, 3);

		root.validate(this);
	}
}
