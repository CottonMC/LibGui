package io.github.cottonmc.cotton.gui.impl.access;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor {
	@Invoker("method_32633")
	void callRenderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y);
}
