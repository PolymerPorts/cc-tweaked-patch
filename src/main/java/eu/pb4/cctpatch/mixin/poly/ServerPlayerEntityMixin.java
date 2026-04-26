package eu.pb4.cctpatch.mixin.poly;

import com.llamalad7.mixinextras.sugar.Local;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dan200.computercraft.shared.media.PrintoutMenu;
import dan200.computercraft.shared.media.items.PrintoutItem;
import dan200.computercraft.shared.peripheral.diskdrive.DiskDriveMenu;
import dan200.computercraft.shared.peripheral.printer.PrinterMenu;
import eu.pb4.cctpatch.impl.poly.gui.ComputerGui;
import eu.pb4.cctpatch.impl.poly.gui.DiskDriveInventoryGui;
import eu.pb4.cctpatch.impl.poly.gui.PrintedPageGui;
import eu.pb4.cctpatch.impl.poly.gui.PrinterInventoryGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Mixin(value = ServerPlayer.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "openMenu", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;)V", shift = At.Shift.BEFORE), cancellable = true)
    private void openCustomScreen(MenuProvider factory, CallbackInfoReturnable<OptionalInt> cir, @Local AbstractContainerMenu handler) {
        if (handler instanceof AbstractComputerMenu wrapped) {
            new ComputerGui((ServerPlayer) (Object) this, wrapped);
            cir.setReturnValue(OptionalInt.empty());
        } else if (handler instanceof PrinterMenu wrapped) {
            new PrinterInventoryGui((ServerPlayer) (Object) this, wrapped);
            cir.setReturnValue(OptionalInt.empty());
        } else if (handler instanceof PrintoutMenu menu && menu.getPrintout().getItem() instanceof PrintoutItem) {
            new PrintedPageGui((ServerPlayer) (Object) this, menu.getPrintout());
            cir.setReturnValue(OptionalInt.empty());
        } else if (handler instanceof DiskDriveMenu menu) {
            new DiskDriveInventoryGui((ServerPlayer) (Object) this, menu);
            cir.setReturnValue(OptionalInt.empty());
        }
    }
}
