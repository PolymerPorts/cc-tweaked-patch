package eu.pb4.cctpatch.mixin.mod.block;

import dan200.computercraft.shared.peripheral.monitor.MonitorBlock;
import dan200.computercraft.shared.peripheral.monitor.MonitorBlockEntity;
import dan200.computercraft.shared.platform.RegistryEntry;
import dan200.computercraft.shared.util.BlockEntityHelpers;
import eu.pb4.cctpatch.impl.poly.ext.MonitorBlockEntityExt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MonitorBlock.class)
public abstract class MonitorBlockMixin implements EntityBlock {
    @Shadow
    @Final
    private RegistryEntry<? extends BlockEntityType<? extends MonitorBlockEntity>> type;
    @Unique
    private final BlockEntityTicker<MonitorBlockEntity> serverTicker = (_, _, _, monitor) -> ((MonitorBlockEntityExt)monitor).updateWatchers();


    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return level.isClientSide() ? null : BlockEntityHelpers.createTickerHelper(type, this.type.get(), serverTicker);
    }
}
