package eu.pb4.cctpatch.mixin.poly;

import com.llamalad7.mixinextras.sugar.Local;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dan200.computercraft.shared.media.PrintoutMenu;
import dan200.computercraft.shared.media.items.PrintoutItem;
import dan200.computercraft.shared.peripheral.diskdrive.DiskDriveMenu;
import dan200.computercraft.shared.peripheral.printer.PrinterMenu;
import eu.pb4.cctpatch.impl.poly.gui.ComputerGui;
import eu.pb4.cctpatch.impl.poly.gui.DiskDriveInventoryGui;
import eu.pb4.cctpatch.impl.poly.gui.PrintedPageGui;
import eu.pb4.cctpatch.impl.poly.gui.PrinterInventoryGui;
import eu.pb4.cctpatch.impl.util.OptionalClientNetworking;
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
        ServerPlayer this_ = (ServerPlayer) (Object) this;
        if (OptionalClientNetworking.canSyncRawToClient(this_)) return;
        if (handler instanceof AbstractComputerMenu wrapped) {
            new ComputerGui(this_, wrapped);
            cir.setReturnValue(OptionalInt.empty());
        } else if (handler instanceof PrinterMenu wrapped) {
            new PrinterInventoryGui(this_, wrapped);
            cir.setReturnValue(OptionalInt.empty());
        } else if (handler instanceof PrintoutMenu menu && menu.getPrintout().getItem() instanceof PrintoutItem) {
            new PrintedPageGui(this_, menu.getPrintout());
            cir.setReturnValue(OptionalInt.empty());
        } else if (handler instanceof DiskDriveMenu menu) {
            new DiskDriveInventoryGui(this_, menu);
            cir.setReturnValue(OptionalInt.empty());
        }
    }
}
