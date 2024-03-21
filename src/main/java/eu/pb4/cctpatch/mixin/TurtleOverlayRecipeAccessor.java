package eu.pb4.cctpatch.mixin;

import dan200.computercraft.shared.turtle.recipes.TurtleOverlayRecipe;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TurtleOverlayRecipe.class)
public interface TurtleOverlayRecipeAccessor {
    @Accessor
    Identifier getOverlay();
}
