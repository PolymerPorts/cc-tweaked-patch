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
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public record EmptyUpgradeModel() implements TurtleUpgradeModel {
    public static final EmptyUpgradeModel INSTANCE = new EmptyUpgradeModel();

    public static final Identifier ID = Identifier.of(ComputerCraftAPI.MOD_ID, "empty");
    public static final MapCodec<TurtleUpgradeModel> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<? extends TurtleUpgradeModel> codec() {
        return CODEC;
    }

    @Override
    public void setupModel(UpgradeData<ITurtleUpgrade> upgrade, TurtleBrain brain, TurtleSide turtleSide, ItemDisplayElement attachment) {
        attachment.setItem(ItemStack.EMPTY);
        attachment.setTransformation(BlockModel.mat());
    }
}
