// SPDX-FileCopyrightText: 2025 The CC: Tweaked Developers
//
// SPDX-License-Identifier: MPL-2.0

package eu.pb4.cctpatch.impl.poly.res.turtleupgrade;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;


public record BasicUpgradeModel(Identifier left, Identifier right) implements TurtleUpgradeModel {
    public static final Identifier ID = Identifier.of(ComputerCraftAPI.MOD_ID, "sided");
    public static final MapCodec<? extends TurtleUpgradeModel> CODEC = RecordCodecBuilder.<BasicUpgradeModel>mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("left").forGetter(BasicUpgradeModel::left),
            Identifier.CODEC.fieldOf("right").forGetter(BasicUpgradeModel::right)
    ).apply(instance, BasicUpgradeModel::new));

    @Override
    public MapCodec<? extends TurtleUpgradeModel> codec() {
        return CODEC;
    }

    @Override
    public void setupModel(UpgradeData<ITurtleUpgrade> upgrade, TurtleBrain brain, TurtleSide turtleSide, ItemDisplayElement attachment) {
        var mat = BlockModel.mat();
        var toolAngle = brain.getToolRenderAngle(turtleSide, 1);
        mat.rotate(RotationAxis.NEGATIVE_X.rotationDegrees(toolAngle));
        attachment.setItem(ItemDisplayElementUtil.getModel(turtleSide == TurtleSide.LEFT ? left : right));
        attachment.setItemDisplayContext(ItemDisplayContext.NONE);
        attachment.setTransformation(mat);
        mat.identity();
        if (attachment.isTransformationDirty()) {
            attachment.startInterpolation();
        }
    }
}
