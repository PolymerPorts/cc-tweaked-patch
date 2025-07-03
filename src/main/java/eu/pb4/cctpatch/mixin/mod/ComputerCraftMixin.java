package eu.pb4.cctpatch.mixin.mod;

import dan200.computercraft.shared.CommonHooks;
import dan200.computercraft.shared.ComputerCraft;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.item.ItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(value = ComputerCraft.class, remap = false)
public class ComputerCraftMixin {
    /**
     * @author Patbox
     * @reason I need to replace item group key getting logic.
     */
    @Overwrite(remap = false)
    private static void lambda$init$9(ItemGroup group, FabricItemGroupEntries entries) {
        CommonHooks.onBuildCreativeTab(PolymerItemGroupUtils.getKey(group), entries.getContext(), entries);
    }
    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/event/registry/FabricRegistryBuilder;attribute(Lnet/fabricmc/fabric/api/event/registry/RegistryAttribute;)Lnet/fabricmc/fabric/api/event/registry/FabricRegistryBuilder;"))
    private static FabricRegistryBuilder<?, ?> noSync(FabricRegistryBuilder instance, RegistryAttribute attribute) {
        return instance;
    }

}
