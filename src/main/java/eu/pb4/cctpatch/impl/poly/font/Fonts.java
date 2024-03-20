package eu.pb4.cctpatch.impl.poly.font;

import eu.pb4.cctpatch.impl.ComputerCraftPolymerPatch;
import eu.pb4.mapcanvas.api.font.BitmapFontBuilder;
import eu.pb4.mapcanvas.api.font.CanvasFont;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import net.fabricmc.loader.api.FabricLoader;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Fonts {
    public static final int FONT_HEIGHT = 9;
    public static final int FONT_WIDTH = 6;

    public static final int MINI_FONT_HEIGHT = 6;
    public static final int MINI_FONT_WIDTH = 4;

    public static final CanvasFont MINI_TERMINAL_FONT;
    public static final CanvasFont TERMINAL_FONT;
    public static final CanvasFont TERMINAL_BACKGROUND_FONT;

    static {
        {
            CanvasFont font;
            CanvasFont fontSmall;
            CanvasFont fontBack;

            var texturePath = FabricLoader.getInstance().getModContainer("computercraft")
                    .get().findPath("assets/computercraft/textures/gui/term_font.png").get();
            var texturePathZoo = FabricLoader.getInstance().getModContainer(ComputerCraftPolymerPatch.MOD_ID)
                    .get().findPath("map/openzoo/4x6.png").get();
            var emptyGlyph = BitmapFontBuilder.Glyph.of(1, 1);

            var b = new byte[256];

            for (int i = 0; i < 256; i++) {
                b[i] = (byte) i;
            }

            /*try {
                var remapped = new String(b, StandardCharsets.ISO_8859_1);

                var map = new Int2IntArrayMap();

                var cp437 = Files.readAllLines(FabricLoader.getInstance().getModContainer(ComputerCraftPolymerPatch.MOD_ID)
                        .get().findPath("cp437.ucm").get());

                for (var l : cp437) {
                    if (l.startsWith("#") || l.startsWith("\u001A")) {
                        continue;
                    }
                    var x = l.split("[\t ]");
                    map.put(Integer.parseInt(x[1].replace("0x", ""), 16),
                            Integer.parseInt(x[0].replace("0x", ""), 16));
                }


                for (int i = 0; i < 256; i++) {
                    b[i] = (byte) map.get(remapped.charAt(i));
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }*/

            try {
                var builderSmall = BitmapFontBuilder.create();
                var builder = BitmapFontBuilder.create();
                var builderBack = BitmapFontBuilder.create();

                buildFontCC(texturePath, builder, builderBack, FONT_HEIGHT, FONT_WIDTH);
                buildFontZoo(texturePathZoo, builderSmall, MINI_FONT_HEIGHT, MINI_FONT_WIDTH, b);

                fontSmall = builderSmall.defaultGlyph(emptyGlyph).build();
                font = builder.defaultGlyph(emptyGlyph).build();
                fontBack = builderBack.defaultGlyph(emptyGlyph).build();
            } catch (Throwable e) {
                e.printStackTrace();
                font = DefaultFonts.VANILLA;
                fontSmall = DefaultFonts.VANILLA;
                fontBack = BitmapFontBuilder.create().defaultGlyph(emptyGlyph).build();
            }

            TERMINAL_FONT = font;
            TERMINAL_BACKGROUND_FONT = fontBack;
            MINI_TERMINAL_FONT = fontSmall;
        }
    }

    private static void buildFontCC(Path texturePath, BitmapFontBuilder builder, BitmapFontBuilder builderBack, int fontHeight, int fontWidth) throws IOException {

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

    private static void buildFontZoo(Path texturePath, BitmapFontBuilder builder, int fontHeight, int fontWidth, byte[] set) throws IOException {

        var image = ImageIO.read(Files.newInputStream(texturePath));

        for (int i = 0; i < 256; i++) {
            int column = i % 32;
            int row = i / 32;

            int xStart = column * fontWidth;
            int yStart = row * fontHeight;

            var glyph = BitmapFontBuilder.Glyph.of(fontWidth, fontHeight).logicalHeight(fontHeight).charWidth(fontWidth);

            for (int x = 0; x < fontWidth;  x++) {
                for (int y = 0; y < fontHeight; y++) {
                    if (image.getRGB(xStart + x, yStart + y) == 0xFFFFFFFF) {
                        glyph.set(x, y);
                    }
                }
            }
            builder.put(Byte.toUnsignedInt(set[i]), glyph);
        }
    }
}
