package eu.pb4.cctpatch.mixin.mod.block;

import dan200.computercraft.shared.lectern.CustomLecternBlock;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

@Mixin(CustomLecternBlock.class)
public class CustomLecternBlockMixin implements PolymerBlock {
    @Override
    public BlockState getPolymerBlockState(BlockState state, PacketContext packetContext) {
        return Blocks.LECTERN.withPropertiesOf(state);
    }
}
