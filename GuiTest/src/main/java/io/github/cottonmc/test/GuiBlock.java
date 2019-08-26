package io.github.cottonmc.test;

import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class GuiBlock extends Block implements BlockEntityProvider {

	public GuiBlock() {
		super(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build());
	}
	
	
	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
		if (!world.isClient) {
			ContainerProviderRegistry.INSTANCE.openContainer(new Identifier(LibGuiTest.MODID, "gui"), player, (buf)->buf.writeBlockPos(pos));
		}
		return true;
	}


	@Override
	public BlockEntity createBlockEntity(BlockView var1) {
		return new GuiBlockEntity();
	}
}
