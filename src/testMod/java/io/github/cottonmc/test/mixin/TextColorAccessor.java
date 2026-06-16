package io.github.cottonmc.test.mixin;

import net.minecraft.network.chat.TextColor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(TextColor.class)
public interface TextColorAccessor {
	@Accessor("NAMED_COLORS")
	static Map<String, TextColor> getNamedColors() {
		throw new AssertionError();
	}
}
