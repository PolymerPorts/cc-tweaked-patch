package eu.pb4.cctpatch.impl.poly.gui;

import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity;
import dan200.computercraft.shared.turtle.inventory.TurtleMenu;
import eu.pb4.cctpatch.impl.poly.PolymerSetup;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TurtleInventoryGui extends SimpleGui {
    private final TurtleMenu wrapped;

    public TurtleInventoryGui(ServerPlayerEntity player, TurtleMenu menu) {
        super(ScreenHandlerType.GENERIC_9X2, player, false);

        this.wrapped = menu;
        var turtle = (TurtleBlockEntity) menu.getComputer().getLevel().getBlockEntity(menu.getComputer().getPosition());
        this.setTitle(Text.empty().append(Text.literal("-1.").setStyle(Style.EMPTY.withFont(PolymerSetup.GUI_FONT)
                .withFormatting(Formatting.WHITE))).append(turtle.getDisplayName())
        );

        this.setSlotRedirect(9, menu.slots.get(menu.slots.size() - 2));
        this.setSlotRedirect(17, menu.slots.get(menu.slots.size() - 1));

        for (int i = 0 ; i < 9; i++) {
            this.setSlotRedirect(i, menu.slots.get(i));
        }

        for (int i = 9; i < turtle.size(); i++) {
            this.setSlotRedirect(i + 1, new Slot(turtle.getAccess().getInventory(), i, 0, 0));
        }

        this.open();
    }

    @Override
    public void onTick() {
        if (!this.wrapped.canUse(player)) {
            this.close();
        }
    }

    @Override
    public void onClose() {
        ComputerGui.open(this.player, this.wrapped);
    }
}
