package eu.pb4.cctpatch.impl.poly.gui;

import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.peripheral.diskdrive.DiskDriveMenu;
import eu.pb4.cctpatch.impl.poly.PolymerSetup;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;

public class DiskDriveInventoryGui extends SimpleGui {
    private final DiskDriveMenu wrapped;

    public DiskDriveInventoryGui(ServerPlayer player, DiskDriveMenu drive) {
        super(MenuType.HOPPER, player, false);
        this.wrapped = drive;
        
        this.setTitle(Component.empty().append(Component.literal("-2.").setStyle(Style.EMPTY.withFont(new FontDescription.Resource(PolymerSetup.GUI_FONT)).withColor(ChatFormatting.WHITE))).append(ModRegistry.Blocks.DISK_DRIVE.get().getName()));
        this.setSlot(2, drive.slots.get(0));
        this.open();
    }

    @Override
    public void onTick() {
        if (!this.wrapped.stillValid(this.player)) {
            this.close();
        }
    }
}
