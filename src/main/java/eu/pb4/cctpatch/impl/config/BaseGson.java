package eu.pb4.cctpatch.impl.config;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Brightness;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

public class BaseGson {
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .registerTypeHierarchyAdapter(Identifier.class, new CodecSerializer(Identifier.CODEC))

            .registerTypeHierarchyAdapter(Item.class, new RegistrySerializer<>(BuiltInRegistries.ITEM))
            .registerTypeHierarchyAdapter(Block.class, new RegistrySerializer<>(BuiltInRegistries.BLOCK))
            .registerTypeHierarchyAdapter(SoundEvent.class, new RegistrySerializer<>(BuiltInRegistries.SOUND_EVENT))
            .registerTypeHierarchyAdapter(MobEffect.class, new RegistrySerializer<>(BuiltInRegistries.MOB_EFFECT))
            .registerTypeHierarchyAdapter(EntityType.class, new RegistrySerializer<>(BuiltInRegistries.ENTITY_TYPE))
            .registerTypeHierarchyAdapter(BlockEntityType.class, new RegistrySerializer<>(BuiltInRegistries.BLOCK_ENTITY_TYPE))
            //.registerTypeHierarchyAdapter(ItemStack.class, new CodecSerializer<>(ItemStack.CODEC))
            .registerTypeHierarchyAdapter(CompoundTag.class, new CodecSerializer<>(CompoundTag.CODEC))
            .registerTypeHierarchyAdapter(BlockPos.class, new CodecSerializer<>(BlockPos.CODEC))
            .registerTypeHierarchyAdapter(Vec3.class, new CodecSerializer<>(Vec3.CODEC))
            .registerTypeHierarchyAdapter(Vec2.class, new CodecSerializer<>(Codec.list(Codec.DOUBLE).xmap(x -> new Vec2(x.get(0).floatValue(), x.get(1).floatValue()), x -> List.of((double) x.x, (double) x.y))))
            .registerTypeHierarchyAdapter(EntityDimensions.class, new CodecSerializer<>(Codec.list(Codec.DOUBLE).xmap(x -> EntityDimensions.fixed(x.get(0).floatValue(), x.get(1).floatValue()), x -> List.of((double) x.width(), (double) x.height()))))
            .registerTypeHierarchyAdapter(BlockState.class, new CodecSerializer<>(BlockState.CODEC))
            .registerTypeHierarchyAdapter(Transformation.class, new CodecSerializer<>(Transformation.CODEC))
            .registerTypeHierarchyAdapter(Display.BillboardConstraints.class, new CodecSerializer<>(Display.BillboardConstraints.CODEC))
            .registerTypeHierarchyAdapter(Display.TextDisplay.Align.class, new CodecSerializer<>(Display.TextDisplay.Align.CODEC))
            .registerTypeHierarchyAdapter(Brightness.class, new CodecSerializer<>(Brightness.CODEC))

            //.registerTypeHierarchyAdapter(Matrix4f.class, new CodecSerializer<>(AffineTransformation.ANY_CODEC.xmap(AffineTransformation::getMatrix, AffineTransformation::new)))
            .setLenient().create();
    private static final HolderLookup.Provider GLOBAL_REGISTRIES = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

    private record ItemStackSerializer() implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
        @Override
        public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonObject()) {
                return ItemStack.CODEC.decode(RegistryOps.create(JsonOps.INSTANCE, GLOBAL_REGISTRIES), jsonElement).result().orElse(Pair.of(ItemStack.EMPTY, null)).getFirst();
            } else {
                return BuiltInRegistries.ITEM.getValue(Identifier.tryParse(jsonElement.getAsString())).getDefaultInstance();
            }
        }

        @Override
        public JsonElement serialize(ItemStack stack, Type type, JsonSerializationContext jsonSerializationContext) {
            //if (stack.getCount() == 1 && !stack.hasNbt()) {
            //    return new JsonPrimitive(Registries.ITEM.getId(stack.getItem()).toString());
            //}

            return ItemStack.CODEC.encodeStart(RegistryOps.create(JsonOps.INSTANCE, GLOBAL_REGISTRIES), stack).result().orElse(null);
        }
    }

    private record StringSerializer<T>(Function<String, T> decode,
                                       Function<T, String> encode) implements JsonSerializer<T>, JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return this.decode.apply(json.getAsString());
            }
            return null;
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(this.encode.apply(src));
        }
    }

    private record RegistrySerializer<T>(Registry<T> registry) implements JsonSerializer<T>, JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive()) {
                return this.registry.getValue(Identifier.tryParse(json.getAsString()));
            }
            return null;
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive("" + this.registry.getKey(src));
        }
    }

    private record CodecSerializer<T>(Codec<T> codec) implements JsonSerializer<T>, JsonDeserializer<T> {
        @Override
        public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return this.codec.decode(JsonOps.INSTANCE, json).getOrThrow().getFirst();
            } catch (Throwable e) {
                return null;
            }
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            try {
                return src != null ? this.codec.encodeStart(JsonOps.INSTANCE, src).getOrThrow() : JsonNull.INSTANCE;
            } catch (Throwable e) {
                return JsonNull.INSTANCE;
            }
        }
    }
}