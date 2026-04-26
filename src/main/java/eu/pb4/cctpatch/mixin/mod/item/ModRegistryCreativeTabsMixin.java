package eu.pb4.cctpatch.mixin.mod.item;

import dan200.computercraft.shared.platform.PlatformHelper;
import dan200.computercraft.shared.platform.RegistrationHelper;
import eu.pb4.cctpatch.impl.util.FakeRegistrationHelper;
import eu.pb4.polymer.core.api.item.PolymerCreativeModeTabUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "dan200/computercraft/shared/ModRegistry$CreativeTabs")
public class ModRegistryCreativeTabsMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Ldan200/computercraft/shared/platform/PlatformHelper;createRegistrationHelper(Lnet/minecraft/resources/ResourceKey;)Ldan200/computercraft/shared/platform/RegistrationHelper;"))
    private static RegistrationHelper<?> registryNoMore(PlatformHelper instance, ResourceKey<Registry<?>> registryRegistryKey) {
        return new FakeRegistrationHelper<CreativeModeTab>(PolymerCreativeModeTabUtils::registerPolymerCreativeModeTab);
    }
}
