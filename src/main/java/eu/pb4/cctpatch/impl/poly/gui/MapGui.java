package eu.pb4.cctpatch.impl.poly.gui;

import com.google.common.base.Predicates;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
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
import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.data.EntityData;
import eu.pb4.polymer.virtualentity.api.elements.BlockDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.DisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.elements.SimpleEntityElement;
import eu.pb4.sgui.api.gui.HotbarGui;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public class MapGui extends HotbarGui {

    private static final Identifier DISTANCE_STORAGE_ID = Identifier.fromNamespaceAndPath("cct-patch", "view_shift");
    private static final Vec3 DEFAULT_SHIFT = new Vec3(0, 0, 1);
    private static final Packet<?> COMMAND_PACKET;

    static {
        var commandNode = new RootCommandNode<SharedSuggestionProvider>();

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

        COMMAND_PACKET = new ClientboundCommandsPacket(commandNode, new ClientboundCommandsPacket.NodeInspector<SharedSuggestionProvider>() {
            @Nullable
            @Override
            public Identifier suggestionId(ArgumentCommandNode<SharedSuggestionProvider, ?> node) {
                return SuggestionProviders.getName(SuggestionProviders.ASK_SERVER);
            }

            @Override
            public boolean isExecutable(CommandNode<SharedSuggestionProvider> node) {
                return true;
            }

            @Override
            public boolean isRestricted(CommandNode<SharedSuggestionProvider> node) {
                return false;
            }
        });
    }

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

    public MapGui(ServerPlayer player) {
        super(player);
        var pos = player.blockPosition().atY(2048);
        this.pos = pos;
        var dir = Direction.NORTH;
        this.canvas = DrawableCanvas.create(5, 3);
        this.zeroPos = pos.relative(dir).relative(dir.getClockWise(), 2).above();
        this.virtualDisplay = VirtualDisplay.builder(this.canvas, zeroPos, dir).glowing().invisible().build();
        this.renderer = CanvasRenderer.of(new CanvasImage(this.canvas.getWidth(), this.canvas.getHeight()));
        this.renderer.add(new ImageButton(560, 32, GuiTextures.CLOSE_ICON, (a, b, c) -> this.close()));

        this.canvas.addPlayer(player);
        this.virtualDisplay.addPlayer(player);
        this.holder.setAttachment(new SelfHolder());
        this.holder.startWatching(player);

        this.cameraPoint = new BlockDisplayElement();
        var x = PlayerDataApi.getGlobalDataFor(this.player, DISTANCE_STORAGE_ID);

        this.setDistance(x != null ? Vec3.CODEC.decode(NbtOps.INSTANCE, x).result()
                .map(Pair::getFirst).orElse(DEFAULT_SHIFT) : DEFAULT_SHIFT);
        this.holder.addElement(this.cameraPoint);

        var horse = new SimpleEntityElement(EntityType.HORSE);
        horse.setInvisible(true);
        horse.setOffset(new Vec3(0, 10, 0));
        horse.setYaw(0);
        horse.setPitch(0);
        this.holder.addElement(horse);

        this.cursorX = this.canvas.getWidth();
        this.cursorY = this.canvas.getHeight(); // MapDecoration.Type.TARGET_POINT
        this.cursor = this.canvas.createIcon(MapDecorationTypes.TARGET_POINT, true, this.cursorX, this.cursorY, (byte) 14, null);

        if (false) {
            this.cursor2 = new ItemDisplayElement(Items.GLASS_PANE);
            this.cursor2.setInterpolationDuration(1);
            this.cursor2.setScale(new Vector3f(1 / 16f));
            this.holder.addElement(this.cursor2);
        } else {
            this.cursor2 = null;
        }

        player.connection.send(VirtualEntityUtils.createClientboundSetCameraPacket(this.cameraPoint.getEntityId()));
        this.xRot = player.getXRot();
        this.yRot = player.getYRot();
        player.connection.send(VirtualEntityUtils.createClientboundSetPassengersPacket(horse.getEntityId(), IntList.of(player.getId())));
        player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, GameType.SPECTATOR.getId()));
        player.connection.send(new ClientboundMoveEntityPacket.Rot(player.getId(), (byte) 0, (byte) 0, player.onGround()));
        player.connection.send(new ClientboundSetEntityDataPacket(player.getId(), List.of(SynchedEntityData.DataValue.create(EntityData.POSE, Pose.STANDING))));
        player.connection.send(COMMAND_PACKET);

        this.player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0F));
        this.player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, 0));
        this.player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, 0));
        this.blockWeather = true;

        for (int i = 0; i < 9; i++) {
            this.setSlot(i, new ItemStack(Items.ENDER_EYE));
        }

        player.connection.send(new ClientboundSystemChatPacket(Component.translatable("text.cctpatch.exit", Component.keybind("key.inventory"))
                .withStyle(ChatFormatting.RED), true));
    }

    public void render() {
        this.renderer.render(this.player.level().getGameTime(), this.cursorX / 2, this.cursorY / 2);
        // Debug maps
        if (false) {
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
        ((ServerPlayNetworkHandlerAccessor) this.player.connection).setAboveGroundTickCount(0);
        ((ServerPlayNetworkHandlerAccessor) this.player.connection).setAboveGroundVehicleTickCount(0);
        this.render();
    }

    @Override
    public void afterRemoval() {
        if (this.cursor != null) {
            this.cursor.remove();
        }
        this.blockWeather = false;
        this.virtualDisplay.removePlayer(this.player);
        this.virtualDisplay.destroy();
        //this.virtualDisplay2.destroy();
        this.canvas.removePlayer(this.player);
        this.canvas.destroy();
        this.player.level().getServer().getCommands().sendCommands(this.player);
        this.holder.stopWatching(this.player);
        var world = this.player.level();
        if (!world.isRaining()) {
            this.player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0F));
        } else {
            this.player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0F));
        }
        this.player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, world.getRainLevel(1)));
        this.player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, world.getThunderLevel(1)));
        this.player.connection.send(new ClientboundSetCameraPacket(this.player));
        this.player.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE,
                this.player.gameMode.getGameModeForPlayer().getId()));
        this.player.connection.send(new ClientboundPlayerPositionPacket(this.player.getId(), PositionMoveRotation.of(this.player), EnumSet.noneOf(Relative.class)));
        super.afterRemoval();
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

        this.cursorX = Mth.clamp(this.cursorX, 5, this.canvas.getWidth() * 2 - 5);
        this.cursorY = Mth.clamp(this.cursorY, 5, this.canvas.getHeight() * 2 - 5);

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
    public boolean onEntityAttacked(int entityId) {
        this.renderer.click(this.cursorX / 2, this.cursorY / 2, ScreenElement.ClickType.LEFT_DOWN);
        return super.onEntityAttacked(entityId);
    }

    @Override
    public boolean onEntityInteracted(int entityId, InteractionHand hand, boolean isSneaking, Vec3 interactionPos) {
        this.renderer.click(this.cursorX / 2, this.cursorY / 2, ScreenElement.ClickType.RIGHT_DOWN);
        return super.onEntityInteracted(entityId, hand, isSneaking, interactionPos);
    }

    public void setDistance(Vec3 vec) {
        PlayerDataApi.setGlobalDataFor(this.player, DISTANCE_STORAGE_ID, Vec3.CODEC.encodeStart(NbtOps.INSTANCE, vec)
                .result().get());

        this.cameraPoint.setOffset(new Vec3(-this.canvas.getSectionsWidth() / 2d - vec.x, -this.canvas.getSectionsHeight() / 2d + vec.y, -0.8 - vec.z));
        this.cameraPoint.tick();
    }

    public void onPlayerAction(ServerboundPlayerActionPacket.Action action, Direction direction, BlockPos pos) {
        if (action == ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS) {
            this.close();
        }
    }

    public void onPlayerInput(Input input) {

    }

    public void onPlayerCommand(int id, ServerboundPlayerCommandPacket.Action action, int data) {
        if (action == ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY) {
            this.close();
        }
    }

    public boolean preventPacket(Packet<?> packet) {
        if (packet instanceof ClientboundGameEventPacket state && this.blockWeather) {
            return state.getEvent() == ClientboundGameEventPacket.CHANGE_GAME_MODE
                    || state.getEvent() == ClientboundGameEventPacket.RAIN_LEVEL_CHANGE
                    || state.getEvent() == ClientboundGameEventPacket.START_RAINING
                    || state.getEvent() == ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE;
        } else if (packet instanceof ClientboundSoundPacket sound) {
            var camera = this.holder.getPos().add(this.cameraPoint.getOffset());
            if (camera.distanceToSqr(sound.getX(), sound.getY(), sound.getZ()) < 64 * 64) {
                return false;
            } else if (this.player.getEyePosition().distanceToSqr(sound.getX(), sound.getY(), sound.getZ()) < 64 * 64) {
                var pos = camera.add(new Vec3(sound.getX(), sound.getY(), sound.getZ()).subtract(this.player.getEyePosition())
                        .yRot(this.player.getYRot() * Mth.DEG_TO_RAD));
                this.player.connection.send(new ClientboundSoundPacket(sound.getSound(), sound.getSource(), pos.x, pos.y, pos.z, sound.getVolume(), sound.getPitch(), sound.getSeed()));
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
        public Vec3 getPos() {
            return Vec3.atLowerCornerOf(zeroPos).add(1, 1, 1 - 1 / 32f);
        }

        @Override
        public ServerLevel getWorld() {
            return MapGui.this.getPlayer().level();
        }

        @Override
        public void updateCurrentlyTracking(Collection<ServerGamePacketListenerImpl> currentlyTracking) {
        }

        @Override
        public void updateTracking(ServerGamePacketListenerImpl tracking) {
        }
    }
}
