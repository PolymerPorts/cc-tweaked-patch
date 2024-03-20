package eu.pb4.cctpatch.impl.poly.model;

import dan200.computercraft.shared.common.IColouredItem;
import dan200.computercraft.shared.computer.core.ComputerState;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import eu.pb4.cctpatch.impl.ComputerCraftPolymerPatch;
import eu.pb4.factorytools.api.resourcepack.BaseItemProvider;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public record PocketComputerModel(PolymerModelData defaultModel,
                                  PolymerModelData onModel,
                                  PolymerModelData blinkingModel,
                                  PolymerModelData dyedModel,
                                  PolymerModelData onDyedModel,
                                  PolymerModelData blinkingDyedModel) {


    public static PocketComputerModel from(Identifier identifier) {
        var defaultModel = PolymerResourcePackUtils.requestModel(BaseItemProvider.requestItem(), identifier.withPrefixedPath("item/"));
        var onModel = PolymerResourcePackUtils.requestModel(Items.FILLED_MAP, identifier.withPrefixedPath("item/").withSuffixedPath("_on"));
        var blinkingModel = PolymerResourcePackUtils.requestModel(Items.FILLED_MAP, identifier.withPrefixedPath("item/").withSuffixedPath("_blinking"));
        var dyedModel = PolymerResourcePackUtils.requestModel(Items.FIREWORK_STAR, new Identifier("computercraft:item/pocket_computer_colour"));
        var onDyedModel = PolymerResourcePackUtils.requestModel(Items.FILLED_MAP, new Identifier("computercraft:item/pocket_computer_colour_on"));
        var blinkingDyedModel = PolymerResourcePackUtils.requestModel(Items.FILLED_MAP, new Identifier("computercraft:item/pocket_computer_colour_blinking"));
        return new PocketComputerModel(defaultModel, onModel, blinkingModel, dyedModel, onDyedModel, blinkingDyedModel);
    }

    public PolymerModelData getModelData(ItemStack itemStack) {
        var computer = PocketComputerItem.getServerComputer(ComputerCraftPolymerPatch.server, itemStack);
        var state = computer != null ? computer.getState() : ComputerState.OFF;
        if (IColouredItem.getColourBasic(itemStack) != -1) {
            return switch (state) {
                case OFF -> dyedModel;
                case ON -> onDyedModel;
                case BLINKING -> blinkingDyedModel;
            };
        }

        return switch (state) {
            case OFF -> defaultModel;
            case ON -> onModel;
            case BLINKING -> blinkingModel;
        };
    }
}
