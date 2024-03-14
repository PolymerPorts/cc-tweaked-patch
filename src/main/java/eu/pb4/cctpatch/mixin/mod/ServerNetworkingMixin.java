package eu.pb4.cctpatch.mixin.mod;

import dan200.computercraft.shared.network.NetworkMessage;
import dan200.computercraft.shared.network.client.ClientNetworkContext;
import dan200.computercraft.shared.network.server.ServerNetworking;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
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
    public static void sendToPlayer(NetworkMessage<ClientNetworkContext> message, ServerPlayerEntity player) {
    }

    @Overwrite
    public static void sendToPlayers(NetworkMessage<ClientNetworkContext> message, Collection<ServerPlayerEntity> players) {
    }

    @Overwrite

    public static void sendToAllPlayers(NetworkMessage<ClientNetworkContext> message, MinecraftServer server) {
    }

    @Overwrite
    public static void sendToAllAround(NetworkMessage<ClientNetworkContext> message, ServerWorld level, Vec3d pos, float distance) {
    }

    @Overwrite
    public static void sendToAllTracking(NetworkMessage<ClientNetworkContext> message, WorldChunk chunk) {
    }
}
