package eu.pb4.computercraftpatch.mixin.poly;

import com.llamalad7.mixinextras.sugar.Local;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import eu.pb4.computercraftpatch.impl.poly.gui.ComputerGui;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "openHandledScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void openCustomScreen(NamedScreenHandlerFactory factory, CallbackInfoReturnable<OptionalInt> cir, @Local ScreenHandler handler) {
        if (handler instanceof AbstractComputerMenu wrapped) {
            new ComputerGui((ServerPlayerEntity) (Object) this, wrapped);
            cir.setReturnValue(OptionalInt.empty());
        }
    }
}
