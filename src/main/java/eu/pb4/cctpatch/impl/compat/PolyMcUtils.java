package eu.pb4.cctpatch.impl.compat;

import io.github.theepicblock.polymc.PolyMc;
import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.misc.PolyMapProvider;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.screen.ScreenHandlerType;

import java.util.ArrayList;
import java.util.List;

public class PolyMcUtils {
    public static final List<ScreenHandlerType<?>> IGNORE_PLS = new ArrayList<>();
    public static void addScreenHandlerBypass(ScreenHandlerType<?> type) {
        IGNORE_PLS.add(type);
    }
}
