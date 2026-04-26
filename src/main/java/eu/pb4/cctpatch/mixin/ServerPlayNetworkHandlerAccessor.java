package eu.pb4.cctpatch.mixin;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerGamePacketListenerImpl.class)
public interface ServerPlayNetworkHandlerAccessor {
    @Accessor
    void setAboveGroundVehicleTickCount(int ticks);

    @Accessor
    void setAboveGroundTickCount(int ticks);
}
