package eu.pb4.cctpatch.impl.poly.model.generic.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record StateDefinition(Optional<Map<String, List<ModelVariant>>> variants, Optional<List<MultiPartDefinition>> multipart) {
    public static final Codec<StateDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ModelVariant.MAP.optionalFieldOf("variants").forGetter(StateDefinition::variants),
        MultiPartDefinition.CODEC.listOf().optionalFieldOf("multipart").forGetter(StateDefinition::multipart)
    ).apply(instance, StateDefinition::new));
}
