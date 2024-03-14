package eu.pb4.cctpatch.mixin;

import dan200.computercraft.shared.turtle.upgrades.TurtleModem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TurtleModem.class)
public interface TurtleModemAccessor {
    @Accessor
    boolean isAdvanced();
}
