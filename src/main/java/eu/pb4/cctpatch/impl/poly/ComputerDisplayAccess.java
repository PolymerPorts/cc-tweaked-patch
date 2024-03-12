package eu.pb4.cctpatch.impl.poly;

import dan200.computercraft.shared.computer.core.ServerComputer;

public interface ComputerDisplayAccess {
    ServerComputer getComputer();

    //@Nullable
    //TileComputerBase getBlockEntity();

    //boolean canStayOpen(ServerPlayer player);

    interface Getter {
        ComputerDisplayAccess getDisplayAccess();
    }
}
