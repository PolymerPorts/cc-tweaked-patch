package eu.pb4.cctpatch.mixin.mod;

import dan200.computercraft.shared.ComputerCraft;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(value = ComputerCraft.class, remap = false)
public class ComputerCraftMixin {
    @Redirect(method = "lambda$init$18", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/Registry;getKey(Ljava/lang/Object;)Ljava/util/Optional;"))
    private static Optional<RegistryKey<ItemGroup>> replaceKey(Registry instance,  Object t) {
        return Optional.of(PolymerItemGroupUtils.getKey((ItemGroup) t));
    }
}
