package eu.pb4.cctpatch.impl.pack;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resource.*;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public record LangPackProvider(ModContainer mod) implements ResourcePackProvider, ResourcePackProfile.PackFactory, ResourcePack {
    public static final ResourcePackProvider INSTANCE = new LangPackProvider(FabricLoader.getInstance().getModContainer("computercraft").get());

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder) {
        profileAdder.accept(ResourcePackProfile.of("$cc-lang", Text.literal("ComputerCraft Polymer Patch"),
                true, this,
                new ResourcePackProfile.Metadata(Text.literal("Lang Files"), ResourcePackCompatibility.COMPATIBLE, FeatureSet.empty(), List.of()),
                ResourcePackProfile.InsertionPosition.BOTTOM, false, ResourcePackSource.BUILTIN
        ));
    }

    @Override
    public ResourcePack open(String name) {
        return this;
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        var path = mod.findPath(String.join("/", segments));

        return path.map(InputSupplier::create).orElse(null);
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        var path = mod.findPath("assets/" + id.getNamespace() + "/" + id.getPath());

        return path.map(InputSupplier::create).orElse(null);
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        var path = mod.findPath("assets/" + namespace + "/" + prefix);

        if (path.isEmpty()) {
            return;
        }

        try {
            String separator = path.get().getFileSystem().getSeparator();
            Files.walkFileTree(path.get(), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String filename = path.get().relativize(file).toString().replace(separator, "/");
                    Identifier identifier = Identifier.of(namespace, prefix + "/" + filename);

                    if (identifier != null) {
                        consumer.accept(identifier, InputSupplier.create(file));
                    }

                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException ignored) {
            //
        }
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        return Set.of("anshar");
    }

    @Nullable
    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
        return null;
    }

    @Override
    public String getName() {
        return "$anshar-lang";
    }

    @Override
    public void close() {

    }


    @Override
    public ResourcePack openWithOverlays(String name, ResourcePackProfile.Metadata metadata) {
        return open(name);
    }
}
