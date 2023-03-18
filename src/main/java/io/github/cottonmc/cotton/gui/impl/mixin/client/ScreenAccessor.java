package io.github.cottonmc.cotton.gui.impl.mixin.client;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor {
	@Accessor("children")
	List<Element> libgui$getChildren();
}
