package eu.pb4.cctpatch.mixin;

import dan200.computercraft.shared.turtle.upgrades.TurtleTool;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TurtleTool.class)
public interface TurtleToolAccessor {
    @Accessor
    ItemStack getItem();
}
