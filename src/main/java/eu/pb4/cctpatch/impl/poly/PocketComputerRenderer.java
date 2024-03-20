package eu.pb4.cctpatch.impl.poly;

import dan200.computercraft.shared.pocket.core.PocketServerComputer;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import eu.pb4.cctpatch.impl.poly.ext.TerminalExt;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;

public class PocketComputerRenderer {
    private final PocketServerComputer computer;
    private ServerPlayerEntity player;
    private PlayerCanvas canvas;

    public PocketComputerRenderer(PocketServerComputer pocketServerComputer) {
        this.computer = pocketServerComputer;
    }

    public void onRemoved(Entity entity) {
        if (this.canvas != null) {
            this.canvas.destroy();
            this.canvas = null;
        }
        this.player = null;
    }

    public void onRendererChanged(Entity entity) {

    }
    public void tick(Entity entity) {
        if (entity instanceof ServerPlayerEntity player
                && ((player.getMainHandStack().getItem() instanceof PocketComputerItem
                    && PocketComputerItem.getServerComputer(player.server, player.getMainHandStack()) == this.computer)
                || (player.getOffHandStack().getItem() instanceof PocketComputerItem
                    && PocketComputerItem.getServerComputer(player.server, player.getOffHandStack()) == this.computer))
        ) {
            if (this.canvas == null) {
                this.player = player;
                this.canvas = DrawableCanvas.create();
                this.canvas.addPlayer(player);
            } else if (this.player != player) {
                this.canvas.removePlayer(this.player);
                this.canvas.addPlayer(player);
                this.player = player;
            }

            var image = TerminalExt.of(this.computer).getMiniRenderer().getImage(player.getWorld().getTime());
            CanvasUtils.draw(this.canvas, 0, 0, image);
            this.canvas.sendUpdates();
        } else if (this.canvas != null) {
            this.canvas.destroy();
            this.canvas = null;
            this.player = null;
        }
    }
    public void updateValues(Entity entity) {

    }

    public int id() {
        return this.canvas != null ? this.canvas.getId() : 0;
    }
}
