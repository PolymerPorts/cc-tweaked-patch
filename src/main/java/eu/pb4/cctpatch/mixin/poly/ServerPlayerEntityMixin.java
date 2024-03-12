package eu.pb4.cctpatch.mixin.poly;

import com.llamalad7.mixinextras.sugar.Local;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.common.HeldItemMenu;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dan200.computercraft.shared.media.items.PrintoutItem;
import dan200.computercraft.shared.peripheral.printer.PrinterMenu;
import eu.pb4.cctpatch.impl.poly.gui.ComputerGui;
import eu.pb4.cctpatch.impl.poly.gui.PrintedPageGui;
import eu.pb4.cctpatch.impl.poly.gui.PrinterInventoryGui;
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
        } else if (handler instanceof PrinterMenu wrapped) {
            new PrinterInventoryGui((ServerPlayerEntity) (Object) this, wrapped);
            cir.setReturnValue(OptionalInt.empty());
        } else if (handler instanceof HeldItemMenu menu && menu.getStack().getItem() instanceof PrintoutItem) {
            new PrintedPageGui((ServerPlayerEntity) (Object) this, menu.getStack());
            cir.setReturnValue(OptionalInt.empty());
        }
    }
}
