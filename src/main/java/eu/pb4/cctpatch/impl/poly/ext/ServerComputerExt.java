package eu.pb4.cctpatch.impl.poly.ext;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.menu.ServerInputState;
import dan200.computercraft.shared.computer.terminal.NetworkedTerminal;
import eu.pb4.cctpatch.impl.poly.PocketComputerRenderer;

public interface ServerComputerExt {
    NetworkedTerminal getTerminalPublic();

    default int getMapId() {
        return 0;
    };

    default PocketComputerRenderer getPocketRenderer() {
        return null;
    }
    static ServerComputerExt of(ServerComputer state) {
        return (ServerComputerExt) state;
    }
}
