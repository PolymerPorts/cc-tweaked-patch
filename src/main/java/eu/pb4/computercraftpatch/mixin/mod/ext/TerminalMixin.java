package eu.pb4.computercraftpatch.mixin.mod.ext;

import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.core.terminal.TextBuffer;
import dan200.computercraft.core.util.Colour;
import eu.pb4.computercraftpatch.impl.poly.Fonts;
import eu.pb4.computercraftpatch.impl.poly.TerminalExt;
import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.CanvasImage;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"MissingUnique", "AddedMixinMembersNamePattern"})
@Mixin(value = Terminal.class, remap = false)
public abstract class TerminalMixin implements TerminalExt {
    @Shadow protected int height;

    @Shadow public abstract TextBuffer getLine(int y);

    @Shadow public abstract TextBuffer getBackgroundColourLine(int y);

    @Shadow public abstract TextBuffer getTextColourLine(int y);

    @Shadow protected int width;
    @Shadow protected boolean cursorBlink;
    @Shadow protected int cursorX;
    @Shadow protected int cursorY;
    private long lastCanvasUpdate = -1;
    private CanvasImage canvasImage;

    @Inject(method = "<init>(IIZLjava/lang/Runnable;)V", at = @At("TAIL"))
    private void setupImage(int width, int height, boolean colour, Runnable changedCallback, CallbackInfo ci) {
        this.canvasImage = new CanvasImage(width * Fonts.FONT_WIDTH, height * Fonts.FONT_HEIGHT);
    }

    @Inject(method = "resize", at = @At("TAIL"))
    private void resizeImage(int width, int height, CallbackInfo ci) {
        this.canvasImage = new CanvasImage(width * Fonts.FONT_WIDTH, height * Fonts.FONT_HEIGHT);
        this.lastCanvasUpdate = -1;
    }

    public DrawableCanvas getRendered(long tick) {
        if (this.lastCanvasUpdate < tick) {
            CanvasUtils.clear(this.canvasImage, CanvasColor.BLACK_LOWEST);
            for (int y = 0; y < this.height; y++) {
                var line = this.getLine(y);
                var bgColor = this.getBackgroundColourLine(y);
                var color = this.getTextColourLine(y);
                for (int x = 0; x < this.width; x++) {
                    CanvasUtils.fill(this.canvasImage, x * Fonts.FONT_WIDTH, y * Fonts.FONT_HEIGHT, x * Fonts.FONT_WIDTH + Fonts.FONT_WIDTH, y * Fonts.FONT_HEIGHT + Fonts.FONT_HEIGHT,
                            CanvasUtils.findClosestColor(Colour.fromInt(15 - Terminal.getColour(bgColor.charAt(x), Colour.BLACK)).getHex()));
                    Fonts.TERMINAL_FONT.drawGlyph(this.canvasImage, line.charAt(x), x * (Fonts.FONT_WIDTH), y * (Fonts.FONT_HEIGHT), 8, 0,
                            CanvasUtils.findClosestColor(Colour.fromInt(15 - Terminal.getColour(color.charAt(x), Colour.BLACK)).getHex()));
                }
            }

            if (this.cursorBlink && tick % 20 > 10) {
                CanvasUtils.fill(this.canvasImage,
                        this.cursorX * Fonts.FONT_WIDTH, this.cursorY * Fonts.FONT_HEIGHT,
                        this.cursorX * Fonts.FONT_WIDTH + Fonts.FONT_WIDTH, this.cursorY * Fonts.FONT_HEIGHT + Fonts.FONT_HEIGHT,
                        CanvasColor.WHITE_GRAY_HIGH
                );
            }

            this.lastCanvasUpdate = tick;

        }
        return this.canvasImage;
    }

    public int getRenderedHeight() {
        return this.canvasImage.getHeight();
    }

    public int getRenderedWidth() {
        return this.canvasImage.getWidth();
    }
}
