// SPDX-FileCopyrightText: 2025 The CC: Tweaked Developers
//
// SPDX-License-Identifier: MPL-2.0

package eu.pb4.cctpatch.impl.poly.res.turtleupgrade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import eu.pb4.polymer.common.impl.LazyIdMapper;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.function.Function;

public interface TurtleUpgradeModel {
    String SOURCE = ComputerCraftAPI.MOD_ID + "/turtle_upgrade";

    Codecs.IdMapper<Identifier, MapCodec<? extends TurtleUpgradeModel>> TYPES = new LazyIdMapper<>((m) -> {
        m.put(BasicUpgradeModel.ID, BasicUpgradeModel.CODEC);
        m.put(ItemUpgradeModel.ID, ItemUpgradeModel.CODEC);
        m.put(SelectUpgradeModel.ID, SelectUpgradeModel.CODEC);
    });

    Codec<TurtleUpgradeModel> CODEC = Codec.lazyInitialized(() -> TYPES.getCodec(Identifier.CODEC).dispatch(TurtleUpgradeModel::codec, Function.identity()));
    MapCodec<? extends TurtleUpgradeModel> codec();

    void setupModel(UpgradeData<ITurtleUpgrade> upgrade, TurtleBrain brain, TurtleSide turtleSide, ItemDisplayElement attachment);
}
