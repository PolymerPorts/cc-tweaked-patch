package eu.pb4.cctpatch.mixin.mod.item;

import dan200.computercraft.core.util.Colour;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.common.IColouredItem;
import dan200.computercraft.shared.media.items.DiskItem;
import eu.pb4.factorytools.api.item.FireworkStarColoredItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ DiskItem.class })
public class SimpleColoredItemMixin implements FireworkStarColoredItem {
    @Override
    public int getItemColor(ItemStack stack) {
        var x = IColouredItem.getColourBasic(stack);
        if (x == -1) {
            //if (this == ModRegistry.Items.DISK.get()) {
            //}

            return Colour.WHITE.getHex();
        }
        return x;
    }
}
