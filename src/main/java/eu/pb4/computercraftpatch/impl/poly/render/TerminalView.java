package eu.pb4.computercraftpatch.impl.poly.render;

import dan200.computercraft.core.terminal.Terminal;
import dan200.computercraft.shared.computer.menu.ServerInputHandler;
import eu.pb4.computercraftpatch.impl.poly.TerminalExt;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;

public final class TerminalView extends ScreenElement {
    public final TerminalExt terminal;
    public final ServerInputHandler inputState;

    public TerminalView(int x, int y, Terminal terminal, ServerInputHandler inputState) {
        super(x, y);
        this.terminal =  TerminalExt.of(terminal);
        this.inputState = inputState;
    }

    public TerminalView(int x, int y, TerminalExt terminal, ServerInputHandler inputState) {
        super(x, y);
        this.terminal = terminal;
        this.inputState = inputState;
    }

    @Override
    public void render(DrawableCanvas canvas, long tick, int mouseX, int mouseY) {
        CanvasUtils.draw(canvas, this.x, this.y, this.terminal.getRendered(tick));
    }

    @Override
    public int width() {
        return this.terminal.getRenderedWidth();
    }

    @Override
    public int height() {
        return this.terminal.getRenderedHeight();
    }

    @Override
    public void click(int x, int y, ClickType type) {
        int lx = x / 6 + 1;
        int ly = y / 9 + 1;

        if (type == ClickType.RIGHT_DOWN) {
            this.inputState.mouseClick(0, lx, ly);
        } else if (type == ClickType.LEFT_DOWN) {
            this.inputState.mouseClick(1, lx, ly);
        }
    }
}
