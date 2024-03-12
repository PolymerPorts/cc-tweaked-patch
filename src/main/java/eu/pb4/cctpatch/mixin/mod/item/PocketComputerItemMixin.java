package eu.pb4.cctpatch.mixin.mod.item;

import dan200.computercraft.shared.common.IColouredItem;
import dan200.computercraft.shared.computer.core.ComputerState;
import dan200.computercraft.shared.computer.items.AbstractComputerItem;
import dan200.computercraft.shared.media.items.PrintoutItem;
import dan200.computercraft.shared.media.items.TreasureDiskItem;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlockItem;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import eu.pb4.cctpatch.impl.ComputerCraftPolymerPatch;
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
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ PocketComputerItem.class })
public class PocketComputerItemMixin implements RegistryCallbackItem, PolymerItem {
    @Unique
    private PolymerModelData defaultModel;
    @Unique
    private PolymerModelData onModel;
    @Unique
    private PolymerModelData blinkingModel;
    @Unique
    private PolymerModelData dyedModel;
    @Unique
    private PolymerModelData onDyedModel;
    @Unique
    private PolymerModelData blinkingDyedModel;

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return getModelData(itemStack).item();
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return getModelData(itemStack).value();
    }

    @Unique
    private PolymerModelData getModelData(ItemStack itemStack) {
        var computer = PocketComputerItem.getServerComputer(ComputerCraftPolymerPatch.server, itemStack);
        var state = computer != null ? computer.getState() : ComputerState.OFF;
        if (IColouredItem.getColourBasic(itemStack) != -1) {
            return switch (state) {
                case OFF -> dyedModel;
                case ON -> onDyedModel;
                case BLINKING -> blinkingDyedModel;
            };
        }

        return switch (state) {
            case OFF -> defaultModel;
            case ON -> onModel;
            case BLINKING -> blinkingModel;
        };
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipContext context, @Nullable ServerPlayerEntity player) {
        var stack = PolymerItemUtils.createItemStack(itemStack, context, player);
        var color = IColouredItem.getColourBasic(itemStack);
        if (color != -1) {
            var ex = new NbtCompound();
            var c = new NbtIntArray(new int[]{color});
            ex.put("Colors", c);
            stack.getOrCreateNbt().put("Explosion", ex);
        }
        return stack;
    }

    @Override
    public void onRegistered(Identifier selfId) {
        this.defaultModel = PolymerResourcePackUtils.requestModel(BaseItemProvider.requestItem(), selfId.withPrefixedPath("item/"));
        this.onModel = PolymerResourcePackUtils.requestModel(BaseItemProvider.requestItem(), selfId.withPrefixedPath("item/").withSuffixedPath("_on"));
        this.blinkingModel = PolymerResourcePackUtils.requestModel(BaseItemProvider.requestItem(), selfId.withPrefixedPath("item/").withSuffixedPath("_blinking"));
        this.dyedModel = PolymerResourcePackUtils.requestModel(Items.FIREWORK_STAR, new Identifier("computercraft:item/pocket_computer_colour"));
        this.onDyedModel = PolymerResourcePackUtils.requestModel(Items.FIREWORK_STAR, new Identifier("computercraft:item/pocket_computer_colour_on"));
        this.blinkingDyedModel = PolymerResourcePackUtils.requestModel(Items.FIREWORK_STAR, new Identifier("computercraft:item/pocket_computer_colour_blinking"));
    }
}
