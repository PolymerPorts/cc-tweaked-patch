package eu.pb4.cctpatch.impl.util;

import dan200.computercraft.shared.platform.RegistrationHelper;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import dan200.computercraft.shared.platform.RegistryEntry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public record FakeRegistrationHelper<T>(BiConsumer<Identifier, T> consumer) implements RegistrationHelper<T> {
    public FakeRegistrationHelper() {
        this((a,b) -> {});
    }

    @Override
    public <U extends T> RegistryEntry<U> register(String s, Supplier<U> supplier) {
        var x = new RegistryEntry<>(Identifier.fromNamespaceAndPath("computercraft", s), supplier.get());
        consumer.accept(x.id, x.obj);
        return x;
    }

    @Override
    public <U extends T> dan200.computercraft.shared.platform.RegistryEntry<U> register(ResourceKey<T> resourceKey, Supplier<U> supplier) {
        var x = new RegistryEntry<>(resourceKey.identifier(), supplier.get());
        consumer.accept(x.id, x.obj);
        return x;
    }

    @Override
    public void register() {

    }

    public record RegistryEntry<T>(Identifier id, T obj) implements dan200.computercraft.shared.platform.RegistryEntry<T> {
        @Override
        public T get() {
            return obj;
        }
    }
}
