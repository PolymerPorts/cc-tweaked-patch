package eu.pb4.cctpatch.impl.poly;

import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.blocks.AbstractComputerBlock;
import eu.pb4.cctpatch.impl.poly.model.generic.BlockStateModel;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface AutoModeledPolymerBlock extends FactoryBlock {
    @Override
    default BlockState getPolymerBlockState(BlockState state) {
        return Blocks.BARRIER.getDefaultState();
    }

    @Override
    default BlockState getPolymerBreakEventBlockState(BlockState state, ServerPlayerEntity player) {
        return state.isOf(ModRegistry.Blocks.COMPUTER_ADVANCED.get())
                || state.isOf(ModRegistry.Blocks.MONITOR_ADVANCED.get())
                || state.isOf(ModRegistry.Blocks.TURTLE_ADVANCED.get()) ? Blocks.GOLD_BLOCK.getDefaultState() : Blocks.STONE.getDefaultState();
    }

    @Override
    default @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new BlockStateModel(initialBlockState);
    }
}
