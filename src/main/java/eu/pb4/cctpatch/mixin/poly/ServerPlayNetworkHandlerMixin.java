package eu.pb4.cctpatch.mixin.poly;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import eu.pb4.cctpatch.impl.poly.gui.MapGui;
import eu.pb4.sgui.api.containerwrappers.AbstractWrapperMenu;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.SignedMessageChain;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.Set;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerPlayNetworkHandlerMixin extends ServerCommonPacketListenerImpl {
    @Shadow
    public ServerPlayer player;
    @Shadow
    private double firstGoodX;
    @Shadow
    private double firstGoodY;
    @Shadow
    private double firstGoodZ;

    public ServerPlayNetworkHandlerMixin(MinecraftServer server, Connection connection, CommonListenerCookie clientData) {
        super(server, connection, clientData);
    }

    @Shadow
    protected abstract PlayerChatMessage getSignedMessage(ServerboundChatPacket packet, LastSeenMessages lastSeenMessages) throws SignedMessageChain.DecodeException;

    @Shadow
    protected abstract void handleMessageDecodeFailure(SignedMessageChain.DecodeException exception);

    @Shadow
    protected abstract Optional<LastSeenMessages> unpackAndApplyLastSeen(LastSeenMessages.Update acknowledgment);

    @Shadow
    public abstract void resetPosition();

    @Inject(method = "handleChat", at = @At("HEAD"), cancellable = true)
    private void ccp_onChat(ServerboundChatPacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof AbstractWrapperMenu handler && handler.getBackingGui() instanceof MapGui computerGui) {
            this.player.level().getServer().execute(() -> {
                computerGui.onChatInput(packet.message());
            });
            ci.cancel();

            Optional<LastSeenMessages> optional = this.unpackAndApplyLastSeen(packet.lastSeenMessages());
            if (optional.isPresent()) {
                this.server.submit(() -> {
                    try {
                        this.getSignedMessage(packet, (LastSeenMessages) optional.get());
                    } catch (SignedMessageChain.DecodeException var6) {
                        this.handleMessageDecodeFailure(var6);
                    }
                });
            }
        }
    }

    @Inject(method = "handleChatCommand", at = @At("HEAD"), cancellable = true)
    private void ccp_onChat(ServerboundChatCommandPacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof AbstractWrapperMenu handler && handler.getBackingGui() instanceof MapGui computerGui) {
            this.server.execute(() -> {
                computerGui.onCommandInput(packet.command());
            });
            ci.cancel();
        }
    }

    @WrapWithCondition(method = "tickPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;absSnapTo(DDDFF)V"))
    private boolean ccp_allowMovement(ServerPlayer instance, double x, double y, double z, float p, float yaw) {
        if (this.player.containerMenu instanceof AbstractWrapperMenu handler && handler.getBackingGui() instanceof MapGui computerGui) {
            double l = instance.getX() - this.firstGoodX;
            double m = instance.getY() - this.firstGoodY;
            double n = instance.getZ() - this.firstGoodZ;
            this.player.level().getChunkSource().move(this.player);
            this.player.doCheckFallDamage(l, m, n, player.onGround());
            this.player.setOnGround(player.onGround());
            this.resetPosition();
            return false;
        }
        return true;
    }

    @Inject(method = "teleport(Lnet/minecraft/world/entity/PositionMoveRotation;Ljava/util/Set;)V", at = @At("HEAD"), cancellable = true)
    private void ccp_noTeleport(PositionMoveRotation pos, Set<Relative> flags, CallbackInfo ci) {
        if (this.player.containerMenu instanceof AbstractWrapperMenu handler && handler.getBackingGui() instanceof MapGui computerGui) {
            ci.cancel();
        }
    }

    @Inject(method = "handleMovePlayer", at = @At("HEAD"), cancellable = true)
    private void ccp_onMove(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof AbstractWrapperMenu handler && handler.getBackingGui() instanceof MapGui computerGui) {
            if (packet.getXRot(0) != 0 || packet.getYRot(0) != 0) {
                this.send(new ClientboundPlayerRotationPacket(0, false, 0, false));
            }
            this.server.execute(() -> {
                var xRot = packet.getXRot(computerGui.xRot);
                var yRot = packet.getYRot(computerGui.yRot);
                if (xRot != 0 || yRot != 0) {
                    computerGui.onCameraMove(yRot, xRot);
                }
            });
            ci.cancel();
        }
    }

    @Inject(method = "handlePlayerAction", at = @At("HEAD"), cancellable = true)
    private void ccp_onPlayerAction(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof AbstractWrapperMenu handler && handler.getBackingGui() instanceof MapGui computerGui) {
            this.server.execute(() -> {
                computerGui.onPlayerAction(packet.getAction(), packet.getDirection(), packet.getPos());
            });
            ci.cancel();
        }
    }

    @Inject(method = "handleCustomCommandSuggestions", at = @At("HEAD"), cancellable = true)
    private void ccp_onCustomSuggestion(ServerboundCommandSuggestionPacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof AbstractWrapperMenu handler && handler.getBackingGui() instanceof MapGui computerGui) {
            this.server.execute(() -> {
                computerGui.onCommandSuggestion(packet.getId(), packet.getCommand());
            });
            ci.cancel();
        }
    }

    @Inject(method = "handleSpectatorAction", at = @At("HEAD"), cancellable = true)
    private void ccp_onSpectatorAction(ServerboundSpectatorActionPacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof AbstractWrapperMenu handler && handler.getBackingGui() instanceof MapGui computerGui) {
            this.server.execute(() -> {
                computerGui.onSpectatorAction();
            });
            ci.cancel();
        }
    }

    @Inject(method = "handlePlayerInput", at = @At("HEAD"), cancellable = true)
    private void ccp_onVehicleMove(ServerboundPlayerInputPacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof AbstractWrapperMenu handler && handler.getBackingGui() instanceof MapGui computerGui) {
            this.server.execute(() -> {
                computerGui.onPlayerInput(packet.input());
            });
            ci.cancel();
        }
    }

    @Inject(method = "handlePlayerCommand", at = @At("HEAD"), cancellable = true)
    private void ccp_onVehicleMove(ServerboundPlayerCommandPacket packet, CallbackInfo ci) {
        if (this.player.containerMenu instanceof AbstractWrapperMenu handler && handler.getBackingGui() instanceof MapGui computerGui) {
            this.server.execute(() -> {
                computerGui.onPlayerCommand(packet.getId(), packet.getAction(), packet.getData());
            });
            ci.cancel();
        }
    }
}
