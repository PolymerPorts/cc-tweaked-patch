package eu.pb4.cctpatch.impl.poly.model.generic.json;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record MultiPartDefinition(When when, List<ModelVariant> apply) {
    public static final Codec<MultiPartDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    When.CODEC.optionalFieldOf("when", When.DEFAULT).forGetter(MultiPartDefinition::when),
                    ModelVariant.CODEC.fieldOf("apply").forGetter(MultiPartDefinition::apply)
            ).apply(instance, MultiPartDefinition::new)
    );

    public record When(Optional<List<Map<String, String>>> or, Optional<List<Map<String, String>>> and,
                       Optional<Map<String, String>> base) {
        public static final When DEFAULT = new When(Optional.empty(), Optional.empty(), Optional.empty());

        private static final Codec<Map<String, String>> STR_MAP = Codec.unboundedMap(Codec.STRING, Codec.STRING);
        private static final Codec<List<Map<String, String>>> LIST_STR_MAP = STR_MAP.listOf();
        public static final Codec<When> CODEC = Codec.either(
                LIST_STR_MAP.fieldOf("OR")
                        .xmap(x -> new When(Optional.of(x), Optional.empty(), Optional.empty()), x -> x.or.orElseThrow()).codec(),
                Codec.either(
                        LIST_STR_MAP.fieldOf("AND")
                             .xmap(x -> new When(Optional.empty(), Optional.of(x), Optional.empty()), x -> x.and.orElseThrow()).codec(),
                        STR_MAP.xmap(x -> new When(Optional.empty(), Optional.empty(), Optional.of(x)), x -> x.base.orElseThrow()))
                ).xmap(x -> x.left().orElseGet(() -> x.right().orElseThrow().left().orElseGet(x.right().get().right()::get)),

                    x -> x.or.isPresent() ? Either.left(x)
                            : x.and.isPresent() ? Either.right(Either.left(x)) : Either.right(Either.right(x))
                );
    }
}
