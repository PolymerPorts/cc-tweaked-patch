package eu.pb4.computercraftpatch.mixin.mod.block;

import dan200.computercraft.shared.common.HorizontalContainerBlock;
import dan200.computercraft.shared.computer.blocks.AbstractComputerBlock;
import dan200.computercraft.shared.peripheral.modem.wired.CableBlock;
import dan200.computercraft.shared.peripheral.modem.wired.WiredModemFullBlock;
import dan200.computercraft.shared.peripheral.modem.wireless.WirelessModemBlock;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlock;
import dan200.computercraft.shared.peripheral.speaker.SpeakerBlock;
import eu.pb4.computercraftpatch.impl.poly.AutoModeledPolymerBlock;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({AbstractComputerBlock.class, SpeakerBlock.class, HorizontalContainerBlock.class, MonitorBlock.class,
        WiredModemFullBlock.class, CableBlock.class, WirelessModemBlock.class, CableBlock.class })
public class GenericBlockMixin implements AutoModeledPolymerBlock {

}
