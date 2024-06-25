package eu.pb4.cctpatch.mixin.mod.item;

import dan200.computercraft.core.util.Colour;
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
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.MapColorComponent;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import xyz.nucleoid.packettweaker.PacketContext;

@Mixin({ PocketComputerItem.class })
public abstract class PocketComputerItemMixin implements RegistryCallbackItem, PolymerItem {
    @Unique
    private PocketComputerModel model;

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.model.getModelData(itemStack, isPlayerboundPacket()).item();
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.model.getModelData(itemStack, isPlayerboundPacket()).value();
    }
    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType context, RegistryWrapper.WrapperLookup lookup, @Nullable ServerPlayerEntity player) {
        var stack = PolymerItemUtils.createItemStack(itemStack, context, lookup, player);
        var data = this.model.getModelData(itemStack, isPlayerboundPacket());
        var color = DyedColorComponent.getColor(stack, Colour.WHITE.getARGB());
        if (data.item() == Items.FIREWORK_STAR) {
            stack.set(DataComponentTypes.FIREWORK_EXPLOSION, new FireworkExplosionComponent(FireworkExplosionComponent.Type.BURST, IntList.of(color), IntList.of(), false, false));
        } else if (data.item() == Items.FILLED_MAP) {
            stack.set(DataComponentTypes.MAP_COLOR, new MapColorComponent(color));

            if (player != null) {
                var computer = PocketComputerItem.getServerComputer(player.server, itemStack);
                if (computer != null) {
                    stack.set(DataComponentTypes.MAP_ID, new MapIdComponent(ServerComputerExt.of(computer).getMapId()));
                }
            }
        }
        return stack;
    }

    @Unique
    private boolean isPlayerboundPacket() {
        var ctx = PacketContext.get();
        return ctx.getEncodedPacket() instanceof InventoryS2CPacket || ctx.getEncodedPacket() instanceof ScreenHandlerSlotUpdateS2CPacket;
    }

    @Override
    public void onRegistered(Identifier selfId) {
        this.model = PocketComputerModel.from(selfId);
    }
}
