package eu.pb4.cctpatch.mixin.mod.item;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.shared.pocket.core.PocketServerComputer;
import eu.pb4.cctpatch.impl.poly.PocketComputerRenderer;
import eu.pb4.cctpatch.impl.poly.ext.ServerComputerExt;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PocketServerComputer.class, remap = false)
public abstract class PocketServerComputerMixin implements ServerComputerExt {
    @Shadow @Nullable
    private Entity entity;

    @Unique
    private final PocketComputerRenderer renderer = new PocketComputerRenderer((PocketServerComputer) (Object) this);

    @Inject(method = "onRemoved", at = @At(value = "INVOKE", target = "Ldan200/computercraft/shared/computer/core/ServerComputer;onRemoved()V"))
    private void onRemovedCall(CallbackInfo ci) {
        this.renderer.onRemoved(this.entity);
    }

    @Inject(method = "onTerminalChanged", at = @At(value = "INVOKE", target = "Ldan200/computercraft/shared/network/server/ServerNetworking;sendToPlayer(Ldan200/computercraft/shared/network/NetworkMessage;Lnet/minecraft/server/network/ServerPlayerEntity;)V"))
    private void onTerminalChangedCall(CallbackInfo ci) {
        this.renderer.onRendererChanged(this.entity);
    }

    @Inject(method = "updateValues", at = @At("HEAD"))
    private void onUpdateValues(Entity entity, ItemStack stack, IPocketUpgrade upgrade, CallbackInfo ci) {
        this.renderer.updateValues(entity);
    }

    @Inject(method = "tickServer", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        this.renderer.tick(this.entity);
    }
    @Override
    public int getMapId() {
        return this.renderer.id();
    }
}
