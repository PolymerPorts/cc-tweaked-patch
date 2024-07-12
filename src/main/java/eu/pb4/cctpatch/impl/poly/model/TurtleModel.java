package eu.pb4.cctpatch.impl.poly.model;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.turtle.blocks.TurtleBlock;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import dan200.computercraft.shared.turtle.upgrades.TurtleCraftingTable;
import dan200.computercraft.shared.turtle.upgrades.TurtleSpeaker;
import dan200.computercraft.shared.turtle.upgrades.TurtleTool;
import dan200.computercraft.shared.util.Holiday;
import eu.pb4.cctpatch.mixin.TurtleModemAccessor;
import eu.pb4.factorytools.api.resourcepack.BaseItemProvider;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class TurtleModel extends BlockModel {
    public static final ModemModel NORMAL_MODEM_MODEL = ModemModel.of(Identifier.of(ComputerCraftAPI.MOD_ID, "block/turtle_modem_normal"));
    public static final ModemModel ADVANCED_MODEM_MODEL = ModemModel.of(Identifier.of(ComputerCraftAPI.MOD_ID, "block/turtle_modem_advanced"));
    public static final SidedModel SPEAKER_MODEL = SidedModel.of(Identifier.of(ComputerCraftAPI.MOD_ID, "block/turtle_speaker"));
    public static final SidedModel CRAFTING_MODEL = SidedModel.of(Identifier.of(ComputerCraftAPI.MOD_ID, "block/turtle_crafting_table"));
    public static final ItemStack COLORED_TURTLE_MODEL = BaseItemProvider.requestModel(Items.FIREWORK_STAR, Identifier.of(ComputerCraftAPI.MOD_ID, "block/turtle_colour"));
    public static final Identifier ELF_OVERLAY_MODEL = Identifier.of(ComputerCraftAPI.MOD_ID, "block/turtle_elf_overlay");

    private static final Map<Identifier, ItemStack> OVERLAYS = new HashMap<>();
    private final ItemDisplayElement base;
    private final ItemDisplayElement overlay;
    private final ItemDisplayElement overlay2;
    private final ItemDisplayElement leftAttachment;
    private final ItemDisplayElement rightAttachment;

    private float baseYaw;
    private ITurtleUpgrade leftUpgrade;
    private ITurtleUpgrade rightUpgrade;
    private Vec3d lastPos;
    private int color = -1;
    private Identifier overlayId;
    private Identifier overlayId2;

    public static void registerOverlay(Identifier identifier) {
        if (!OVERLAYS.containsKey(identifier)) {
            OVERLAYS.put(identifier, BaseItemProvider.requestModel(identifier));
        }
    }

    public TurtleModel(BlockState state, BlockPos pos) {
        this.lastPos = Vec3d.ofCenter(pos);
        this.baseYaw = state.get(TurtleBlock.FACING).asRotation();
        this.base = ItemDisplayElementUtil.createSimple(ItemDisplayElementUtil.getModel(state.getBlock().asItem()));
        this.base.setTeleportDuration(1);
        this.base.setModelTransformation(ModelTransformationMode.NONE);
        this.base.setYaw(this.baseYaw);
        this.overlay = ItemDisplayElementUtil.createSimple();
        this.overlay.setTeleportDuration(1);
        this.overlay.setModelTransformation(ModelTransformationMode.NONE);
        this.overlay.setYaw(this.baseYaw);
        this.overlay2 = ItemDisplayElementUtil.createSimple();
        this.overlay2.setTeleportDuration(1);
        this.overlay2.setModelTransformation(ModelTransformationMode.NONE);
        this.overlay2.setYaw(this.baseYaw);
        this.leftAttachment = ItemDisplayElementUtil.createSimple();
        this.leftAttachment.setInterpolationDuration(1);
        this.leftAttachment.setModelTransformation(ModelTransformationMode.NONE);
        this.leftAttachment.setYaw(this.baseYaw);
        this.rightAttachment = ItemDisplayElementUtil.createSimple();
        this.rightAttachment.setInterpolationDuration(1);
        this.rightAttachment.setModelTransformation(ModelTransformationMode.NONE);
        this.rightAttachment.setYaw(this.baseYaw);
        this.addElement(this.base);
        this.addElement(this.overlay);
        this.addElement(this.overlay2);
        this.addElement(this.leftAttachment);
        this.addElement(this.rightAttachment);
    }

    @Override
    protected void startWatchingExtraPackets(ServerPlayNetworkHandler player, Consumer<Packet<ClientPlayPacketListener>> packetConsumer) {
        super.startWatchingExtraPackets(player, packetConsumer);
        packetConsumer.accept(VirtualEntityUtils.createRidePacket(this.base.getEntityId(), IntList.of(this.leftAttachment.getEntityId(),
                this.rightAttachment.getEntityId(), this.overlay.getEntityId())));
    }

    @Override
    protected void notifyElementsOfPositionUpdate(Vec3d newPos, Vec3d delta) {
    }

    public void setYaw(float yaw) {
        if (this.baseYaw == yaw) {
            return;
        }

        this.baseYaw = yaw;
        this.base.setYaw(this.baseYaw);
        this.overlay.setYaw(this.baseYaw);
        this.overlay2.setYaw(this.baseYaw);
        this.leftAttachment.setYaw(this.baseYaw);
        this.rightAttachment.setYaw(this.baseYaw);
    }
    public void setUpgrades(TurtleBrain brain, ITurtleUpgrade left, ITurtleUpgrade right) {
        if (this.leftUpgrade != left) {
            this.leftUpgrade = left;
            this.leftAttachment.setItem(getUpgradeModel(left, brain, TurtleSide.LEFT));
        }
        if (this.rightUpgrade != right) {
            this.rightUpgrade = right;
            this.rightAttachment.setItem(getUpgradeModel(right, brain, TurtleSide.RIGHT));
        }
    }

    private ItemStack getUpgradeModel(ITurtleUpgrade upgrade, TurtleBrain brain, TurtleSide turtleSide) {
        if(upgrade == null){
            return ItemStack.EMPTY;
        }
        if (upgrade instanceof TurtleModemAccessor modem) {
            var type = modem.isAdvanced() ? ADVANCED_MODEM_MODEL : NORMAL_MODEM_MODEL;
            var x = brain.getUpgradeData(turtleSide).get(ModRegistry.DataComponents.ON.get());
            if (x != null && x.isPresent() && x.get())
                return type.onModel.get(turtleSide);
            return type.offModel.get(turtleSide);
        } else if (upgrade instanceof TurtleSpeaker) {
            return SPEAKER_MODEL.get(turtleSide);
        } else if (upgrade instanceof TurtleCraftingTable) {
            return CRAFTING_MODEL.get(turtleSide);
        }

        return upgrade.getCraftingItem();
    }

    public void update(TurtleBrain turtleBrain) {
        var pos = turtleBrain.getVisualPosition(1);
        this.setYaw(turtleBrain.getVisualYaw(1));

        if (!pos.equals(this.lastPos)) {
            this.base.notifyMove(this.lastPos, pos, pos.subtract(this.lastPos));
            this.lastPos = pos;
        }

        if (this.color != turtleBrain.getColour()) {
            this.color = turtleBrain.getColour();
            if (this.color == -1) {
                this.base.setItem(ItemDisplayElementUtil.getModel(this.blockState().getBlock().asItem()));
            } else {
                var model = COLORED_TURTLE_MODEL.copy();
                model.set(DataComponentTypes.FIREWORK_EXPLOSION, new FireworkExplosionComponent(FireworkExplosionComponent.Type.BURST, IntList.of(this.color), IntList.of(), false, false));
                this.base.setItem(model);
            }
        }

        {
            var overlay = turtleBrain.getOverlay();
            Identifier overlay2 = null;
            if ((overlay == null || overlay.value().showElfOverlay()) && Holiday.getCurrent() == Holiday.CHRISTMAS) {
                overlay2 = ELF_OVERLAY_MODEL;
            }

            if ((this.overlayId != null && overlay == null) || (overlay != null && !overlay.value().model().equals(this.overlayId))) {
                this.overlayId = overlay != null ? overlay.value().model() : null;
                this.overlay.setItem(OVERLAYS.getOrDefault(this.overlayId, ItemStack.EMPTY));
            }

            if (!Objects.equals(overlay2, this.overlayId2)) {
                this.overlayId2 = overlay2;
                this.overlay2.setItem(OVERLAYS.getOrDefault(this.overlayId2, ItemStack.EMPTY));
            }
        }

        this.setUpgrades(turtleBrain, turtleBrain.getUpgrade(TurtleSide.LEFT), turtleBrain.getUpgrade(TurtleSide.RIGHT));

        var mat = BlockModel.mat();
        for (var side : TurtleSide.values()) {
            var upgrade = side == TurtleSide.RIGHT ? rightUpgrade : leftUpgrade;
            if (upgrade == null) continue;
            var toolAngle = turtleBrain.getToolRenderAngle(side, 1);
            mat.rotate(RotationAxis.NEGATIVE_X.rotationDegrees(toolAngle));
            if (upgrade instanceof TurtleTool) {
                mat.rotateY(MathHelper.HALF_PI);
                mat.translate(0, 0, side == TurtleSide.RIGHT ? -6.5f / 16f : 6.5f / 16f);
            }

            var att = (side == TurtleSide.RIGHT ? this.rightAttachment : this.leftAttachment);
            att.setTransformation(mat);
            mat.identity();
            if (att.isTransformationDirty()) {
                att.startInterpolation();
            }
        }
    }

    public record ModemModel(SidedModel onModel, SidedModel offModel) {
        public static ModemModel of(Identifier identifier) {
            return new ModemModel(
                    SidedModel.of(identifier.withSuffixedPath("_on")),
                    SidedModel.of(identifier.withSuffixedPath("_off"))
            );
        }
    }

    public record SidedModel(ItemStack left, ItemStack right) {
        public static SidedModel of(Identifier identifier) {
            return new SidedModel(
                    BaseItemProvider.requestModel(identifier.withSuffixedPath("_left")),
                    BaseItemProvider.requestModel(identifier.withSuffixedPath("_right"))
            );
        }

        public ItemStack get(TurtleSide side) {
            return side == TurtleSide.RIGHT ? right : left;
        }
    }
}
