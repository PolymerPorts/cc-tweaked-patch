package eu.pb4.cctpatch.impl.poly.gui;

import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.peripheral.diskdrive.DiskDriveMenu;
import eu.pb4.cctpatch.impl.poly.PolymerSetup;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DiskDriveInventoryGui extends SimpleGui {
    private final DiskDriveMenu wrapped;

    public DiskDriveInventoryGui(ServerPlayerEntity player, DiskDriveMenu drive) {
        super(ScreenHandlerType.HOPPER, player, false);
        this.wrapped = drive;
        
        this.setTitle(Text.empty().append(Text.literal("-2.").setStyle(Style.EMPTY.withFont(PolymerSetup.GUI_FONT).withColor(Formatting.WHITE))).append(ModRegistry.Blocks.DISK_DRIVE.get().getName()));
        this.setSlotRedirect(2, drive.slots.get(0));
        this.open();
    }

    @Override
    public void onTick() {
        if (!this.wrapped.canUse(this.player)) {
            this.close();
        }
    }
}
