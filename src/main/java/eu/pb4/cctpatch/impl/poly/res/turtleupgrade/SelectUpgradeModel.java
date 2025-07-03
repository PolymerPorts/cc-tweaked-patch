// SPDX-FileCopyrightText: 2025 The CC: Tweaked Developers
//
// SPDX-License-Identifier: MPL-2.0

package eu.pb4.cctpatch.impl.poly.res.turtleupgrade;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.component.ComponentType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.*;
import java.util.stream.Collectors;

public record SelectUpgradeModel<T>(Cases<T> cases, Optional<TurtleUpgradeModel> fallback, MutableObject<Map<T, TurtleUpgradeModel>> caseMap) implements TurtleUpgradeModel {
    public static final Identifier ID = Identifier.of(ComputerCraftAPI.MOD_ID, "select");
    public static final MapCodec<? extends TurtleUpgradeModel> CODEC = RecordCodecBuilder.<SelectUpgradeModel<?>>mapCodec(instance -> instance.group(
            Cases.CODEC.forGetter(SelectUpgradeModel::cases),
            TurtleUpgradeModel.CODEC.optionalFieldOf("fallback").forGetter(SelectUpgradeModel::fallback)
    ).apply(instance, SelectUpgradeModel::new));


    public SelectUpgradeModel(Cases<T> cases, Optional<TurtleUpgradeModel> fallback) {
        this(cases, fallback, new MutableObject<>());
    }

    @Override
    public MapCodec<? extends TurtleUpgradeModel> codec() {
        return CODEC;
    }

    @Override
    public void setupModel(UpgradeData<ITurtleUpgrade> upgrade, TurtleBrain brain, TurtleSide turtleSide, ItemDisplayElement attachment) {
        var value = upgrade.get(this.cases.component);
        if (value == null) {
            fallback.orElse(EmptyUpgradeModel.INSTANCE).setupModel(upgrade, brain, turtleSide, attachment);
            return;
        }
        if (caseMap.getValue() == null) {
            var map = new HashMap<T, TurtleUpgradeModel>();
            caseMap.setValue(map);
            for (var x : cases.cases) {
                for (var key : x.getFirst()) {
                    map.put(key, x.getSecond());
                }
            }
        }

        caseMap.getValue().getOrDefault(value, fallback.orElse(EmptyUpgradeModel.INSTANCE)).setupModel(upgrade, brain, turtleSide, attachment);
    }

    public record Cases<T>(ComponentType<T> component, List<Pair<List<T>, TurtleUpgradeModel>> cases) {
        private static final MapCodec<Cases<?>> CODEC = ComponentType.CODEC.dispatchMap("property", Cases::component, Util.memoize(Cases::codec));

        private static <T> MapCodec<Cases<T>> codec(ComponentType<T> component) {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(
                    MapCodec.unit(component).forGetter(Cases::component),
                    caseCodec(component.getCodecOrThrow()).listOf().fieldOf("cases").validate(Cases::validate).forGetter(Cases::cases)
            ).apply(instance, Cases<T>::new));
        }

        private static <T> Codec<Pair<List<T>, TurtleUpgradeModel>> caseCodec(Codec<T> codec) {
            return RecordCodecBuilder.create(instance -> instance.group(
                    codec.listOf().fieldOf("when").forGetter(Pair::getFirst),
                    TurtleUpgradeModel.CODEC.fieldOf("model").forGetter(Pair::getSecond)
            ).apply(instance, Pair::new));
        }

        private static <T> DataResult<List<Pair<List<T>, TurtleUpgradeModel>>> validate(List<Pair<List<T>, TurtleUpgradeModel>> cases) {
            Multiset<T> multiset = HashMultiset.create();
            for (var condition : cases) multiset.addAll(condition.getFirst());

            if (multiset.isEmpty()) return DataResult.error(() -> "Empty cases");
            if (multiset.size() != multiset.entrySet().size()) {
                return DataResult.error(() -> "Duplicate case conditions: " + multiset.entrySet().stream()
                        .filter(x -> x.getCount() > 1)
                        .map(x -> Objects.toString(x.getElement()))
                        .collect(Collectors.joining(", ")));
            }

            return DataResult.success(cases);
        }
    }
}
