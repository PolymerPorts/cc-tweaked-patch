package eu.pb4.computercraftpatch.mixin.mod.item;

import dan200.computercraft.shared.computer.items.AbstractComputerItem;
import dan200.computercraft.shared.media.items.DiskItem;
import dan200.computercraft.shared.media.items.PrintoutItem;
import dan200.computercraft.shared.media.items.TreasureDiskItem;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlockItem;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import eu.pb4.factorytools.api.item.AutoModeledPolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({AbstractComputerItem.class, DiskItem.class, PocketComputerItem.class, PrintoutItem.class,
        CableBlockItem.class, TreasureDiskItem.class
})
public class GenericItemMixin implements AutoModeledPolymerItem {
    @Override
    public Item getPolymerItem() {
        return Items.STICK;
    }
}
