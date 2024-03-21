package eu.pb4.cctpatch.mixin;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayNetworkHandler.class)
public interface ServerPlayNetworkHandlerAccessor {
    @Accessor
    void setVehicleFloatingTicks(int ticks);

    @Accessor
    void setFloatingTicks(int ticks);
}
