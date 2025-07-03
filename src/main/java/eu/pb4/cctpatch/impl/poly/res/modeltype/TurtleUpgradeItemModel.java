package eu.pb4.cctpatch.impl.poly.res.modeltype;

import com.mojang.serialization.MapCodec;
import dan200.computercraft.api.ComputerCraftAPI;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.ItemModel;
import net.minecraft.util.Identifier;

public record TurtleUpgradeItemModel(/*ModelTransformation transforms*/) implements ItemModel {
    public static final Identifier ID = Identifier.of(ComputerCraftAPI.MOD_ID, "turtle/upgrade");
    public static final MapCodec<TurtleUpgradeItemModel> CODEC = MapCodec.unit(new TurtleUpgradeItemModel());

    @Override
    public MapCodec<? extends ItemModel> codec() {
        return CODEC;
    }
}