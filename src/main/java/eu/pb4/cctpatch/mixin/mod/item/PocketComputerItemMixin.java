package eu.pb4.cctpatch.mixin.mod.item;

import dan200.computercraft.shared.common.IColouredItem;
import dan200.computercraft.shared.computer.core.ComputerState;
import dan200.computercraft.shared.computer.items.AbstractComputerItem;
import dan200.computercraft.shared.media.items.PrintoutItem;
import dan200.computercraft.shared.media.items.TreasureDiskItem;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlockItem;
import dan200.computercraft.shared.pocket.core.PocketServerComputer;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import eu.pb4.cctpatch.impl.ComputerCraftPolymerPatch;
import eu.pb4.cctpatch.impl.poly.ext.ServerComputerExt;
import eu.pb4.cctpatch.impl.poly.model.PocketComputerModel;
import eu.pb4.factorytools.api.item.AutoModeledPolymerItem;
import eu.pb4.factorytools.api.item.FireworkStarColoredItem;
import eu.pb4.factorytools.api.item.RegistryCallbackItem;
import eu.pb4.factorytools.api.resourcepack.BaseItemProvider;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ PocketComputerItem.class })
public abstract class PocketComputerItemMixin implements RegistryCallbackItem, PolymerItem {
    @Unique
    private PocketComputerModel model;

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.model.getModelData(itemStack).item();
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.model.getModelData(itemStack).value();
    }
    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipContext context, @Nullable ServerPlayerEntity player) {
        var stack = PolymerItemUtils.createItemStack(itemStack, context, player);
        var data = this.model.getModelData(itemStack);
        var color = IColouredItem.getColourBasic(itemStack);
        if (data.item() == Items.FIREWORK_STAR) {
            var ex = new NbtCompound();
            var c = new NbtIntArray(new int[]{color});
            ex.put("Colors", c);
            stack.getOrCreateNbt().put("Explosion", ex);
        } else if (data.item() == Items.FILLED_MAP) {
            var display = stack.getOrCreateNbt().getCompound("display");
            display.putInt("MapColor", color != -1 ? color : 0xffffff);

            if (player != null) {
                var computer = PocketComputerItem.getServerComputer(player.server, itemStack);
                if (computer != null) {
                    stack.getOrCreateNbt().putInt("map", ServerComputerExt.of(computer).getMapId());
                }
            }
        }
        return stack;
    }

    @Override
    public void onRegistered(Identifier selfId) {
        this.model = PocketComputerModel.from(selfId);
    }
}
