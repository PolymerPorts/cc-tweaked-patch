package eu.pb4.cctpatch.mixin.mod.item;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.shared.pocket.core.PocketBrain;
import dan200.computercraft.shared.pocket.core.PocketHolder;
import dan200.computercraft.shared.pocket.core.PocketServerComputer;
import eu.pb4.cctpatch.impl.poly.PocketComputerRenderer;
import eu.pb4.cctpatch.impl.poly.ext.ServerComputerExt;
import eu.pb4.cctpatch.mixin.PocketBrainAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PocketServerComputer.class, remap = false)
public abstract class PocketServerComputerMixin implements ServerComputerExt {
    @Shadow @Final private PocketBrain brain;
    @Unique
    private final PocketComputerRenderer renderer = new PocketComputerRenderer((PocketServerComputer) (Object) this);

    @Inject(method = "onRemoved", at = @At(value = "INVOKE", target = "Ldan200/computercraft/shared/computer/core/ServerComputer;onRemoved()V"))
    private void onRemovedCall(CallbackInfo ci) {
        this.renderer.onRemoved(((PocketBrainAccessor) (Object) this.brain).callHolder() instanceof PocketHolder.EntityHolder ent ? ent.entity() : null);
    }
    @Inject(method = "tickServer", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        this.renderer.tick(((PocketBrainAccessor) (Object) this.brain).callHolder() instanceof PocketHolder.EntityHolder ent ? ent.entity() : null);
    }

    @Override
    public PocketComputerRenderer getPocketRenderer() {
        return this.renderer;
    }

    @Override
    public int getMapId() {
        return this.renderer.id();
    }
}
