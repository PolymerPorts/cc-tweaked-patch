package eu.pb4.cctpatch.mixin.poly;

import eu.pb4.cctpatch.impl.poly.gui.MapGui;
import eu.pb4.sgui.api.containerwrappers.AbstractWrapperMenu;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerEntityMixin {

    @Shadow public AbstractContainerMenu containerMenu;

    @Inject(method = "hurtServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Avatar;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z", shift = At.Shift.BEFORE))
    private void ccp_closeOnDamage(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (amount > 0 && this.containerMenu instanceof AbstractWrapperMenu handler && handler.getBackingGui() instanceof MapGui computerGui) {
            computerGui.close();
        }
    }
}
