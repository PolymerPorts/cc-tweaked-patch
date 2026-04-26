package eu.pb4.cctpatch.impl.poly.res.property;

import com.mojang.serialization.MapCodec;
import dan200.computercraft.api.ComputerCraftAPI;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.bool.BooleanProperty;
import net.minecraft.resources.Identifier;

public record TurtleShowElfOverlay() implements BooleanProperty {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(ComputerCraftAPI.MOD_ID, "turtle/show_elf_overlay");
    private static final TurtleShowElfOverlay INSTANCE = new TurtleShowElfOverlay();
    public static final MapCodec<TurtleShowElfOverlay> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<? extends BooleanProperty> codec() {
        return CODEC;
    }
}