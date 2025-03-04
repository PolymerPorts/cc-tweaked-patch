package eu.pb4.cctpatch.mixin.mod.item;

import dan200.computercraft.shared.turtle.items.TurtleItem;
import eu.pb4.factorytools.api.item.AutoModeledPolymerItem;
import eu.pb4.factorytools.api.item.RegistryCallbackItem;
import eu.pb4.factorytools.api.resourcepack.BaseItemProvider;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({ TurtleItem.class })
public abstract class TurtleItemMixin extends Item implements RegistryCallbackItem, PolymerItem, AutoModeledPolymerItem {
    @Unique
    private PolymerModelData defaultModel;
    @Unique
    private PolymerModelData dyedModel;

    public TurtleItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public Item getPolymerItem() {
        return getModelData(this.getDefaultStack()).item();
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return getModelData(itemStack).item();
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return getModelData(itemStack).value();
    }

    @Override
    public int getPolymerCustomModelData() {
        return getModelData(this.getDefaultStack()).value();
    }

    @Unique
    private PolymerModelData getModelData(ItemStack itemStack) {
        if (DyedColorComponent.getColor(itemStack, -1) != -1) {
            return dyedModel;
        }

        return defaultModel;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType context, RegistryWrapper.WrapperLookup lookup, @Nullable ServerPlayerEntity player) {
        var stack = PolymerItemUtils.createItemStack(itemStack, context, lookup, player);
        var color = DyedColorComponent.getColor(itemStack, -1);
        if (color != -1) {
            stack.set(DataComponentTypes.FIREWORK_EXPLOSION, new FireworkExplosionComponent(FireworkExplosionComponent.Type.BURST, IntList.of(color), IntList.of(), false, false));
        }
        return stack;
    }

    @Override
    public void onRegistered(Identifier selfId) {
        this.defaultModel = PolymerResourcePackUtils.requestModel(BaseItemProvider.requestItem(),
                Identifier.of(selfId.getNamespace(), "item/" + selfId.getPath()));
        this.dyedModel = PolymerResourcePackUtils.requestModel(Items.FIREWORK_STAR, Identifier.of("computercraft:block/turtle_colour"));
    }
}
