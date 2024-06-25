package eu.pb4.cctpatch.impl.config;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.Brightness;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Function;

public class BaseGson {
    private static final RegistryWrapper.WrapperLookup GLOBAL_REGISTRIES = DynamicRegistryManager.of(Registries.REGISTRIES);

    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .registerTypeHierarchyAdapter(Identifier.class, new Identifier.Serializer())

            .registerTypeHierarchyAdapter(Item.class, new RegistrySerializer<>(Registries.ITEM))
            .registerTypeHierarchyAdapter(Block.class, new RegistrySerializer<>(Registries.BLOCK))
            .registerTypeHierarchyAdapter(SoundEvent.class, new RegistrySerializer<>(Registries.SOUND_EVENT))
            .registerTypeHierarchyAdapter(StatusEffect.class, new RegistrySerializer<>(Registries.STATUS_EFFECT))
            .registerTypeHierarchyAdapter(EntityType.class, new RegistrySerializer<>(Registries.ENTITY_TYPE))
            .registerTypeHierarchyAdapter(BlockEntityType.class, new RegistrySerializer<>(Registries.BLOCK_ENTITY_TYPE))
            //.registerTypeHierarchyAdapter(ItemStack.class, new CodecSerializer<>(ItemStack.CODEC))
            .registerTypeHierarchyAdapter(NbtCompound.class, new CodecSerializer<>(NbtCompound.CODEC))
            .registerTypeHierarchyAdapter(BlockPos.class, new CodecSerializer<>(BlockPos.CODEC))
            .registerTypeHierarchyAdapter(Vec3d.class, new CodecSerializer<>(Vec3d.CODEC))
            .registerTypeHierarchyAdapter(Vec2f.class, new CodecSerializer<>(Codec.list(Codec.DOUBLE).xmap(x -> new Vec2f(x.get(0).floatValue(), x.get(1).floatValue()), x -> List.of((double) x.x, (double) x.y))))
            .registerTypeHierarchyAdapter(EntityDimensions.class, new CodecSerializer<>(Codec.list(Codec.DOUBLE).xmap(x -> EntityDimensions.fixed(x.get(0).floatValue(), x.get(1).floatValue()), x -> List.of((double) x.width(), (double) x.height()))))
            .registerTypeHierarchyAdapter(BlockState.class, new CodecSerializer<>(BlockState.CODEC))
            .registerTypeHierarchyAdapter(AffineTransformation.class, new CodecSerializer<>(AffineTransformation.CODEC))
            .registerTypeHierarchyAdapter(DisplayEntity.BillboardMode.class, new CodecSerializer<>(DisplayEntity.BillboardMode.CODEC))
            .registerTypeHierarchyAdapter(DisplayEntity.TextDisplayEntity.TextAlignment.class, new CodecSerializer<>(DisplayEntity.TextDisplayEntity.TextAlignment.CODEC))
            .registerTypeHierarchyAdapter(Brightness.class, new CodecSerializer<>(Brightness.CODEC))

            //.registerTypeHierarchyAdapter(Matrix4f.class, new CodecSerializer<>(AffineTransformation.ANY_CODEC.xmap(AffineTransformation::getMatrix, AffineTransformation::new)))
            .setLenient().create();
    private record ItemStackSerializer() implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
        @Override
        public ItemStack deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (jsonElement.isJsonObject()) {
                return ItemStack.CODEC.decode(RegistryOps.of(JsonOps.INSTANCE, GLOBAL_REGISTRIES), jsonElement).result().orElse(Pair.of(ItemStack.EMPTY, null)).getFirst();
            } else {
                return Registries.ITEM.get(Identifier.tryParse(jsonElement.getAsString())).getDefaultStack();
            }
        }

        @Override
        public JsonElement serialize(ItemStack stack, Type type, JsonSerializationContext jsonSerializationContext) {
            //if (stack.getCount() == 1 && !stack.hasNbt()) {
            //    return new JsonPrimitive(Registries.ITEM.getId(stack.getItem()).toString());
            //}

            return ItemStack.CODEC.encodeStart(RegistryOps.of(JsonOps.INSTANCE, GLOBAL_REGISTRIES), stack).result().orElse(null);
        }
    }

    private record StringSerializer<T>(Function<String, T> decode, Function<T, String> encode) implements JsonSerializer<T>, JsonDeserializer<T> {
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
                return this.registry.get(Identifier.tryParse(json.getAsString()));
            }
            return null;
        }

        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive("" + this.registry.getId(src));
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