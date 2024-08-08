package eu.pb4.cctpatch.mixin.mod.item;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.shared.pocket.core.PocketBrain;
import dan200.computercraft.shared.pocket.core.PocketHolder;
import dan200.computercraft.shared.pocket.core.PocketServerComputer;
import eu.pb4.cctpatch.impl.poly.PocketComputerRenderer;
import eu.pb4.cctpatch.impl.poly.ext.ServerComputerExt;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PocketBrain.class, remap = false)
public abstract class PocketBrainMixin {
    @Shadow @Final private PocketServerComputer computer;

    @Shadow public abstract Entity getEntity();

    @Inject(method = "updateHolder", at = @At("TAIL"))
    private void onUpdateValues(PocketHolder newHolder, CallbackInfo ci) {
        ServerComputerExt.of(this.computer).getPocketRenderer().updateValues(this.getEntity());
    }
}
