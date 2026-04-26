package eu.pb4.cctpatch.mixin.mod;

import dan200.computercraft.shared.network.NetworkMessage;
import dan200.computercraft.shared.network.client.ClientNetworkContext;
import dan200.computercraft.shared.network.server.ServerNetworking;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Collection;

/**
 * Nuke it!
 */
@SuppressWarnings("OverwriteAuthorRequired")
@Mixin(value = ServerNetworking.class, remap = false)
public class ServerNetworkingMixin {

    @Overwrite
    public static void sendToPlayer(NetworkMessage<ClientNetworkContext> message, ServerPlayer player) {
    }

    @Overwrite
    public static void sendToPlayers(NetworkMessage<ClientNetworkContext> message, Collection<ServerPlayer> players) {
    }

    @Overwrite

    public static void sendToAllPlayers(NetworkMessage<ClientNetworkContext> message, MinecraftServer server) {
    }

    @Overwrite
    public static void sendToAllAround(NetworkMessage<ClientNetworkContext> message, ServerLevel level, Vec3 pos, float distance) {
    }

    @Overwrite
    public static void sendToAllTracking(NetworkMessage<ClientNetworkContext> message, LevelChunk chunk) {
    }
}
