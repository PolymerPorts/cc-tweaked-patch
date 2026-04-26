package eu.pb4.cctpatch.mixin.poly;

import eu.pb4.cctpatch.impl.poly.gui.MapGui;
import eu.pb4.sgui.api.containerwrappers.AbstractWrapperMenu;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerPlayerConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonNetworkHandlerMixin {
    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;)V", at = @At("HEAD"), cancellable = true)
    private void onPacketSent(Packet<?> packet, ChannelFutureListener channelFutureListener, CallbackInfo ci) {
        if (this instanceof ServerPlayerConnection pl && pl.getPlayer().containerMenu instanceof AbstractWrapperMenu handler && handler.getBackingGui() instanceof MapGui computerGui) {
            if (computerGui.preventPacket(packet)) {
                ci.cancel();
            }
        }
    }
}
