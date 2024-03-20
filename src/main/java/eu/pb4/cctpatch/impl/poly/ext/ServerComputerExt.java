package eu.pb4.cctpatch.impl.poly.ext;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.menu.ServerInputState;
import dan200.computercraft.shared.computer.terminal.NetworkedTerminal;

public interface ServerComputerExt {
    NetworkedTerminal getTerminalPublic();

    default int getMapId() {
        return 0;
    };
    static ServerComputerExt of(ServerComputer state) {
        return (ServerComputerExt) state;
    }
}
