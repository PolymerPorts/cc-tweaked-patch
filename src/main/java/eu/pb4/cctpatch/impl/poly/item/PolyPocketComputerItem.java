package eu.pb4.cctpatch.impl.poly.item;

import dan200.computercraft.shared.computer.core.ComputerState;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import eu.pb4.cctpatch.impl.ComputerCraftPolymerPatch;
import eu.pb4.cctpatch.impl.poly.ext.ServerComputerExt;
import eu.pb4.polymer.core.api.item.PolymerItem;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public record PolyPocketComputerItem() implements PolymerItem {
    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.TRIAL_KEY;
    }

    @Override
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context) {
        var computer = ComputerCraftPolymerPatch.server != null ? PocketComputerItem.getServerComputer(ComputerCraftPolymerPatch.server, stack) : null;

        out.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(), List.of(),
                List.of(computer != null ? computer.getState().asString() : ComputerState.OFF.asString()),
                computer != null ? IntList.of(computer.getBrain().getLight()) : IntList.of()));

        if (ComputerCraftPolymerPatch.server != null && computer != null) {
            var mapId = ServerComputerExt.of(computer).getMapId();
            if (mapId < 0) {
                out.set(DataComponentTypes.MAP_ID, new MapIdComponent(mapId));
            }
        }
    }
}
