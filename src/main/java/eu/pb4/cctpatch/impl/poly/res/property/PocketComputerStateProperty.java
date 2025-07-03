package eu.pb4.cctpatch.impl.poly.res.property;

import com.mojang.serialization.MapCodec;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.shared.computer.core.ComputerState;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.select.SelectProperty;
import net.minecraft.util.Identifier;

public record PocketComputerStateProperty() implements SelectProperty<ComputerState> {
    public static final Identifier ID = Identifier.of(ComputerCraftAPI.MOD_ID, "pocket_computer_state");
    public static final PocketComputerStateProperty INSTANCE = new PocketComputerStateProperty();
    public static final MapCodec<PocketComputerStateProperty> CODEC = MapCodec.unit(INSTANCE);
    public static final Type<PocketComputerStateProperty, ComputerState> TYPE = new Type<>(CODEC, ComputerState.CODEC);


    @Override
    public Type<? extends SelectProperty<ComputerState>, ComputerState> type() {
        return TYPE;
    }
}