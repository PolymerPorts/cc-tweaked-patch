package eu.pb4.cctpatch.impl.poly.gui;

import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.peripheral.printer.PrinterMenu;
import eu.pb4.cctpatch.impl.poly.PolymerSetup;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class PrinterInventoryGui extends SimpleGui {
    private final PrinterMenu wrapped;

    public PrinterInventoryGui(ServerPlayerEntity player, PrinterMenu printer) {
        super(ScreenHandlerType.GENERIC_9X3, player, false);
        this.wrapped = printer;
        
        this.setTitle(Text.empty().append(Text.literal("-0.").setStyle(Style.EMPTY.withFont(PolymerSetup.GUI_FONT).withColor(Formatting.WHITE))).append(ModRegistry.Blocks.PRINTER.get().getName()));
        this.setSlotRedirect(10, printer.slots.get(0));

        for (var i = 0; i < 6; i++) {
            this.setSlotRedirect(i + 3, printer.slots.get(1 + i));
            this.setSlotRedirect(i + 3 + 18, printer.slots.get(7 + i));
        }

        this.open();
    }

    @Override
    public void onTick() {
        if (!this.wrapped.canUse(this.player)) {
            this.close();
        }
    }
}
