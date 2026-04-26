package eu.pb4.cctpatch.impl.poly;

import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.factorytools.api.block.model.generic.BSMMParticleBlock;
import eu.pb4.factorytools.api.block.model.generic.BlockStateModel;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

public interface AutoModeledPolymerBlock extends FactoryBlock, BSMMParticleBlock {
    @Override
    default BlockState getPolymerBlockState(BlockState state, PacketContext packetContext) {
        return Blocks.BARRIER.defaultBlockState();
    }

    @Override
    default @Nullable ElementHolder createElementHolder(ServerLevel world, BlockPos pos, BlockState initialBlockState) {
        return BlockStateModel.longRange(initialBlockState, pos);
    }
}
