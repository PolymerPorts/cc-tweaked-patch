package eu.pb4.cctpatch.impl.poly.res.tint;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dan200.computercraft.api.ComputerCraftAPI;
import eu.pb4.polymer.resourcepack.extras.api.format.item.tint.ItemTintSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

public record PocketComputerLight(int defaultColour) implements ItemTintSource {
    public static final Identifier ID = Identifier.of(ComputerCraftAPI.MOD_ID, "pocket_computer_light");
    public static final MapCodec<PocketComputerLight> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codecs.RGB.fieldOf("default").forGetter(PocketComputerLight::defaultColour)
    ).apply(instance, PocketComputerLight::new));


    @Override
    public MapCodec<? extends ItemTintSource> codec() {
        return CODEC;
    }
}