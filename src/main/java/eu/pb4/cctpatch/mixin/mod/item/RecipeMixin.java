package eu.pb4.cctpatch.mixin.mod.item;

import dan200.computercraft.shared.common.ClearColourRecipe;
import dan200.computercraft.shared.common.ColourableRecipe;
import dan200.computercraft.shared.media.recipes.DiskRecipe;
import dan200.computercraft.shared.media.recipes.PrintoutRecipe;
import dan200.computercraft.shared.pocket.recipes.PocketComputerUpgradeRecipe;
import dan200.computercraft.shared.recipe.CustomShapedRecipe;
import dan200.computercraft.shared.recipe.CustomShapelessRecipe;
import dan200.computercraft.shared.turtle.recipes.TurtleUpgradeRecipe;
import eu.pb4.polymer.core.api.item.PolymerRecipe;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ CustomShapedRecipe.class, CustomShapelessRecipe.class, ColourableRecipe.class, ClearColourRecipe.class,
        TurtleUpgradeRecipe.class, PocketComputerUpgradeRecipe.class,
        PrintoutRecipe.class, DiskRecipe.class,
})
public class RecipeMixin implements PolymerRecipe {
}
