package eu.pb4.cctpatch.impl.poly.model.generic.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public record ModelVariant(Identifier model, int x, int y, boolean uvlock, int weigth) {
    private static final Codec<ModelVariant> BASE = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("model").forGetter(ModelVariant::model),
            Codec.INT.optionalFieldOf("x", 0).forGetter(ModelVariant::x),
            Codec.INT.optionalFieldOf("y", 0).forGetter(ModelVariant::y),
            Codec.BOOL.optionalFieldOf("uvlock", false).forGetter(ModelVariant::uvlock),
            Codec.INT.optionalFieldOf("weigth", 1).forGetter(ModelVariant::weigth)
            ).apply(instance, ModelVariant::new)
    );

    public static final Codec<List<ModelVariant>> CODEC = Codec.withAlternative(BASE.listOf(), BASE, List::of);
    public static final Codec<Map<String, List<ModelVariant>>> MAP = Codec.unboundedMap(Codec.STRING, CODEC);
}
