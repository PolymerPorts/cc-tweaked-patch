package eu.pb4.cctpatch.mixin.mod.block;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlock;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlockEntity;
import dan200.computercraft.shared.peripheral.monitor.ServerMonitor;
import eu.pb4.cctpatch.impl.poly.font.Fonts;
import eu.pb4.cctpatch.impl.poly.ext.ServerMonitorExt;
import eu.pb4.cctpatch.impl.poly.ext.TerminalExt;
import eu.pb4.mapcanvas.api.core.CanvasColor;
import eu.pb4.mapcanvas.api.core.CanvasImage;
import eu.pb4.mapcanvas.api.core.DrawableCanvas;
import eu.pb4.mapcanvas.api.core.PlayerCanvas;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;

@Mixin(MonitorBlockEntity.class)
public abstract class MonitorBlockEntityMixin extends BlockEntity {
    @Shadow private int xIndex;
    @Shadow private int yIndex;

    @Shadow public abstract int getHeight();

    @Shadow private int height;
    @Shadow private int width;

    @Shadow @Nullable
    protected abstract ServerMonitor getServerMonitor();

    @Shadow protected abstract void eachComputer(Consumer<IComputerAccess> fun);

    @Unique
    private PlayerCanvas canvas = null;
    @Unique
    private VirtualDisplay display = null;
    @Unique
    private final Set<ServerPlayer> currentWatchers = new HashSet<>();

    public MonitorBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(BlockEntityType type, BlockPos pos, BlockState state, boolean advanced, CallbackInfo ci) {
        this.updateDisplaySize();
    }

    @Inject(method = "setRemoved", at = @At("TAIL"))
    private void onRemoved(CallbackInfo ci) {
        if (this.display != null) {
            this.display.destroy();
            this.canvas.destroy();
            this.currentWatchers.clear();
            this.display = null;
            this.canvas = null;
        }
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void onReadNbt(ValueInput nbt, CallbackInfo ci) {
        if (level != null) {
            this.updateDisplaySize();
        }
    }

    @Inject(method = "blockTick", at = @At("TAIL"), remap = false)
    private void onTick(CallbackInfo ci) {
        if ((this.xIndex != 0 || this.yIndex != 0) && this.display != null) {
            this.updateDisplaySize();
        } else {
            this.updateDisplay();
        }
        this.updateWatchers();
    }

    @Inject(method = "createServerMonitor", at = @At("RETURN"), remap = false)
    private void onCreateServerMonitor(CallbackInfoReturnable<ServerMonitor> cir) {
        this.updateDisplaySize();
    }

    @Inject(method = "updateBlockState", at = @At("RETURN"), remap = false)
    private void onUpdateBlockState(CallbackInfo ci) {
        this.updateDisplaySize();
    }

    //@Inject(method = "resize", at = @At(value = "INVOKE", target = ""))

    @Unique
    private void updateDisplaySize() {
        if (this.display != null) {
            this.display.destroy();
            this.display = null;
        }
        if (this.canvas != null) {
            this.canvas.destroy();
            this.canvas = null;
        }

        this.currentWatchers.clear();

        if (this.xIndex == 0 && this.yIndex == 0) {
            var facing = this.getBlockState().getValue(MonitorBlock.FACING);
            var orientation = this.getBlockState().getValue(MonitorBlock.ORIENTATION);

            int rotation;
            Direction dir;
            BlockPos blockPos;

            if (orientation == Direction.NORTH) {
                rotation = 0;
                dir = facing;
                blockPos = this.getBlockPos().relative(dir).above(this.getHeight() - 1);
            } else {
                dir = orientation;
                rotation = facing.get2DDataValue();
                blockPos = this.getBlockPos().relative(dir).relative(facing, orientation.getStepY() * (1 - this.height));
            }

            this.canvas = DrawableCanvas.create(this.width, this.height);
            this.display = VirtualDisplay.builder(this.canvas, blockPos, dir).rotation(Rotation.values()[rotation]).glowing(true)
                    .invisible().raycast().interactionCallback(this::onClick).build();
            this.updateDisplay();
        }
    }

    @Unique
    private void onClick(ServerPlayer player, VirtualDisplay.ClickType type, int x, int y) {
        var monitor = this.getServerMonitor();
        if (monitor != null && type == VirtualDisplay.ClickType.RIGHT) {
            x = (x - 20) / Fonts.FONT_WIDTH / ServerMonitorExt.of(monitor).getTextScalePublic();
            y = (y - 21) / Fonts.FONT_HEIGHT / ServerMonitorExt.of(monitor).getTextScalePublic();
            if (x >= 0 && y >= 0 && x < monitor.getTerminal().getWidth() && y < monitor.getTerminal().getHeight()) {
                int finalX = x + 1;
                int finalY = y + 1;
                eachComputer(c -> c.queueEvent( "monitor_touch", c.getAttachmentName(), finalX, finalY) );
            }

        }

    }

    @Unique
    private void updateDisplay() {
        if (this.xIndex != 0 && this.yIndex != 0) {
            if (this.canvas != null) {
                this.canvas.destroy();
            }
            if (this.display != null) {
                this.display.destroy();
            }

            return;
        }


        {
            var image = new CanvasImage(this.canvas.getWidth(), this.canvas.getHeight());

            var monitor = this.getServerMonitor();
            if (monitor != null && monitor.getTerminal() != null) {
                CanvasUtils.fill(image, 16, 16, image.getWidth() - 16, image.getHeight() - 16, CanvasColor.BLACK_NORMAL);

                assert this.level != null;
                var screen = TerminalExt.of(monitor.getTerminal()).getRenderer().getImage(this.level.getGameTime());
                var scale = ServerMonitorExt.of(monitor).getTextScalePublic();

                int sWidth = (int) (screen.getWidth() * scale);
                int sHeight = (int) (screen.getHeight() * scale);

                CanvasUtils.draw(image, 20, 21, sWidth, sHeight, screen);
            } else {
                CanvasUtils.fill(image, 20, 21, image.getWidth() - 20, image.getHeight() - 20, CanvasColor.BLACK_LOWEST);
            }

            CanvasUtils.draw(this.canvas, 0, 0, image);
        }

        this.updateWatchers();
    }

    @Unique
    public void updateWatchers() {
        if (this.level != null && this.display != null && this.canvas != null) {
            var pos = this.getBlockPos();
            var players = ((ServerLevel) this.level).getPlayers((p) -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 4096);

            for (var player : players) {
                if (!this.currentWatchers.contains(player)) {
                    this.canvas.addPlayer(player);
                    this.display.addPlayer(player);
                    this.currentWatchers.add(player);
                }
            }

            for (var player : new ArrayList<>(this.currentWatchers)) {
                if (!players.contains(player)) {
                    this.display.removePlayer(player);
                    this.canvas.removePlayer(player);
                    this.currentWatchers.remove(player);
                }
            }

            this.canvas.sendUpdates();
        }
    }
}
