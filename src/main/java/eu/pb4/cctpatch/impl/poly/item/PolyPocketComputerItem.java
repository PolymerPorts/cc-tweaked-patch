package eu.pb4.cctpatch.impl.poly.item;

import dan200.computercraft.shared.computer.core.ComputerState;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.items.ServerComputerReference;
import dan200.computercraft.shared.pocket.core.PocketServerComputer;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import eu.pb4.cctpatch.impl.ComputerCraftPolymerPatch;
import eu.pb4.cctpatch.impl.poly.ext.ServerComputerExt;
import eu.pb4.polymer.core.api.item.PolymerItem;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.List;

import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.saveddata.maps.MapId;

public record PolyPocketComputerItem() implements PolymerItem {
    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        return Items.TRIAL_KEY;
    }

    @Override
    public void modifyBasePolymerItemStack(ItemStack out, ItemStack stack, PacketContext context, HolderLookup.Provider provider) {
        var computer = ComputerCraftPolymerPatch.server != null && ServerComputerReference.get(stack, ServerContext.get(ComputerCraftPolymerPatch.server).registry()) instanceof PocketServerComputer x ? x : null;

        out.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(),
                List.of(computer != null ? computer.getState().getSerializedName() : ComputerState.OFF.getSerializedName()),
                computer != null ? IntList.of(computer.getBrain().getLight()) : IntList.of()));

        if (ComputerCraftPolymerPatch.server != null && computer != null) {
            var mapId = ServerComputerExt.of(computer).getMapId();
            if (mapId < 0) {
                out.set(DataComponents.MAP_ID, new MapId(mapId));
            }
        }
    }
}
