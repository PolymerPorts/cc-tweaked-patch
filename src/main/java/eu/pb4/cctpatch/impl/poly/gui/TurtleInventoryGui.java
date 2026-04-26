package eu.pb4.cctpatch.impl.poly.gui;

import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity;
import dan200.computercraft.shared.turtle.inventory.TurtleMenu;
import eu.pb4.cctpatch.impl.poly.PolymerSetup;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;

public class TurtleInventoryGui extends SimpleGui {
    private final TurtleMenu wrapped;

    public TurtleInventoryGui(ServerPlayer player, TurtleMenu menu) {
        super(MenuType.GENERIC_9x4, player, false);

        this.wrapped = menu;
        var turtle = (TurtleBlockEntity) menu.getComputer().getLevel().getBlockEntity(menu.getComputer().getPosition());
        this.setTitle(Component.empty().append(Component.literal("-1.").setStyle(Style.EMPTY.withFont(new FontDescription.Resource(PolymerSetup.GUI_FONT))
                .applyFormat(ChatFormatting.WHITE))).append(turtle.getDisplayName())
        );

        this.setSlot(7 + 9, menu.slots.get(menu.slots.size() - 2));
        this.setSlot(7 + 9 * 2, menu.slots.get(menu.slots.size() - 1));

        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                this.setSlot(x + 1 + y * 9, menu.slots.get(x + y * 4));
            }
        }

        this.open();
    }

    @Override
    public void onTick() {
        if (!this.wrapped.stillValid(player)) {
            this.close();
        }
    }

    @Override
    public void afterRemoval() {
        this.player.level().getServer().schedule(new TickTask(this.player.level().getServer().getTickCount(), () -> ComputerGui.open(this.player, this.wrapped)));
    }
}
