package eu.pb4.cctpatch.mixin.mod.command;

import com.mojang.brigadier.CommandDispatcher;
import dan200.computercraft.shared.command.CommandComputerCraft;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = CommandComputerCraft.class, remap = false)
public class CommandComputerCraftMixin {
    /**
     * @author Patbox
     * @reason tmp workaround
     */
    @Overwrite
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {

    }
}
