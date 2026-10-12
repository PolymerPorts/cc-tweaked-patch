package eu.pb4.cctpatch.impl.util;

import eu.pb4.polymer.common.api.PolymerCommonUtils;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;

// Client side cc-tweaks detection until we get a proper mod protocol api
// https://github.com/FabricMC/fabric-api/pull/4011
public class OptionalClientNetworking {
    private static final Identifier CHANNEL_ID = Identifier.fromNamespaceAndPath("computercraft", "monitor_client");

    public static boolean canSyncRawToClient(@Nullable ServerPlayer player) {
        return player != null && canSyncRawToClient(player.connection);
    }

    public static boolean canSyncRawToClient(ServerGamePacketListenerImpl listener) {
        return ServerPlayNetworking.canSend(listener, CHANNEL_ID);
    }

    public static boolean canSyncRawToClient(PacketContext context) {
        return canSyncRawToClient(PolymerCommonUtils.getPlayer(context));
    }
}
