package eu.pb4.cctpatch.impl.poly.render;

import com.google.common.base.Supplier;
import eu.pb4.cctpatch.impl.poly.Keys;
import eu.pb4.cctpatch.impl.poly.ext.ServerInputStateExt;
import eu.pb4.cctpatch.impl.poly.gui.ComputerGui;
import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.font.DefaultFonts;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;

public class KeyboardView extends ScreenElement {
    private static final Key[][] KEYS = new Key[][] {
        new Key[] { k("ESC", Keys.ESCAPE), e(16), k("F1", Keys.F1), k("F2", Keys.F2), k("F3", Keys.F3), k("F4", Keys.F4), e(12), k("F5", Keys.F5), k("F6", Keys.F6), k("F7", Keys.F7), k("F8", Keys.F8), e(12), k("F9", Keys.F9), k("F10", Keys.F10), k("F11", Keys.F11), k("F12", Keys.F12), e(5), k("Prn", Keys.PRINT_SCREEN),  k("SLk", Keys.SCROLL_LOCK), k("⏸", Keys.PAUSE)  },
        new Key[] { k("~\n`", Keys.GRAVE_ACCENT, '`', '~'), k("!\n1", Keys.NUM_1, '1', '!'), k("@\n2", Keys.NUM_2, '2', '@'), k("#\n3", Keys.NUM_3, '3', '#'), k("$\n4", Keys.NUM_4, '4', '$'), k("%\n5", Keys.NUM_5, '5', '%'), k("^\n6", Keys.NUM_6, '6', '^'), k("&\n7", Keys.NUM_7, '7', '&'), k("*\n8", Keys.NUM_8, '8', '*'), k("(\n9", Keys.NUM_9, '9', '('), k(")\n0", Keys.NUM_0, '0', ')'), k("_\n-", Keys.MINUS, '-', '_'), k("+\n=", Keys.EQUAL), k("<--", Keys.BACKSPACE, 44), e(5), k("Ins", Keys.INSERT), k("Hm", Keys.HOME), k("P⏶", Keys.PAGE_UP) },
        new Key[] { k("Tab", Keys.TAB, 32), k("Q", Keys.Q), k("W", Keys.W), k("E", Keys.E), k("R", Keys.R), k("T", Keys.T), k("Y", Keys.Y), k("U", Keys.U), k("I", Keys.I), k("O", Keys.O), k("P", Keys.P), k("{\n[", Keys.LEFT_BRACKET, '[', '{'), k("}\n]", Keys.RIGHT_BRACKET, ']', '}'), k("Enter", Keys.ENTER, 32), e(5), k("Del", Keys.DELETE), k("End", Keys.END), k("P⏷", Keys.PAGE_DOWN)  },
        new Key[] { k("Caps", Keys.CAPS_LOCK, 38), k("A", Keys.A), k("S", Keys.S), k("D", Keys.D), k("F", Keys.F), k("G", Keys.G), k("H", Keys.H), k("J", Keys.J), k("K", Keys.K), k("L", Keys.L), k(":\n;", Keys.SEMICOLON, ';', ':'), k("\"\n'", Keys.APOSTROPHE, '\'', '"'), k("|\n\\", Keys.BACKSLASH, '\\', '|'), k("", Keys.ENTER, 26) },
        new Key[] { k("Shift", Keys.LEFT_SHIFT, 54), k("Z", Keys.Z), k("X", Keys.X), k("C", Keys.C), k("V", Keys.V), k("B", Keys.B), k("N", Keys.N), k("M", Keys.M), k("<\n,", Keys.COMMA, ',', '<'), k(">\n.", Keys.PERIOD, '.', '>'), k("?\n/", Keys.SLASH, '/', '?'), k("Shift", Keys.RIGHT_SHIFT, 54), e(27), k("▲", Keys.UP) },
        new Key[] { k("Ctrl", Keys.LEFT_CONTROL, 33), k("⛏", Keys.MENU, 27), k("Alt", Keys.LEFT_ALT, 27), k(" ", Keys.SPACE, 24 * 6), k("Alt", Keys.RIGHT_ALT, 27), k("⚗", Keys.RIGHT_SUPER, 27), k("Ctrl", Keys.RIGHT_CONTROL, 33), e(5), k("◀", Keys.LEFT), k("▼", Keys.DOWN), k("▶", Keys.RIGHT) }
    };

