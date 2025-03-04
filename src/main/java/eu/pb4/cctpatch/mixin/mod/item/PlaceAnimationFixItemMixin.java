package eu.pb4.cctpatch.mixin.mod.item;

import dan200.computercraft.shared.computer.items.ComputerItem;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlockItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ComputerItem.class, CableBlockItem.class})
public abstract class PlaceAnimationFixItemMixin extends BlockItem {
    public PlaceAnimationFixItemMixin(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var x = super.useOnBlock(context);
        if (x == ActionResult.CONSUME) {
            if (context.getPlayer() instanceof ServerPlayerEntity player) {
                var pos = Vec3d.ofCenter(context.getBlockPos().offset(context.getSide()));
                var blockSoundGroup = this.getBlock().getDefaultState().getSoundGroup();
                player.networkHandler.sendPacket(new PlaySoundS2CPacket(Registries.SOUND_EVENT.getEntry(this.getPlaceSound(this.getBlock().getDefaultState())), SoundCategory.BLOCKS, pos.x, pos.y, pos.z, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F, context.getPlayer().getRandom().nextLong()));
            }
            return ActionResult.SUCCESS;
        }
        return x;
    }
}
