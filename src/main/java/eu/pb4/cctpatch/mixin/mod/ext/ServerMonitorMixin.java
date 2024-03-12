package eu.pb4.cctpatch.mixin.mod.ext;

import dan200.computercraft.shared.peripheral.monitor.ServerMonitor;
import eu.pb4.cctpatch.impl.poly.ext.ServerMonitorExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerMonitor.class)
public class ServerMonitorMixin implements ServerMonitorExt {
    @Shadow private int textScale;

    public int getTextScalePublic() {
        return this.textScale;
    }
}
