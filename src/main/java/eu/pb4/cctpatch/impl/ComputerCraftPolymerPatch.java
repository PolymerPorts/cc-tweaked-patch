package eu.pb4.cctpatch.impl;

import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.shared.recipe.function.RecipeFunction;
import eu.pb4.cctpatch.impl.config.PatchConfig;
import eu.pb4.cctpatch.impl.poly.font.Fonts;
import eu.pb4.cctpatch.impl.poly.PolymerSetup;
import eu.pb4.cctpatch.impl.poly.res.ResourcePackGenerator;
import eu.pb4.cctpatch.impl.poly.textures.GuiTextures;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComputerCraftPolymerPatch implements ModInitializer {
	public static final String MOD_ID = "cc-tweaked-polymer-patch";
    public static final Logger LOGGER = LoggerFactory.getLogger("cc-tweaked-polymer-patch");
	public static MinecraftServer server;

	@Override
	public void onInitialize() {
		Fonts.TERMINAL_FONT.hashCode();
		GuiTextures.ADVANCED_COMPUTER.hashCode();
		PatchConfig.instance.hashCode();

		PolymerUtils.markAsServerOnlyRegistry(RecipeFunction.REGISTRY);
		PolymerUtils.markAsServerOnlyRegistry(ITurtleUpgrade.REGISTRY);
		PolymerUtils.markAsServerOnlyRegistry(IPocketUpgrade.REGISTRY);

		ResourcePackGenerator.setup();

		ServerLifecycleEvents.SERVER_STARTING.register((server1 -> {
			server = server1;
		}));
		ServerLifecycleEvents.SERVER_STOPPED.register((server1 -> server = null));
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register(((a, b, c) -> {
			PatchConfig.loadOrCreateConfig();
		}));

		PolymerSetup.setup();
	}
}