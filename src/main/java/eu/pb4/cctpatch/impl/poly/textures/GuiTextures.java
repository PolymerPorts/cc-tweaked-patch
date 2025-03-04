package eu.pb4.cctpatch.impl.poly.textures;

import eu.pb4.cctpatch.impl.ComputerCraftPolymerPatch;
import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.CanvasImage;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import net.fabricmc.loader.api.FabricLoader;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;

public class GuiTextures {
    public static ButtonTexture CLOSE_ICON;
    public static ButtonTexture SHUTDOWN_ICON;
    public static ButtonTexture SHUTDOWN_ACTIVE;
    public static ButtonTexture TERMINATE;

    public static ComputerTexture ADVANCED_COMPUTER;
    public static ComputerTexture COMPUTER;
    public static ComputerTexture COMMAND_COMPUTER;

    public static PrintedPageTexture PRINTED_PAGE;

    static {
        var texturePath = FabricLoader.getInstance().getModContainer("computercraft").get().getPath("assets/computercraft/textures/gui/");
        var selfPath = FabricLoader.getInstance().getModContainer(ComputerCraftPolymerPatch.MOD_ID).get().getPath("map/");

        SHUTDOWN_ICON = createButton(texturePath, "sprites/buttons/turned_off");
        SHUTDOWN_ACTIVE = createButton(texturePath, "sprites/buttons/turned_on");
        TERMINATE = createButton(texturePath, "sprites/buttons/terminate");

        CLOSE_ICON = createButton(selfPath, "close");

        ADVANCED_COMPUTER = ComputerTexture.from(
                readTexture(texturePath.resolve("border_advanced.png")),
                readTexture(texturePath.resolve("sidebar_advanced.png"))
        );
        COMPUTER = ComputerTexture.from(
                readTexture(texturePath.resolve("border_normal.png")),
                readTexture(texturePath.resolve("sidebar_normal.png"))
        );
        COMMAND_COMPUTER = ComputerTexture.from(
                readTexture(texturePath.resolve("border_command.png")),
                readTexture(texturePath.resolve("sidebar_command.png")));

        PRINTED_PAGE = PrintedPageTexture.from(readTexture(texturePath.resolve("printout.png")));
    }

    private static ButtonTexture createButton(Path texturePath, String name) {
        return new ButtonTexture(readTexture(texturePath.resolve(name + ".png")), readTexture(texturePath.resolve(name + "_hover.png")));
    }

    private static CanvasImage readTexture(Path resolve) {
        try {
            return CanvasImage.from(ImageIO.read(Files.newInputStream(resolve)));
        } catch (Throwable e) {
            var x = new CanvasImage(32, 32);
            CanvasUtils.clear(x, CanvasColor.RED_HIGH);
            return x;
        }

    }
}
