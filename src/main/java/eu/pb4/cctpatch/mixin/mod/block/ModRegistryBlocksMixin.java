package eu.pb4.cctpatch.mixin.mod.block;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.platform.RegistrationHelper;
import eu.pb4.cctpatch.impl.poly.model.generic.BlockStateModelManager;
import eu.pb4.cctpatch.impl.util.WrappingRegistrationHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModRegistry.Blocks.class)
public class ModRegistryBlocksMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Ldan200/computercraft/shared/platform/PlatformHelper;createRegistrationHelper(Lnet/minecraft/registry/RegistryKey;)Ldan200/computercraft/shared/platform/RegistrationHelper;"))
    private static RegistrationHelper<Block> passBlocks(RegistrationHelper<Block> original) {
        return new WrappingRegistrationHelper<>(original, BlockStateModelManager::addBlock);
    }

    @ModifyReturnValue(method = { "properties", "turtleProperties", "modemProperties" }, at = @At("RETURN"))
    private static AbstractBlock.Settings changeProperties(AbstractBlock.Settings original) {
        return original.nonOpaque();
    }

}
