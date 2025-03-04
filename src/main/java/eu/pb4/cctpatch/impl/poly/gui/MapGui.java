package eu.pb4.cctpatch.impl.poly.gui;

import com.google.common.base.Predicates;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.datafixers.util.Pair;
import eu.pb4.cctpatch.impl.poly.render.CanvasRenderer;
import eu.pb4.cctpatch.impl.poly.render.ImageButton;
import eu.pb4.cctpatch.impl.poly.render.ScreenElement;
import eu.pb4.cctpatch.impl.poly.textures.GuiTextures;
import eu.pb4.cctpatch.mixin.ServerPlayNetworkHandlerAccessor;
import eu.pb4.mapcanvas.api.core.*;
import eu.pb4.mapcanvas.api.utils.CanvasUtils;
import eu.pb4.mapcanvas.api.utils.VirtualDisplay;
import eu.pb4.playerdata.api.PlayerDataApi;
import eu.pb4.polymer.core.api.utils.PolymerUtils;
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.attachment.ManualAttachment;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.DisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.SimpleEntityElement;
import eu.pb4.polymer.virtualentity.api.tracker.EntityTrackedData;
import eu.pb4.sgui.api.gui.HotbarGui;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapDecorationType;
import net.minecraft.item.map.MapDecorationTypes;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class MapGui extends HotbarGui {

    private static final Identifier DISTANCE_STORAGE_ID = Identifier.of("cct-patch", "view_shift");
    private static final Vec3d DEFAULT_SHIFT = new Vec3d(0, 0, 1);
    private static final Packet<?> COMMAND_PACKET;
    public final CombinedPlayerCanvas canvas;
    public final VirtualDisplay virtualDisplay;
    public final CanvasRenderer renderer;
    public final BlockPos pos;
    @Nullable
    public final CanvasIcon cursor;
    @Nullable
    public final ItemDisplayElement cursor2;
    public final DisplayElement cameraPoint;

    public final ElementHolder holder = new ElementHolder();
    private final BlockPos zeroPos;

    public float xRot;
    public float yRot;
    public int cursorX;
    public int cursorY;
    public int mouseMoves;
    private boolean blockWeather;

    public MapGui(ServerPlayerEntity player) {
        super(player);
        var pos = player.getBlockPos().withY(2048);
        this.pos = pos;
        var dir = Direction.NORTH;
        this.canvas = DrawableCanvas.create(5, 3);
        this.zeroPos =  pos.offset(dir).offset(dir.rotateYClockwise(), 2).up();
        this.virtualDisplay = VirtualDisplay.of(this.canvas, zeroPos, dir, 0, true);
        this.renderer = CanvasRenderer.of(new CanvasImage(this.canvas.getWidth(), this.canvas.getHeight()));
        this.renderer.add(new ImageButton(560, 32, GuiTextures.CLOSE_ICON, (a, b, c) -> this.close()));

        this.canvas.addPlayer(player);
        this.virtualDisplay.addPlayer(player);
        this.holder.setAttachment(new SelfHolder());
        this.holder.startWatching(player);

        this.cameraPoint = new BlockDisplayElement();
        var x = PlayerDataApi.getGlobalDataFor(this.player, DISTANCE_STORAGE_ID);

        this.setDistance(x != null ? Vec3d.CODEC.decode(NbtOps.INSTANCE, x).result()
                .map(Pair::getFirst).orElse(DEFAULT_SHIFT) : DEFAULT_SHIFT);
        this.holder.addElement(this.cameraPoint);

        var horse = new SimpleEntityElement(EntityType.HORSE);
        horse.setInvisible(true);
        horse.setOffset(new Vec3d(0, 10, 0));
        horse.setYaw(0);
        horse.setPitch(0);
        this.holder.addElement(horse);

        this.cursorX = this.canvas.getWidth();
        this.cursorY = this.canvas.getHeight(); // MapDecoration.Type.TARGET_POINT
        this.cursor = true ? this.canvas.createIcon(MapDecorationTypes.TARGET_POINT, true, this.cursorX, this.cursorY, (byte) 14, null) : null;

        if (false) {
            this.cursor2 = new ItemDisplayElement(Items.GLASS_PANE);
            this.cursor2.setInterpolationDuration(1);
            this.cursor2.setScale(new Vector3f(1 / 16f));
            this.holder.addElement(this.cursor2);
        } else {
            this.cursor2 = null;
        }

        player.networkHandler.sendPacket(VirtualEntityUtils.createSetCameraEntityPacket(this.cameraPoint.getEntityId()));
        this.xRot = player.getPitch();
        this.yRot = player.getYaw();
        player.networkHandler.sendPacket(VirtualEntityUtils.createRidePacket(horse.getEntityId(), IntList.of(player.getId())));
        player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_MODE_CHANGED, GameMode.SPECTATOR.getId()));
        player.networkHandler.sendPacket(new EntityS2CPacket.Rotate(player.getId(), (byte) 0, (byte) 0, player.isOnGround()));
        player.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(player.getId(), List.of(DataTracker.SerializedEntry.of(EntityTrackedData.POSE, EntityPose.STANDING))));
        player.networkHandler.sendPacket(COMMAND_PACKET);

        this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STOPPED, 0.0F));
        this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, 0));
        this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, 0));
        this.blockWeather = true;

        for (int i = 0; i < 9; i++) {
            this.setSlot(i, new ItemStack(Items.ENDER_EYE));
        }

        player.networkHandler.sendPacket(new GameMessageS2CPacket(Text.translatable("text.cctpatch.exit", "Ctrl", Text.keybind("key.drop"))
                .formatted(Formatting.RED), true));
    }

    public void render() {
        this.renderer.render(this.player.getWorld().getTime(), this.cursorX / 2, this.cursorY / 2);
        // Debug maps
        if (false && FabricLoader.getInstance().isDevelopmentEnvironment()) {
            for (int x = 0; x < this.canvas.getSectionsWidth(); x++) {
                CanvasUtils.fill(this.renderer.canvas(), x * 128, 0, x * 128 + 1, this.canvas.getHeight(), CanvasColor.RED_HIGH);
            }
            for (int x = 0; x < this.canvas.getSectionsHeight(); x++) {
                CanvasUtils.fill(this.renderer.canvas(), 0, x * 128, this.canvas.getWidth(), x * 128 + 1, CanvasColor.BLUE_HIGH);
            }
        }

        CanvasUtils.draw(this.canvas, 0, 0, this.renderer.canvas());
        this.canvas.sendUpdates();
    }

    @Override
    public void onTick() {
        this.holder.tick();
        ((ServerPlayNetworkHandlerAccessor) this.player.networkHandler).setVehicleFloatingTicks(0);
        ((ServerPlayNetworkHandlerAccessor) this.player.networkHandler).setFloatingTicks(0);
        this.render();
    }

    @Override
    public void onClose() {
        if (this.cursor != null) {
            this.cursor.remove();
        }
        this.blockWeather = false;
        this.virtualDisplay.removePlayer(this.player);
        this.virtualDisplay.destroy();
        //this.virtualDisplay2.destroy();
        this.canvas.removePlayer(this.player);
        this.canvas.destroy();
        this.player.server.getCommandManager().sendCommandTree(this.player);
        this.holder.stopWatching(this.player);
        var world = this.player.getServerWorld();
        if (!world.isRaining()) {
            this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STOPPED, 0.0F));
        } else {
            this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_STARTED, 0.0F));
        }
        this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED, world.getRainGradient(1)));
        this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED, world.getThunderGradient(1)));        this.player.networkHandler.sendPacket(new SetCameraEntityS2CPacket(this.player));
        this.player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.GAME_MODE_CHANGED,
                this.player.interactionManager.getGameMode().getId()));
        this.player.networkHandler.sendPacket(new PlayerPositionLookS2CPacket(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch(), EnumSet.noneOf(PositionFlag.class), 0));
        super.onClose();
    }

    public void onChatInput(String message) {

    }

    public void onCommandInput(String command) {

    }

    public void onCommandSuggestion(int id, String fullCommand) {

    }

    public void onCameraMove(float xRot, float yRot) {
        this.mouseMoves++;
        if (this.mouseMoves < 4) {
            return;
        }

        this.xRot = xRot;
        this.yRot = yRot;

        this.cursorX = this.cursorX + (int) ((xRot > 0.3 ? 6 : xRot < -0.3 ? -6 : 0) * (Math.abs(xRot) - 0.3));
        this.cursorY = this.cursorY + (int) ((yRot > 0.3 ? 6 : yRot < -0.3 ? -6 : 0) * (Math.abs(yRot) - 0.3));

        this.cursorX = MathHelper.clamp(this.cursorX, 5, this.canvas.getWidth() * 2 - 5);
        this.cursorY = MathHelper.clamp(this.cursorY, 5, this.canvas.getHeight() * 2 - 5);

        if (this.cursor != null) {
            this.cursor.move(this.cursorX + 4, this.cursorY + 4, this.cursor.getRotation());
        }
        if (this.cursor2 != null) {
            this.cursor2.setTranslation(new Vector3f(-(this.cursorX + 4) / 256f, -(this.cursorY + 4) / 256f, 0));
            this.cursor2.startInterpolationIfDirty();
            this.cursor2.tick();
        }
    }

    @Override
    public boolean onClickEntity(int entityId, EntityInteraction type, boolean isSneaking, @Nullable Vec3d interactionPos) {
        if (type == EntityInteraction.ATTACK) {
            this.renderer.click(this.cursorX / 2, this.cursorY / 2, ScreenElement.ClickType.LEFT_DOWN);
        } else {
            this.renderer.click(this.cursorX / 2, this.cursorY / 2, ScreenElement.ClickType.RIGHT_DOWN);
        }

        return super.onClickEntity(entityId, type, isSneaking, interactionPos);
    }

    public void setDistance(Vec3d vec) {
        PlayerDataApi.setGlobalDataFor(this.player, DISTANCE_STORAGE_ID, Vec3d.CODEC.encodeStart(NbtOps.INSTANCE, vec)
                .result().get());

        this.cameraPoint.setOffset(new Vec3d(-this.canvas.getSectionsWidth() / 2d - vec.x, -this.canvas.getSectionsHeight() / 2d + vec.y, -0.8 - vec.z));
        this.cameraPoint.tick();
    }

    public void onPlayerAction(PlayerActionC2SPacket.Action action, Direction direction, BlockPos pos) {
        if (action == PlayerActionC2SPacket.Action.DROP_ALL_ITEMS) {
            this.close();
        }
    }

    // deltaX/Z is currently useless while in camera mode, as it is always 0
    public void onPlayerInput(float deltaX, float deltaZ, boolean jumping, boolean shiftKeyDown) {

    }

    public void onPlayerCommand(int id, ClientCommandC2SPacket.Mode action, int data) {
    }

    static {
        var commandNode = new RootCommandNode<CommandSource>();

        commandNode.addChild(
            new ArgumentCommandNode<>(
                "command",
                StringArgumentType.greedyString(),
                null,
                Predicates.alwaysTrue(),
                null,
                null,
                true,
                (ctx, builder) -> null
            )
        );

        COMMAND_PACKET = new CommandTreeS2CPacket(commandNode);
    }


    public boolean preventPacket(Packet<?> packet) {
        if (packet instanceof GameStateChangeS2CPacket state && this.blockWeather) {
            return state.getReason() == GameStateChangeS2CPacket.GAME_MODE_CHANGED
                    || state.getReason() == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED
                    || state.getReason() == GameStateChangeS2CPacket.RAIN_STARTED
                    || state.getReason() == GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED;
        } else if (packet instanceof PlaySoundS2CPacket sound){
            var camera = this.holder.getPos().add(this.cameraPoint.getOffset());
            if (camera.squaredDistanceTo(sound.getX(), sound.getY(), sound.getZ()) < 64 * 64) {
                return false;
            } else if (this.player.getEyePos().squaredDistanceTo(sound.getX(), sound.getY(), sound.getZ()) < 64 * 64) {
                var pos = camera.add(new Vec3d(sound.getX(), sound.getY(), sound.getZ()).subtract(this.player.getEyePos())
                        .rotateY(-this.player.getYaw() * MathHelper.RADIANS_PER_DEGREE));
                this.player.networkHandler.sendPacket(new PlaySoundS2CPacket(sound.getSound(), sound.getCategory(), pos.x, pos.y, pos.z, sound.getVolume(), sound.getPitch(), sound.getSeed()));
            }

            return true;
        }

        return false;
    }

    private class SelfHolder implements HolderAttachment {

        @Override
        public ElementHolder holder() {
            return MapGui.this.holder;
        }

        @Override
        public void destroy() {

        }

        @Override
        public Vec3d getPos() {
            return Vec3d.of(zeroPos).add(1, 1,1 - 1 / 32f);
        }

        @Override
        public ServerWorld getWorld() {
            return MapGui.this.getPlayer().getServerWorld();
        }

        @Override
        public void updateCurrentlyTracking(Collection<ServerPlayNetworkHandler> currentlyTracking) {}

        @Override
        public void updateTracking(ServerPlayNetworkHandler tracking) {}
    }
}
