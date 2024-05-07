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
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
    private final Set<ServerPlayerEntity> currentWatchers = new HashSet<>();

    public MonitorBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(BlockEntityType type, BlockPos pos, BlockState state, boolean advanced, CallbackInfo ci) {
        this.updateDisplaySize();
    }

    @Inject(method = "markRemoved", at = @At("TAIL"))
    private void onRemoved(CallbackInfo ci) {
        if (this.display != null) {
            this.display.destroy();
            this.canvas.destroy();
            this.currentWatchers.clear();
            this.display = null;
            this.canvas = null;
        }
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void onReadNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries, CallbackInfo ci) {
        if (world != null) {
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
            var facing = this.getCachedState().get(MonitorBlock.FACING);
            var orientation = this.getCachedState().get(MonitorBlock.ORIENTATION);

            int rotation;
            Direction dir;
            BlockPos blockPos;

            if (orientation == Direction.NORTH) {
                rotation = 0;
                dir = facing;
                blockPos = this.getPos().offset(dir).up(this.getHeight() - 1);
            } else {
                dir = orientation;
                rotation = facing.getHorizontal();
                blockPos = this.getPos().offset(dir).offset(facing, orientation.getOffsetY() * (1 - this.height));
            }

            this.canvas = DrawableCanvas.create(this.width, this.height);
            this.display = VirtualDisplay.builder(this.canvas, blockPos, dir).rotation(BlockRotation.values()[rotation]).glowing(true)
                    .invisible().raycast().callback(this::onClick).build();
            this.updateDisplay();
        }
    }

    @Unique
    private void onClick(ServerPlayerEntity player, ClickType action, int x, int y) {
        var monitor = this.getServerMonitor();
        if (monitor != null && action == ClickType.RIGHT) {
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

                assert this.world != null;
                var screen = TerminalExt.of(monitor.getTerminal()).getRenderer().getImage(this.world.getTime());
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
        if (this.world != null && this.display != null && this.canvas != null) {
            var pos = this.getPos();
            var players = ((ServerWorld) this.world).getPlayers((p) -> p.squaredDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < 4096);

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
