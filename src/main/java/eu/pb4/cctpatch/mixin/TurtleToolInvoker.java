package eu.pb4.cctpatch.mixin;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.turtle.upgrades.TurtleTool;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TurtleTool.class)
public interface TurtleToolInvoker {
    @Invoker("getToolStack")
    ItemStack getToolStack(ITurtleAccess turtle, TurtleSide side);
}
