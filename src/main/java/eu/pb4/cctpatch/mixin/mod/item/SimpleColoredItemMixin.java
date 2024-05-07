package eu.pb4.cctpatch.mixin.mod.item;

import dan200.computercraft.core.util.Colour;
import dan200.computercraft.shared.media.items.DiskItem;
import eu.pb4.factorytools.api.item.FireworkStarColoredItem;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ DiskItem.class })
public class SimpleColoredItemMixin implements FireworkStarColoredItem {
    @Override
    public int getItemColor(ItemStack stack) {
        var x = DyedColorComponent.getColor(stack, Colour.WHITE.getARGB());
        if (x == -1) {
            return Colour.WHITE.getHex();
        }
        return x;
    }
}
