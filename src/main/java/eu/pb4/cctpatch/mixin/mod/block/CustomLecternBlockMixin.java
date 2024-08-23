package eu.pb4.cctpatch.mixin.mod.block;

import dan200.computercraft.shared.lectern.CustomLecternBlock;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CustomLecternBlock.class)
public class CustomLecternBlockMixin implements PolymerBlock {
    @Override
    public BlockState getPolymerBlockState(BlockState state) {
        return Blocks.LECTERN.getStateWithProperties(state);
    }
}
