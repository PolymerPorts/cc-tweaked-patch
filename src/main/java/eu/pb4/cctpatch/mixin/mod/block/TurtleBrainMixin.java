package eu.pb4.cctpatch.mixin.mod.block;

import com.llamalad7.mixinextras.sugar.Local;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import eu.pb4.cctpatch.impl.poly.model.TurtleModel;
import eu.pb4.polymer.virtualentity.api.attachment.BlockAwareAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.BlockBoundAttachment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TurtleBrain.class)
public abstract class TurtleBrainMixin {
    @Shadow public abstract BlockPos getPosition();

    @Shadow public abstract Level getLevel();

    @Unique
    private TurtleModel model;

    @Inject(method = "teleportTo", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void shiftModelPosition(Level world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (this.model != null) {
            var old = this.model.getAttachment();
            BlockBoundAttachment.of(this.model, (ServerLevel) world, pos, BlockAwareAttachment.get(this.model).getBlockState());
            old.destroy();
        }
    }

    @Inject(method = "update", at = @At("TAIL"), remap = false)
    private void onUpdate(CallbackInfo ci) {
        if (this.model == null) {
            var x = BlockAwareAttachment.get(this.getLevel(), this.getPosition());
            if (x == null) {
                return;
            }
            if (x.holder() instanceof TurtleModel model) {
                this.model = model;
            } else {
                return;
            }
        }

        this.model.update((TurtleBrain) (Object) this);
        this.model.tick();
    }

}
