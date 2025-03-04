package eu.pb4.cctpatch.mixin.mod.block;

import dan200.computercraft.shared.common.HorizontalContainerBlock;
import dan200.computercraft.shared.computer.blocks.AbstractComputerBlock;
import dan200.computercraft.shared.computer.blocks.ComputerBlock;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlock;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemFullBlock;
import dan200.computercraft.shared.peripheral.modem.wireless.WirelessModemBlock;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlock;
import dan200.computercraft.shared.peripheral.redstone.RedstoneRelayBlock;
import dan200.computercraft.shared.peripheral.speaker.SpeakerBlock;
import eu.pb4.cctpatch.impl.poly.AutoModeledPolymerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ ComputerBlock.class, SpeakerBlock.class, HorizontalContainerBlock.class, MonitorBlock.class,
        WiredModemFullBlock.class, CableBlock.class, WirelessModemBlock.class, RedstoneRelayBlock.class })
public class GenericBlockModelMixin implements AutoModeledPolymerBlock {
}
