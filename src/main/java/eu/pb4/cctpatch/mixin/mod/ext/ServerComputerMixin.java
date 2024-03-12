package eu.pb4.cctpatch.mixin.mod.ext;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.terminal.NetworkedTerminal;
import dan200.computercraft.shared.peripheral.monitor.ServerMonitor;
import eu.pb4.cctpatch.impl.poly.ext.ServerComputerExt;
import eu.pb4.cctpatch.impl.poly.ext.ServerMonitorExt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerComputer.class)
public class ServerComputerMixin implements ServerComputerExt {
    @Shadow @Final private NetworkedTerminal terminal;

    @Override
    public NetworkedTerminal getTerminalPublic() {
        return this.terminal;
    }
}
