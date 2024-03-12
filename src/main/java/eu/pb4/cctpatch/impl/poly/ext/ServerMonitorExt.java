package eu.pb4.cctpatch.impl.poly.ext;

import dan200.computercraft.shared.computer.menu.ServerInputState;
import dan200.computercraft.shared.peripheral.monitor.ServerMonitor;

public interface ServerMonitorExt {
    int getTextScalePublic();


    static ServerMonitorExt of(ServerMonitor state) {
        return (ServerMonitorExt) state;
    }
}
