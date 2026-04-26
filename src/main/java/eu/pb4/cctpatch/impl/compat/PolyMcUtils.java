package eu.pb4.cctpatch.impl.compat;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.inventory.MenuType;

public class PolyMcUtils {
    public static final List<MenuType<?>> IGNORE_PLS = new ArrayList<>();
    public static void addScreenHandlerBypass(MenuType<?> type) {
        IGNORE_PLS.add(type);
    }
}
