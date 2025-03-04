package eu.pb4.cctpatch.impl.poly.gui;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.inventory.AbstractComputerMenu;
import dan200.computercraft.shared.computer.menu.ServerInputState;
import dan200.computercraft.shared.turtle.inventory.TurtleMenu;
import eu.pb4.cctpatch.impl.poly.Keys;
import eu.pb4.cctpatch.impl.poly.ext.ServerInputStateExt;
import eu.pb4.cctpatch.impl.poly.ext.TerminalExt;
import eu.pb4.cctpatch.impl.poly.render.*;
import eu.pb4.cctpatch.impl.poly.textures.GuiTextures;
import eu.pb4.cctpatch.impl.poly.textures.RepeatingCanvas;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.ChatSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class ComputerGui extends MapGui {

    private static final Packet<ClientPlayPacketListener> ADDITIONAL_SUGGESTIONS_PACKET;
    private static final Packet<ClientPlayPacketListener> ADDITIONAL_SUGGESTIONS_REMOVE_PACKET;

    private static final Map<String, BiConsumer<ComputerGui, String>> ACTIONS = new HashMap<>();

    static {

        for (int i = 0; i < 12; i++) {
            ACTIONS.put("f" + (i + 1), pressKey(Keys.F1 + i));
        }
        ACTIONS.put("enter", pressKey(Keys.ENTER));
        ACTIONS.put("backspace", pressKey(Keys.BACKSPACE));
        ACTIONS.put("bsp", pressKey(Keys.BACKSPACE));
        ACTIONS.put("back", pressKey(Keys.BACKSPACE));
        ACTIONS.put("esc", pressKey(Keys.ESCAPE));
        ACTIONS.put("ctrl", pressKey(Keys.LEFT_CONTROL));
        ACTIONS.put("shift", pressKey(Keys.LEFT_SHIFT));
        ACTIONS.put("shift_hold", holdKey(Keys.LEFT_SHIFT));
        ACTIONS.put("tab", pressKey(Keys.TAB));
        ACTIONS.put("up", pressKey(Keys.UP));
        ACTIONS.put("down", pressKey(Keys.DOWN));
        ACTIONS.put("left", pressKey(Keys.LEFT));
        ACTIONS.put("right", pressKey(Keys.RIGHT));

        ACTIONS.put("close", (gui, arg) -> gui.close());
        ACTIONS.put("exit", (gui, arg) -> gui.close());
        ACTIONS.put("quit", (gui, arg) -> gui.close());

        ACTIONS.put("press", (gui, arg) -> {
            if (arg != null && !arg.isEmpty()) {
                char character = arg.charAt(0);

                var args = arg.length() == 1 ? new String[]{arg} : arg.split(" ", 2);

                try {
                    if (args[0].length() > 1) {
                        character = (char) Integer.parseInt(arg);
                    }
                } catch (Throwable e) {

                }

                int count = 1;

                try {
                    count = Math.min(Integer.parseInt(args[1]), 255);
                } catch (Throwable t) {

                }

                for (int i = 0; i < count; i++) {
                    gui.pressButton(character);
                }
            }
        });

        ACTIONS.put("moveview", (gui, arg) -> {
            try {
                var args = arg.split(" ");
                double z = args.length > 0 && !args[0].isEmpty() ? Math.min(Math.max(Double.parseDouble(args[0]), 1), 8) : 1;
                double x = args.length > 1 && !args[1].isEmpty() ? Math.min(Math.max(Double.parseDouble(args[1]), -8), 8) : 0;
                gui.setDistance(new Vec3d(x, 0, z));
            } catch (Exception e) {
                gui.player.networkHandler.sendPacket(new GameMessageS2CPacket(Text.empty(), true));
            }
        });

        ACTIONS.put("view", ACTIONS.get("moveview"));


        var list = ACTIONS.keySet().stream().map(x -> ";" + x).collect(Collectors.toList());

        ADDITIONAL_SUGGESTIONS_PACKET = new ChatSuggestionsS2CPacket(
                ChatSuggestionsS2CPacket.Action.ADD, list
        );
        ADDITIONAL_SUGGESTIONS_REMOVE_PACKET = new ChatSuggestionsS2CPacket(
                ChatSuggestionsS2CPacket.Action.REMOVE, list
        );
    }

    public final ImageButton closeButton;
    public final ImageButton terminateButton;
    public final ServerInputState<AbstractComputerMenu> input;
    public final KeyboardView keyboard;
    public final AbstractComputerMenu wrapped;
    private final ServerComputer computer;
    public String currentInput = "";
    public IntSet keysToReleaseNextTick = new IntArraySet();

    public ComputerGui(ServerPlayerEntity player, AbstractComputerMenu menu) {
        super(player);
        this.wrapped = menu;
        //noinspection unchecked
        this.input = (ServerInputState<AbstractComputerMenu>) wrapped.getInput();
        this.computer = wrapped.getComputer();

        {
            var terminal = TerminalExt.of(this.wrapped.getComputer()).getRenderer();
            int centerX = canvas.getWidth() / 2;
            int centerY = canvas.getHeight() / 2 - 48;

            boolean turtle = this.wrapped instanceof TurtleMenu;

            int termX = centerX - terminal.renderedWidth() / 2;
            int termY = centerY - terminal.renderedHeight() / 2;

            if (turtle) {
                termX -= 36;
            }

            var terminalView = new TerminalView(
                termX, termY,
                terminal,
                this.input
            );

            terminalView.zIndex = 2;

            this.renderer.add(terminalView);

            var compText = switch (this.computer.getFamily()) {
                case NORMAL -> GuiTextures.COMPUTER;
                case ADVANCED -> GuiTextures.ADVANCED_COMPUTER;
                case COMMAND -> GuiTextures.COMMAND_COMPUTER;
            };

            if (turtle) {
                var xi = termX + terminal.renderedWidth() + 32;
                var yi = termY - 28;
                var inv = new TurtleInventoryView(xi, yi, this, (TurtleMenu) this.wrapped);
                this.renderer.add(inv);

                this.renderer.add(new ImageView(
                        xi, yi - compText.top().getHeight(),
                        new RepeatingCanvas(compText.top(), inv.width(), compText.top().getHeight())
                    )
                );

                this.renderer.add(new ImageView(
                        xi, yi + inv.height(),
                        new RepeatingCanvas(compText.bottom(), inv.width(), compText.bottom().getHeight())
                    )
                );

                this.renderer.add(new ImageView(
                        xi - compText.leftSide().getWidth(), yi,
                        new RepeatingCanvas(compText.leftSide(), compText.leftSide().getWidth(), inv.height())
                    )
                );

                this.renderer.add(new ImageView(
                        xi + inv.width(), yi,
                        new RepeatingCanvas(compText.rightSide(), compText.rightSide().getWidth(), inv.height())
                    )
                );

                this.renderer.add(new ImageView(xi - compText.leftTop().getWidth(), yi - compText.leftTop().getHeight(), compText.leftTop()));
                this.renderer.add(new ImageView(xi + inv.width(), yi - compText.rightTop().getHeight(), compText.rightTop()));

                this.renderer.add(new ImageView(xi - compText.leftBottom().getWidth(), yi + inv.height(), compText.leftBottom()));
                this.renderer.add(new ImageView(xi + inv.width(), yi + inv.height(), compText.rightBottom()));
            }

            {
                int sideX = termX - compText.sideButtonPlateSide().getWidth() - compText.leftSide().getWidth() + 3;

                var sideTop = new ImageView(sideX - 3, termY + 8, compText.sideButtonPlateTop());
                sideTop.zIndex = -1;
                this.renderer.add(sideTop);

                int sideY = termY + 8 + compText.sideButtonPlateTop().getHeight();

                int size = 0;

                {
                    this.closeButton = new ImageButton(sideX, sideY + size, GuiTextures.SHUTDOWN_ICON, (x, y, t) -> {
                        if (this.wrapped.isOn()) {
                            this.wrapped.getComputer().shutdown();
                        } else {
                            this.wrapped.getComputer().turnOn();
                        }
                    });

                    this.closeButton.zIndex = 2;
                    this.renderer.add(this.closeButton);
                    size += this.closeButton.height() + 2;


                    this.terminateButton = new ImageButton(sideX, sideY + size, GuiTextures.TERMINATE, (x, y, t) -> {
                        this.wrapped.getComputer().queueEvent("terminate");
                    });

                    this.terminateButton.zIndex = 2;
                    this.renderer.add(this.terminateButton);
                    size += this.terminateButton.height() + 2;
                }

                size -= 2;

                var side = new ImageView(
                    sideX - 3, sideY,
                    new RepeatingCanvas(compText.sideButtonPlateSide(), compText.sideButtonPlateSide().getWidth(), size)
                );
                side.zIndex = -1;
                this.renderer.add(side);

                var sideBottom = new ImageView(sideX - 3, sideY + size, compText.sideButtonPlateBottom());
                sideBottom.zIndex = -1;
                this.renderer.add(sideBottom);
            }

            this.renderer.add(new ImageView(
                    termX, termY - compText.top().getHeight(),
                    new RepeatingCanvas(compText.top(), terminal.renderedWidth(), compText.top().getHeight())
                )
            );

            this.renderer.add(new ImageView(
                    termX, termY + terminal.renderedHeight(),
                    new RepeatingCanvas(compText.bottom(), terminal.renderedWidth(), compText.bottom().getHeight())
                )
            );

            this.renderer.add(new ImageView(
                    termX - compText.leftSide().getWidth(), termY,
                    new RepeatingCanvas(compText.leftSide(), compText.leftSide().getWidth(), terminal.renderedHeight())
                )
            );

            this.renderer.add(new ImageView(
                    termX + terminal.renderedWidth(), termY,
                    new RepeatingCanvas(compText.rightSide(), compText.rightSide().getWidth(), terminal.renderedHeight())
                )
            );

            this.renderer.add(new ImageView(termX - compText.leftTop().getWidth(), termY - compText.leftTop().getHeight(), compText.leftTop()));
            this.renderer.add(new ImageView(termX + terminal.renderedWidth(), termY - compText.rightTop().getHeight(), compText.rightTop()));

            this.renderer.add(new ImageView(termX - compText.leftBottom().getWidth(), termY + terminal.renderedHeight(), compText.leftBottom()));
            this.renderer.add(new ImageView(termX + terminal.renderedWidth(), termY + terminal.renderedHeight(), compText.rightBottom()));

            this.renderer.add(terminalView);

            this.keyboard = new KeyboardView(centerX - (KeyboardView.KEYBOARD_WIDTH / 2), terminalView.y + terminalView.height() + 16, this);
            this.renderer.add(this.keyboard);
        }

        this.render();


        player.networkHandler.sendPacket(ADDITIONAL_SUGGESTIONS_PACKET);

        for (int i = 0; i < 9; i++) {
            this.setSlot(i, new ItemStack(Items.STICK));
        }

        this.open();
    }

    public static void open(ServerPlayerEntity player, AbstractComputerMenu menu) {
        if (player.isOnGround()) {
            new ComputerGui(player, menu);
        }
    }

    private static BiConsumer<ComputerGui, String> pressKey(int key) {
        return (gui, arg) -> {
            int i;
            try {
                i = Integer.parseInt(arg);
            } catch (Exception e) {
                i = 1;
            }

            for (int a = 0; a < i; a++) {
                gui.input.keyDown(key, false);
            }
            gui.keysToReleaseNextTick.add(key);
        };
    }

    private static BiConsumer<ComputerGui, String> holdKey(int key) {
        return (gui, arg) -> {
            if (!ServerInputStateExt.of(gui.input).isKeyDown(key)) {
                gui.input.keyDown(key, true);
            } else {
                gui.input.keyUp(key);
            }
        };
    }

    public void render() {
        if (this.computer.isOn()) {
            this.closeButton.texture = GuiTextures.SHUTDOWN_ACTIVE;
        } else {
            this.closeButton.texture = GuiTextures.SHUTDOWN_ICON;
        }

        super.render();
    }

    @Override
    public void onTick() {
        if (this.wrapped.canUse(this.player)) {
            this.render();
            super.onTick();
        } else {
            this.close();
        }

        for (var key : this.keysToReleaseNextTick) {
            this.input.keyUp(key);
        }
        this.keysToReleaseNextTick.clear();
    }

    @Override
    public void onClose() {
        this.player.networkHandler.sendPacket(ADDITIONAL_SUGGESTIONS_REMOVE_PACKET);

        super.onClose();
    }

    public void onChatInput(String message) {
        if (message.startsWith(";")) {
            for (var line : message.substring(1).split(";")) {
                var args = line.split(" ", 2);

                var action = ACTIONS.get(args[0]);
                if (action != null) {
                    action.accept(this, args.length != 2 ? "" : args[1]);
                }
            }
        } else {
            if (!message.startsWith("/")) {
                for (var character : message.codePoints().toArray()) {
                    if (character >= 32 && character <= 126 || character >= 160 && character <= 255) {
                        this.input.charTyped((byte) character);
                    }
                }

            }

            this.input.keyDown(Keys.ENTER, false);
            this.keysToReleaseNextTick.add(Keys.ENTER);
            this.currentInput = "";
        }
    }

    public void onCommandInput(String command) {
        this.input.keyDown(Keys.ENTER, false);
        this.keysToReleaseNextTick.add(Keys.ENTER);
        this.currentInput = "";
    }

    public void onCommandSuggestion(int id, String fullCommand) {
        var old = this.currentInput;
        var commandBuilder = new StringBuilder();

        for (var character : fullCommand.substring(1).codePoints().toArray()) {
            if (character >= 32 && character <= 126 || character >= 160 && character <= 255) {
                commandBuilder.append(Character.toChars(character));
            }
        }
        var command = commandBuilder.toString();

        if (!old.equals(command)) {
            int i;
            for (i = 0; i < old.length(); i++) {
                if (command.length() <= i || command.charAt(i) != old.charAt(i)) {
                    break;
                }
            }

            var inputExt = ServerInputStateExt.of(this.input);

            for (var tmp = i; tmp < old.length(); tmp++) {
                if (!this.keysToReleaseNextTick.contains(Keys.BACKSPACE) && !inputExt.isKeyDown(Keys.BACKSPACE)) {
                    this.input.keyDown(Keys.BACKSPACE, false);
                    this.keysToReleaseNextTick.add(Keys.BACKSPACE);
                } else {
                    this.input.keyDown(Keys.BACKSPACE, false);
                }
            }

            for (; i < command.length(); i++) {
                pressButton(command.charAt(i));
            }

            this.currentInput = command;
        }
    }

    public void pressButton(char character) {
        if (character >= 32 && character <= 126 || character >= 160 && character <= 255) {
            var key = KeyboardView.CHAR_TO_KEY.get(character);
            if (key != null) {
                this.input.keyDown(key.key(), false);
                this.keysToReleaseNextTick.add(key.key());
            }

            if (key.upperCase() == character && key.lowerCase() != character) {
                if (!this.keysToReleaseNextTick.contains(Keys.LEFT_SHIFT) && !ServerInputStateExt.of(this.input).isKeyDown(Keys.LEFT_SHIFT)) {
                    this.input.keyDown(Keys.LEFT_SHIFT, false);

                    this.keysToReleaseNextTick.add(Keys.LEFT_SHIFT);
                }
            }

            this.input.charTyped((byte) character);
        }
    }

    @Override
    public boolean onClickEntity(int entityId, EntityInteraction type, boolean isSneaking, @Nullable Vec3d interactionPos) {
        return super.onClickEntity(entityId, type, isSneaking, interactionPos);
    }

    public void onPlayerAction(PlayerActionC2SPacket.Action action, Direction direction, BlockPos pos) {
        if (action == PlayerActionC2SPacket.Action.DROP_ALL_ITEMS) {
            this.close();
        }
    }
}
