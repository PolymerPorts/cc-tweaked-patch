package eu.pb4.cctpatch.impl.poly.res;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dan200.computercraft.shared.computer.core.ComputerState;
import eu.pb4.cctpatch.impl.ComputerCraftPolymerPatch;
import eu.pb4.cctpatch.impl.poly.res.modeltype.TurtleOverlayModel;
import eu.pb4.cctpatch.impl.poly.res.modeltype.TurtleUpgradeItemModel;
import eu.pb4.cctpatch.impl.poly.res.property.PocketComputerStateProperty;
import eu.pb4.cctpatch.impl.poly.res.property.TurtleShowElfOverlay;
import eu.pb4.cctpatch.impl.poly.res.tint.PocketComputerLight;
import eu.pb4.cctpatch.impl.poly.model.TurtleModel;
import eu.pb4.cctpatch.impl.poly.res.turtleupgrade.TurtleUpgradeModel;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.polymer.resourcepack.extras.api.ResourcePackExtras;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.*;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.bool.BooleanProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.select.CustomModelDataStringProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.item.property.select.SelectProperty;
import eu.pb4.polymer.resourcepack.extras.api.format.item.tint.CustomModelDataTintSource;
import eu.pb4.polymer.resourcepack.extras.api.format.item.tint.DyeTintSource;
import eu.pb4.polymer.resourcepack.extras.api.format.item.tint.ItemTintSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import nl.theepicblock.resourcelocatorapi.ResourceLocatorApi;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ResourcePackGenerator {
    public static void setup() {
        PolymerResourcePackUtils.addModAssets("computercraft");
        PolymerResourcePackUtils.addModAssets(ComputerCraftPolymerPatch.MOD_ID);
        ResourcePackExtras.forDefault().addBridgedModelsFolder(Identifier.of("computercraft", "block"), ((identifier, resourcePackBuilder) -> {
            if (identifier.getPath().equals("block/turtle_colour")) {
                return new ItemAsset(new BasicItemModel(identifier, List.of(new DyeTintSource(0xFFFFFF))), ItemAsset.Properties.DEFAULT);
            }
            return new ItemAsset(new BasicItemModel(identifier), ItemAsset.Properties.DEFAULT);
        }));

        SelectProperty.TYPES.put(PocketComputerStateProperty.ID, PocketComputerStateProperty.TYPE);
        BooleanProperty.TYPES.put(TurtleShowElfOverlay.ID, TurtleShowElfOverlay.CODEC);
        ItemTintSource.TYPES.put(PocketComputerLight.ID, PocketComputerLight.CODEC);
        ItemModel.TYPES.put(TurtleUpgradeItemModel.ID, TurtleUpgradeItemModel.CODEC);
        ItemModel.TYPES.put(TurtleOverlayModel.ID, TurtleOverlayModel.CODEC);

        PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(ResourcePackGenerator::build);
    }

    private static void build(ResourcePackBuilder builder) {
        loadData();

        builder.addWriteConverter((path, data) -> {
            try {
                if (path.startsWith("assets/computercraft/items/pocket_computer_")) {
                    var asset = ItemAsset.fromJson(new String(data, StandardCharsets.UTF_8));
                    var replacer = new ItemModel.Replacer[] { null };
                    replacer[0] = (parent, model) -> {
                        if (model instanceof SelectItemModel<?, ?> selectItemModel && selectItemModel.switchValue().property() instanceof PocketComputerStateProperty) {
                            return new SelectItemModel<>(new SelectItemModel.Switch<>(
                                    new CustomModelDataStringProperty(0),
                                    selectItemModel.switchValue().cases()
                                            .stream().map(x -> new SelectItemModel.Case<>(
                                                    x.values().stream().map(y -> ((ComputerState) y).asString()).toList(),
                                                    replacer[0].modifyDeep(model, x.model()))).toList()
                                    ), selectItemModel.fallback().map(x -> replacer[0].modifyDeep(model, x)));
                        }
                        if (model instanceof BasicItemModel basicItemModel && basicItemModel.tints().stream().anyMatch(x -> x instanceof PocketComputerLight)) {
                            return new BasicItemModel(basicItemModel.model(),
                                    basicItemModel.tints().stream().map(x -> x instanceof PocketComputerLight light ? new CustomModelDataTintSource(0, light.defaultColour()) : x).toList());
                        }
                        return model;
                    };
                    return new ItemAsset(replacer[0].modifyDeep(EmptyItemModel.INSTANCE, asset.model()), asset.properties()).toBytes();
                } else if (path.startsWith("assets/computercraft/items/turtle_")) {
                    var asset = ItemAsset.fromJson(new String(data, StandardCharsets.UTF_8));
                    var replacer = new ItemModel.Replacer[] { null };
                    replacer[0] = (parent, model) -> {
                        if (model instanceof ConditionItemModel conditionItemModel && conditionItemModel.property() instanceof TurtleShowElfOverlay) {
                            return replacer[0].modifyDeep(model, conditionItemModel.onTrue());
                        }
                        if (model instanceof TurtleOverlayModel || model instanceof TurtleUpgradeItemModel) {
                            return EmptyItemModel.INSTANCE;
                        }
                        return model;
                    };
                    return new ItemAsset(replacer[0].modifyDeep(EmptyItemModel.INSTANCE, asset.model()), asset.properties()).toBytes();
                }

                if (path.startsWith("assets/computercraft/textures/block/monitor_") && path.endsWith(".png")) {
                    var id = Integer.parseInt(path.split("[_.]")[2]);
                    if (id > 15 && id < 32) {
                        var image = ImageIO.read(new ByteArrayInputStream(data));
                        for (var x = 0; x < image.getWidth(); x++) {
                            for (var y = 0; y < image.getHeight(); y++) {
                                if (ColorHelper.getAlpha(image.getRGB(x, y)) == 0) {
                                    image.setRGB(x, y, 0xFF111111);
                                }
                            }
                        }
                        var out = new ByteArrayOutputStream();
                        ImageIO.write(image, "png", out);
                        return out.toByteArray();
                    }
                }
            } catch (Throwable ignored) {
                ignored.printStackTrace();
            }

            return data;
        });
    }

    private static void loadData() {
        try (var container = ResourceLocatorApi.createGlobalAssetContainer()) {
            for (var overlay : container.locateFiles(TurtleOverlay.SOURCE)) {
                try {
                    var decoded = TurtleOverlay.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(new String(overlay.getRight().get().readAllBytes(), StandardCharsets.UTF_8))).getOrThrow();
                    TurtleModel.OVERLAY.put(overlay.getLeft().withPath(x -> x.substring(TurtleOverlay.SOURCE.length() + 1, x.length() - ".json".length())), decoded.getFirst());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            for (var overlay : container.locateFiles(TurtleUpgradeModel.SOURCE)) {
                try {
                    var decoded = TurtleUpgradeModel.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseString(new String(overlay.getRight().get().readAllBytes(), StandardCharsets.UTF_8))).getOrThrow();
                    TurtleModel.UPGRADES.put(overlay.getLeft().withPath(x -> x.substring(TurtleUpgradeModel.SOURCE.length() + 1, x.length() - ".json".length())), decoded.getFirst());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        } catch (Exception e) {

        }
    }
}
