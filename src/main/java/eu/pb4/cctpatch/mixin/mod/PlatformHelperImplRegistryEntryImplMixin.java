package eu.pb4.cctpatch.mixin.mod;

import eu.pb4.cctpatch.impl.compat.PolyMcUtils;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.api.other.PolymerScreenHandlerUtils;
import io.github.theepicblock.polymc.PolyMc;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dan200/computercraft/shared/platform/PlatformHelperImpl$RegistryEntryImpl")
public class PlatformHelperImplRegistryEntryImplMixin {
    @Shadow @Nullable
    private Object instance;

    @Inject(method = "register(Lnet/minecraft/registry/Registry;)V", at = @At("TAIL"))
    private void onRegister(Registry<?> registry, CallbackInfo ci) {
        if (registry == Registries.BLOCK_ENTITY_TYPE) {
            PolymerBlockUtils.registerBlockEntity((BlockEntityType<?>) this.instance);
        } else if (registry == Registries.SCREEN_HANDLER) {
            PolymerScreenHandlerUtils.registerType((ScreenHandlerType<?>) this.instance);
            PolyMcUtils.addScreenHandlerBypass((ScreenHandlerType<?>) this.instance);
        }
    }

}
