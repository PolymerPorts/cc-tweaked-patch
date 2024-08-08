package eu.pb4.cctpatch.mixin;

import dan200.computercraft.shared.pocket.core.PocketBrain;
import dan200.computercraft.shared.pocket.core.PocketHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PocketBrain.class)
public interface PocketBrainAccessor {
    @Invoker
    PocketHolder callHolder();
}
