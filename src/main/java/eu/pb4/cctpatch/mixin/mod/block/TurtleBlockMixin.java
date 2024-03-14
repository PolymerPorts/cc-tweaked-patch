package eu.pb4.cctpatch.mixin.mod.block;

import dan200.computercraft.shared.common.HorizontalContainerBlock;
import dan200.computercraft.shared.computer.blocks.AbstractComputerBlock;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlock;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemFullBlock;
import dan200.computercraft.shared.peripheral.modem.wireless.WirelessModemBlock;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlock;
import dan200.computercraft.shared.peripheral.speaker.SpeakerBlock;
import dan200.computercraft.shared.turtle.blocks.TurtleBlock;
import eu.pb4.cctpatch.impl.poly.AutoModeledPolymerBlock;
import eu.pb4.cctpatch.impl.poly.model.TurtleModel;
import eu.pb4.cctpatch.impl.poly.model.generic.BlockStateModel;
import eu.pb4.factorytools.api.block.FactoryBlock;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TurtleBlock.class)
public class TurtleBlockMixin implements FactoryBlock {
    @Override
    public Block getPolymerBlock(BlockState state) {
        return Blocks.BARRIER;
    }


    @Override
    public @Nullable ElementHolder createElementHolder(ServerWorld world, BlockPos pos, BlockState initialBlockState) {
        return new TurtleModel(initialBlockState, pos);
    }
}
