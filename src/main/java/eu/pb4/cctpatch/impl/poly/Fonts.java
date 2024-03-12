package eu.pb4.cctpatch.impl.poly;

import eu.pb4.mapcanvas.api.font.BitmapFontBuilder;
import eu.pb4.mapcanvas.api.font.CanvasFont;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import net.fabricmc.loader.api.FabricLoader;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Fonts {
    public static final int FONT_HEIGHT = 9;
    public static final int FONT_WIDTH = 6;

    public static final int MINI_FONT_HEIGHT = 6;
    public static final int MINI_FONT_WIDTH = 4;

    public static final CanvasFont MINI_TERMINAL_FONT = null;
    public static final CanvasFont TERMINAL_FONT;
    public static final CanvasFont TERMINAL_BACKGROUND_FONT;

    static {
        {
            CanvasFont font;
            CanvasFont fontBack;

            var texturePath = FabricLoader.getInstance().getModContainer("computercraft").get().getPath("assets/computercraft/textures/").resolve("gui/term_font.png");
            var emptyGlyph = BitmapFontBuilder.Glyph.of(1, 1);
            try {
                var builder = BitmapFontBuilder.create();
                var builderBack = BitmapFontBuilder.create();

                buildFont(texturePath, builder, builderBack, FONT_HEIGHT, FONT_WIDTH);

                font = builder.defaultGlyph(emptyGlyph).build();
                fontBack = builderBack.defaultGlyph(emptyGlyph).build();
            } catch (Throwable e) {
                e.printStackTrace();
                font = DefaultFonts.VANILLA;
                fontBack = BitmapFontBuilder.create().defaultGlyph(emptyGlyph).build();
            }

            TERMINAL_FONT = font;
            TERMINAL_BACKGROUND_FONT = fontBack;
        }
    }

    private static void buildFont(Path texturePath, BitmapFontBuilder builder, BitmapFontBuilder builderBack, int fontHeight, int fontWidth) throws IOException {

        var image = ImageIO.read(Files.newInputStream(texturePath));

        for (int i = 0; i < 256; i++) {
            int column = i % 16;
            int row = i / 16;

            int xStart = column * (fontWidth + 2);
            int yStart = row * (fontHeight + 2);

            var glyph = BitmapFontBuilder.Glyph.of(fontWidth, fontHeight).logicalHeight(fontHeight).charWidth(fontWidth);
            var glyphBack = BitmapFontBuilder.Glyph.of(fontWidth, fontHeight).logicalHeight(fontHeight).charWidth(fontWidth);

            for (int x = 0; x < fontWidth;  x++) {
                for (int y = 0; y < fontHeight; y++) {
                    if (image.getRGB(xStart + x, yStart + y) != 0) {
                        glyph.set(x, y);
                    }
                    if (image.getRGB(xStart + x + 128, yStart + y) != 0) {
                        glyphBack.set(x, y);
                    }
                }
            }
            builder.put(i, glyph);
            builderBack.put(i, glyphBack);
        }

    }
}
