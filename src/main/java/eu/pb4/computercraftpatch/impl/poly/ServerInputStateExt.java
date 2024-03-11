package eu.pb4.computercraftpatch.impl.poly;

import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.computer.menu.ServerInputState;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;

public interface ServerInputStateExt {
    boolean isKeyDown(int key);


    static ServerInputStateExt of(ServerInputState<?> state) {
        return (ServerInputStateExt) state;
    }
}
