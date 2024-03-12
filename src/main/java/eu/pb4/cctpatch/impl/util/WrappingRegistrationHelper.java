package eu.pb4.cctpatch.impl.util;

import dan200.computercraft.shared.platform.RegistrationHelper;
import dan200.computercraft.shared.platform.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public record WrappingRegistrationHelper<T>(RegistrationHelper<T> original, BiConsumer<Identifier, T> consumer, List<RegistryEntry<T>> entries) implements RegistrationHelper<T> {
    public WrappingRegistrationHelper(RegistrationHelper<T> original, BiConsumer<Identifier, T> consumer) {
        this(original, consumer, new ArrayList<>());
    }

    @Override
    public <U extends T> RegistryEntry<U> register(String s, Supplier<U> supplier) {
        var x = original.register(s, supplier);
        //noinspection unchecked
        entries.add((RegistryEntry<T>) x);
        return x;
    }

    @Override
    public void register() {
        original.register();

        for (var x : entries) {
            consumer.accept(x.id(), x.get());
        }
    }
}
