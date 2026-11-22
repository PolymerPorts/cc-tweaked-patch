package eu.pb4.cctpatch.impl.poly.res.turtleupgrade;

import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.impl.UpgradeData;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;

import java.util.EnumMap;

public record ShulkerUpgradeModel() implements TurtleUpgradeModel {
    public static final Identifier ID = Identifier.fromNamespaceAndPath("computercraft", "shulker");
    public static final MapCodec<ShulkerUpgradeModel> CODEC = MapCodec.unit(new ShulkerUpgradeModel());

    private static final Identifier LEFT = Identifier.fromNamespaceAndPath("computercraft", "block/turtle_shulker_left");
    private static final Identifier RIGHT = Identifier.fromNamespaceAndPath("computercraft", "block/turtle_shulker_right");

    private static final Identifier LEFT_COLOUR = Identifier.fromNamespaceAndPath("computercraft", "block/turtle_shulker_colour_left");
    private static final Identifier RIGHT_COLOUR = Identifier.fromNamespaceAndPath("computercraft", "block/turtle_shulker_colour_right");

    private static final EnumMap<DyeColor, Integer> SHULKER_COLOURS = Util.make(new EnumMap<>(DyeColor.class), m -> {
        m.put(DyeColor.WHITE, 0xFFE6EAEA);
        m.put(DyeColor.ORANGE, 0xFFF57410);
        m.put(DyeColor.MAGENTA, 0xFFBA3FAF);
        m.put(DyeColor.LIGHT_BLUE, 0xFF3BB4DB);
        m.put(DyeColor.YELLOW, 0xFFFCC724);
        m.put(DyeColor.LIME, 0xFF71BC18);
        m.put(DyeColor.PINK, 0xFFF38BAA);
        m.put(DyeColor.GRAY, 0xFF3E4246);
        m.put(DyeColor.LIGHT_GRAY, 0xFF8C8C83);
        m.put(DyeColor.CYAN, 0xFF168792);
        m.put(DyeColor.PURPLE, 0xFF7426A9);
        m.put(DyeColor.BLUE, 0xFF33359B);
        m.put(DyeColor.BROWN, 0xFF734828);
        m.put(DyeColor.GREEN, 0xFF546D1C);
        m.put(DyeColor.RED, 0xFF9C2522);
        m.put(DyeColor.BLACK, 0xFF1F1F23);
    });

    @Override
    public MapCodec<? extends TurtleUpgradeModel> codec() {
        return CODEC;
    }

    @Override
    public void setupModel(UpgradeData<ITurtleUpgrade> upgrade, TurtleBrain brain, TurtleSide turtleSide, ItemDisplayElement attachment) {
        var colour = upgrade.getItem().item().value() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock block
                ? block.getColor()
                : null;


        var mat = BlockModel.mat();
        var toolAngle = brain.getToolRenderAngle(turtleSide, 1);
        mat.rotate(Axis.XN.rotationDegrees(toolAngle));

        var item = ItemDisplayElementUtil.getModel(turtleSide == TurtleSide.LEFT
                ? colour != null ? LEFT_COLOUR : LEFT
                : colour != null ? RIGHT_COLOUR : RIGHT
        ).get();

        if (colour != null) {
            item = item.copy();
            item.set(DataComponents.DYED_COLOR, new DyedItemColor(SHULKER_COLOURS.get(colour)));
        }

        attachment.setItem(item);
        attachment.setItemDisplayContext(ItemDisplayContext.NONE);
        attachment.setTransformation(mat);
        mat.identity();

        if (attachment.isTransformationDirty()) {
            attachment.startInterpolation();
        }
    }
}
