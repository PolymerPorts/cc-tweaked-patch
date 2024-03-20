package eu.pb4.cctpatch.impl.poly.render;

import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.computer.menu.ServerInputHandler;
import eu.pb4.cctpatch.impl.poly.TerminalRenderer;
import eu.pb4.cctpatch.impl.poly.ext.TerminalExt;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;

public final class TerminalView extends ScreenElement {
    public final TerminalRenderer terminal;
    public final ServerInputHandler inputState;

    public TerminalView(int x, int y, TerminalRenderer terminal, ServerInputHandler inputState) {
        super(x, y);
        this.terminal =  terminal;
        this.inputState = inputState;
    }

    @Override
    public void render(DrawableCanvas canvas, long tick, int mouseX, int mouseY) {
        CanvasUtils.draw(canvas, this.x, this.y, this.terminal.getImage(tick));
        /*for (var x = 0; x < width(); x += 128) {
            for (var y = 0; y < height(); y += 1) {
                canvas.set(this.x + x, this.y + y, CanvasColor.RED_HIGH);
            }
        }

        for (var x = 0; x < width(); x += 1) {
            for (var y = 0; y < height(); y += 128) {
                canvas.set(this.x + x, this.y + y, CanvasColor.EMERALD_GREEN_HIGH);
            }
        }*/
    }

    @Override
    public int width() {
        return this.terminal.renderedWidth();
    }

    @Override
    public int height() {
        return this.terminal.renderedHeight();
    }

    @Override
    public void click(int x, int y, ClickType type) {
        int lx = x / this.terminal.fontWidth() + 1;
        int ly = y / terminal.fontHeight() + 1;

        if (type == ClickType.RIGHT_DOWN) {
            this.inputState.mouseClick(0, lx, ly);
        } else if (type == ClickType.LEFT_DOWN) {
            this.inputState.mouseClick(1, lx, ly);
        }
    }
}
