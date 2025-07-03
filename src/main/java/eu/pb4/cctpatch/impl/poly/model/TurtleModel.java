package eu.pb4.cctpatch.impl.poly.model;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.shared.turtle.blocks.TurtleBlock;
import dan200.computercraft.shared.turtle.core.TurtleBrain;
import dan200.computercraft.shared.util.Holiday;
import eu.pb4.cctpatch.impl.poly.res.TurtleOverlay;
import eu.pb4.cctpatch.impl.poly.res.turtleupgrade.EmptyUpgradeModel;
import eu.pb4.cctpatch.impl.poly.res.turtleupgrade.ItemUpgradeModel;
import eu.pb4.cctpatch.impl.poly.res.turtleupgrade.TurtleUpgradeModel;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class TurtleModel extends BlockModel {
    public static final ItemStack COLORED_TURTLE_MODEL = ItemDisplayElementUtil.getModel(Identifier.of(ComputerCraftAPI.MOD_ID, "block/turtle_colour"));
    public static final Identifier ELF_OVERLAY_MODEL = Identifier.of(ComputerCraftAPI.MOD_ID, "block/turtle_elf_overlay");
    public static final Map<Identifier, TurtleOverlay> OVERLAY = new HashMap<>();
    public static final Map<Identifier, TurtleUpgradeModel> UPGRADES = new HashMap<>();
    private final ItemDisplayElement base;
    private final ItemDisplayElement overlay;
    private final ItemDisplayElement overlay2;
    private final ItemDisplayElement leftAttachment;
    private final ItemDisplayElement rightAttachment;

    private float baseYaw;
    private UpgradeData<ITurtleUpgrade> leftUpgrade;
    private UpgradeData<ITurtleUpgrade> rightUpgrade;
    private Vec3d lastPos;
    private int color = -1;
    private Identifier overlayId;
    private Identifier overlayId2;

    public TurtleModel(BlockState state, BlockPos pos) {
        this.lastPos = Vec3d.ofCenter(pos);
        this.baseYaw = state.get(TurtleBlock.FACING).getPositiveHorizontalDegrees();
        this.base = ItemDisplayElementUtil.createSimple(ItemDisplayElementUtil.getModel(state.getBlock().asItem()));
        this.base.setTeleportDuration(1);
        this.base.setItemDisplayContext(ItemDisplayContext.NONE);
        this.base.setYaw(this.baseYaw);
        this.overlay = ItemDisplayElementUtil.createSimple();
        this.overlay.setTeleportDuration(1);
        this.overlay.setItemDisplayContext(ItemDisplayContext.NONE);
        this.overlay.setYaw(this.baseYaw);
        this.overlay2 = ItemDisplayElementUtil.createSimple();
        this.overlay2.setTeleportDuration(1);
        this.overlay2.setItemDisplayContext(ItemDisplayContext.NONE);
        this.overlay2.setYaw(this.baseYaw);
        this.leftAttachment = ItemDisplayElementUtil.createSimple();
        this.leftAttachment.setInterpolationDuration(1);
        this.leftAttachment.setItemDisplayContext(ItemDisplayContext.NONE);
        this.leftAttachment.setYaw(this.baseYaw);
        this.rightAttachment = ItemDisplayElementUtil.createSimple();
        this.rightAttachment.setInterpolationDuration(1);
        this.rightAttachment.setItemDisplayContext(ItemDisplayContext.NONE);
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

    private void setupUpgradeModel(UpgradeData<ITurtleUpgrade> upgrade, TurtleBrain brain, TurtleSide turtleSide, ItemDisplayElement attachment) {
        if (upgrade == null) {
            EmptyUpgradeModel.INSTANCE.setupModel(null, brain, turtleSide, attachment);
            return;
        }

        var id = upgrade.holder().registryKey().getValue();

        var model = UPGRADES.getOrDefault(id, ItemUpgradeModel.INSTANCE);

        model.setupModel(upgrade, brain, turtleSide, attachment);
    }

    public void update(TurtleBrain turtleBrain) {
        var pos = turtleBrain.getVisualPosition(1);
        this.setYaw(turtleBrain.getVisualYaw(1));

        if (!pos.equals(this.lastPos)) {
            this.base.setOverridePos(pos);
            this.lastPos = pos;
        }

        if (this.color != turtleBrain.getColour()) {
            this.color = turtleBrain.getColour();
            if (this.color == -1) {
                this.base.setItem(ItemDisplayElementUtil.getModel(this.blockState().getBlock().asItem()));
            } else {
                var model = COLORED_TURTLE_MODEL.copy();
                model.set(DataComponentTypes.DYED_COLOR, new DyedColorComponent(this.color));
                this.base.setItem(model);
            }
        }

        {
            var overlay = OVERLAY.get(turtleBrain.getOverlay());
            Identifier overlay2 = null;
            if ((overlay == null || overlay.showElfOverlay()) && Holiday.getCurrent() == Holiday.CHRISTMAS) {
                overlay2 = ELF_OVERLAY_MODEL;
            }

            if ((this.overlayId != null && overlay == null) || (turtleBrain.getOverlay() != null && !turtleBrain.getOverlay().equals(this.overlayId))) {
                this.overlayId = overlay != null ? turtleBrain.getOverlay() : null;
                this.overlay.setItem(overlay == null ? ItemStack.EMPTY : ItemDisplayElementUtil.getModel(overlay.model()));
            }

            if (!Objects.equals(overlay2, this.overlayId2)) {
                this.overlayId2 = overlay2;
                this.overlay.setItem(ItemDisplayElementUtil.getModel(overlay2));
            }
        }

        var leftUpgrade = turtleBrain.getUpgradeWithData(TurtleSide.LEFT);
        //if (!Objects.equals(this.leftUpgrade, leftUpgrade)) {
            this.leftUpgrade = leftUpgrade;
            setupUpgradeModel(leftUpgrade, turtleBrain, TurtleSide.LEFT, this.leftAttachment);
        //}

        var rightUpgrade = turtleBrain.getUpgradeWithData(TurtleSide.RIGHT);
        //if (!Objects.equals(this.rightUpgrade, rightUpgrade)) {
            this.rightUpgrade = rightUpgrade;
            setupUpgradeModel(rightUpgrade, turtleBrain, TurtleSide.RIGHT, this.rightAttachment);
        //}
    }
}
