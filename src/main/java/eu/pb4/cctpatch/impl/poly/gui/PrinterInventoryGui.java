package eu.pb4.cctpatch.impl.poly.gui;

import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.peripheral.printer.PrinterMenu;
import eu.pb4.cctpatch.impl.poly.PolymerSetup;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;

public class PrinterInventoryGui extends SimpleGui {
    private final PrinterMenu wrapped;

    public PrinterInventoryGui(ServerPlayer player, PrinterMenu printer) {
        super(MenuType.GENERIC_9x3, player, false);
        this.wrapped = printer;
        
        this.setTitle(Component.empty().append(Component.literal("-0.").setStyle(Style.EMPTY.withFont(new FontDescription.Resource(PolymerSetup.GUI_FONT)).withColor(ChatFormatting.WHITE))).append(ModRegistry.Blocks.PRINTER.get().getName()));
        this.setSlot(10, printer.slots.get(0));

        for (var i = 0; i < 6; i++) {
            this.setSlot(i + 3, printer.slots.get(1 + i));
            this.setSlot(i + 3 + 18, printer.slots.get(7 + i));
        }

        this.open();
    }

    @Override
    public void onTick() {
        if (!this.wrapped.stillValid(this.player)) {
            this.close();
        }
    }
}
