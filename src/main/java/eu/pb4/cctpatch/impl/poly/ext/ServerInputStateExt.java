package eu.pb4.cctpatch.impl.poly.ext;

import dan200.computercraft.shared.computer.menu.ServerInputState;

public interface ServerInputStateExt {
    boolean isKeyDown(int key);


    static ServerInputStateExt of(ServerInputState<?> state) {
        return (ServerInputStateExt) state;
    }
}