    public static final int KEYBOARD_WIDTH = ((Supplier<Integer>)() -> {
        int longest = 0;

        for (var keyLine : KEYS) {
            int length = 0;
            for (var key : keyLine) {
                length += key.width() + 2;
            }

            longest = Math.max(longest, length);
        }

        return longest;
    }).get();

    public static final int[] LINE_WIDTH = ((Supplier<int[]>)() -> {
        var array = new int[KEYS.length];

        for (int i = 0; i < KEYS.length; i++) {
            int length = 0;
            for (var key : KEYS[i]) {
                length += key.width() + 2;
            }

            array[i] = length;
        }

        return array;
    }).get();

    public static final int[] KEY_SPACING = ((Supplier<int[]>)() -> {
        var array = new int[KEYS.length];

        for (int i = 0; i < KEYS.length; i++) {
            int length = 0;
            for (var key : KEYS[i]) {
                length += key.width();
            }

            array[i] = (KEYBOARD_WIDTH - length) / KEYS[i].length;
        }

        return array;
    }).get();

    public static final Char2ObjectMap<Key> CHAR_TO_KEY = ((Supplier<Char2ObjectMap<Key>>)() -> {
        var map = new Char2ObjectOpenHashMap<Key>();

        for (var i : KEYS) {
            for (var x : i) {
                map.put(x.lowerCase, x);
                map.put(x.upperCase, x);
            }
        }

        return map;
    }).get();

    private final ComputerGui gui;

    public KeyboardView(int x, int y, ComputerGui gui) {
        super(x, y);
        this.gui = gui;
    }

    @Override
    public void render(DrawableCanvas canvas, long tick, int mouseX, int mouseY) {
        int buttonCollisionHeight = 16;
        int y = 0;
        var inputExt = ServerInputStateExt.of(this.gui.input);
        for (int l = 0; l < KEYS.length; l++) {
            int x = 0;//(KEYBOARD_WIDTH - LINE_WIDTH[l]) / 2;
            for (var key : KEYS[l]) {
                if (key.key() != -1) {
                    var isHeld = inputExt.isKeyDown(key.key());
                    var a = isHeld ? 1 : 0;

                    if (key.key() == Keys.ENTER) {
                        var tX2 = 0;//(KEYBOARD_WIDTH - LINE_WIDTH[3]) / 2;

                        for (var keyTmp : KEYS[3]) {
                            if (keyTmp.key != Keys.ENTER) {
                                tX2 += (keyTmp.width() + 2);
                            }
                        }

                        boolean hover = ScreenElement.isIn(mouseX, mouseY, this.x + tX2 , this.y + 32 - 3, this.x + tX2 + 32, this.y + 32 + buttonCollisionHeight)
                            || ScreenElement.isIn(mouseX, mouseY, this.x + tX2 , this.y + 48 - 3, this.x + tX2 + 26, this.y + 48 + buttonCollisionHeight);

                        var color = isHeld
                            ? CanvasColor.WHITE_GRAY_LOW
                            : hover ? CanvasColor.WHITE_GRAY_NORMAL : CanvasColor.WHITE_GRAY_HIGH;

                        var color2 = isHeld
                            ? CanvasColor.GRAY_LOW
                            : hover ? CanvasColor.WHITE_GRAY_LOW : CanvasColor.WHITE_GRAY_LOW;

                        if (l == 3) {
                            if (!isHeld) {
                                CanvasUtils.fill(canvas, this.x + x, this.y + y * 16 - 3, this.x + x + key.width() + 1, this.y + y * 16 + 14 + 1, color2);
                            }

                            CanvasUtils.fill(canvas, this.x + x + a, this.y + y * 16 - 3 + a, this.x + x + key.width() + a, this.y + y * 16 + 14 + a, color);
                        } else {
                            if (!isHeld) {
                                CanvasUtils.fill(canvas, this.x + x, this.y + y * 16, this.x + x + key.width() + 1, this.y + y * 16 + 14 + 1, color2);
                            }

                            CanvasUtils.fill(canvas, this.x + x + a, this.y + y * 16 + a, this.x + x + key.width() + a, this.y + y * 16 + 14 + a, color);
                        }
                    } else {
                        var hover = ScreenElement.isIn(mouseX, mouseY, this.x + x, this.y + y * 16, this.x + x + key.width(), this.y + y * 16 + buttonCollisionHeight);

                        var color = isHeld
                            ? CanvasColor.WHITE_GRAY_LOW
                            : hover
                            ? CanvasColor.WHITE_GRAY_NORMAL : CanvasColor.WHITE_GRAY_HIGH;

                        var color2 = isHeld
                            ? CanvasColor.GRAY_LOW
                            : hover ? CanvasColor.WHITE_GRAY_LOW : CanvasColor.WHITE_GRAY_LOW;

                        if (!isHeld) {
                            CanvasUtils.fill(canvas, this.x + x, this.y + y * 16, this.x + x + key.width() + 1, this.y + y * 16 + 14 + 1, color2);
                        }
                        CanvasUtils.fill(canvas, this.x + x + a, this.y + y * 16 + a, this.x + x + key.width() + a, this.y + y * 16 + 14 + a, color);
                    }

                    var lines = key.display.split("\n");
                    if (lines.length == 1) {
                        var line = lines[0];
                        var width = DefaultFonts.VANILLA.getTextWidth(line, 8);
                        DefaultFonts.VANILLA.drawText(canvas, line, this.x + x + (key.width() - width) / 2 + a, this.y + y * 16 + 4 + a, 8, CanvasColor.BLACK_HIGH);
                    } else {
                        var merged = String.join("|", lines);

                        var offset = DefaultFonts.VANILLA.getTextWidth(merged, 8);
                        var widthChange = offset / (lines.length / 2);
                        var heightChange = 8 / lines.length + 1;
                        var startHeight = 4 / heightChange;
                        var startWidth = offset / 2;

                        for (int i = 0; i < lines.length; i++) {
                            var line = lines[i];
                            var width = DefaultFonts.VANILLA.getTextWidth(line, 8);
                            DefaultFonts.VANILLA.drawText(canvas, line, this.x + x + (key.width() - width + startWidth - widthChange * i) / 2 + a, this.y + y * 16 + i * heightChange + startHeight + 1 + a, 8, CanvasColor.BLACK_HIGH);
                        }
                    }
                }

                x += (key.width() + 2);
            }
            y++;
        }
    }

