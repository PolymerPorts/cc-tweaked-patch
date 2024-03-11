package eu.pb4.computercraftpatch.impl.poly.gui;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.server.network.ServerPlayerEntity;


/*public class PrinterInventoryGui extends SimpleGui {
    private final TilePrinter printer;

    public PrinterInventoryGui(ServerPlayerEntity player, TilePrinter printer) {
        super(MenuType.GENERIC_9x3, player, false);

        var pack = PolymerResourcePackUtils.hasPack(player);
        this.setTitle(pack
            ? Component.empty().append(Component.literal("-0.").setStyle(Style.EMPTY.withFont(PolymerSetup.GUI_FONT).withColor(ChatFormatting.WHITE))).append(printer.getDisplayName())
            : printer.getDisplayName()
        );
        this.setSlotRedirect(10, new ValidatingSlot(printer, 0, 0, 0, TilePrinter::isInk));

        for (var i = 0; i < 6; i++) {
            this.setSlotRedirect(i + 3, new ValidatingSlot(printer, i + 1, 0, 0, TilePrinter::isPaper));
            this.setSlotRedirect(i + 3 + 18, new ValidatingSlot(printer, i + 7, 0, 0, (x) -> false));
        }

        if (!pack) {
            while (this.getFirstEmptySlot() != -1) {
                this.addSlot(PolymerSetup.FILLER_ITEM);
            }
        }

        this.printer = printer;

        this.open();
    }

    @Override
    public void onTick() {
        if (this.printer.isRemoved() || !this.printer.isUsable(this.player)) {
            this.close();
        }
    }
}*/
