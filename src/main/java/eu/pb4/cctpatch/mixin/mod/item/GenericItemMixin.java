package eu.pb4.cctpatch.mixin.mod.item;

import dan200.computercraft.shared.computer.items.ComputerItem;
import dan200.computercraft.shared.media.items.PrintoutItem;
import dan200.computercraft.shared.media.items.TreasureDiskItem;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlockItem;
import dan200.computercraft.shared.turtle.items.TurtleItem;
import eu.pb4.factorytools.api.item.AutoModeledPolymerItem;
import eu.pb4.factorytools.api.resourcepack.BaseItemProvider;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ComputerItem.class, PrintoutItem.class,
        CableBlockItem.class, TreasureDiskItem.class
})
public class GenericItemMixin implements AutoModeledPolymerItem {
    @Unique
    private final Item polymerItem = BaseItemProvider.requestItem();
    @Override
    public Item getPolymerItem() {
        return this.polymerItem;
    }
}
