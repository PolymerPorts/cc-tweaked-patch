package eu.pb4.cctpatch.mixin.mod;

import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import eu.pb4.cctpatch.impl.compat.PolyMcUtils;
import eu.pb4.cctpatch.impl.poly.item.PolyBaseItem;
import eu.pb4.cctpatch.impl.poly.item.PolyPocketComputerItem;
import eu.pb4.polymer.core.api.block.PolymerBlockUtils;
import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.other.PolymerMenuUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dan200/computercraft/shared/platform/PlatformHelperImpl$RegistryEntryImpl")
public class PlatformHelperImplRegistryEntryImplMixin {
    @Shadow
    @Nullable
    private Object instance;

    @Inject(method = "register(Lnet/minecraft/core/Registry;)V", at = @At("TAIL"))
    private void onRegister(Registry<?> registry, CallbackInfo ci) {
        if (registry == BuiltInRegistries.BLOCK_ENTITY_TYPE) {
            PolymerBlockUtils.registerBlockEntity((BlockEntityType<?>) this.instance);
        } else if (registry == BuiltInRegistries.MENU) {
            PolymerMenuUtils.registerType((MenuType<?>) this.instance);
            PolyMcUtils.addScreenHandlerBypass((MenuType<?>) this.instance);
        } else if (registry == BuiltInRegistries.ITEM) {
            PolymerItem polymerItem;

            if (this.instance instanceof PocketComputerItem) {
                polymerItem = new PolyPocketComputerItem();
            } else {
                polymerItem = new PolyBaseItem((Item) this.instance);
            }

            PolymerItem.registerOverlay((Item) this.instance, polymerItem);
        }
    }

}
