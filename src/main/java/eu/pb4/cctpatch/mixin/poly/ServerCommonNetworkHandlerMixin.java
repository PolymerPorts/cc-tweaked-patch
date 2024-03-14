package eu.pb4.cctpatch.mixin.poly;

import eu.pb4.cctpatch.impl.poly.gui.MapGui;
import eu.pb4.sgui.virtual.VirtualScreenHandlerInterface;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.server.network.PlayerAssociatedNetworkHandler;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonNetworkHandler.class)
public class ServerCommonNetworkHandlerMixin {
    @Inject(method = "send", at = @At("HEAD"), cancellable = true)
    private void onPacketSent(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        if (this instanceof PlayerAssociatedNetworkHandler pl && pl.getPlayer().currentScreenHandler instanceof VirtualScreenHandlerInterface handler && handler.getGui() instanceof MapGui computerGui) {
            if (computerGui.preventPacket(packet)) {
                ci.cancel();
            }
        }
    }
}
