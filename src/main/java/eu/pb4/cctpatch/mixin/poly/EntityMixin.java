package eu.pb4.cctpatch.mixin.poly;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public class EntityMixin {
    //@ModifyExpressionValue(method = "isLogicalSideForUpdatingMovement", at = @At(value = "INVOKE", target = ""))
}
