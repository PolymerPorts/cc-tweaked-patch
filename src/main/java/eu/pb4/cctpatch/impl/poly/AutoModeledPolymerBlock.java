package eu.pb4.cctpatch.impl.poly;

import eu.pb4.cctpatch.impl.poly.model.generic.BlockStateModel;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public interface AutoModeledPolymerBlock extends FactoryBlock {
    @Override
    default Block getPolymerBlock(BlockState state) {
        return Blocks.BARRIER;
    }

    @Override
    default @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new BlockStateModel(initialBlockState);
    }
}
