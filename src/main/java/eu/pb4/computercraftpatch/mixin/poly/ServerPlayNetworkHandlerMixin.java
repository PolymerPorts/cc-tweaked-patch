package eu.pb4.computercraftpatch.mixin.poly;

import eu.pb4.computercraftpatch.impl.poly.gui.MapGui;
import eu.pb4.sgui.virtual.VirtualScreenHandlerInterface;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageChain;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.Optional;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin extends ServerCommonNetworkHandler {
    @Shadow
    public ServerPlayerEntity player;

    public ServerPlayNetworkHandlerMixin(MinecraftServer server, ClientConnection connection, ConnectedClientData clientData) {
        super(server, connection, clientData);
    }

    @Shadow protected abstract Optional<LastSeenMessageList> validateMessage(LastSeenMessageList.Acknowledgment acknowledgment);

    @Shadow protected abstract SignedMessage getSignedMessage(ChatMessageC2SPacket packet, LastSeenMessageList lastSeenMessages) throws MessageChain.MessageChainException;

    @Shadow protected abstract void handleMessageChainException(MessageChain.MessageChainException exception);

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    private void ccp_onChat(ChatMessageC2SPacket packet, CallbackInfo ci) {
        if (this.player.currentScreenHandler instanceof VirtualScreenHandlerInterface handler && handler.getGui() instanceof MapGui computerGui) {
            this.player.server.execute(() -> {
                computerGui.onChatInput(packet.chatMessage());
            });
            ci.cancel();

            Optional<LastSeenMessageList> optional = this.validateMessage(packet.acknowledgment());
            if (optional.isPresent()) {
                this.server.submit(() -> {
                    try {
                        this.getSignedMessage(packet, (LastSeenMessageList)optional.get());
                    } catch (MessageChain.MessageChainException var6) {
                        this.handleMessageChainException(var6);
                    }
                });
            }
        }
    }

    @Inject(method = "onCommandExecution", at = @At("HEAD"), cancellable = true)
    private void ccp_onChat(CommandExecutionC2SPacket packet, CallbackInfo ci) {
        if (this.player.currentScreenHandler instanceof VirtualScreenHandlerInterface handler && handler.getGui() instanceof MapGui computerGui) {
            this.server.execute(() -> {
                computerGui.onCommandInput(packet.command());
            });
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    private void ccp_onMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if (this.player.currentScreenHandler instanceof VirtualScreenHandlerInterface handler && handler.getGui() instanceof MapGui computerGui) {
            if (packet.getPitch(0) != 0 || packet.getYaw(0) != 0) {
                this.sendPacket(new PlayerPositionLookS2CPacket(player.getX(), player.getY(), player.getZ(), 0, 0, EnumSet.noneOf(PositionFlag.class), 0));
            }
            this.server.execute(() -> {
                var xRot = packet.getPitch(computerGui.xRot);
                var yRot = packet.getYaw(computerGui.yRot);
                if (xRot != 0 || yRot != 0) {
                    computerGui.onCameraMove(yRot, xRot);
                }
            });
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerAction", at = @At("HEAD"), cancellable = true)
    private void ccp_onPlayerAction(PlayerActionC2SPacket packet, CallbackInfo ci) {
        if (this.player.currentScreenHandler instanceof VirtualScreenHandlerInterface handler && handler.getGui() instanceof MapGui computerGui) {
            this.server.execute(() -> {
                computerGui.onPlayerAction(packet.getAction(), packet.getDirection(), packet.getPos());
            });
            ci.cancel();
        }
    }

    @Inject(method = "onRequestCommandCompletions", at = @At("HEAD"), cancellable = true)
    private void ccp_onCustomSuggestion(RequestCommandCompletionsC2SPacket packet, CallbackInfo ci) {
        if (this.player.currentScreenHandler instanceof VirtualScreenHandlerInterface handler && handler.getGui() instanceof MapGui computerGui) {
            this.server.execute(() -> {
                computerGui.onCommandSuggestion(packet.getCompletionId(), packet.getPartialCommand());
            });
            ci.cancel();
        }
    }

    @Inject(method = "onPlayerInput", at = @At("HEAD"), cancellable = true)
    private void ccp_onVehicleMove(PlayerInputC2SPacket packet, CallbackInfo ci) {
        if (this.player.currentScreenHandler instanceof VirtualScreenHandlerInterface handler && handler.getGui() instanceof MapGui computerGui) {
            this.server.execute(() -> {
                computerGui.onPlayerInput(packet.getForward(), packet.getSideways(), packet.isJumping(), packet.isSneaking());
            });
            ci.cancel();
        }
    }

    @Inject(method = "onClientCommand", at = @At("HEAD"), cancellable = true)
    private void ccp_onVehicleMove(ClientCommandC2SPacket packet, CallbackInfo ci) {
        if (this.player.currentScreenHandler instanceof VirtualScreenHandlerInterface handler && handler.getGui() instanceof MapGui computerGui) {
            this.server.execute(() -> {
                computerGui.onPlayerCommand(packet.getEntityId(), packet.getMode(), packet.getMountJumpHeight());
            });
            ci.cancel();
        }
    }
}
