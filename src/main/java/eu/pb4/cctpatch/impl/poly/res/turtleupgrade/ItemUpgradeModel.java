// SPDX-FileCopyrightText: 2025 The CC: Tweaked Developers
//
// SPDX-License-Identifier: MPL-2.0

package eu.pb4.cctpatch.impl.poly.res.turtleupgrade;

import com.mojang.serialization.MapCodec;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public record ItemUpgradeModel() implements TurtleUpgradeModel {
    public static final ItemUpgradeModel INSTANCE = new ItemUpgradeModel();

    public static final Identifier ID = Identifier.of(ComputerCraftAPI.MOD_ID, "item");
    public static final MapCodec<TurtleUpgradeModel> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<? extends TurtleUpgradeModel> codec() {
        return CODEC;
    }

    @Override
    public void setupModel(UpgradeData<ITurtleUpgrade> upgrade, TurtleBrain brain, TurtleSide turtleSide, ItemDisplayElement attachment) {
        var mat = BlockModel.mat();
        var toolAngle = brain.getToolRenderAngle(turtleSide, 1);
        mat.rotate(RotationAxis.POSITIVE_X.rotationDegrees(toolAngle));
        mat.rotateY(MathHelper.HALF_PI);
        mat.translate(0, 0, turtleSide == TurtleSide.RIGHT ? -6.5f / 16f : 6.5f / 16f);
        attachment.setItem(upgrade.getUpgradeItem());
        attachment.setItemDisplayContext(ItemDisplayContext.FIXED);
        attachment.setTransformation(mat);
        mat.identity();
        if (attachment.isTransformationDirty()) {
            attachment.startInterpolation();
        }
    }
}
