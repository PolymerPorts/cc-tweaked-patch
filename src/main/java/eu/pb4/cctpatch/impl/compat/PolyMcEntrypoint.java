package eu.pb4.cctpatch.impl.compat;

import io.github.theepicblock.polymc.api.PolyRegistry;

public class PolyMcEntrypoint implements io.github.theepicblock.polymc.api.PolyMcEntrypoint {
    @Override
    public void registerPolys(PolyRegistry registry) {
        for (var x : PolyMcUtils.IGNORE_PLS) {
            registry.registerGuiPoly(x, (base, player, syncId) -> base);
        }
    }
}
