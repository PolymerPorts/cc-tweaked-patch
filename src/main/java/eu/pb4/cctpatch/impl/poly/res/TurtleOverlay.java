// SPDX-FileCopyrightText: 2024 The CC: Tweaked Developers
//
// SPDX-License-Identifier: MPL-2.0

package eu.pb4.cctpatch.impl.poly.res;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dan200.computercraft.api.ComputerCraftAPI;
import net.minecraft.util.Identifier;


public record TurtleOverlay(Identifier model, boolean showElfOverlay) {
    public static final String SOURCE = ComputerCraftAPI.MOD_ID + "/turtle_overlay";

    public static final Codec<TurtleOverlay> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Identifier.CODEC.fieldOf("model").forGetter(TurtleOverlay::model),
        Codec.BOOL.optionalFieldOf("show_elf_overlay", false).forGetter(TurtleOverlay::showElfOverlay)
    ).apply(instance, TurtleOverlay::new));

    public static final Identifier ELF_MODEL = Identifier.of(ComputerCraftAPI.MOD_ID, "block/turtle_elf_overlay");
}
