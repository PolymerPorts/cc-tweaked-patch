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
import eu.pb4.factorytools.api.util.LazyItemStack;
import eu.pb4.factorytools.api.virtualentity.BlockModel;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.virtualentity.api.VirtualEntityUtils;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class TurtleModel extends BlockModel {
    public static final LazyItemStack COLORED_TURTLE_MODEL = ItemDisplayElementUtil.getModel(Identifier.fromNamespaceAndPath(ComputerCraftAPI.MOD_ID, "block/turtle_colour"));
    public static final Identifier ELF_OVERLAY_MODEL = Identifier.fromNamespaceAndPath(ComputerCraftAPI.MOD_ID, "block/turtle_elf_overlay");
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
    private Vec3 lastPos;
    private int color = -1;
    private Identifier overlayId;
    private Identifier overlayId2;

    public TurtleModel(BlockState state, BlockPos pos) {
        this.lastPos = Vec3.atCenterOf(pos);
        this.baseYaw = state.getValue(TurtleBlock.FACING).toYRot();
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
    protected void startWatchingExtraPackets(ServerGamePacketListenerImpl player, Consumer<Packet<ClientGamePacketListener>> packetConsumer) {
        super.startWatchingExtraPackets(player, packetConsumer);
        packetConsumer.accept(VirtualEntityUtils.createClientboundSetPassengersPacket(this.base.getEntityId(), IntList.of(this.leftAttachment.getEntityId(),
                this.rightAttachment.getEntityId(), this.overlay.getEntityId())));
    }

    @Override
    protected void notifyElementsOfPositionUpdate(Vec3 newPos, Vec3 delta) {
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

        var id = upgrade.holder().key().identifier();

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
                this.base.setItem(ItemDisplayElementUtil.getModel(this.blockState().getBlock().asItem()).get());
            } else {
                var model = COLORED_TURTLE_MODEL.get().copy();
                model.set(DataComponents.DYED_COLOR, new DyedItemColor(this.color));
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
                this.overlay.setItem(overlay == null ? ItemStack.EMPTY : ItemDisplayElementUtil.getModel(overlay.model()).get());
            }

            if (!Objects.equals(overlay2, this.overlayId2)) {
                this.overlayId2 = overlay2;
                this.overlay.setItem(ItemDisplayElementUtil.getModel(overlay2).get());
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
