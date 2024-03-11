package eu.pb4.computercraftpatch.impl;

import eu.pb4.computercraftpatch.impl.poly.PolymerSetup;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComputerCraftPolymerPatch implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("computercraft-polymer-patch");

	@Override
	public void onInitialize() {
		/*PolymerResourcePackUtils.addModAssets("anshar");
		PolymerResourcePackUtils.getInstance().creationEvent.register(x -> {
			x.addWriteConverter((path, bytes) -> path.startsWith("/assets/anshar/sounds/tunes") ? null : bytes);
		});
		 */

		PolymerSetup.setup();
	}
}