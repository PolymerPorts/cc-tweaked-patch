package eu.pb4.cctpatch.impl.poly.model.generic;

import eu.pb4.factorytools.api.block.CustomBreakingParticleBlock;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleEffect;

public interface BSMMParticleBlock extends CustomBreakingParticleBlock {

    @Override
    default ParticleEffect getBreakingParticle(BlockState blockState) {
        return BlockStateModelManager.getParticle(blockState);
    }
}