    @Override
    public void click(int x, int y, ClickType type) {
        var height = KEYS.length;
        var inputExt = ServerInputStateExt.of(this.gui.input);
        for (int ly = 0; ly < height; ly++) {
            var lys = ly * 16;
            if (lys <= y && lys + 16 > y) {
                int lxs = 0;//(KEYBOARD_WIDTH - LINE_WIDTH[ly]) / 2;
                for (var key : KEYS[ly]) {
                    if (lxs <= x && lxs + key.width() > x) {
                        var id = key.key();
                        if (inputExt.isKeyDown(id)) {
                            if (type == ClickType.LEFT_DOWN) {
                                this.gui.input.keyUp(id);
                            }
                        } else {
                            this.gui.input.keyDown(id, type == ClickType.RIGHT_DOWN);
                            var shift = inputExt.isKeyDown(Keys.LEFT_SHIFT) || inputExt.isKeyDown(Keys.RIGHT_SHIFT);
                            var character = shift || inputExt.isKeyDown(Keys.CAPS_LOCK)
                                ? key.upperCase() : key.lowerCase();
                            if (character >= 32 && character <= 126 || character >= 160 && character <= 255) {
                                this.gui.input.charTyped((byte) character);
                            }

                            if (type == ClickType.LEFT_DOWN) {
                                this.gui.keysToReleaseNextTick.add(key.key());
                            }
                       }


                        return;
                    }

                    lxs += (key.width() + 2);
                }
                return;
            }
        }
    }

    @Override
    public int width() {
        return KEYBOARD_WIDTH;
    }

    @Override
    public int height() {
        return KEYS.length * 18;
    }


    static private Key k(String display, int key, int width) {
        return new Key(display, key, width, (char) Character.toLowerCase(key), (char) Character.toUpperCase(key));
    }

    static private Key k(String display, int key) {
        return new Key(display, key, 20, (char) Character.toLowerCase(key), (char) Character.toUpperCase(key));
    }
    static private Key k(String display, int key, char lowerCase, char upperCase) {
        return new Key(display, key, 20, lowerCase, upperCase);
    }
    static private Key e() {
        return new Key("", -1, 20, (char) 0, (char) 0);
    }
    static private Key e(int width) {
        return new Key("", -1, width, (char) 0, (char) 0);
    }

    public record Key(String display, int key, int width, char lowerCase, char upperCase) {}
}
