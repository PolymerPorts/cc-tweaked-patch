package eu.pb4.cctpatch.mixin.mod.item;

import dan200.computercraft.shared.platform.PlatformHelper;
import dan200.computercraft.shared.platform.RegistrationHelper;
import eu.pb4.cctpatch.impl.util.FakeRegistrationHelper;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import eu.pb4.polymer.rsm.api.RegistrySyncUtils;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "dan200/computercraft/shared/ModRegistry$RecipeSerializers")
public class ModRegistryRecipeSerializersMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Ldan200/computercraft/shared/platform/PlatformHelper;createRegistrationHelper(Lnet/minecraft/registry/RegistryKey;)Ldan200/computercraft/shared/platform/RegistrationHelper;"))
    private static RegistrationHelper<?> registryNoMore(PlatformHelper instance, RegistryKey<Registry<?>> registryRegistryKey) {
        return new FakeRegistrationHelper<RecipeSerializer<?>>((id, obj) -> {
            Registry.register(Registries.RECIPE_SERIALIZER, id, obj);
            RegistrySyncUtils.setServerEntry(Registries.RECIPE_SERIALIZER, obj);
        });
    }
}
