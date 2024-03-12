package eu.pb4.cctpatch.mixin.mod.item;

import dan200.computercraft.shared.ModRegistry;
import eu.pb4.factorytools.api.item.FactoryBlockItem;
import eu.pb4.polymer.core.api.block.PolymerBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import java.util.function.BiFunction;

@Mixin(ModRegistry.Items.class)
public class ModRegistryItemsMixin {
    @ModifyArg(
            method = "<clinit>",
                    at = @At(value = "INVOKE",
                    target = "Ldan200/computercraft/shared/ModRegistry$Items;ofBlock(Ldan200/computercraft/shared/platform/RegistryEntry;Ljava/util/function/BiFunction;)Ldan200/computercraft/shared/platform/RegistryEntry;"
            ),
            slice =  @Slice(
                    from = @At(value = "FIELD", target = "Ldan200/computercraft/shared/ModRegistry$Blocks;SPEAKER:Ldan200/computercraft/shared/platform/RegistryEntry;")
            )
    )
    private static BiFunction<Block, Item.Settings, BlockItem> replaceBlockItem(BiFunction<Block, Item.Settings, BlockItem> sup) {
        return (a, b) -> new FactoryBlockItem((Block & PolymerBlock) a, b);
    }
}
