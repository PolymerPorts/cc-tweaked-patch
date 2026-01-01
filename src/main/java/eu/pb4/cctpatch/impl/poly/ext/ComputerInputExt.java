package eu.pb4.cctpatch.impl.poly.ext;

import dan200.computercraft.core.input.ComputerInput;
import dan200.computercraft.core.input.UserComputerInput;

public interface ComputerInputExt {
    boolean isKeyDown(int key);

    static ComputerInputExt of(ComputerInput state) {
        return (ComputerInputExt) state;
    }
}
