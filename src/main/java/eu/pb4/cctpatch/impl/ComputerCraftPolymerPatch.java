package eu.pb4.cctpatch.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dan200.computercraft.shared.turtle.TurtleOverlay;
import eu.pb4.cctpatch.impl.config.PatchConfig;
import eu.pb4.cctpatch.impl.poly.font.Fonts;
import eu.pb4.cctpatch.impl.poly.PolymerSetup;
import eu.pb4.cctpatch.impl.poly.model.TurtleModel;
import eu.pb4.cctpatch.impl.poly.textures.GuiTextures;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.ColorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class ComputerCraftPolymerPatch implements ModInitializer {
	public static final String MOD_ID = "cc-tweaked-polymer-patch";
    public static final Logger LOGGER = LoggerFactory.getLogger("cc-tweaked-polymer-patch");
	public static MinecraftServer server;

	@Override
	public void onInitialize() {
		Fonts.TERMINAL_FONT.hashCode();
		GuiTextures.ADVANCED_COMPUTER.hashCode();
		TurtleModel.CRAFTING_MODEL.left();
		PatchConfig.instance.hashCode();

		PolymerResourcePackUtils.addModAssets("computercraft");
		PolymerResourcePackUtils.addModAssets(MOD_ID);
		ServerLifecycleEvents.SERVER_STARTING.register((server1 -> {
			server = server1;
			for (var x : server1.getRegistryManager().get(TurtleOverlay.REGISTRY)) {
				TurtleModel.registerOverlay(x.model());
			}
		}));
		ServerLifecycleEvents.SERVER_STOPPED.register((server1 -> server = null));
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(((a, b, c) -> {
			PatchConfig.loadOrCreateConfig();
		}));

		TurtleModel.registerOverlay(TurtleModel.ELF_OVERLAY_MODEL);

		PolymerResourcePackUtils.RESOURCE_PACK_CREATION_EVENT.register(builder -> {
			builder.addWriteConverter((path, data) -> {
				try {
					if (path.startsWith("assets/computercraft/models/block/turtle_colour.json")) {
						var json = JsonParser.parseString(new String(data, StandardCharsets.UTF_8)).getAsJsonObject();
						var elements = json.getAsJsonArray("elements");
						for (var el : elements) {
							var faces = el.getAsJsonObject().getAsJsonObject("faces");
							for (var key : faces.keySet()) {
								var val = faces.get(key).getAsJsonObject();
								if (val.get("tintindex") != null) {
									val.addProperty("tintindex", 1);
								}
							}
						}
						return json.toString().getBytes(StandardCharsets.UTF_8);
					} else if (path.startsWith("assets/computercraft/models/") && path.endsWith(".json")) {
						var json = JsonParser.parseString(new String(data, StandardCharsets.UTF_8)).getAsJsonObject();
						if (json.get("loader") != null && json.get("model") != null) {
							var obj = new JsonObject();
							obj.add("parent", json.get("model"));
							return obj.toString().getBytes(StandardCharsets.UTF_8);
						}
					} else if (path.startsWith("assets/computercraft/textures/block/monitor_") && path.endsWith(".png")) {
						var id = Integer.parseInt(path.split("[_.]")[2]);
						if (id > 15 && id < 32) {
							var image = ImageIO.read(new ByteArrayInputStream(data));
							for (var x = 0; x < image.getWidth(); x++) {
								for (var y = 0; y < image.getHeight(); y++) {
									if (ColorHelper.Argb.getAlpha(image.getRGB(x, y)) == 0) {
										image.setRGB(x, y, 0xFF111111);
									}
								}
							}
							var out = new ByteArrayOutputStream();
							ImageIO.write(image, "png", out);
							return out.toByteArray();
						}
					}
				} catch (Throwable ignored) {}

				return data;
			});
		});

		PolymerSetup.setup();
	}
}