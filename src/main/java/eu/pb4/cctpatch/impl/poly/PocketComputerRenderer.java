package eu.pb4.cctpatch.impl.poly;

import dan200.computercraft.shared.pocket.core.PocketServerComputer;
import dan200.computercraft.shared.pocket.items.PocketComputerItem;
import eu.pb4.cctpatch.impl.config.PatchConfig;
import eu.pb4.cctpatch.impl.poly.ext.TerminalExt;
import eu.pb4.cctpatch.impl.poly.render.ImageView;
import eu.pb4.cctpatch.impl.poly.textures.GuiTextures;
import eu.pb4.cctpatch.impl.poly.textures.RepeatingCanvas;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPlayerInventoryS2CPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.commons.lang3.mutable.MutableObject;

import java.nio.charset.Charset;

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

    public void tick(Entity entity) {
        if (!PatchConfig.instance.displayPocketComputerScreenInHand || !(entity instanceof ServerPlayerEntity player) || !this.computer.isOn()) {
            if (this.canvas != null) {
                this.canvas.destroy();
            }
            this.canvas = null;
            this.player = null;
            return;
        }
        var mut = new MutableObject<ItemStack>();

        var slot = findStack(player, mut);
        if (slot != -1) {
            if (this.canvas == null) {
                this.player = player;
                this.canvas = DrawableCanvas.create();
                this.drawInitial();
                this.canvas.addPlayer(player);
                player.networkHandler.sendPacket(new SetPlayerInventoryS2CPacket(slot, mut.getValue()));

            } else if (this.player != player) {
                this.canvas.removePlayer(this.player);
                this.canvas.addPlayer(player);
                this.player = player;
            }

            this.drawUpdate();
            this.canvas.sendUpdates();
        } else if (this.canvas != null) {
            this.canvas.destroy();
            this.canvas = null;
            this.player = null;
        }
    }

    private int findStack(ServerPlayerEntity player, MutableObject<ItemStack> mut) {
        for (var slot = 0; slot < player.getInventory().size(); slot++) {
            if (player.getInventory().getStack(slot).getItem() instanceof PocketComputerItem
                    && PocketComputerItem.getServerComputer(player.getEntityWorld().getServer(), player.getInventory().getStack(slot)) == this.computer) {
                if (mut != null) {
                    mut.setValue(player.getInventory().getStack(slot));
                }
                return slot;
            }
        }
        return -1;
    }

    private void drawInitial() {
        var terminal = TerminalExt.of(this.computer).getMiniRenderer();
        int termX = 64 - terminal.renderedWidth() / 2;
        int termY = 64 - terminal.renderedHeight() / 2;
        var compText = switch (this.computer.getFamily()) {
            case NORMAL -> GuiTextures.COMPUTER;
            case ADVANCED -> GuiTextures.ADVANCED_COMPUTER;
            case COMMAND -> GuiTextures.COMMAND_COMPUTER;
        };

        new ImageView(
                termX, termY - compText.top().getHeight(),
                new RepeatingCanvas(compText.top(), terminal.renderedWidth(), compText.top().getHeight())
        ).render(this.canvas, 0, 0, 0);

        new ImageView(
                termX, termY + terminal.renderedHeight(),
                new RepeatingCanvas(compText.bottom(), terminal.renderedWidth(), compText.bottom().getHeight())
        ).render(this.canvas, 0, 0, 0);

        new ImageView(
                termX - compText.leftSide().getWidth(), termY,
                new RepeatingCanvas(compText.leftSide(), compText.leftSide().getWidth(), terminal.renderedHeight())
        ).render(this.canvas, 0, 0, 0);


        new ImageView(
                termX + terminal.renderedWidth(), termY,
                new RepeatingCanvas(compText.rightSide(), compText.rightSide().getWidth(), terminal.renderedHeight())
        ).render(this.canvas, 0, 0, 0);

        new ImageView(termX - compText.leftTop().getWidth(), termY - compText.leftTop().getHeight(), compText.leftTop())
                .render(this.canvas, 0, 0, 0);
        new ImageView(termX + terminal.renderedWidth(), termY - compText.rightTop().getHeight(), compText.rightTop())
                .render(this.canvas, 0, 0, 0);

        new ImageView(termX - compText.leftBottom().getWidth(), termY + terminal.renderedHeight(), compText.leftBottom())
                .render(this.canvas, 0, 0, 0);
        new ImageView(termX + terminal.renderedWidth(), termY + terminal.renderedHeight(), compText.rightBottom())
                .render(this.canvas, 0, 0, 0);
    }

    private void drawUpdate() {
        var image = TerminalExt.of(this.computer).getMiniRenderer().getImage(player.getEntityWorld().getTime());
        CanvasUtils.draw(this.canvas, (128 - image.getWidth()) / 2, (128 - image.getHeight()) / 2, image);
    }

    public void updateValues(Entity entity) {
        tick(entity);
    }

    public int id() {
        return this.canvas != null ? this.canvas.getId() : 0;
    }
}
