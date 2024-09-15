package eu.pb4.cctpatch.impl.poly;

import dan200.computercraft.core.terminal.Palette;
import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.core.util.Colour;
import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.CanvasImage;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.font.CanvasFont;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import net.minecraft.util.math.ColorHelper;

public class TerminalRenderer {
    private final int fontWidth;
    private final int fontHeight;
    private final Terminal terminal;
    private final int size;
    private final CanvasFont font;
    private long lastCanvasUpdate = -1;
    private CanvasImage canvasImage;

    public TerminalRenderer(Terminal terminal, CanvasFont font, int fontWidth, int fontHeight, int size) {
        this.fontWidth = fontWidth;
        this.fontHeight = fontHeight;
        this.font = font;
        this.size = size;
        this.terminal = terminal;
    }


    public void init(int width, int height) {
        this.canvasImage = new CanvasImage(width * this.fontWidth, height * this.fontHeight);
        this.lastCanvasUpdate = -1;
    }

    public void setColor(boolean colour) {
    }

    public DrawableCanvas getImage(long tick) {
        if (this.lastCanvasUpdate < tick) {
            CanvasUtils.clear(this.canvasImage, CanvasColor.BLACK_LOWEST);
            var palette = this.terminal.getPalette();
            for (int y = 0; y < terminal.getHeight(); y++) {
                var line = terminal.getLine(y);
                var bgColor = terminal.getBackgroundColourLine(y);
                var color = terminal.getTextColourLine(y);
                for (int x = 0; x < terminal.getWidth(); x++) {
                    CanvasUtils.fill(this.canvasImage, x * this.fontWidth, y * this.fontHeight, 
                            x * this.fontWidth + this.fontWidth, y * this.fontHeight + this.fontHeight,
                            getColor(bgColor.charAt(x), Colour.BLACK, palette));
                    this.font.drawGlyph(this.canvasImage, line.charAt(x), x * (this.fontWidth), y * (this.fontHeight), this.size, 0,
                            getColor(color.charAt(x), Colour.BLACK, palette));
                }
            }

            if (terminal.getCursorBlink() && tick % 20 > 10) {
                CanvasUtils.fill(this.canvasImage,
                        terminal.getCursorX() * this.fontWidth, terminal.getCursorY() * this.fontHeight,
                        terminal.getCursorX() * this.fontWidth + this.fontWidth, terminal.getCursorY() * this.fontHeight + this.fontHeight,
                        CanvasColor.WHITE_GRAY_HIGH
                );
            }

            this.lastCanvasUpdate = tick;

        }
        return this.canvasImage;
    }

    public static CanvasColor getColor(char c, Colour def, Palette palette) {
        var x = palette.getRenderColours(15 - Terminal.getColour(c, def));

        return CanvasUtils.findClosestColor(x);
        //return CanvasUtils.findClosestColor(Colour.fromInt(15 - Terminal.getColour(c, def)).getHex());
    }


    public int renderedWidth() {
        return this.canvasImage.getWidth();
    }

    public int renderedHeight() {
        return this.canvasImage.getHeight();
    }

    public int fontHeight() {
        return fontHeight;
    }

    public int fontWidth() {
        return fontWidth;
    }
}
