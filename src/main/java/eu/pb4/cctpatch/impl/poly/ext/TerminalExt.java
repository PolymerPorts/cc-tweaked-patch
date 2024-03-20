package eu.pb4.cctpatch.impl.poly.ext;

import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.computer.core.ServerComputer;
import eu.pb4.cctpatch.impl.poly.TerminalRenderer;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;

public interface TerminalExt {
    TerminalRenderer getRenderer();
    TerminalRenderer getMiniRenderer();

    static TerminalExt of(Terminal terminal) {
        return (TerminalExt) terminal;
    }

    static TerminalExt of(ServerComputer computer) {
        return of(((ServerComputerExt) computer).getTerminalPublic());
    }
}
