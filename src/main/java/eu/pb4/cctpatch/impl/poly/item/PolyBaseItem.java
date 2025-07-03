package eu.pb4.cctpatch.impl.poly.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import xyz.nucleoid.packettweaker.PacketContext;

public record PolyBaseItem(Item item) implements PolymerItem {
    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext packetContext) {
        return Items.TRIAL_KEY;
    }

    @Override
    public boolean isPolymerBlockInteraction(BlockState state, ServerPlayerEntity player, Hand hand, ItemStack stack, ServerWorld world, BlockHitResult blockHitResult, ActionResult actionResult) {
        return item instanceof BlockItem;
    }

    @Override
    public boolean isIgnoringBlockInteractionPlaySoundExceptedEntity(BlockState state, ServerPlayerEntity player, Hand hand, ItemStack stack, ServerWorld world, BlockHitResult blockHitResult) {
        return item instanceof BlockItem;
    }
}
