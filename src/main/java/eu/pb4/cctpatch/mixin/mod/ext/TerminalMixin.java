package eu.pb4.cctpatch.mixin.mod.ext;

import dan200.computercraft.core.terminal.Terminal;
import eu.pb4.cctpatch.impl.poly.font.Fonts;
import eu.pb4.cctpatch.impl.poly.TerminalRenderer;
import eu.pb4.cctpatch.impl.poly.ext.TerminalExt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"MissingUnique", "AddedMixinMembersNamePattern"})
@Mixin(value = Terminal.class, remap = false)
public abstract class TerminalMixin implements TerminalExt {
    private final TerminalRenderer renderer = new TerminalRenderer((Terminal) (Object) this,
            Fonts.TERMINAL_FONT, Fonts.FONT_WIDTH, Fonts.FONT_HEIGHT, 8);

    private final TerminalRenderer rendererMini = new TerminalRenderer((Terminal) (Object) this,
            Fonts.MINI_TERMINAL_FONT, Fonts.MINI_FONT_WIDTH, Fonts.MINI_FONT_HEIGHT, 8);

    @Inject(method = "<init>(IIZLjava/lang/Runnable;)V", at = @At("TAIL"))
    private void setupImage(int width, int height, boolean colour, Runnable changedCallback, CallbackInfo ci) {
        this.renderer.setColor(colour);
        this.renderer.init(width, height);
        this.rendererMini.setColor(colour);
        this.rendererMini.init(width, height);
    }

    @Inject(method = "resize", at = @At("TAIL"))
    private void resizeImage(int width, int height, CallbackInfo ci) {
        this.renderer.init(width, height);
        this.rendererMini.init(width, height);
    }

    @Override
    public TerminalRenderer getRenderer() {
        return renderer;
    }

    @Override
    public TerminalRenderer getMiniRenderer() {
        return rendererMini;
    }
}
