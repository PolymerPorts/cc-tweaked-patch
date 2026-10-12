package eu.pb4.cctpatch.mixin.mod;

import dan200.computercraft.shared.network.NetworkMessage;
import eu.pb4.cctpatch.impl.util.OptionalClientNetworking;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class ServerCommonPacketListenerImplMixin {
    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;)V", at = @At("HEAD"), cancellable = true)
    public void preventPackets(Packet<?> packet, @Nullable ChannelFutureListener listener, CallbackInfo ci) {
        if (!((Object) this instanceof ServerGamePacketListenerImpl impl)) {
            return;
        }
        if (packet instanceof ClientboundCustomPayloadPacket customPayload && customPayload.payload() instanceof NetworkMessage<?> && !OptionalClientNetworking.canSyncRawToClient(impl)) {
            ci.cancel();
        }
    }

}
