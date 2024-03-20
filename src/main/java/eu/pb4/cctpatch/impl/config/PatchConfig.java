package eu.pb4.cctpatch.impl.config;


import com.google.gson.annotations.SerializedName;
import eu.pb4.cctpatch.impl.ComputerCraftPolymerPatch;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public final class PatchConfig {
    public static PatchConfig instance = loadOrCreateConfig();
    @SerializedName("display_pocket_computer_screen_in_hand")
    public boolean displayPocketComputerScreenInHand = true;

    public static PatchConfig loadOrCreateConfig() {
        try {
            PatchConfig config;
            var configFile = FabricLoader.getInstance().getConfigDir().resolve(ComputerCraftPolymerPatch.MOD_ID + ".json");

            if (Files.exists(configFile)) {
                config = BaseGson.GSON.fromJson(Files.readString(configFile, StandardCharsets.UTF_8), PatchConfig.class);
            } else {
                config = new PatchConfig();
            }

            saveConfig(config);
            return instance = config;
        }
        catch(IOException exception) {
            ComputerCraftPolymerPatch.LOGGER.error("Something went wrong while reading the config!", exception);
            return instance = new PatchConfig();
        }
    }

    public static void saveConfig(PatchConfig config) {
        var configFile = FabricLoader.getInstance().getConfigDir().resolve(ComputerCraftPolymerPatch.MOD_ID + ".json");
        try {
            Files.writeString(configFile, BaseGson.GSON.toJson(config), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (Exception e) {
            ComputerCraftPolymerPatch.LOGGER.error("Something went wrong while saving the config!", e);
        }
    }
}
