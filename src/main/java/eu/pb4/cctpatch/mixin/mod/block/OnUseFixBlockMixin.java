package eu.pb4.cctpatch.mixin.mod.block;

import dan200.computercraft.shared.common.HorizontalContainerBlock;
import dan200.computercraft.shared.computer.blocks.AbstractComputerBlock;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlock;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemFullBlock;
import dan200.computercraft.shared.peripheral.modem.wireless.WirelessModemBlock;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlock;
import dan200.computercraft.shared.peripheral.speaker.SpeakerBlock;
import eu.pb4.cctpatch.impl.poly.AutoModeledPolymerBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({AbstractComputerBlock.class, HorizontalContainerBlock.class, MonitorBlock.class,
        WiredModemFullBlock.class, CableBlock.class })
public class OnUseFixBlockMixin {
    /*@Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void test(BlockState state, World level, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (hand == Hand.OFF_HAND) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }*/
}
