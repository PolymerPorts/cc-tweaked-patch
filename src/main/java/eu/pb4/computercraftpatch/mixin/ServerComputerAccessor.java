package eu.pb4.computercraftpatch.mixin;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.terminal.NetworkedTerminal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerComputer.class)
public interface ServerComputerAccessor {
    @Accessor
    NetworkedTerminal getTerminal();
}
