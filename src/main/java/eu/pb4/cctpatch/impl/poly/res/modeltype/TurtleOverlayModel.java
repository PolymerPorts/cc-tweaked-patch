package eu.pb4.cctpatch.impl.poly.res.modeltype;

import com.mojang.serialization.MapCodec;
import dan200.computercraft.api.ComputerCraftAPI;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.ItemModel;
import net.minecraft.resources.Identifier;

public record TurtleOverlayModel(/*ModelTransformation transforms*/) implements ItemModel {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(ComputerCraftAPI.MOD_ID, "turtle/overlay");
    public static final MapCodec<TurtleOverlayModel> CODEC = MapCodec.unit(new TurtleOverlayModel());

    @Override
    public MapCodec<? extends ItemModel> codec() {
        return CODEC;
    }
}