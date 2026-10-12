package eu.pb4.cctpatch.impl.poly.item;

import eu.pb4.cctpatch.impl.util.OptionalClientNetworking;
import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;

public record PolyBaseItem(Item item) implements PolymerItem {
    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        if (OptionalClientNetworking.canSyncRawToClient(context)) return itemStack.getItem();
        return Items.TRIAL_KEY;
    }

    @Override
    public boolean isPolymerBlockInteraction(BlockState state, ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, BlockHitResult blockHitResult, InteractionResult actionResult) {
        return item instanceof BlockItem;
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayer player, InteractionHand hand, ItemStack stack, ServerLevel world, BlockHitResult blockHitResult) {
        return item instanceof BlockItem;
    }

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context, HolderLookup.Provider lookup) {
        if (OptionalClientNetworking.canSyncRawToClient(context)) return itemStack;
        return PolymerItem.super.getPolymerItemStack(itemStack, tooltipType, context, lookup);
    }

    @Override
    public boolean canSyncRawToClient(PacketContext context) {
        return OptionalClientNetworking.canSyncRawToClient(context);
    }
}
